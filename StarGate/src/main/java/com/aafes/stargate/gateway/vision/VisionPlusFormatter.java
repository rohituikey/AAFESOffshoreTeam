///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.aafes.stargate.gateway.vision;
//
//import com.aafes.stargate.gateway.GatewayException;
//import com.aafes.stargate.authorizer.entity.Transaction;
//import com.aafes.stargate.util.DeviceType;
//import com.aafes.stargate.util.InputType;
//import com.aafes.stargate.util.MediaType;
//import com.aafes.stargate.util.RequestType;
//import com.aafes.stargate.util.ResponseType;
//import com.solab.iso8583.IsoMessage;
//import com.solab.iso8583.IsoType;
//import com.solab.iso8583.MessageFactory;
//import com.solab.iso8583.impl.SimpleTraceGenerator;
//import com.solab.iso8583.parse.ConfigParser;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.text.ParseException;
//import java.util.Arrays;
//import javax.ejb.Stateless;
//import org.apache.commons.lang3.ArrayUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// *
// * @author nguyentul
// */
//@Stateless
//public class VisionPlusFormatter {
//
//    private static final Logger LOG
//            = LoggerFactory.getLogger(VisionPlusFormatter.class.getSimpleName());
//
//    public static void parseISO8583(byte[] buf, Transaction t) throws
//            IOException, ParseException, Exception {
//        LOG.debug("From the MQ Bridge (EBCDIC): " + new String(buf,
//                "IBM-1047"));
//        MessageFactory mfact = ConfigParser.createFromClasspathConfig(
//                "config.xml");
//        String bitmapByte = javax.xml.bind.DatatypeConverter.printHexBinary(
//                Arrays.copyOfRange(buf, 200, 208));
//        byte[] mtid = Arrays.copyOfRange(buf, 196, 200);
//        byte[] details = Arrays.copyOfRange(buf, 208, buf.length - 1);
//        String rspString = new String(bitmapByte);
//        byte[] response = ArrayUtils.addAll(mtid, bitmapByte.getBytes());
//        response = ArrayUtils.addAll(response, details);
//        logResponse(response);
//        IsoMessage resp = null;
//        try {
//            resp = mfact.parseMessage(response, 0);
//        } catch (Exception ex) {
//            throw new Exception("Unable to Parse response: " + Common.
//                    convertStackTraceToString(ex));
//        }
//
//        try {
//            t.setSTAN(resp.getField(11).toString());
//        } catch (NullPointerException ex) {
//            //throw new GatewayException("Unknown AuthCode.");
//            t.setSTAN("");
//        }
//
//      
//        
//        try {
//            if (t.getCardSequenceNumber().length() == 0 || t.
//                    getCardSequenceNumber() == null) {
//                t.setCardSequenceNumber(resp.getField(23).toString());
//            }
//        } catch (NullPointerException ex) {
//            //throw new GatewayException("Unknown Card Sequence");
//            t.setCardSequenceNumber("");
//        }
//
//        //TODO : It may change to Auth Number
//        try {
//            t.setAuthNumber(resp.getField(38).toString());
//        } catch (NullPointerException ex) {
//            //throw new GatewayException("Unknown AuthCode.");
//            t.setAuthNumber("");
//        }
//
//        try {
//            t.setReasonCode(resp.getField(39).toString());
//        } catch (NullPointerException ex) {
//            //throw new GatewayException("Unknown Response Code.");
//            t.setReasonCode("");
//        }
//
//        String additional_rsp = resp.getField(60).toString();
////        LOG.debug(
////                "response field 60: " + additional_rsp + ", length: " + additional_rsp.
////                length());
//        String respType = null;
//        String rspReasonCode = null;
//        String rspDesc = null;
//        String rspBalance = null;
//        if (additional_rsp != null || additional_rsp.length() >= 14) {
//            respType = additional_rsp.substring(0, 1);
//            rspReasonCode = additional_rsp.substring(1, 4);
//            rspDesc = additional_rsp.substring(4, 14);
//            if (additional_rsp.length() > 14
//                    && (t.getRequestType().equalsIgnoreCase(
//                            RequestType.INQUIRY)
//                    || t.getRequestType().equalsIgnoreCase(
//                            RequestType.PAYMENT))) {
//                rspBalance = additional_rsp.substring(14, 26);
//                try {
//                    t.setBalanceAmount(Long.parseLong(rspBalance));
//                } catch (NumberFormatException e) {
//                    throw new GatewayException("INVALID_AMOUNT");
//                }
//
//            } else if (additional_rsp.length() >= 83
//                    && (t.getRequestType().equalsIgnoreCase(
//                            RequestType.SALE))) {
//                rspBalance = additional_rsp.substring(71, 83);
//                try {
//                    t.setBalanceAmount(Long.parseLong(rspBalance));
//                } catch (NumberFormatException e) {
//                    throw new GatewayException("INVALID_AMOUNT");
//                }
//
//            }
//        }
//
//        if (rspReasonCode == null || rspReasonCode.equalsIgnoreCase("")) {
//            t.setResponseType(ResponseType.DECLINED);
//            t.setReasonCode("");
//        } else {
//            t.setReasonCode(rspReasonCode);
//        }
//        if (rspDesc != null || rspDesc.length() > 0) {
//            t.setDescriptionField(rspDesc);
//        } else {
//            t.setDescriptionField(ResponseType.DECLINED);
//        }
//
//        switch (respType) {
//            case "A":
//                t.setResponseType(ResponseType.APPROVED);
//                break;
//            case "D":
//                t.setResponseType(ResponseType.DECLINED);
//                break;
//            case "R":
//                t.setResponseType(ResponseType.REFERRAL);
//                break;
//            case "P":
//                t.setResponseType(ResponseType.PENDING);
//                break;
//            case "C":
//                t.setResponseType(ResponseType.CALLCREDIT);
//                break;
//            case "S":
//                t.setResponseType(ResponseType.SendToService);
//                break;
//            default:
//                //crdtmsg.setResponseType(ResponseType.DECLINED);
//                throw new Exception("UNKNOWN_RESPONSE_TYPE");
//        }
//    }
//
//    private static void logResponse(byte[] response) {
//        if (response.length < 1) {
//            LOG.info("From Vision: (nothing?!)");
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
//        LOG.info("From Vision: " + logResponse);
//    }
//
//    public IsoMessage toISO8583(Transaction t) throws IOException,
//            GatewayException {
//
//        MessageFactory mfact = ConfigParser.createFromClasspathConfig(
//                "config.xml");
//        mfact.setTraceNumberGenerator(new SimpleTraceGenerator((int) (System.currentTimeMillis() % 10000)));
//        mfact.setAssignDate(true);
//        IsoMessage req = mfact.newMessage(0x100);
//
//        String forceFlag;
//        if (t.getReversal() != null && !t.getReversal().isEmpty()
//                || t.getVoidFlag() != null && !t.getVoidFlag().isEmpty()) {
//            forceFlag = buildReversalRequest(req, t);
//        } else {
//            forceFlag = buildSaleRequest(req, t);
//        }
//
//        String localAccountNumber = StringUtils.leftPad(t.getAccount(), 19, '0');
//        req.setValue(2, localAccountNumber, IsoType.LLVAR, 19);
//
//        //Field 4 Amount
//        try {
//            int amount = Integer.parseInt(Long.toString(t.getAmount()).trim());
//            String amt_STR = String.format("%012d", amount);
//            req.setValue(4, amt_STR, IsoType.NUMERIC, 12); // Amount
//        } catch (NumberFormatException e) {
//            throw new GatewayException("INVALID_AMOUNT");
//        }
//
//        // Field 12 Local Time
//        req.setValue(12, t.getLocalDateTime(), IsoType.DATE10, 12); // Local Transaction Time
//
//        // Field 14 Expiration
//        req.setValue(14, t.getExpiration(), IsoType.DATE_EXP, 4); // Expiration
//
//        // Field 18 in config.xml
//        // Field 19 in config.xml
//        // Field 22
//        // TODO : Will never get device type
//        // Field 18 - 19 from Config File
//        
//        //Field 37
//        if(t.getRrn() != null)
//        {
//            req.setValue(37, t.getRrn(), IsoType.ALPHA, 12);
//        }
//        
//        // Field 22
//        if (t.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
//            req.setValue(22, "90", IsoType.ALPHA, 3); //POS Data Code
//            t.setInputCode("90");
//        } else if (t.getDeviceType().equalsIgnoreCase(DeviceType.ICS)
//                && t.getInputType().equalsIgnoreCase(
//                        InputType.KEYED)) {
//            req.setValue(22, "81", IsoType.ALPHA, 3); //POS Data Code
//            t.setInputCode("81");
//        } else {
//            req.setValue(22, "01", IsoType.ALPHA, 3); //POS Data Code
//            t.setInputCode("01");
//        }
//
//        // Field 35 - tracek 2 data
//        if (t.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
//
//            if (t.getTrack2() != null && t.getTrack2().trim().
//                    length() > 0) {
//                req.setValue(35, t.getTrack2(), IsoType.LLVAR, 37); //Card Sequence Number
//            }
//            if (t.getTrack1() != null && t.getTrack1().trim().
//                    length() > 0) {
//                req.setValue(45, t.getTrack1(), IsoType.LLVAR, 76); //Card Sequence Number
//            }
//        }
//
//        // Field 42 Card Acceptor Identification Code
//        //TODO : check facility number
//        String facility = t.getFacility().substring(0,9);
//        facility = "000000"+facility;
//        req.setValue(42, facility,
//                IsoType.NUMERIC, 15); // Fac ID
//
//        // AVS Field 48
//        String AVS = "";
//        if (Validator.isZipCode(t.getZipCode())) {
//            String zipCode = "";
//
//            if (t.getZipCode().trim().length() == 5) {
//                zipCode = String.format("%s    ", t.getZipCode().trim());
//            } else if (t.getBillingZipCode().trim().length() == 9) {
//                zipCode = String.format("%s", t.getZipCode().trim());
//            } else {
//                throw new GatewayException("Invalid Zip : " + t.
//                        getZipCode().trim());
//            }
//
//            if (t.getBillingAddress1() != null && t.
//                    getBillingAddress1().length() != 0) {
//                AVS = zipCode + t.getBillingAddress1();
//            } else {
//                AVS = zipCode;
//            }
//
//        } else if (Validator.isZipCode(t.getBillingZipCode())) {
//            String zipCode = "";
//            switch (t.getBillingZipCode().trim().length()) {
//                case 5:
//                    zipCode = String.format("%s    ", t.getBillingZipCode().
//                            trim());
//                    break;
//                case 9:
//                    zipCode = String.
//                            format("%s", t.getBillingZipCode().trim());
//                    break;
//                default:
//                    throw new GatewayException("Invalid Zip : " + t.
//                            getBillingZipCode().trim());
//            }
//
//            if (t.getBillingAddress1() != null || t.
//                    getBillingAddress1().length() != 0) {
//                AVS = zipCode + t.getBillingAddress1() ;
//            } else {
//                AVS = zipCode;
//            }
//        }
//
//        // TODO : Will never get Device type
//        if (t.getDeviceType().equalsIgnoreCase(DeviceType.WEB)) {
//            if (AVS.length() > 0) {
//                LOG.debug("AVS Request: " + AVS);
//                req.setValue(48, AVS, IsoType.LLLVAR, 29); // AVS
//            } else {
//                throw new GatewayException("Invalid Zip : " + t.
//                        getBillingZipCode().trim());
//            }
//        }
//
//        // Field 49 in config.xml
//        //TODO: Can we reject if plan number is empty ?
//        String PlanNbr = t.getPlanNumber();
//        if (t.getMedia().equalsIgnoreCase(MediaType.MIL_STAR)
//                && PlanNbr.equalsIgnoreCase("00000")) {
//            PlanNbr = "10001";
//        }
//
//        String MerchantOrg = t.getMerchantOrg();
//
//        if (MerchantOrg.equalsIgnoreCase(
//                "")) {
//            MerchantOrg = "000";
//            t.setMerchantOrg(MerchantOrg);
//        }
//
//        String DownPayment = t.getDownPayment();
//        if (DownPayment
//                == null || DownPayment.equalsIgnoreCase(
//                        "")) {
//            DownPayment = "000000000000";
//            t.setDownPayment(DownPayment);
//        }
//
//        String SKUNbr = t.getSKUNumber();
//        if (SKUNbr
//                == null || SKUNbr.equalsIgnoreCase(
//                        "")) {
//            SKUNbr = "00000000000";
//            t.setSKUNumber(SKUNbr);
//        }
//
//        String Card = t.getCardReferenceID();
//        if (Card
//                == null || Card.equalsIgnoreCase(
//                        "")) {
//            Card = "005";
//            t.setCardReferenceID(Card);
//        }
//
//        // TODO : Do we get CVV every time ?
//        String CVV = t.getCvv();
//
//        String privateData = String.format("%3s%5s%12s%11s%1s%3s%s",
//                MerchantOrg, PlanNbr,
//                DownPayment, SKUNbr,
//                forceFlag, Card, CVV);
//
//        req.setValue(
//                62, privateData, IsoType.LLLVAR, 92);
//        //}
//
//        req.setCharacterEncoding(
//                "UTF-8");
//        req.setBinaryBitmap(
//                true);
//
//        return req;
//    }
//
//    private String buildReversalRequest(IsoMessage req, Transaction t) {
//
//        String forceFlag;
//
//        switch (t.getRequestType()) {
//            case RequestType.FINAL:
//            case RequestType.SALE:
//                req.setValue(3, "200000", IsoType.NUMERIC, 6); // Processing Code
//                break;
//            case RequestType.LOAD:
//            case RequestType.REFUND:
//                req.setValue(3, "200000", IsoType.NUMERIC, 6); // Processing Code
//
//            case RequestType.PAYMENT:
//                req.setValue(3, "000000", IsoType.NUMERIC, 6); // Processing Code
//                break;
//            case RequestType.PREAUTH:
//            case RequestType.INQUIRY:
//            default:
//                throw new GatewayException("INVALID_REQUEST_TYPE");
//        }
//
//        // Field 7 Assign on the top
//        // Field 11
//        // TODO : What is Stan ?
////        if (t.getSTAN() == null
////                || t.getSTAN().equalsIgnoreCase("")
////                || t.getSTAN().length() < 2) {
////            throw new GatewayException("Invalid STAN for Reversal.");
////
////        }
//
//        //Field 23
//        // Seq number will be set base on TrackData,
//        // if Keyed in, wil/ use default in config.xml
//        //Field 23
//        String SeqNumber = "0001";
//        if (t.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
//            if (t.getTrack2() != null) {
//                if (t.getMedia().
//                        equalsIgnoreCase(MediaType.MIL_STAR)
//                        && t.getTrack2().length() >= 34) {
//                    SeqNumber = t.getTrack2().substring(30, 34);
//                }
//
//            } else if (t.getTrack1() != null) {
//                if (t.getMedia().
//                        equalsIgnoreCase(MediaType.MIL_STAR)
//                        && t.getTrack1().length() >= 34) {
//                    SeqNumber = t.getTrack1().substring(30, 34);
//                }
//            }
//            //else { using default Value in config.xml
//            //}
//            if (Validator.isNumberOnly(SeqNumber) && SeqNumber != null) {
//                t.setCardSequenceNumber(SeqNumber);
//            } else {
//                SeqNumber = "0001";
//            }
//            req.setValue(23, SeqNumber, IsoType.ALPHA, 4); //Card Sequence Number
//        }
//
//        t.setCardSequenceNumber(SeqNumber);
//        
//        if (t.getFacility() != null) {
//            if (t.getFacility().length() >= 8) {
//                req.setValue(42,
//                        t.getFacility().substring(0, 8) + "0",
//                        IsoType.NUMERIC, 15); // Fac ID
//            } else {
//                throw new GatewayException("INVALID_FACILITY");
//            }
//        } else {
//            throw new GatewayException("INVALID_FACILITY");
//        }
//          switch (t.getRequestType()) {
//            case RequestType.REFUND:
//            case RequestType.LOAD:
//            case RequestType.PAYMENT:
//                forceFlag = "Y";
//                break;
//            default:
//                forceFlag = "N";
//        }
//
//        return forceFlag;
//    }
//
//    private String buildSaleRequest(IsoMessage req, Transaction t) {
//        String forceFlag;
//
//        switch (t.getRequestType()) {
//            case RequestType.SALE:
//            case RequestType.FINAL:
//                req.setValue(3, "000000", IsoType.NUMERIC, 6); // Processing Code
//                break;
//            case RequestType.PAYMENT:
//                req.setValue(3, "200000", IsoType.NUMERIC, 6); // Processing Code
//                break;
//            case RequestType.LOAD:
//            case RequestType.REFUND:
//                req.setValue(3, "200000", IsoType.NUMERIC, 6); // Processing Code
//                break;
//            case RequestType.INQUIRY:
//                req.setValue(3, "300000", IsoType.NUMERIC, 6); // Processing Code
//                break;
//            case RequestType.PREAUTH:
//                if (t.getMedia().equals(MediaType.MIL_STAR)) {
//                    req.setValue(3, "000000", IsoType.NUMERIC, 6); // Processing Code
//                } else {
//                    req.setValue(3, "300000", IsoType.NUMERIC, 6); // Processing Code
//                }
//
//                break;
//            default:
//                throw new GatewayException("INVALID_REQUEST_TYPE");
//        }
//
//        String SeqNumber = "";
//        if (t.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
//            if (t.getTrack2() != null) {
//                if (t.getMedia().equalsIgnoreCase(MediaType.MIL_STAR)
//                        && t.getTrack2().length() >= 34) {
//                    SeqNumber = t.getTrack2().substring(30, 34);
//                }
//
//            } else if (t.getTrack1() != null) {
//                if (t.getMedia().equalsIgnoreCase(MediaType.MIL_STAR)
//                        && t.getTrack1().length() >= 34) {
//                    SeqNumber = t.getTrack1().substring(30, 34);
//                }
//            } else {
//                SeqNumber = "0001";
//            }
//
//            t.setCardSequenceNumber(SeqNumber);
//            req.setValue(23, SeqNumber, IsoType.ALPHA, 4); //Card Sequence Number
//        }
//        t.setCardSequenceNumber(req.getField(23).toString());
//
//        switch (t.getRequestType()) {
//            case RequestType.FINAL:
//                forceFlag = "Y";
//                break;
//            default:
//                forceFlag = "N";
//
//        }
//
//        return forceFlag;
//    }
//    
//    
//}
