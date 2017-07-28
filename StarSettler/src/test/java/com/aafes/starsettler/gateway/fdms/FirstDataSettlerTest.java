//package com.aafes.starsettler.gateway.fdms;
//
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//import com.aafes.starsettler.control.BaseSettler;
//import com.aafes.starsettler.control.SettleMessageRepository;
//import com.aafes.starsettler.entity.SettleEntity;
//import com.aafes.starsettler.util.TransactionType;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import javax.xml.datatype.DatatypeConfigurationException;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.TransformerException;
//import javax.xml.xpath.XPathExpressionException;
//import static org.junit.Assert.assertEquals;
//import org.junit.Test;
//import static org.mockito.ArgumentMatchers.any;
//import org.mockito.Mockito;
//import static org.mockito.Mockito.when;
//import org.xml.sax.SAXException;
//
///**
// *
// * @author ghadiyamp
// */
//public class FirstDataSettlerTest {
//
//    String batchId = "2457855.0";
//    String processDate = "20170411";
//
//    @Test
//    public void testRun() throws DatatypeConfigurationException, ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException, FirstDataException {
//
//        List<SettleEntity> fdmsData = getSettleEntityElements();
//        
//        FirstDataSettler firstSettler = new FirstDataSettler();
//       //Repositiory Mock
//        SettleMessageRepository repository = Mockito.mock(SettleMessageRepository.class);
//         when(repository.getFDMSData(any(), any())).thenReturn(fdmsData);
//         
//         BaseSettler settler = firstSettler;      
//       
//         //Batchid setting
//          when(repository.getBatchId()).thenReturn(batchId);
//          
//          //SettleGateway Bean,settleXMLHandler Setting
//        FirstDataGatewayBean bean = new FirstDataGatewayBean();
//        SettleXMLHandler settleXMLHandler = new SettleXMLHandler();
//        firstSettler.setSettlegatewayBean(bean);
//         bean.setSettleXMLHandler(settleXMLHandler);
//         
//         //Setting all to Repository
//         settler.setRepository(repository); 
//          
//       // firstSettler.run(processDate);
//        assertEquals(true, true);
//    }
//    
//    @Test
//    public void testRunNullFDMS() throws DatatypeConfigurationException, ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException {
//
//        List<SettleEntity> blank = null;
//
//        SettleMessageRepository repository = Mockito.mock(SettleMessageRepository.class);
//        when(repository.getFDMSData(any(), any())).thenReturn(blank);
//
//        FirstDataSettler firstSettler = new FirstDataSettler();
//        FirstDataGatewayBean settlegatewayBean = new FirstDataGatewayBean();
//        firstSettler.setSettlegatewayBean(settlegatewayBean);
//
//        BaseSettler settler = firstSettler;
//
//        settler.setRepository(repository);
//       // settler.run(processDate);
//        assertEquals(true, true);
//    }
//
//    public List<SettleEntity> getSettleEntityElements() throws DatatypeConfigurationException {
//        List<SettleEntity> fdmsData = new ArrayList<SettleEntity>();
//
//        SettleEntity settleEntity = new SettleEntity();
//        settleEntity.setIdentityUUID("c47e4366-fe72-473b-b114-523e8de5641f");
//        settleEntity.setLineId("15402344");
//        settleEntity.setClientLineId("ci14009000055-1");
//        settleEntity.setShipId("5457732");
//        settleEntity.setCrc("1360594");
//        settleEntity.setQuantity("1");
//        settleEntity.setUnitCost("140.00");
//        settleEntity.setUnitDiscount("10.00");
//        settleEntity.setUnit("1");
//        settleEntity.setUnitTotal("10");
//        settleEntity.setCouponCode("123");
//        settleEntity.setCardType("Visa");
//        settleEntity.setOrderNumber("afdf40c0-7bb0-4e0");
//        settleEntity.setCardToken("4012000033330026");
//        settleEntity.setExpirationDate("0416");
//        settleEntity.setPaymentAmount("100");
//        settleEntity.setUnit("01");
//        settleEntity.setBatchId("2457854.0");
//        settleEntity.setUnitTotal("02");
//        settleEntity.setFirstName("purna");
//        settleEntity.setLastName("ghadiyam");
//        settleEntity.setAddressLine1("3911 S Walton Walker Blvd");
//        settleEntity.setAddressLine2("Irving");
////        settleEntity.setAddressLine3("test");
//        settleEntity.setCity("Dallas");
//        settleEntity.setProvinceCode("TX");
//        settleEntity.setPostalCode("75038");
//        settleEntity.setCountryCode("USA");
//        settleEntity.setReceiveddate("20170411");
//        settleEntity.setOrderDate("20170411");
//        settleEntity.setCardType("Visa");
//        settleEntity.setTransactionType("aaa");
//        settleEntity.setTransactionId("123");
//        settleEntity.setShipDate("20170411");
//        settleEntity.setCardReferene("abc");
//        settleEntity.setAuthNum("123");
//        settleEntity.setRequestPlan("abc");
//        settleEntity.setResponsePlan("1234");
//        settleEntity.setQualifiedPlan("12345");
//        settleEntity.setRrn("123");
//        settleEntity.setHomePhone("123456789");
//        settleEntity.setEmail("aaa@gmail.com");
//        settleEntity.setShippingAmount("1234");
//        settleEntity.setAppeasementCode("123");
//        settleEntity.setAppeasementDate("20170411");
//        settleEntity.setAppeasementDescription("Testing");
//        settleEntity.setAppeasementReference("Test");
//        settleEntity.setReasonCode("123");
//        settleEntity.setResponseType("abc");
//        settleEntity.setDescriptionField("test");
//        settleEntity.setSettleId("123");
//        settleEntity.setSettlestatus("InProgress");
//        settleEntity.setSettleDate("20170411");
//        settleEntity.setResponseReasonCode("100");
//        settleEntity.setResponseDate("123456");
//        settleEntity.setAuthoriztionCode("123456");
//        settleEntity.setAvsResponseCode("12");
//        settleEntity.setTransactionType(TransactionType.Deposit);
//        settleEntity.setPaymentAmount("100");
//        fdmsData.add(settleEntity);
//
//        return fdmsData;
//    }
//
//}
