/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.test;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.svs.SVSGateway;
import com.aafes.stargate.gateway.svs.SVSGatewayProcessor;
import com.aafes.stargate.util.InputType;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.StarGateConstants;
import com.aafes.stargate.util.SvsUtil;
import java.net.MalformedURLException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alugumetlas
 *
 */
public class TestBalanceEnquiry {

    Transaction transaction = new Transaction();
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TestBalanceEnquiry.class.getSimpleName());

    @Before
    public void getKeyedMILSTARSaleRequestatPOS() throws DatatypeConfigurationException {

        transaction.setMedia(MediaType.GIFT_CARD);
        transaction.setRequestType(RequestType.INQUIRY);
        transaction.setInputType(InputType.KEYED);
        transaction.setPan("12121221121212");
        transaction.setAccount("6006491572010002439");
        transaction.setExpiration("2113");
        transaction.setAmount((long) 25.00);
        transaction.setCurrencycode("USD");
        transaction.setGcpin("00007020");
        transaction.setLocalDateTime(SvsUtil.formatLocalDateTime());
        transaction.setOrderNumber("55548741536");


        transaction.setSTAN(SvsUtil.generateStan());
        transaction.setTransactionId("326598985232");
        transaction.setCurrencycode(StarGateConstants.CURRENCY);
        transaction.setDivisionnumber(StarGateConstants.MERCHANT_DIVISION_NUMBER);
        transaction.setMerchantOrg(StarGateConstants.MERCHANT_NAME);
    
    }

    @Test
    public void testBalanceEnquirySuccessOrFailedIncaseOFPinGetLocked() throws MalformedURLException {

        LOGGER.info("method...." + "testBalanceEnquirySuccess");
        SVSGateway gateway = new SVSGateway();
        SVSGatewayProcessor svsgp = new SVSGatewayProcessor();
        gateway.setSvsgp(svsgp);
        Transaction t = gateway.processMessage(transaction);
        if (t.getReasonCode() == "20") {
            Assert.assertEquals(t.getReasonCode(), "20");
        }
        Assert.assertEquals(t.getReasonCode(), "01");
    }

    @Test
    public void testForTransactionFailedDueToInvalidGcPin() throws MalformedURLException {
        LOGGER.info("method...." + "testForTransactionFailedDueToInvalidGcPin");
        SVSGateway sVSGateway = new SVSGateway();
        SVSGatewayProcessor svsgp = new SVSGatewayProcessor();
        sVSGateway.setSvsgp(svsgp);
        transaction.setGcpin("sudhu");
        Transaction t = sVSGateway.processMessage(transaction);
        Assert.assertEquals(t.getReasonCode(), "20");

    }
    /*
    *we are not setting stan in balance inquiry request so i ignored it...
    */
    @Ignore
    @Test
    public void testBalanceInqueryFailForWrongStan() throws MalformedURLException {

        SVSGateway gateway = new SVSGateway();
        SVSGatewayProcessor svsgp = new SVSGatewayProcessor();
        gateway.setSvsgp(svsgp);
        transaction.setSTAN("ss");
        Transaction t = gateway.processMessage(transaction);
        Assert.assertEquals(t.getReasonCode(), "15");

    }

    @Test
    public void testForTransactionFailedDueToInvalidCardNumber() throws MalformedURLException {
        LOGGER.info("method...." + "testForTransactionFailedDueToInvalidCardNumber");
        SVSGateway sVSGateway = new SVSGateway();
        SVSGatewayProcessor svsgp = new SVSGatewayProcessor();
        sVSGateway.setSvsgp(svsgp);
        transaction.setAccount("62666485547567458");
        Transaction t = sVSGateway.processMessage(transaction);
        System.out.println(t.getReasonCode());
        Assert.assertEquals(t.getReasonCode(), "04");

    }




}
