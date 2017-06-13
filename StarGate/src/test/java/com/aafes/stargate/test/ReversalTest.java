///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.aafes.stargate.test;
//
//import com.aafes.stargate.authorizer.entity.Transaction;
//import com.aafes.stargate.control.Configurator;
//import com.aafes.stargate.gateway.svs.ProcessorFactory;
//import com.aafes.stargate.gateway.svs.SVSGateway;
//import com.aafes.stargate.gateway.svs.SVSGatewayProcessor;
//import com.aafes.stargate.gateway.svs.SVSReversalProcessor;
//import com.aafes.stargate.util.MediaType;
//import com.aafes.stargate.util.RequestType;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.runners.MockitoJUnitRunner;
//
///**
// *
// * @author uikuyr
// */
//@RunWith(MockitoJUnitRunner.class)
//public class ReversalTest {
//    
//   
//    private SVSReversalProcessor sVSReversalProcessor = new SVSReversalProcessor();
//    private ProcessorFactory processorFactory = new ProcessorFactory();
//    private SVSGatewayProcessor svsgp = new SVSGatewayProcessor();
//    private Transaction transaction = new Transaction();
//
//    @Mock
//    private Configurator configurator;
//    
//    @InjectMocks
//    private SVSGateway sVSGateway = new SVSGateway();
//    
//    @Before
//    public void setUp() {
//        processorFactory.setsVSReversalProcessor(sVSReversalProcessor);
//        svsgp.setProcessorFactory(processorFactory);
//        sVSGateway.setSvsgp(svsgp);
//        transaction.setRequestType(RequestType.REVERSAL);
//        transaction.setMedia(MediaType.GIFT_CARD);
//        transaction.setAmount((long) 1.00);
//        transaction.setCurrencycode("USD");
//        transaction.setAccount("6006496628299904508");
//        transaction.setGcpin("00002496");
//        transaction.setOrderNumber("00009999");
//        transaction.setSTAN("112233");
//    }
//
//    @Ignore
//    @Test
//    public void testProcessRequest() {
//        Transaction result = sVSGateway.processMessage(transaction);
//        System.out.println("result.getReasonCode()" +result.getReasonCode());
//        Assert.assertEquals("01", result.getReasonCode());
//    }
//    
//    @Test
//    public void testProcessRequest_accountIsNull() {
//        transaction.setAccount("");
//        Mockito.when(configurator.get("INVALID_ACCOUNT_NUMBER")).thenReturn("911");
//        Transaction result = sVSGateway.processMessage(transaction);
//        Assert.assertEquals("911", result.getReasonCode());
//    }
//    
//    @Ignore
//    @Test
//    public void testProcessRequest_StanIsNull() {
//        transaction.setSTAN("");
//        Transaction result = sVSGateway.processMessage(transaction);
//        Assert.assertEquals("15", result.getReasonCode());
//    }
//    
//}
