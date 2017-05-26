/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.test;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.gateway.svs.ProcessorFactory;
import com.aafes.stargate.gateway.svs.RedemptionProcessor;
import com.aafes.stargate.gateway.svs.SVSGateway;
import com.aafes.stargate.gateway.svs.SVSGatewayProcessor;
import com.aafes.stargate.util.InputType;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.SvsUtil;
import com.google.common.collect.ComparisonChain;
import java.net.MalformedURLException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alugumetlas
 */
@RunWith(MockitoJUnitRunner.class)
public class TestRedemptionRequest {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TestRedemptionRequest.class.getSimpleName());

    private RedemptionProcessor redemptionProcessor = new RedemptionProcessor();
    private ProcessorFactory processorFactory = new ProcessorFactory();
    private SVSGatewayProcessor svsgp = new SVSGatewayProcessor();
    private Transaction transaction = new Transaction();

    @Mock
    private Configurator configurator;
    @InjectMocks
    private SVSGateway sVSGateway = new SVSGateway();

    @Before
    public void setUp() {
        processorFactory.setRedemptionProcessor(redemptionProcessor);
        svsgp.setProcessorFactory(processorFactory);
        sVSGateway.setSvsgp(svsgp);

        transaction.setMedia(MediaType.GIFT_CARD);
        transaction.setRequestType(RequestType.REDEMPTION);

       
        transaction.setAccount("6006491572010001514");
        transaction.setAmount((long) 29.00);
        transaction.setGcpin("5196");
        transaction.setOrderNumber("9999");
        transaction.setRrn("9RprkC1gEYoN");
      
    }

    @Test
    public void testRedemptionRequestSuccessOrFailedIncaseOFPinGetLocked() throws MalformedURLException {

        LOGGER.info("method...." + "testRedemptionRequestSuccessOrFailedIncaseOFPinGetLocked-");

        Transaction t = sVSGateway.processMessage(transaction);;
        if (transaction.getReasonCode() == "29") {
            Assert.assertEquals(t.getReasonCode(), "29");
        }
        Assert.assertEquals(t.getReasonCode(), "01");
         LOGGER.info("RETURN DISCRIPTION--"+t.getDescriptionField());
    }

  

    @Test
    public void testForTransactionFailedDueToInvalidCardNumber() throws MalformedURLException {
        LOGGER.info("method...." + "testForTransactionFailedDueToInvalidCardNumber");

        transaction.setAccount("");
        Mockito.when(configurator.get("INVALID_ACCOUNT_NUMBER")).thenReturn("911");
        Transaction t = sVSGateway.processMessage(transaction);

        Assert.assertEquals(t.getReasonCode(), "911");

    }
      @Test
    public void testForTransactionFailedDueToInvalidGcPin() throws MalformedURLException {
        LOGGER.info("method...." + "testForTransactionFailedDueToInvalidGcPin");

        transaction.setGcpin("s");
        Transaction t = sVSGateway.processMessage(transaction);

        Assert.assertEquals(t.getReasonCode(), "200");

    }

    @Test
    public void testForAmount() throws MalformedURLException {
        LOGGER.info("method...." + "testForWrongRoutingId");
        Transaction t = sVSGateway.processMessage(transaction);
        Assert.assertEquals(t.getBalanceAmount(), 29);

    }

    @Test
    public void testAuthorizationNullOrNot() {
        Transaction t = sVSGateway.processMessage(transaction);
        Assert.assertNotNull(t.getAuthoriztionCode());
    }

}
