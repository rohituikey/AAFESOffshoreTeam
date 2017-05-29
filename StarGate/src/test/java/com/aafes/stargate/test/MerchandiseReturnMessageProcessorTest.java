package com.aafes.stargate.test;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.gateway.svs.MerchandiseReturnMessageProcessor;
import com.aafes.stargate.gateway.svs.ProcessorFactory;
import com.aafes.stargate.gateway.svs.SVSGateway;
import com.aafes.stargate.gateway.svs.SVSGatewayProcessor;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.RequestType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author uikuyr
 */
@RunWith(MockitoJUnitRunner.class)
public class MerchandiseReturnMessageProcessorTest {

    private MerchandiseReturnMessageProcessor merchandiseReturnMessageProcessor = new MerchandiseReturnMessageProcessor();
    private ProcessorFactory processorFactory = new ProcessorFactory();
    private SVSGatewayProcessor svsgp = new SVSGatewayProcessor();
    private Transaction transaction = new Transaction();

    @Mock
    private Configurator configurator;
    
    @InjectMocks
    private SVSGateway sVSGateway = new SVSGateway();
    
    @Before
    public void setUp() {
        processorFactory.setMerchandiseReturnMessageProcessor(merchandiseReturnMessageProcessor);
        svsgp.setProcessorFactory(processorFactory);
        sVSGateway.setSvsgp(svsgp);
        transaction.setRequestType(RequestType.REFUND);
        transaction.setMedia(MediaType.GIFT_CARD);
        transaction.setAmount((long) 250.00);
        transaction.setCurrencycode("USD");
        transaction.setAccount("6006496628299904508");
        transaction.setGcpin("2496");
        transaction.setOrderNumber("00009999");
        transaction.setSTAN("112233");
    }

    @Test
    public void testProcessRequest() {
        Transaction result = sVSGateway.processMessage(transaction);
        if(!result.getReasonCode().equals("01") && !result.getReasonCode().equals("14")){
            Assert.fail("Neither the response is successfull nor the Maximum Working Balance Exceeded");
        }
    }
    
    @Test
    public void testProcessRequest_accountIsNull() {
        transaction.setAccount("");
        Mockito.when(configurator.get("INVALID_ACCOUNT_NUMBER")).thenReturn("911");
        Transaction result = sVSGateway.processMessage(transaction);
        Assert.assertEquals("911", result.getReasonCode());
    }
    
    @Ignore
    @Test
    public void testProcessRequest_invalidStan() {
        transaction.setSTAN("");
        Transaction result = sVSGateway.processMessage(transaction);
        Assert.assertEquals("15", result.getReasonCode());
    }
    
}
