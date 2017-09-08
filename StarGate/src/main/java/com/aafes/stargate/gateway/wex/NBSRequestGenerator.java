/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.nbsresponse.NBSResponse;
import com.aafes.nbsresponseacknowledgmentschema.ResponseAcknowlegment;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.util.InputType;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
import com.aafes.stargate.util.SvsUtil;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOHeader;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.LoggerFactory;

/**
 *
 * @author uikuyr
 */
public class NBSRequestGenerator {
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(NBSRequestGenerator.class.getSimpleName());
    private String iso8583Format;
    private int promptCountIndex;
    private ISOMsg isoMsg;
    private ISOHeader iSOHeader;
    private GenericPackager packager;
    private ResponseAcknowlegment responseAcknowlegment;
    private NBSResponse nBSResponse;
    Transaction transaction = new Transaction();
    
    private String applicationName;
    private String applicationVersion;
    private String daylightSavingsTimeAtSiteOne;
    private String captureOnlyRequest;
    private String sessionTypeAuth;
    private String transTypePreAuth;
    private String transTypeFinalAndSale;
    private String transTypeRefund;
    private String cardTypeWex;
    private String serviceType;
    private int index = 0;
    private String SCHEMA_PATH = "";
    String[] productDetails;
    @EJB
    private Configurator configurator;
    
    public byte[] generateLogOnPacketRequest(Transaction t, boolean isTimeoutRetry) {
        
        if (configurator == null) {
            configurator = new Configurator();
            configurator.postConstruct();
        }
        
        applicationName = configurator.get("APPLICATION_NAME");
        applicationVersion = configurator.get("APPLICATION_VERSION");
        daylightSavingsTimeAtSiteOne = configurator.get("DAYLIGHT_SAVINGS_TIME_AT_SITE_ONE");
        captureOnlyRequest = configurator.get("CAPTURE_ONLY_REQUEST");
        sessionTypeAuth = configurator.get("SESSION_TYPE_AUTH");
        transTypePreAuth = configurator.get("TRANS_TYPE_PRE_AUTH");
        transTypeFinalAndSale = configurator.get("TRANS_TYPE_FINAL_AND_SALE");
        transTypeRefund = configurator.get("TRANS_TYPE_REFUND");
        cardTypeWex = configurator.get("CARD_TYPE_WEX");
        serviceType = configurator.get("SERVICE_TYPE");
        
        if (applicationName == null) {
            applicationName = configurator.get("APPLICATION_NAME");
        }
        if (applicationVersion == null) {
            applicationVersion = configurator.get("APPLICATION_VERSION");
        }
        if (daylightSavingsTimeAtSiteOne == null) {
            daylightSavingsTimeAtSiteOne = configurator.get("DAYLIGHT_SAVINGS_TIME_AT_SITE_ONE");
        }
        if (captureOnlyRequest == null) {
            captureOnlyRequest = configurator.get("CAPTURE_ONLY_REQUEST");
        }
        if (sessionTypeAuth == null) {
            sessionTypeAuth = configurator.get("SESSION_TYPE_AUTH");
        }
        if (transTypePreAuth == null) {
            transTypePreAuth = configurator.get("TRANS_TYPE_PRE_AUTH");
        }
        if (transTypeFinalAndSale == null) {
            transTypeFinalAndSale = configurator.get("TRANS_TYPE_FINAL_AND_SALE");
        }
        if (transTypeRefund == null) {
            transTypeRefund = configurator.get("TRANS_TYPE_REFUND");
        }
        if (cardTypeWex == null) {
            cardTypeWex = configurator.get("CARD_TYPE_WEX");
        }
        if (serviceType == null) {
            serviceType = configurator.get("SERVICE_TYPE");
        }
        transaction = t;
        try {
            SCHEMA_PATH = "src/main/resources/xml/NBSLogonPackager.xml";
            try {
                packager = new GenericPackager(SCHEMA_PATH);
            } catch (Exception e) {
                SCHEMA_PATH = System.getProperty("jboss.server.config.dir") + "/NBSLogonPackager.xml";
                packager = new GenericPackager(SCHEMA_PATH);
            }
            isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0200");
            if(!isTimeoutRetry){
                isoMsg.set(2, transaction.getTermId());
                isoMsg.set(3, applicationName);
                isoMsg.set(4, applicationVersion);
                isoMsg.set(5, createDateFormat());
            }
            isoMsg.set(6, sessionTypeAuth);
            isoMsg.set(7, transaction.getTransactionId().substring(0, 4));
            if (transaction.getRequestType().equalsIgnoreCase(RequestType.PREAUTH)) {
                isoMsg.set(8, transTypePreAuth);
            } else if (transaction.getRequestType().equalsIgnoreCase(RequestType.FINAL_AUTH)
                    || transaction.getRequestType().equalsIgnoreCase(RequestType.SALE)) {
                isoMsg.set(8, transTypeFinalAndSale);
            } else if (transaction.getRequestType().equalsIgnoreCase(RequestType.REFUND)) {
                isoMsg.set(8, transTypeRefund);
            }
            isoMsg.set(9, cardTypeWex);
            isoMsg.set(10, transaction.getCatFlag());
            isoMsg.set(11, transaction.getPumpNmbr());
            isoMsg.set(12, serviceType);
            
            if (transaction.getRequestType().equals(RequestType.FINAL_AUTH)) {
                isoMsg.set(6, captureOnlyRequest);
                isoMsg.set(13, Long.toString(transaction.getAmount()));
                isoMsg.set(14, Long.toString(transaction.getAmtPreAuthorized()));
                if (null != transaction.getTransactionId()) {
                    isoMsg.set(15, transaction.getTransactionId().substring(0, 4));
                }
                isoMsg.set(16, createDateAndTime());
                isoMsg.set(21, transaction.getAuthNumber());
            }
            isoMsg.set(17, "2");
            if (transaction.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
                isoMsg.set(18, transaction.getTrack2());
            } else if (transaction.getInputType().equalsIgnoreCase(InputType.KEYED))//isoMsg.set(23, transaction.getTrack2());//Track0 formatt
            {
                if (!(transaction.getRequestType().equals(RequestType.FINAL_AUTH))) {
                    isoMsg.set(19, Long.toString(t.getAmount()));
                }
            }
            if ("10".equalsIgnoreCase(transTypeFinalAndSale) || "30".equalsIgnoreCase(transTypeRefund)) {
                isoMsg.set(20, (t.getTransactionId() + t.getTermId()));
            }
            
            isoMsg.set(22, t.getPromptDetailCount().toString());

            //prompt details count
            if (null != t.getPromptDetailCount()) {
                if (null != t.getVehicleId()) {
                    isoMsg.set(23, "1");
                    isoMsg.set(24, t.getVehicleId());
                }
                if (null != t.getDriverId()) {
                    isoMsg.set(23, "3");
                    isoMsg.set(24, t.getDriverId());
                }
                if (null != t.getOdoMeter()) {
                    isoMsg.set(23, "4");
                    isoMsg.set(24, t.getOdoMeter());
                }
            }
            
            isoMsg.set(25, t.getProdDetailCount());
            
            index = 25;
            if (null != t.getProducts() && (t.getProducts().size()) > 0) {
                for (String nonFuelString : t.getProducts()) {
                    if (nonFuelString.contains(":")) {
                        productDetails = nonFuelString.split(":");
                    }
                    isoMsg.set(++index, productDetails[2]);
                    isoMsg.set(++index, productDetails[1]);
                    isoMsg.set(++index, productDetails[0]);
                    isoMsg.set(++index, productDetails[3]);
                }
            }

//            isoMsg.set(15, nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexPromptDetails().getPromptDetailCount().toString());
//            for (promptCountIndex=16 ; promptCountIndex  <  (nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexPromptDetails().getPromptDetailCount().getPromptTypeOrPromptValue().size()); promptCountIndex++) {
//                 isoMsg.set(promptCountIndex, (nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexPromptDetails().getPromptDetailCount().getPromptTypeOrPromptValue().get(promptCountIndex)).toString());
//            }
//            isoMsg.set(promptCountIndex+1, nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexProductDetails().getProdDetailCount().toString());
//            for (promptCountIndex=promptCountIndex+2 ; promptCountIndex  <  (nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexProductDetails().getProdDetailCount().getPriceOrQuantityOrProductCode().size()); promptCountIndex++) {
//                 isoMsg.set(promptCountIndex, (nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexProductDetails().getProdDetailCount().getPriceOrQuantityOrProductCode().get(promptCountIndex)).toString());
//            }
            byte[] data = isoMsg.pack();
            iso8583Format = new String(data);
            LOG.info("output for NBS Iso 8583 format= " + iso8583Format);
            return data;
        } catch (ISOException ex) {
            Logger.getLogger(NBSRequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String createDateAndTime() {
        //        YYMMDDhhmmss
        //2017-08-03 09:31:54.316
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date();
        String dt = dateFormat.format(date);
        dt = dt.substring(2, 4) + dt.substring(5, 7) + dt.substring(8, 10) + dt.substring(11, 13) + dt.substring(14, 16) + dt.substring(17, 19);
        
        return dt;
    }
    
    public String[] seperateResponse(byte[] response) {
        String responseString = new String(response);
        String[] result = {"", ""};
        String mTI = responseString.substring(0, 4);
        result[0] = responseString.substring(0, responseString.substring(4).indexOf(mTI) + 4);
        result[1] = responseString.substring(result[0].length());
        return result;
    }
    
    public Transaction unmarshalAcknowledgment(String response) {
        try {
            isoMsg = new ISOMsg();
            SCHEMA_PATH = "src/main/resources/xml/ResponseAcknowledgment.xml";
            try {
                packager = new GenericPackager(SCHEMA_PATH);
            } catch (Exception e) {
                SCHEMA_PATH = System.getProperty("jboss.server.config.dir") + "/ResponseAcknowledgment.xml";
                packager = new GenericPackager(SCHEMA_PATH);
            }
            isoMsg.setPackager(packager);
            isoMsg.unpack(response.getBytes());
            if (isoMsg.getString(2).trim().equalsIgnoreCase("c$")) {
                transaction.setResponseType(ResponseType.ACCEPTED);
            } else if (isoMsg.getString(2).trim().equalsIgnoreCase("c?")) {
                transaction.setResponseType(ResponseType.CANCELED);
            }else if (isoMsg.getString(2).trim().equalsIgnoreCase("c!")) {
                transaction.setResponseType(ResponseType.REJECTED);
            }
            transaction.setReasonCode(isoMsg.getString(3));
            return transaction;
        } catch (ISOException ex) {
            Logger.getLogger(NBSRequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(NBSRequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return transaction;
    }
    
    public Transaction unmarshalNbsResponse(String response) {
        try {
            isoMsg = new ISOMsg();
            SCHEMA_PATH = "src/main/resources/xml/NBSResponse.xml";
            try {
                packager = new GenericPackager(SCHEMA_PATH);
            } catch (Exception e) {
                SCHEMA_PATH = System.getProperty("jboss.server.config.dir") + "/NBSResponse.xml";
                packager = new GenericPackager(SCHEMA_PATH);
            }
            isoMsg.setPackager(packager);
            isoMsg.unpack(response.getBytes());
            transaction.setReasonCode(isoMsg.getString(6));
            transaction.setResponseType(isoMsg.getString(7));
            transaction.setMedia(isoMsg.getString(8));
            transaction.setLocalDateTime(SvsUtil.formatLocalDateTime());
            //9-15 are not to use
            transaction.setAuthNumber(isoMsg.getString(16));
            transaction.setAmount(Long.parseLong(isoMsg.getString(17)));
            transaction.setProdDetailCount(isoMsg.getString(18));
            transaction.setQuantity(new BigDecimal(isoMsg.getString(18)));
            transaction.setProductCode(isoMsg.getString(20));
            //transaction.set//productDeatailsMaxAmount/21 no field matched
            return transaction;
        } catch (ISOException ex) {
            Logger.getLogger(NBSRequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return transaction;
    }
    
    public byte[] logOffRequest() {
        byte[] result = null;
        try {
            isoMsg = new ISOMsg();
            SCHEMA_PATH = "src/main/resources/xml/NBSLogOff.xml";
            try {
                packager = new GenericPackager(SCHEMA_PATH);
            } catch (Exception e) {
                SCHEMA_PATH = System.getProperty("jboss.server.config.dir") + "/NBSLogOff.xml";
                packager = new GenericPackager(SCHEMA_PATH);
            }
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0200");
            isoMsg.set(2, "O");
            byte[] data = isoMsg.pack();
            return data;
        } catch (ISOException ex) {
            Logger.getLogger(NBSRequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    private String createDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date();
        String ts = dateFormat.format(date);
        //2017-08-08 08:39:30.967
        ts = ts.substring(11, 13) + ts.substring(14, 16) + daylightSavingsTimeAtSiteOne;
        return ts;
    }

//    private String CreateDF_forTransaction(String df)
//    {
//        //yyyy-MM-dd HH:mm:ss.SSS
//        //2017-08-03 09:31:54.316
//        
//       // WYYMMDDhhmmss
//       // 3170621071655
//      df = "20"+df.substring(1, 3)+"-"+df.substring(3, 5)+"-"
//              +df.substring(5, 7)+" "+df.substring(7, 9)+":"+df.substring(9, 11)+":"+df.substring(11, 13)+".000";
//        return df;
//    }
    public NBSResponse getnBSResponse() {
        return nBSResponse;
    }
    
    public void setnBSResponse(NBSResponse nBSResponse) {
        this.nBSResponse = nBSResponse;
    }
    
    public String getIso8583Format() {
        return iso8583Format;
    }
    
    public void setIso8583Format(String iso8583Format) {
        this.iso8583Format = iso8583Format;
    }
    
    public int getPromptCountIndex() {
        return promptCountIndex;
    }
    
    public void setPromptCountIndex(int promptCountIndex) {
        this.promptCountIndex = promptCountIndex;
    }
    
    public ISOMsg getIsoMsg() {
        return isoMsg;
    }
    
    public void setIsoMsg(ISOMsg isoMsg) {
        this.isoMsg = isoMsg;
    }
    
    public GenericPackager getPackager() {
        return packager;
    }
    
    public void setPackager(GenericPackager packager) {
        this.packager = packager;
    }
    
    public ResponseAcknowlegment getResponseAcknowlegment() {
        return responseAcknowlegment;
    }
    
    public void setResponseAcknowlegment(ResponseAcknowlegment responseAcknowlegment) {
        this.responseAcknowlegment = responseAcknowlegment;
    }

    /**
     * @return the configurator
     */
    public Configurator getConfigurator() {
        return configurator;
    }

    /**
     * @param configurator the configurator to set
     */
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }
    
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
    
    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }
    
    public void setDaylightSavingsTimeAtSiteOne(String daylightSavingsTimeAtSiteOne) {
        this.daylightSavingsTimeAtSiteOne = daylightSavingsTimeAtSiteOne;
    }
    
    public void setCaptureOnlyRequest(String captureOnlyRequest) {
        this.captureOnlyRequest = captureOnlyRequest;
    }
    
    public void setSessionTypeAuth(String sessionTypeAuth) {
        this.sessionTypeAuth = sessionTypeAuth;
    }
    
    public void setTransTypePreAuth(String transTypePreAuth) {
        this.transTypePreAuth = transTypePreAuth;
    }
    
    public void setTransTypeFinalAndSale(String transTypeFinalAndSale) {
        this.transTypeFinalAndSale = transTypeFinalAndSale;
    }
    
    public void setTransTypeRefund(String transTypeRefund) {
        this.transTypeRefund = transTypeRefund;
    }
    
    public void setCardTypeWex(String cardTypeWex) {
        this.cardTypeWex = cardTypeWex;
    }
    
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
    
    public void setSCHEMA_PATH(String SCHEMA_PATH) {
        this.SCHEMA_PATH = SCHEMA_PATH;
    }
}
