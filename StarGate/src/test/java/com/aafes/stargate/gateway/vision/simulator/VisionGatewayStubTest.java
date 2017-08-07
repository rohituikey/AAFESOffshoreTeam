///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.aafes.stargate.gateway.vision.simulator;
//
//import com.aafes.stargate.authorizer.entity.Transaction;
//import com.aafes.stargate.util.ResponseType;
//import java.util.Date;
//import org.junit.Before;
//import org.junit.Test;
//import static org.junit.Assert.*;
//import org.junit.Ignore;
//
///**
// *
// * @author uikuyr
// */
//@Ignore
//public class VisionGatewayStubTest {
//    
//    private Transaction transaction;
//    
//    private VisionGatewayStub subjectUnderTest;
//        
//    @Before
//    public void setUp() {
//        transaction = new Transaction();
//        subjectUnderTest = new VisionGatewayStub();
//    }
//
//    @Test
//    public void testProcessMessage_Approval() throws Exception {
//        transaction.setComment("Approve");
//        transaction = subjectUnderTest.processMessage(transaction);
//        assertEquals("01", transaction.getReasonCode());
//        assertEquals(ResponseType.APPROVED, transaction.getResponseType());
//    }
//
//    @Test
//    public void testProcessMessage_Reject() throws Exception {
//        transaction.setComment("Reject");
//        transaction = subjectUnderTest.processMessage(transaction);
//        assertEquals("200", transaction.getReasonCode());
//        assertEquals(ResponseType.DECLINED, transaction.getResponseType());
//    }
//    
//    /**
//     *
//     * @throws Exception
//     */
//    @Test
//    @Ignore("Functionality removed")
//    public void testProcessMessage_requestHoldForThreeSeconds() throws Exception {
//        transaction.setComment("Approve");
//        Date startTime = new Date();
//        transaction = subjectUnderTest.processMessage(transaction);
//        Date stopTime = new Date();
//        assertTrue((stopTime.getTime()-startTime.getTime())>3);
//    }
//    
//}
