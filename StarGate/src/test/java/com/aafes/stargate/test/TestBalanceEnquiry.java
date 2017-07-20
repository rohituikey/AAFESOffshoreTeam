/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.test;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.svs.BalanceInquiryProcessor;
import com.aafes.stargate.gateway.svs.SVSGateway;
import com.aafes.stargate.gateway.svs.SVSGatewayProcessor;
import com.aafes.stargate.util.InputType;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.StarGateConstants;
import com.aafes.stargate.util.SvsUtil;
import java.net.MalformedURLException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.WebServiceException;
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

        transaction.setAccount("6006491572010002439");
        transaction.setExpiration("2113");
        transaction.setAmount((long) 25.00);
        transaction.setCurrencycode("USD");
        transaction.setGcpin("7020");

        transaction.setOrderNumber("55548741536");

        transaction.setSTAN(SvsUtil.generateStan());
        transaction.setTransactionId("326598985232");

    }

    @Test
    public void testBalanceEnquirySuccessOrFailedIncaseOFPinGetLocked() throws MalformedURLException {
        try{
        LOGGER.info("method...." + "testBalanceEnquirySuccess");
        BalanceInquiryProcessor balanceInquiryProcessor = new BalanceInquiryProcessor();
        balanceInquiryProcessor.processRequest(transaction);;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            if (!(e instanceof WebServiceException)){
                 Assert.fail();
            } 
        }
//        if (transaction.getReasonCode() == "20") {
//            Assert.assertEquals(transaction.getReasonCode(), "20");
//        }
//        Assert.assertEquals("01", transaction.getReasonCode());

    }
    @Ignore
    @Test
    public void testForTransactionFailedDueToInvalidGcPin() throws MalformedURLException {
        LOGGER.info("method...." + "testForTransactionFailedDueToInvalidGcPin");

        transaction.setGcpin("sudhu");
        BalanceInquiryProcessor balanceInquiryProcessor = new BalanceInquiryProcessor();
        balanceInquiryProcessor.processRequest(transaction);;
        Assert.assertEquals(transaction.getReasonCode(), "20");

    }
 @Ignore
    @Test
    public void testForTransactionFailedDueToInvalidCardNumber() throws MalformedURLException {
        LOGGER.info("method...." + "testForTransactionFailedDueToInvalidCardNumber");

        transaction.setAccount("62666485547567458");
        BalanceInquiryProcessor balanceInquiryProcessor = new BalanceInquiryProcessor();
        balanceInquiryProcessor.processRequest(transaction);;
        System.out.println(transaction.getReasonCode());
        Assert.assertEquals(transaction.getReasonCode(), "04");
    }

//    @Test
//    public void testForNullTranscationId() {
//        LOGGER.info("method...." + "testForNullTranscationId");
//        transaction.setTransactionId("");
//        BalanceInquiryProcessor balanceInquiryProcessor = new BalanceInquiryProcessor();
//        balanceInquiryProcessor.processRequest(transaction);
//        System.out.println(transaction.getReasonCode());
//        Assert.assertEquals(transaction.getDescriptionField(), "TRANSACTION ID IS NULL");
//    }

}
