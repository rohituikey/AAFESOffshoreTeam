/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.wex.simulator.NBSConnector;
import com.aafes.stargate.util.InputType;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author uikuyr
 */
public class NBSRequestGeneratorTest {

    Transaction t;
    NBSRequestGenerator testSubject;

//    NbsLogonRequest nbsLogonRequest;
//    NBSRequestGenerator subjectUnderTest = new NBSRequestGenerator();
//    NBSClient nBSClient = new NBSClient();
    @Before
    public void setUp() {
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

        t = new Transaction();
        t.setTermId("01");
        t.setTransactionId("01000");
        t.setCatFlag("N");
        t.setPumpNmbr("00");
        t.setAmount(12);
        t.setAmtPreAuthorized(12);
        t.setAuthNumber("23");
        t.setTrack2("123456");
        t.setInputType(InputType.SWIPED);
        t.setPromptDetailCount(BigDecimal.ONE);

        testSubject = new NBSRequestGenerator();
    }
    @Ignore
    @Test
    public void testGenerateLogOnPacketRequest() throws SocketTimeoutException {
//        System.out.println("generateLogOnPacketRequest");
//        try {
//            
//            String result = subjectUnderTest.generateLogOnPacketRequest(nbsLogonRequest);
//            String response = nBSClient.generateResponse(result);
//            Assert.assertEquals("APPROVED", response);
//        } catch (Exception ex) {
//            fail(ex.getMessage());
//        }
            byte[] result=testSubject.generateLogOnPacketRequest(t,false);
            NBSConnector nBSConnector = new NBSConnector();
            nBSConnector.sendRequest(result);
  //          String[] results = testSubject.seperateResponse("02006000000000000000002c$00310002007FFFF80000000000001A00402780133170621071655001N00200008Approved003WEX0000000000000000015001000630833900575.00001100775.000000300100578965".getBytes());
//            testSubject.unmarshalAcknowledgment(result[0]);
//            testSubject.unmarshalNbsResponse(result[1]);
    }

//        @Test
//        public void testUnmarshalResponse() {
//        System.out.println("generateLogOnPacketRequest");
//            try {
//                ResponseAcknowlegment result = new ResponseAcknowlegment();
//                subjectUnderTest.setResponseAcknowlegment(result);
//                result = subjectUnderTest.unmarshalAcknowledgment("02310060000000000000002c$003100");
//                String response = nBSClient.generateResponse(result);
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
}
