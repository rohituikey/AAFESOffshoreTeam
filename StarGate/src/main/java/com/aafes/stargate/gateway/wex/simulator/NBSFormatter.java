/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex.simulator;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.gateway.vision.Common;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.apache.commons.lang3.ArrayUtils;

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

    Transaction transaction = new Transaction();
    @EJB
    private Configurator configurator;

    public byte[] createRequest(Transaction t) {
        try {
            MessageFactory mfact = ConfigParser.createFromClasspathConfig("NBSconfig.xml");
            mfact.setTraceNumberGenerator(new SimpleTraceGenerator((int) (System.currentTimeMillis() % 10000)));
            mfact.setAssignDate(true);

            IsoMessage isoMsg = mfact.newMessage(0x100);
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

            isoMsg.setValue(10, transaction.getTermId(), IsoType.ALPHA, 15);
            isoMsg.setValue(12, applicationName, IsoType.ALPHA, 7);
            isoMsg.setValue(13, applicationVersion, IsoType.NUMERIC, 4);
            isoMsg.setValue(14, createDateFormat(), IsoType.ALPHA, 5);
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
                isoMsg.setValue(114, transaction.getTransactionId(), IsoType.NUMERIC, 4);
                isoMsg.setValue(115, createDateAndTime(transaction.getLocalDateTime()), IsoType.DATE10, 12);
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
            isoMsg.setCharacterEncoding(
                    "UTF-8");
            isoMsg.setBinaryBitmap(
                    true);
            byte[] data = isoMsg.writeData();

            return data;

        } catch (IOException ex) {
            Logger.getLogger(NBSFormatter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Transaction createResponse(byte[] buf, Transaction transaction) throws IOException, Exception {
        MessageFactory mfact = ConfigParser.createFromClasspathConfig(
                "config.xml");
        String bitmapByte = javax.xml.bind.DatatypeConverter.printHexBinary(
                Arrays.copyOfRange(buf, 200, 208));
        byte[] mtid = Arrays.copyOfRange(buf, 196, 200);
        byte[] details = Arrays.copyOfRange(buf, 208, buf.length - 1);
        String rspString = new String(bitmapByte);
        byte[] response = ArrayUtils.addAll(mtid, bitmapByte.getBytes());
        response = ArrayUtils.addAll(response, details);
//        logResponse(response);
        IsoMessage resp = null;
        try {
            resp = mfact.parseMessage(response, 0);
        } catch (Exception ex) {
            throw new Exception("Unable to Parse response: " + Common.
                    convertStackTraceToString(ex));
        }
        //promptType.setPromptType(resp.getString(23));
        try {
            transaction.setAuthNumber(resp.getField(24).toString());
        } catch (NullPointerException ex) {
            //throw new GatewayException("Unknown AuthCode.");
            transaction.setAuthNumber("");
        }
        //promptType.setMaxAmount(new BigDecimal(resp.getString(25)));
        //promptType.setProductAuthCount(new BigInteger(resp.getField(26)));
        try {
            transaction.setProdDetailCount(resp.getField(26).toString());
        } catch (NullPointerException ex) {
            transaction.setProdDetailCount("");
        }
        try {
            transaction.setQuantity(new BigDecimal(resp.getField(27).toString()));
        } catch (NullPointerException ex) {
            transaction.setQuantity(new BigDecimal(""));
        }

        try {
            transaction.setProductCode(resp.getField(28).toString());
        } catch (NullPointerException ex) {
            transaction.setProductCode("");
        }
        //productDetails.setMaxAmount(new BigDecimal(resp.getField(29)));
        try {
            transaction.setResponseType(resp.getField(15).toString());
        } catch (NullPointerException ex) {
            transaction.setResponseType("");
        }
        try {
            if (resp.getField(16).toString().equalsIgnoreCase(cardTypeWex)) {
                transaction.setMedia(MediaType.WEX);
            }
        } catch (NullPointerException ex) {
            transaction.setMedia("");
        }
        try {
            transaction.setLocalDateTime(this.CreateDF_forTransaction(resp.getField(12).toString()));
        } catch (NullPointerException ex) {
            transaction.setLocalDateTime("");
        }
        try {
            transaction.setFuelDollerAmount(new BigDecimal(resp.getField(25).toString()));
        } catch (NullPointerException ex) {
            transaction.setFuelDollerAmount(new BigDecimal(""));
        }
        try {
            transaction.setReasonCode(resp.getField(14).toString());
        } catch (NullPointerException ex) {
            transaction.setReasonCode("");
        }
              //   application config ,session (auth or captureOnly),max amount need to be mapped
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
    public String createDateAndTime(String dt) {
        //        YYMMDDhhmmss
        //2017-08-03 09:31:54.316
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
}
