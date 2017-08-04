///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.aafes.stargate.gateway.wex;
//
//import com.aafes.nbslogonrequestschema.NbsLogonRequest;
//import com.aafes.nbsresponseacknowledgmentschema.ResponseAcknowlegment;
//import com.aafes.stargate.gateway.wex.simulator.NBSClient;
//import com.aafes.stargate.util.ResponseType;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import org.jpos.iso.ISOMsg;
//import org.junit.Assert;
//import static org.junit.Assert.fail;
//import org.junit.Before;
//import org.junit.Test;
//
///**
// *
// * @author uikuyr
// */
//public class NBSRequestGeneratorTest {
//
//    NbsLogonRequest nbsLogonRequest;
//    NBSRequestGenerator subjectUnderTest = new NBSRequestGenerator();
//    NBSClient nBSClient = new NBSClient();
//
//    @Before
//    public void setUp() {
//        nbsLogonRequest = new NbsLogonRequest();
//        NbsLogonRequest.HeaderRecord headerRecord = new NbsLogonRequest.HeaderRecord();
//        NbsLogonRequest.HeaderRecord.CardSpecificData cardSpecificData = new NbsLogonRequest.HeaderRecord.CardSpecificData();
////        NbsLogonRequest.HeaderRecord.CardSpecificData.WexProductDetails wexProductDetails = new NbsLogonRequest.HeaderRecord.CardSpecificData.WexProductDetails();
////        NbsLogonRequest.HeaderRecord.CardSpecificData.WexPromptDetails wexPromptDetails= new NbsLogonRequest.HeaderRecord.CardSpecificData.WexPromptDetails();
////        NbsLogonRequest.HeaderRecord.CardSpecificData.WexProductDetails.ProdDetailCount prodDetailCount = new NbsLogonRequest.HeaderRecord.CardSpecificData.WexProductDetails.ProdDetailCount();
////        NbsLogonRequest.HeaderRecord.CardSpecificData.WexPromptDetails.PromptDetailCount promptDetailCount= new NbsLogonRequest.HeaderRecord.CardSpecificData.WexPromptDetails.PromptDetailCount();
//
//        cardSpecificData.setAcctInfo("dummy Info");
//        cardSpecificData.setAmount(BigDecimal.ONE);
//
//        headerRecord.setA("dummy Info");
//        headerRecord.setCATFlag(BigInteger.ONE);
//        headerRecord.setCardType("dummy Info");
//        headerRecord.setKey(BigInteger.ONE);
//        headerRecord.setPumpNo("dummy Info");
//        headerRecord.setServiceType("S");
//        headerRecord.setTrack(BigInteger.ONE);
//        headerRecord.setTransType(BigInteger.ONE);
//        headerRecord.setCardSpecificData(cardSpecificData);
//
//        nbsLogonRequest.setAppName("dummy Appname");
//        nbsLogonRequest.setAppVersion(new BigInteger("1"));
//        nbsLogonRequest.setTermId("dummy Term ID");
//        nbsLogonRequest.setTimeZone(new BigInteger("02501"));
//        nbsLogonRequest.setHeaderRecord(headerRecord);
//
//        ISOMsg iSOMsg = new ISOMsg();
//        subjectUnderTest.setIsoMsg(iSOMsg);
//    }
//
//    @Test
//    public void testGenerateLogOnPacketRequest() {
//        System.out.println("generateLogOnPacketRequest");
//        try {
//            
//            String result = subjectUnderTest.generateLogOnPacketRequest(nbsLogonRequest);
//            String response = nBSClient.generateResponse(result);
//            Assert.assertEquals("APPROVED", response);
//        } catch (Exception ex) {
//            fail(ex.getMessage());
//        }
//    }
//
//        @Test
//        public void testUnmarshalResponse() {
//        System.out.println("generateLogOnPacketRequest");
//            try {
//                ResponseAcknowlegment result = new ResponseAcknowlegment();
//                subjectUnderTest.setResponseAcknowlegment(result);
//                result = subjectUnderTest.unmarshalAcknowledgment("02310060000000000000002c$003100");
//                //String response = nBSClient.generateResponse(result);
//                Assert.assertEquals(ResponseType.APPROVED, result.getResponseType());
//            } catch (Exception ex) {
//                fail(ex.getMessage());
//            }
//        }
//        
//        @Test
//        public void seperateResponse(){
//            String[] result=subjectUnderTest.seperateResponse("02310060000000000000002c$0031000231007FFFF800000000011AuthRequest0064659870020100310000225007Message008cardType002010054host00847596587008458745690057856900478960045896005753910067896450027900478960059856300578965");
//            Assert.assertEquals("02310060000000000000002c$0031000",result[0]);
//            Assert.assertEquals("231007FFFF800000000011AuthRequest0064659870020100310000225007Message008cardType002010054host00847596587008458745690057856900478960045896005753910067896450027900478960059856300578965", result[1]);
//        }
//    }
