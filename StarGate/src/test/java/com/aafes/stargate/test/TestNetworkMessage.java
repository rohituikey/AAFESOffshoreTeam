/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.aafes.stargate.test;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.svs.NetworkMessageProcessor;
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
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alugumetlas
 */
public class TestNetworkMessage {
    
    Transaction transaction = new Transaction();
    
    NetworkMessageProcessor messageProcessor = new NetworkMessageProcessor();
    
    @Test
    public void testNetworkMessageSuccess() throws MalformedURLException {
       
        messageProcessor.processRequest(transaction);
        Assert.assertEquals("00", transaction.getReasonCode());
        
    }
    
}
