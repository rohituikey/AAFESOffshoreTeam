/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.nbsresponse.NBSResponse;
import com.aafes.nbsresponseacknowledgmentschema.ResponseAcknowlegment;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.util.InputType;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.jpos.iso.ISOException;
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
    private GenericPackager packager;
    private ResponseAcknowlegment responseAcknowlegment;
    private NBSResponse nBSResponse;
    Transaction transaction = new Transaction();
    @Inject
    private String APPLICATION_NAME;
    @Inject
    private String APPLICATION_VERSION;
    @Inject
    private String DAYLIGHT_SAVINGS_TIME_AT_SITE_ONE;
    @Inject
    private String CAPTURE_ONLY_REQUEST;
    @Inject
    private String TRANS_TYPE_PRE_AUTH;
    @Inject
    private String TRANS_TYPE_FINAL_AND_SALE;
    @Inject
    private String TRANS_TYPE_REFUND;
    @Inject
    private String CARD_TYPE_WEX;
    @Inject
    private String SERVICE_TYPE;
    @Inject
    private String SESSION_TYPE_AUTH;
    private String SCHEMA_PATH = "";

    public String generateLogOnPacketRequest(Transaction t) {
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
            isoMsg.setMTI("0231");
            isoMsg.set(10, transaction.getTermId());
            isoMsg.set(12, APPLICATION_NAME);
            isoMsg.set(13, APPLICATION_VERSION);
            isoMsg.set(14, createDateFormat());
            isoMsg.set(15, SESSION_TYPE_AUTH);
            isoMsg.set(16, transaction.getTransactionId().substring(0, 4));
            if (transaction.getRequestType().equalsIgnoreCase(RequestType.PREAUTH)) isoMsg.set(17, TRANS_TYPE_PRE_AUTH);
            else if (transaction.getRequestType().equalsIgnoreCase(RequestType.FINAL_AUTH)
                    || transaction.getRequestType().equalsIgnoreCase(RequestType.SALE)) {
                isoMsg.set(17, TRANS_TYPE_FINAL_AND_SALE);
            }
            else if (transaction.getRequestType().equalsIgnoreCase(RequestType.REFUND)) isoMsg.set(17, TRANS_TYPE_REFUND);
            isoMsg.set(18, CARD_TYPE_WEX);
            isoMsg.set(19, transaction.getCatFlag());
            isoMsg.set(110, transaction.getPumpNmbr());
            isoMsg.set(111, SERVICE_TYPE);

            if (transaction.getRequestType().equals(RequestType.FINAL_AUTH)) {
                isoMsg.set(15, CAPTURE_ONLY_REQUEST);
                isoMsg.set(112, Long.toString(transaction.getAmount()));
                isoMsg.set(113, Long.toString(transaction.getAmtPreAuthorized()));
                isoMsg.set(114, transaction.getTransactionId());
                isoMsg.set(115, createDateAndTime(transaction.getLocalDateTime()));
                isoMsg.set(120, transaction.getAuthNumber());
            }
            isoMsg.set(116, "2");
            if (transaction.getInputType().equalsIgnoreCase(InputType.SWIPED)) isoMsg.set(117, transaction.getTrack2());
            else if (transaction.getInputType().equalsIgnoreCase(InputType.KEYED))//isoMsg.set(113, transaction.getTrack2());//Track0 formatt
            if (!(transaction.getRequestType().equals(RequestType.FINAL_AUTH))) isoMsg.set(118, Long.toString(t.getAmount()));
            if (TRANS_TYPE_FINAL_AND_SALE.equals(10) || TRANS_TYPE_REFUND.equals(30)) isoMsg.set(119, (t.getTransactionId() + t.getTermId()));

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
            return iso8583Format;
        } catch (ISOException ex) {
            Logger.getLogger(NBSRequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return iso8583Format;
    }

    public String createDateAndTime(String dt) {
    //        YYMMDDhhmmss
    //2017-08-03 09:31:54.316
        dt = dt.substring(2, 4) + dt.substring(5, 7) + dt.substring(8, 10) + dt.substring(11, 13) + dt.substring(14, 16) + dt.substring(17, 19  );

        return dt;
    }

    public String[] seperateResponse(String response) {
        String[] result = {"", ""};
        String mTI = response.substring(0, 4);
        result[0] = response.substring(0, response.substring(4).indexOf(mTI) + 4);
        result[1] = response.substring(result[0].length());
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
            if (isoMsg.getString(10).trim().equalsIgnoreCase("c$")) {
                transaction.setResponseType(ResponseType.APPROVED);
            } else if (isoMsg.getString(10).trim().equalsIgnoreCase("c?")) {
                transaction.setResponseType(ResponseType.DECLINED);
            }
            transaction.setReasonCode(isoMsg.getString(11));
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
//            NBSResponse.AuthResponse authResponse = new NBSResponse.AuthResponse();
//            NBSResponse.AuthResponse.PromptTypeDetails promptType = new NBSResponse.AuthResponse.PromptTypeDetails();
//            NBSResponse.AuthResponse.ProductDetails productDetails = new NBSResponse.AuthResponse.ProductDetails();
            SCHEMA_PATH = "src/main/resources/xml/NBSResponse.xml";
            try {
                packager = new GenericPackager(SCHEMA_PATH);
            } catch (Exception e) {
                SCHEMA_PATH = System.getProperty("jboss.server.config.dir") + "/NBSResponse.xml";
                 packager = new GenericPackager(SCHEMA_PATH);
            }
            isoMsg.setPackager(packager);
            isoMsg.unpack(response.getBytes());

            //promptType.setPromptType(isoMsg.getString(23));
            transaction.setAuthNumber(isoMsg.getString(24));
            //promptType.setMaxAmount(new BigDecimal(isoMsg.getString(25)));
            //promptType.setProductAuthCount(new BigInteger(isoMsg.getString(26)));

            transaction.setProdDetailCount(isoMsg.getString(27));
            transaction.setProductCode(isoMsg.getString(28));
            //productDetails.setMaxAmount(new BigDecimal(isoMsg.getString(29)));

            transaction.setResponseType(isoMsg.getString(15));
            transaction.setMedia(isoMsg.getString(16));
            transaction.setLocalDateTime(createDateFormat());
//            authResponse.setIdentity(isoMsg.getString(17));
  //          authResponse.setHostNumber(isoMsg.getString(18));
//            authResponse.setCardNumber(isoMsg.getString(19));
//            authResponse.setVehicleNumber(new BigInteger(isoMsg.getString(20)));
//            authResponse.setServiceOption(new BigInteger(isoMsg.getString(21)));
//            authResponse.setPromptCount(new BigInteger(isoMsg.getString(22)));
//            authResponse.setProductDetails(productDetails);
//            authResponse.setPromptTypeDetails(promptType);
//
//            nBSResponse.setA(isoMsg.getString(10));
//            nBSResponse.setKey(new BigInteger(isoMsg.getString(11)));
//            nBSResponse.setApplicationUpdateNeeded(isoMsg.getString(12));
//            nBSResponse.setAuthCode(new BigInteger(isoMsg.getString(13)));
//            nBSResponse.setA(isoMsg.getString(14));
//            nBSResponse.setAuthResponse(authResponse);

            return transaction;
        } catch (ISOException ex) {
            Logger.getLogger(NBSRequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return transaction;
    }

    public String logOffRequest() {
        String result = "";
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
            isoMsg.setMTI("0231");
            isoMsg.set(10, "O");
            byte[] data = isoMsg.pack();
            result = new String(data);
            return result;
        } catch (ISOException ex) {
            Logger.getLogger(NBSRequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    private String createDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date();
        String ts = dateFormat.format(date);
        //2017-08-03 09:31:54.316
        ts = ts.substring(11, 13) + ts.substring(14, 16) + DAYLIGHT_SAVINGS_TIME_AT_SITE_ONE;
        return ts;
    }
    
    private String CreateDF_forTransaction(String df)
    {
        //yyyy-MM-dd HH:mm:ss.SSS
        //2017-08-03 09:31:54.316
        
       // WYYMMDDhhmmss
       // 3170621071655
      df = "20"+df.substring(1, 3)+"-"+df.substring(3, 5)+"-"
              +df.substring(5, 7)+" "+df.substring(7, 9)+":"+df.substring(9, 11)+":"+df.substring(11, 13)+".000";
        return df;
    }

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
}