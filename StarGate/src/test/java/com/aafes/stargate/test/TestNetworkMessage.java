/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.aafes.stargate.test;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.svs.NetworkMessageProcessor;
import com.aafes.stargate.gateway.svs.ProcessorFactory;
import com.aafes.stargate.gateway.svs.SVSGateway;
import com.aafes.stargate.gateway.svs.SVSGatewayProcessor;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.RequestType;
import java.net.MalformedURLException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author alugumetlas
 */
@Ignore
public class TestNetworkMessage {
    
   
    private NetworkMessageProcessor networkMessageProcessor = new NetworkMessageProcessor();
    private ProcessorFactory processorFactory = new ProcessorFactory();
    private SVSGatewayProcessor svsgp = new SVSGatewayProcessor();
    private Transaction transaction = new Transaction();
    private SVSGateway sVSGateway = new SVSGateway();
    
    @Before
    public void setUp() {
        processorFactory.setNetworkMessageProcessor(networkMessageProcessor);
        svsgp.setProcessorFactory(processorFactory);
        sVSGateway.setSvsgp(svsgp);

        transaction.setMedia(MediaType.GIFT_CARD);
        transaction.setRequestType(RequestType.NETWORK);
    }

    @Test
    public void testNetworkMessageSuccess() throws MalformedURLException {
    Transaction t = sVSGateway.processMessage(transaction);
    Assert.assertEquals("00", t.getReasonCode());

    }
    @Test
    public void testAuthorizationNullOrNot()
    {
        Transaction t = sVSGateway.processMessage(transaction);
        Assert.assertNotNull(t.getAuthoriztionCode());
    }

}
