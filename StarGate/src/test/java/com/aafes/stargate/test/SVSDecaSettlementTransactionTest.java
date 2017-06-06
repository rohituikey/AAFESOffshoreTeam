/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.test;

import com.aafes.stargate.authorizer.BaseStrategy;
import com.aafes.stargate.authorizer.RetailStrategy;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.GatewayFactory;
import com.aafes.stargate.gateway.svs.MerchandiseReturnMessageProcessor;
import com.aafes.stargate.gateway.svs.ProcessorFactory;
import com.aafes.stargate.gateway.svs.SVSGateway;
import com.aafes.stargate.gateway.svs.SVSGatewayProcessor;
import com.aafes.stargate.gateway.svs.SVSIssueGiftCardProcessor;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.StrategyType;
import com.datastax.driver.mapping.Mapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 *
 * @author singha
 */
public class SVSDecaSettlementTransactionTest {
    
//    Transaction transaction = new Transaction();
//    @InjectMocks
//    private SVSGateway sVSGateway = new SVSGateway();
//
//    @Before
//    public void init() throws DatatypeConfigurationException{
//        
//        SVSGatewayProcessor gatewayProcessor = new SVSGatewayProcessor();
//        sVSGateway.setSvsgp(gatewayProcessor);
//        transaction.setMedia(MediaType.GIFT_CARD);
//        RetailStrategy retailStrategy = new RetailStrategy();
//        retailStrategy.processRequest(transaction);
//
//    }
    
    
    private SVSIssueGiftCardProcessor SVSIssueGiftCard = new SVSIssueGiftCardProcessor();
      private MerchandiseReturnMessageProcessor merchandiseReturnMessageProcessor = new MerchandiseReturnMessageProcessor();
    private ProcessorFactory processorFactory = new ProcessorFactory();
    private SVSGatewayProcessor svsgp = new SVSGatewayProcessor();
    private Transaction transaction = new Transaction();
    
    @Mock
    private Mapper mapper;

    @InjectMocks
    private  GatewayFactory  gateFactory= new GatewayFactory();
    
    @InjectMocks
    private SVSGateway sVSGateway = new SVSGateway();

    @Before
    public void setUp() {
        processorFactory.setIssueGiftCard(SVSIssueGiftCard);
        processorFactory.setMerchandiseReturnMessageProcessor(merchandiseReturnMessageProcessor);
        svsgp.setProcessorFactory(processorFactory);
        sVSGateway.setSvsgp(svsgp);
        transaction.setStrategy(StrategyType.MPG);
        transaction.setTransactionId("1258");
        transaction.setOrderNumber("3641258");
        transaction.setTransactiontype("Sale");
        transaction.setIdentityUuid("302aa1e2-22f0-4a3d-a41c-2c4339d847c4");
        transaction.setRrn("hbXQUJ6pGth7");
        transaction.setAmount((long) 1.00);
        transaction.setSettleIndicator("true");
        transaction.setReversal("true");
        transaction.setLocalDateTime("2017-02-05T09:10:01");
        transaction.setCustomerId("01");
        transaction.setAccount("6019440000000321");
        transaction.setRequestType(RequestType.REFUND);
    }

    @Test
    public void decaSettlement() {
       // Mockito.when(gatewayFactory.pickGateway(transaction)).thenReturn("9002");
       BaseStrategy bs =new RetailStrategy();
       MerchandiseReturnMessageProcessor merchandiseReturnMessageProcessor = new MerchandiseReturnMessageProcessor();
       bs.setGatewayFactory(gateFactory);
       Transaction result = sVSGateway.processMessage(transaction);
       Assert.assertEquals("01", result.getReasonCode());
    }
    
}
