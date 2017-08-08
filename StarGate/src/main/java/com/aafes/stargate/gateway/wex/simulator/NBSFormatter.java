/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex.simulator;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.gateway.wex.NBSRequestGenerator;
import com.aafes.stargate.util.InputType;
import com.aafes.stargate.util.RequestType;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.impl.SimpleTraceGenerator;
import com.solab.iso8583.parse.ConfigParser;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

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
