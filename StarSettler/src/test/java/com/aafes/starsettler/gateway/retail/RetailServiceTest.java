//package com.aafes.starsettler.gateway.retail;
//
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//import com.aafes.starsettler.entity.SettleEntity;
//import com.aafes.starsettler.gateway.retailer.RetailFile;
//import com.aafes.starsettler.gateway.retailer.RetailSFTP;
//import com.aafes.starsettler.gateway.retailer.RetailService;
//import com.aafes.starsettler.gateway.retailer.RetailSettler;
//import java.io.IOException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import javax.xml.datatype.DatatypeConfigurationException;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.TransformerException;
//import javax.xml.xpath.XPathExpressionException;
//import static org.junit.Assert.assertEquals;
//import org.junit.Test;
//import org.xml.sax.SAXException;
//
///**
// *
// * @author burangir
// */
//public class RetailServiceTest {
//    private DateFormat dateFormat = null;
//    
//    public RetailServiceTest() {
//        this.dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//    }
//
//    //private final DateFormat dateFormat;
//    @Test
//    public void testvision() throws DatatypeConfigurationException, ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException, Exception {
//        List<SettleEntity> retailDataList = getMockData();
//        //convert this list to map hashmap
//
//        HashMap<String, SettleEntity> retailMap = new HashMap<String, SettleEntity>();
//        for (SettleEntity vision : retailDataList) {
//            retailMap.put(vision.getProvinceCode(), vision);
//
//        }
//
//        RetailService retailService = new RetailService();
//        RetailSettler retailSettler = new RetailSettler();
//        retailService.setSftp(new RetailSFTP());
//        retailService.setRetailFile(new RetailFile());
//        retailService.setSourcePath("./");
//        retailService.setSFTPHOST("hqjudgedredd01");
//        retailService.setDestination("");
//        retailService.setSourcePassword("");
//        retailService.setSFTPUSER("purna");
//        retailService.setIdentity("");
//        
//        retailSettler.setRetailService(retailService);
//        retailService.generateAndSendToRetail(retailMap);
//        assertEquals("true", "true");
//    }
//    
//    @Test
//    public void testvisionPassword() throws DatatypeConfigurationException, ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException, Exception {
//        List<SettleEntity> retailDataList = getMockData();
//        Date date = new Date();
//            String createdDate = dateFormat.format(date);
//        //convert this list to map hashmap
//
//        HashMap<String, SettleEntity> retailMap = new HashMap<String, SettleEntity>();
//        for (SettleEntity retail : retailDataList) {
//            retailMap.put(retail.getProvinceCode(), retail);
//
//        }
//
//        RetailService retailService = new RetailService();
//        RetailSettler retailSettler = new RetailSettler();
//        RetailSFTP sftp = new RetailSFTP();
//        retailService.setSftp(new RetailSFTP());
//        retailService.setRetailFile(new RetailFile());
//        retailService.setSourcePath("./");
//        retailService.setSFTPHOST("hqjudgedredd01");
//        retailService.setDestination("");
//        retailService.setSourcePassword("none");
//        retailService.setSFTPUSER("purna");
//        retailService.setIdentity("");
//        retailSettler.setRetailService(retailService);
//        retailService.generateAndSendToRetail(retailMap);
//        
//       sftp.sendByPassword(createdDate);
//       assertEquals("true", "true");
//    }
//
//      private List<SettleEntity> getMockData() {
//        List<SettleEntity> giftCardData = new ArrayList<>();
//
//        SettleEntity settleEntity = new SettleEntity();
//        settleEntity.setOrderDate("2016-12-06T19:01:0");
//        settleEntity.setOrderNumber("3317153619");
//        settleEntity.setPaymentAmount("10");
//        settleEntity.setCardToken("6019440000000320");
//        settleEntity.setRequestPlan("20001");
//        settleEntity.setRrn("gW7BroSRcMT3");
//        settleEntity.setAuthNum("000033");
//        settleEntity.setSettleDate("20170411");
//        settleEntity.setProvinceCode("USA");
//
//        giftCardData.add(settleEntity);
//
//        SettleEntity settleEntity1 = new SettleEntity();
//        settleEntity1.setOrderDate("2015-14-08T19:01:0");
//        settleEntity1.setOrderNumber("3317153628");
//        settleEntity1.setPaymentAmount("20");
//        settleEntity1.setCardToken("6019440000000321");
//        settleEntity1.setRequestPlan("10001");
//        settleEntity1.setRrn("gW7BroSRcMT4");
//        settleEntity1.setAuthNum("000034");
//        settleEntity1.setSettleDate("20170511");
//        settleEntity1.setProvinceCode("USA");
//
//        giftCardData.add(settleEntity1);
//
//        SettleEntity settleEntity2 = new SettleEntity();
//        settleEntity2.setOrderDate("2015-14-08T19:01:0");
//        settleEntity2.setOrderNumber("3317156628");
//        settleEntity2.setPaymentAmount("30");
//        settleEntity2.setCardToken("6019440000000451");
//        settleEntity2.setRequestPlan("20001");
//        settleEntity2.setRrn("gW7BroSRcMT8");
//        settleEntity2.setAuthNum("000038");
//        settleEntity2.setSettleDate("20170611");
//        settleEntity2.setProvinceCode("USA");
//
//        giftCardData.add(settleEntity2);
//
//        SettleEntity settleEntity3 = new SettleEntity();
//        settleEntity3.setOrderDate("2015-14-08T19:01:0");
//        settleEntity3.setOrderNumber("3317156628");
//        settleEntity3.setPaymentAmount("40");
//        settleEntity3.setCardToken("6019440000000451");
//        settleEntity3.setRequestPlan("20001");
//        settleEntity3.setRrn("gW7BroSRcMT9");
//        settleEntity3.setAuthNum("000038");
//        settleEntity3.setSettleDate("20170711");
//        settleEntity3.setProvinceCode("USA");
//
//        giftCardData.add(settleEntity3);
//
//        SettleEntity settleEntity4 = new SettleEntity();
//        settleEntity4.setOrderDate("2015-14-08T19:01:0");
//        settleEntity4.setOrderNumber("3317158628");
//        settleEntity4.setPaymentAmount("50");
//        settleEntity4.setCardToken("6019440000000491");
//        settleEntity4.setRequestPlan("20001");
//        settleEntity4.setRrn("gW7BroSRiMT9");
//        settleEntity4.setAuthNum("000048");
//        settleEntity4.setSettleDate("20170811");
//        settleEntity4.setProvinceCode("USA");
//
//        giftCardData.add(settleEntity4);
//
//        return giftCardData;
//    }
// }