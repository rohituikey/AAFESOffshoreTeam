///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.aafes.stargate.test;
//
//import com.aafes.stargate.authorizer.entity.Transaction;
//import com.aafes.stargate.gateway.fdms.CompassGateway;
//import com.aafes.stargate.gateway.fdms.CompassGatewayProcessor;
//import com.aafes.stargate.util.MediaType;
//import com.aafes.stargate.util.StrategyType;
//import org.junit.Assert;
//import org.junit.Test;
//
///**
// *
// * @author singha
// */
//public class FDMSTimeoutTest {
//
//    private Transaction transaction = new Transaction();
//
//    @Test
//    public void testfdmsTimeout() {
//        CompassGateway compassGateway = new CompassGateway();
//        CompassGatewayProcessor compassGatewayProcessor = new CompassGatewayProcessor();
//        compassGateway.setCgp(compassGatewayProcessor);
//        
//        transaction.setMedia(MediaType.AMEX);
//        transaction.setStrategy(StrategyType.ECOMM);
//        transaction.setIdentityUuid("c47e4366-fe72-473b-b114-523e8de5641f");
//        Transaction result = compassGateway.processMessage(transaction);
//        Assert.assertEquals("000", result.getReasonCode());
//
//    }
//
//}
