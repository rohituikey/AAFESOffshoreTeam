/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex.simulator;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.util.InputType;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.RequestType;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.impl.SimpleTraceGenerator;
import com.solab.iso8583.parse.ConfigParser;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;

/**
 *
 * @author uikuyr
 */
public class NBSFormatter {

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
    String[] productDetails;

    Transaction transaction = new Transaction();
    @EJB
    private Configurator configurator;

    public IsoMessage createRequest(Transaction t, int retryCount) {
        try {
            MessageFactory mfact = ConfigParser.createFromClasspathConfig("NBSconfig.xml");
            mfact.setTraceNumberGenerator(new SimpleTraceGenerator((int) (System.currentTimeMillis() % 10000)));
            mfact.setAssignDate(true);

            IsoMessage isoMsg = mfact.newMessage(0x200);
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

            transaction = t;
            if(retryCount == 0){
                isoMsg.setValue(10, transaction.getTermId(), IsoType.ALPHA, 15);
                isoMsg.setValue(12, applicationName, IsoType.ALPHA, 7);
                isoMsg.setValue(13, applicationVersion, IsoType.NUMERIC, 4);
                isoMsg.setValue(14, createDateFormat(), IsoType.ALPHA, 5);
            }else{
                isoMsg.setValue(10, "", IsoType.ALPHA, 15);
                isoMsg.setValue(12, "", IsoType.ALPHA, 7);
                isoMsg.setValue(13, "", IsoType.NUMERIC, 4);
                isoMsg.setValue(14, "", IsoType.ALPHA, 5);
            }
            isoMsg.setValue(15, sessionTypeAuth, IsoType.ALPHA, 15);
            isoMsg.setValue(16, transaction.getTransactionId().substring(0, 4), IsoType.ALPHA, 4);
            if (transaction.getRequestType().equalsIgnoreCase(RequestType.PREAUTH)) {
                isoMsg.setValue(17, transTypePreAuth, IsoType.ALPHA, 2);
            } else if (transaction.getRequestType().equalsIgnoreCase(RequestType.FINAL_AUTH)
                    || transaction.getRequestType().equalsIgnoreCase(RequestType.SALE)) {
                isoMsg.setValue(17, transTypeFinalAndSale, IsoType.ALPHA, 2);
            } else if (transaction.getRequestType().equalsIgnoreCase(RequestType.REFUND)) {
                isoMsg.setValue(17, transTypeRefund, IsoType.ALPHA, 2);
            }
            isoMsg.setValue(18, cardTypeWex, IsoType.ALPHA, 2);
            isoMsg.setValue(19, transaction.getCatFlag(), IsoType.ALPHA, 1);
            isoMsg.setValue(110, transaction.getPumpNmbr(), IsoType.NUMERIC, 2);
            isoMsg.setValue(111, serviceType, IsoType.ALPHA, 1);

            if (transaction.getRequestType().equals(RequestType.FINAL_AUTH)) {
                isoMsg.setValue(15, captureOnlyRequest, IsoType.ALPHA, 15);
                isoMsg.setValue(112, Long.toString(transaction.getAmount()), IsoType.AMOUNT, 9);
                isoMsg.setValue(113, Long.toString(transaction.getAmtPreAuthorized()), IsoType.AMOUNT, 9);
                isoMsg.setValue(114, transaction.getTransactionId().substring(0, 4), IsoType.NUMERIC, 4);
                isoMsg.setValue(115, createDateAndTime(), IsoType.DATE10, 12);
                isoMsg.setValue(120, transaction.getAuthNumber(), IsoType.ALPHA, 6);
            }
            isoMsg.setValue(116, "2", IsoType.ALPHA, 25);
            if (transaction.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
                isoMsg.setValue(117, transaction.getTrack2(), IsoType.LLVAR, 79);
            } else if (transaction.getInputType().equalsIgnoreCase(InputType.KEYED))//isoMsg.set(113, transaction.getTrack2());//Track0 formatt
            {
                if (!(transaction.getRequestType().equals(RequestType.FINAL_AUTH))) {
                    isoMsg.setValue(118, Long.toString(t.getAmount()), IsoType.ALPHA, 9);
                }
            }
            if ("10".equalsIgnoreCase(transTypeFinalAndSale) || "30".equalsIgnoreCase(transTypeRefund)) {
                isoMsg.setValue(119, (t.getTransactionId() + t.getTermId()), IsoType.ALPHA, 12);
            }

//            isoMsg.set(15, nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexPromptDetails().getPromptDetailCount().toString());
//            for (promptCountIndex=16 ; promptCountIndex  <  (nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexPromptDetails().getPromptDetailCount().getPromptTypeOrPromptValue().size()); promptCountIndex++) {
//                 isoMsg.set(promptCountIndex, (nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexPromptDetails().getPromptDetailCount().getPromptTypeOrPromptValue().get(promptCountIndex)).toString());
//            }
//            isoMsg.set(promptCountIndex+1, nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexProductDetails().getProdDetailCount().toString());
//            for (promptCountIndex=promptCountIndex+2 ; promptCountIndex  <  (nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexProductDetails().getProdDetailCount().getPriceOrQuantityOrProductCode().size()); promptCountIndex++) {
//                 isoMsg.set(promptCountIndex, (nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexProductDetails().getProdDetailCount().getPriceOrQuantityOrProductCode().get(promptCountIndex)).toString());
//            }
            isoMsg.setValue(121, t.getPromptDetailCount(), IsoType.NUMERIC, 2);

            //prompt details count
            if (null != t.getPromptDetailCount()) {
                if (null != t.getVehicleId()) {
                    isoMsg.setValue(122, 1, IsoType.ALPHA, 1);
                    isoMsg.setValue(123, t.getVehicleId(), IsoType.ALPHA, 10);
                }
                if (null != t.getDriverId()) {
                    isoMsg.setValue(122, 3, IsoType.ALPHA, 1);
                    isoMsg.setValue(123, t.getDriverId(), IsoType.ALPHA, 10);
                }
                if (null != t.getOdoMeter()) {
                    isoMsg.setValue(122, 4, IsoType.ALPHA, 1);
                    isoMsg.setValue(123, t.getOdoMeter(), IsoType.ALPHA, 10);
                }
            }

            isoMsg.setValue(124, t.getProdDetailCount(), IsoType.NUMERIC, 2);

            // for (int indexNumber = 0; indexNumber < Integer.valueOf(t.getProdDetailCount()); indexNumber++) {
//            if (null!=t.getFuelProductGroup() && t.getFuelProductGroup().size()> 0) {
//                for(TransactionFuelProdGroup fuelProdGroup : t.getFuelProductGroup()){
//                isoMsg.setValue(125+index, fuelProdGroup.getFuelPricePerUnit(), IsoType.AMOUNT, 9);
//                isoMsg.setValue(126+index, fuelProdGroup.getFuelQuantity(), IsoType.AMOUNT, 10);
//                isoMsg.setValue(127+index, fuelProdGroup.getFuelProductCode(), IsoType.NUMERIC, 3);
//                isoMsg.setValue(128+index, fuelProdGroup.getFuelDollarAmount(), IsoType.AMOUNT, 7);
//                index = index+1;
//                }
//            }
//            
//            index=index+125;
//            if (null!=t.getNonFuelProductGroup() && (t.getNonFuelProductGroup().size()) > 0) {
//                
//                for(TransactionNonFuelProductGroup nonFuelProdGroup : t.getNonFuelProductGroup()){
//                isoMsg.setValue(index, nonFuelProdGroup.getNonFuelPricePerUnit(), IsoType.AMOUNT, 9);
//                isoMsg.setValue(index+1, nonFuelProdGroup.getNonFuelQuantity(), IsoType.AMOUNT, 10);
//                isoMsg.setValue(index+2, nonFuelProdGroup.getNonFuelProductCode(), IsoType.NUMERIC, 3);
//                isoMsg.setValue(index+3, nonFuelProdGroup.getNonFuelAmount(), IsoType.AMOUNT, 7);
//                index = index+1;
//                }
//            }
            //}
            index = 124;
            if (null != t.getProducts() && (t.getProducts().size()) > 0) {
                for (String nonFuelString : t.getProducts()) {
                    if(nonFuelString.contains(":")){
                      productDetails = nonFuelString.split(":");
                    }
                    isoMsg.setValue(++index,productDetails[2], IsoType.AMOUNT, 9);
                    isoMsg.setValue(++index , productDetails[1], IsoType.AMOUNT, 10);
                    isoMsg.setValue(++index ,productDetails[0], IsoType.NUMERIC, 3);
                    isoMsg.setValue(++index ,productDetails[3], IsoType.AMOUNT, 7);
                    if (index == 128) index = 19;
                }
            }

            isoMsg.setCharacterEncoding(
                    "UTF-8");
            isoMsg.setBinaryBitmap(
                    true);

            return isoMsg;

        } catch (IOException ex) {
            Logger.getLogger(NBSFormatter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Transaction createResponse(String buf) throws IOException, Exception {
        try {
            MessageFactory mfact = ConfigParser.createFromClasspathConfig("NBSResponceConfig.xml");
//            String bitmapByte = javax.xml.bind.DatatypeConverter.printHexBinary(Arrays.copyOfRange(buf, 4, 5));
//            byte[] mtid = Arrays.copyOfRange(buf, 0, 4);
//            byte[] details = Arrays.copyOfRange(buf, 5, buf.length - 1);
//            String rspString = bitmapByte;
//            byte[] response = ArrayUtils.addAll(mtid, bitmapByte.getBytes());
//            response = ArrayUtils.addAll(response, details);

            IsoMessage resp = null;
            System.out.println(buf);
            if (buf.startsWith("0100")) {
                try {
                    transaction.setReasonCode("100");
                } catch (NullPointerException ex) {
                    transaction.setReasonCode("");
                }
            } else if (buf.startsWith("0200")) {
                try {
                    transaction.setReasonCode("200");
                } catch (NullPointerException ex) {
                    transaction.setReasonCode("");
                }
            }

            if (buf.contains("Approved")) {
                try {
                    transaction.setResponseType("Approved");
                } catch (NullPointerException ex) {
                    transaction.setResponseType("");
                }
            } else if (buf.contains("Declined")) {
                try {
                    transaction.setResponseType("Declined");
                } catch (NullPointerException ex) {
                    transaction.setResponseType("");
                }
            }

            if (transaction.getMedia() == null || transaction.getMedia().isEmpty()) {
                try {
                    if (resp.getField(16).toString().equalsIgnoreCase(cardTypeWex)) {
                        transaction.setMedia(MediaType.WEX);
                    }
                } catch (NullPointerException ex) {

                    transaction.setMedia("");
                }
            }

//            try {
//                resp = mfact.parseMessage(buf.getBytes(), 0);
//            } catch (Exception ex) {
//                throw new Exception("Unable to Parse response: " + Common.
//                        convertStackTraceToString(ex));
//            }
            //if (transaction.getLocalDateTime() == null || transaction.getLocalDateTime().isEmpty()) {
                try {
                    transaction.setLocalDateTime(this.CreateDF_forTransaction(resp.getField(12).toString()));
                } catch (NullPointerException ex) {
                    transaction.setLocalDateTime("");
                }
            //}

            if (transaction.getAuthNumber() == null || transaction.getAuthNumber().isEmpty()) {
                try {
                    transaction.setAuthNumber(resp.getField(24).toString());
                } catch (NullPointerException ex) {
                    //throw new GatewayException("Unknown AuthCode.");
                    transaction.setAuthNumber("");
                }
            }

            if (transaction.getFuelDollerAmount() == null) {
                try {
                    transaction.setFuelDollerAmount(new BigDecimal(resp.getField(25).toString()));
                } catch (NullPointerException ex) {
                    transaction.setFuelDollerAmount(new BigDecimal(BigInteger.ONE));
                }
            }
            if (transaction.getProdDetailCount() == null) {
                try {
                    transaction.setProdDetailCount(resp.getField(26).toString());
                } catch (NullPointerException ex) {
                    transaction.setProdDetailCount("");
                }
            }
            if (transaction.getQuantity() == null) {
                try {
                    transaction.setQuantity(new BigDecimal(resp.getField(27).toString()));
                } catch (NullPointerException ex) {
                    transaction.setQuantity(new BigDecimal(BigInteger.ONE));
                }
            }
            if (transaction.getProductCode() == null) {
                try {
                    transaction.setProductCode(resp.getField(28).toString());
                } catch (NullPointerException ex) {
                    transaction.setProductCode("");
                }
            }
            //productDetails.setMaxAmount(new BigDecimal(resp.getField(29)));

            //   application config ,session (auth or captureOnly),max amount need to be mapped
//        } catch (ISOException ex) {
//            Logger.getLogger(NBSFormatter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(NBSFormatter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return transaction;
    }

    private String CreateDF_forTransaction(String df) {
        //yyyy-MM-dd HH:mm:ss.SSS
        //2017-08-03 09:31:54.316

        // WYYMMDDhhmmss
        // 3170621071655
        df = "20" + df.substring(1, 3) + "-" + df.substring(3, 5) + "-"
                + df.substring(5, 7) + " " + df.substring(7, 9) + ":" + df.substring(9, 11) + ":" + df.substring(11, 13) + ".000";
        return df;
    }

//private static void logResponse(byte[] response) {
//        if (response.length < 1) {
//            return;
//        }
//        String logResponse;
//        int offset = response.length - 1;
//        while (response[offset] == '@') {
//            offset--;
//        }
//        int length = offset + 1;
//        try {
//            logResponse = new String(response, 0, length, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            logResponse = new String(response, 0, length);
//        }
//    }
    public String createDateAndTime() {
        //        YYMMDDhhmmss
        //2017-08-03 09:31:54.316
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date();
        String dt = dateFormat.format(date);
        dt = dt.substring(2, 4) + dt.substring(5, 7) + dt.substring(8, 10) + dt.substring(11, 13) + dt.substring(14, 16) + dt.substring(17, 19);

        return dt;
    }

    private String createDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date();
        String ts = dateFormat.format(date);
        //2017-08-03 09:31:54.316
        ts = ts.substring(11, 13) + ts.substring(14, 16) + daylightSavingsTimeAtSiteOne;
        return ts;
    }

    //uncomment for testing purpose
//    public String generateNewResponse() {
//        String logonResponse = logonResponse();
//        String nbsResponse = nbsResponse();
//        return logonResponse+nbsResponse;
//    }
//
//    private String logonResponse() {
//        try {
//            MessageFactory mfact = ConfigParser.createFromClasspathConfig("NBSAcknowlegment.xml");
//            mfact.setTraceNumberGenerator(new SimpleTraceGenerator((int) (System.currentTimeMillis() % 10000)));
//            mfact.setAssignDate(true);
//
//            IsoMessage isoMessage = mfact.newMessage(0x100);
//        if (true) {
//            isoMessage.setValue(10, "c$",IsoType.ALPHA,2);
//            isoMessage.setValue(11, "100",IsoType.ALPHA,3);
//        } else {
//            isoMessage.setValue(10, "c?",IsoType.ALPHA,2);
//            isoMessage.setValue(11, "200",IsoType.ALPHA,3);
//        }
//        isoMessage.setCharacterEncoding(
//                    "UTF-8");
//            isoMessage.setBinaryBitmap(
//                    true);
//            byte[] data = isoMessage.writeData();
//
//            return new String(data);
//        } catch (IOException ex) {
//            //Logger.getLogger(NBSStubImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
//    private String nbsResponse() {
//        try {
//            MessageFactory mfact = ConfigParser.createFromClasspathConfig("NBSResponse.xml");
//            mfact.setTraceNumberGenerator(new SimpleTraceGenerator((int) (System.currentTimeMillis() % 10000)));
//            mfact.setAssignDate(true);
//
//            IsoMessage isoMessage = mfact.newMessage(0x100);
//        isoMessage.setValue(10, "A",IsoType.ALPHA,25);
//        isoMessage.setValue(11, "0278",IsoType.NUMERIC,4);
//
//        isoMessage.setValue(12, "3170621071655",IsoType.ALPHA,13);
//        isoMessage.setValue(13, "N",IsoType.ALPHA,1);
//        if (true) {
//            isoMessage.setValue(14, "00",IsoType.ALPHA,2);
//            isoMessage.setValue(15, "Approved",IsoType.ALPHA,32);
//        } else {
//            isoMessage.setValue(14, "01",IsoType.ALPHA,2);
//            isoMessage.setValue(15, "Declined",IsoType.ALPHA,32);
//        }
//        isoMessage.setValue(16, "WEX",IsoType.ALPHA,4);
//        isoMessage.setValue(17, "",IsoType.ALPHA,6);
//        isoMessage.setValue(18, "",IsoType.ALPHA,7);
//        isoMessage.setValue(19, "",IsoType.ALPHA,4);
//        isoMessage.setValue(20, "",IsoType.ALPHA,4);
//        isoMessage.setValue(21, "",IsoType.ALPHA,4);
//        isoMessage.setValue(22, "5",IsoType.ALPHA,2);
//        isoMessage.setValue(23, "0",IsoType.ALPHA,1);
//        isoMessage.setValue(24, "308339",IsoType.ALPHA,6);
//        isoMessage.setValue(25, "75.00",IsoType.AMOUNT,10);
//        isoMessage.setValue(26, "1",IsoType.ALPHA,2);
//        isoMessage.setValue(27, "75.0000",IsoType.ALPHA,10);
//        isoMessage.setValue(28, "001",IsoType.ALPHA,3);
//        isoMessage.setValue(29, "78965",IsoType.ALPHA,7);
//
//        
//        isoMessage.setCharacterEncoding(
//                    "UTF-8");
//            isoMessage.setBinaryBitmap(
//                    true);
//            byte[] data = isoMessage.writeData();
//
//            return new String(data);
//        } catch (IOException ex) {
//         //   Logger.getLogger(NBSStubImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
//    public void unmarshallTest(IsoMessage isoMessage){
//        try {
//            MessageFactory mfact = ConfigParser.createFromClasspathConfig("NBSRequest.xml");
//            IsoMessage resp = null;
//            resp = mfact.parseMessage(isoMessage.writeData(), 0);
//            resp.writeData();
//        } catch (IOException ex) {
//            Logger.getLogger(NBSFormatter.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ParseException ex) {
//            Logger.getLogger(NBSFormatter.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            Logger.getLogger(NBSFormatter.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }
}
