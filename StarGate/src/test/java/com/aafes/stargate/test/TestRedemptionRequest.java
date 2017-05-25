/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.test;


import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.svs.RedemptionProcessor;
import com.aafes.stargate.util.InputType;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.RequestType;
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
 */
public class TestRedemptionRequest {

    Transaction transaction = new Transaction();
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TestRedemptionRequest.class.getSimpleName());

    @Before
    public void getKeyedMILSTARSaleRequestatPOS() throws DatatypeConfigurationException {

        transaction.setMedia(MediaType.GIFT_CARD);
        transaction.setRequestType(RequestType.REDEMPTION);
        transaction.setInputType(InputType.KEYED);
        transaction.setAccount("6006491572010002439");
        transaction.setAmount((long) 229.00);
        transaction.setGcpin("7020");
        transaction.setOrderNumber("9999");
        transaction.setSTAN("112233");

    }
@Ignore
    @Test
    public void testRedemptionRequestSuccessOrFailedIncaseOFPinGetLocked() throws MalformedURLException {

        LOGGER.info("method...." + "testRedemptionRequestSuccessOrFailedIncaseOFPinGetLocked");
        RedemptionProcessor redemptionProcessor = new RedemptionProcessor();
        redemptionProcessor.processRequest(transaction);
        if (transaction.getReasonCode() == "29") {
            Assert.assertEquals(transaction.getReasonCode(), "29");
        }
        Assert.assertEquals(transaction.getReasonCode(), "01");
    }

    @Ignore
    @Test
    public void testForTransactionFailedDueToInvalidGcPin() throws MalformedURLException {
        LOGGER.info("method...." + "testForTransactionFailedDueToInvalidGcPin");
        RedemptionProcessor redemptionProcessor = new RedemptionProcessor();
        redemptionProcessor.processRequest(transaction);
        transaction.setGcpin("ss");
        Assert.assertEquals(transaction.getReasonCode(), "20");

    }

    @Ignore
    @Test
    public void testForTransactionFailedDueToInvalidCardNumber() throws MalformedURLException {
        LOGGER.info("method...." + "testForTransactionFailedDueToInvalidCardNumber");
        RedemptionProcessor redemptionProcessor = new RedemptionProcessor();
        redemptionProcessor.processRequest(transaction);
        transaction.setAccount("62666485547567458");
        System.out.println(transaction.getReasonCode());
        Assert.assertEquals(transaction.getReasonCode(), "04");

    }
    @Ignore
    @Test
    public void testForNullTranscationId() {
        LOGGER.info("method...." + "testForNullTranscationId");
        RedemptionProcessor redemptionProcessor = new RedemptionProcessor();
        redemptionProcessor.processRequest(transaction);
        transaction.setTransactionId("");
        System.out.println(transaction.getReasonCode());
        Assert.assertEquals(transaction.getDescriptionField(), "TRANSACTION ID IS NULL");
    }
    @Ignore
    @Test
    public void testForAmount() throws MalformedURLException {
        LOGGER.info("method...." + "testForWrongRoutingId");
        RedemptionProcessor redemptionProcessor = new RedemptionProcessor();
        redemptionProcessor.processRequest(transaction);
        transaction.setAmount(250);
        Assert.assertEquals(transaction.getAmount(), 250);

    }

}
