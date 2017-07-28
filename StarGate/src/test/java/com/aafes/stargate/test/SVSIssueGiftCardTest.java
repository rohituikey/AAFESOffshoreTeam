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
//import com.aafes.stargate.gateway.svs.SVSIssueGiftCardProcessor;
//import com.aafes.stargate.util.MediaType;
//import com.aafes.stargate.util.RequestType;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//
///**
// *
// * @author uikuyr
// */
//public class SVSIssueGiftCardTest {
//
//    private SVSIssueGiftCardProcessor SVSIssueGiftCard = new SVSIssueGiftCardProcessor();
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
//        processorFactory.setIssueGiftCard(SVSIssueGiftCard);
//        svsgp.setProcessorFactory(processorFactory);
//        sVSGateway.setSvsgp(svsgp);
//        transaction.setRequestType(RequestType.ISSUEGIFTCARD);
//        transaction.setMedia(MediaType.GIFT_CARD);
//        transaction.setAmount((long) 9999.99);
//        transaction.setCurrencycode("USD");
//        transaction.setAccount("6006491286999929112");
//        transaction.setGcpin("08760234");
//        transaction.setOrderNumber("00000001");
//        transaction.setSTAN("123456");
//        transaction.setTransactionId("2143651105080020");
//    }
//
//    @Ignore
//    @Test
//    public void testProcessRequest() {
//        Transaction result = sVSGateway.processMessage(transaction);
//        Assert.assertEquals("01", result.getReasonCode());
//    }
//    @Ignore
//    @Test
//    public void testProcessRequest_accountIsNull() {
//        transaction.setAccount("");
//        Mockito.when(configurator.get("INVALID_ACCOUNT_NUMBER")).thenReturn("911");
//        Transaction result = sVSGateway.processMessage(transaction);
//        Assert.assertEquals("911", result.getReasonCode());
//    }
//    @Ignore
//    @Test
//    public void testProcessRequest_invalidStan() {
//        transaction.setSTAN("");
//        Transaction result = sVSGateway.processMessage(transaction);
//        Assert.assertEquals("15", result.getReasonCode());
//    }
//    
//}
