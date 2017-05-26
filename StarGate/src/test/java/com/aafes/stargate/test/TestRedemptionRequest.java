/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.test;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.gateway.svs.NetworkMessageProcessor;
import com.aafes.stargate.gateway.svs.ProcessorFactory;
import com.aafes.stargate.gateway.svs.RedemptionProcessor;
import com.aafes.stargate.gateway.svs.SVSGateway;
import com.aafes.stargate.gateway.svs.SVSGatewayProcessor;
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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
        transaction.setRequestType(RequestType.NETWORK);
        transaction.setInputType(InputType.KEYED);
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

        Transaction t = sVSGateway.processMessage(transaction);;
        if (transaction.getReasonCode() == "29") {
            Assert.assertEquals(t.getReasonCode(), "29");
        }
        Assert.assertEquals(t.getReasonCode(), "01");
    }

    @Ignore
    @Test
    public void testForTransactionFailedDueToInvalidGcPin() throws MalformedURLException {
        LOGGER.info("method...." + "testForTransactionFailedDueToInvalidGcPin");

        transaction.setGcpin("ss");
        Transaction t = sVSGateway.processMessage(transaction);

        Assert.assertEquals(t.getReasonCode(), "20");

    }

    @Ignore
    @Test
    public void testForTransactionFailedDueToInvalidCardNumber() throws MalformedURLException {
        LOGGER.info("method...." + "testForTransactionFailedDueToInvalidCardNumber");

        transaction.setAccount("62666485547567458");
        Transaction t = sVSGateway.processMessage(transaction);

        Assert.assertEquals(t.getReasonCode(), "04");

    }
    @Ignore
    @Test
    public void testForNullTranscationId() {
        LOGGER.info("method...." + "testForNullTranscationId");

        transaction.setTransactionId("");
        Transaction t = sVSGateway.processMessage(transaction);;

        Assert.assertEquals(t.getDescriptionField(), "TRANSACTION ID IS NULL");
    }
    @Ignore
    @Test
    public void testForAmount() throws MalformedURLException {
        LOGGER.info("method...." + "testForWrongRoutingId");

        transaction.setAmount(250);
        Transaction t = sVSGateway.processMessage(transaction);

        Assert.assertEquals(t.getAmount(), 250);

    }

}
