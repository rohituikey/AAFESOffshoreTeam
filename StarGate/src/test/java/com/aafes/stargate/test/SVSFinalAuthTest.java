///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.aafes.stargate.test;
//
//import com.aafes.stargate.authorizer.entity.Transaction;
//import com.aafes.stargate.gateway.svs.SVSGateway;
//import com.aafes.stargate.gateway.svs.SVSGatewayProcessor;
//import com.aafes.stargate.util.InputType;
//import com.aafes.stargate.util.MediaType;
//import com.aafes.stargate.util.RequestType;
//import com.aafes.stargate.util.SvsUtil;
//import javax.xml.datatype.DatatypeConfigurationException;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//
///**
// *
// * @author uikuyr
// */
//public class SVSFinalAuthTest {
//    
//    Transaction transaction = new Transaction();
//    SVSGateway sVSGateway;
//
//    @Before
//    public void init() throws DatatypeConfigurationException{
//        
//        sVSGateway = new SVSGateway();
//        SVSGatewayProcessor gatewayProcessor = new SVSGatewayProcessor();
//        sVSGateway.setSvsgp(gatewayProcessor);
//        
//        transaction.setMedia(MediaType.GIFT_CARD);
//        transaction.setRequestType(RequestType.PREAUTH);
//        transaction.setInputType(InputType.KEYED);
//        transaction.setAccount("6006491572010002421");
//        transaction.setExpiration("2113");
//        transaction.setAmount((long) 25.00);
//        transaction.setGcpin("00003685");
//        transaction.setTrack1("");
//        transaction.setTrack2("");
//        //XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar("2016-12-07T10:12:01");
//        transaction.setLocalDateTime(SvsUtil.formatLocalDateTime());
//        transaction.setOrderNumber("55548741536");
//        // GET routingID IN PROCESSOR CLASS
//        transaction.setTransactionId("326598985232");
//        transaction.setDivisionnumber("99999");
//        transaction.setMerchantOrg("IT-D VP OFFICE");
//
//    }
//    
//    @Test
//    public void testPreAuthFinal_success(){
//        Transaction preAuthResponse = sVSGateway.processMessage(transaction);
//        if(preAuthResponse.getReasonCode().equals("01")){
//        transaction.setRequestType(RequestType.FINAL_AUTH);
//        Transaction resultTransaction = sVSGateway.processMessage(transaction);
//        Assert.assertEquals("01", resultTransaction.getReasonCode());
//        }
//    }
//    
//    @Test
//    public void testPreAuthFinal_wrongPin(){
//        Transaction preAuthResponse = sVSGateway.processMessage(transaction);
//        if(preAuthResponse.getReasonCode().equals("01")){
//        transaction.setGcpin("wrong_Pin");
//        transaction.setRequestType(RequestType.FINAL_AUTH);
//        Transaction resultTransaction = sVSGateway.processMessage(transaction);
//        Assert.assertEquals("20", resultTransaction.getReasonCode());
//        }
//    }
//    
//    @Test
//    public void testPreAuthFinal_NullGCpin(){
//        Transaction preAuthResponse = sVSGateway.processMessage(transaction);
//        if(preAuthResponse.getReasonCode().equals("01")){
//        transaction.setGcpin("");
//        transaction.setRequestType(RequestType.FINAL_AUTH);
//        Transaction resultTransaction = sVSGateway.processMessage(transaction);
//        Assert.assertTrue(resultTransaction.getReasonCode().isEmpty());
//        }
//    }
//    
//    @Test
//    public void testPreAuthFinal_NullAccountNumber(){
//        transaction.setRequestType(RequestType.PREAUTH);
//        Transaction preAuthResponse = sVSGateway.processMessage(transaction);
//        if(preAuthResponse.getReasonCode().equals("01")){
//        transaction.setAccount("");
//        transaction.setRequestType(RequestType.FINAL_AUTH);
//        Transaction resultTransaction = sVSGateway.processMessage(transaction);
//        Assert.assertTrue(resultTransaction.getReasonCode().isEmpty());
//        }
//    }
//    
//     @Test
//    public void testPreAuthFinal_NullTransacionID(){
//        transaction.setRequestType(RequestType.PREAUTH);
//        Transaction preAuthResponse = sVSGateway.processMessage(transaction);
//        if(preAuthResponse.getReasonCode().equals("01")){
//        transaction.setTransactionId(null);
//        transaction.setRequestType(RequestType.FINAL_AUTH);
//        Transaction resultTransaction = sVSGateway.processMessage(transaction);
//        Assert.assertTrue(resultTransaction.getReasonCode().isEmpty());
//        }
//    }
//    
//}
