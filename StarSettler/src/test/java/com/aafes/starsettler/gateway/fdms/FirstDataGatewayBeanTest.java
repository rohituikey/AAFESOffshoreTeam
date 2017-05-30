///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.aafes.starsettler.gateway.fdms;
//
//import com.aafes.starsettler.control.CassandraSessionFactory;
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
//import org.xml.sax.SAXException;
//
///**
// *
// * @author ghadiyamp
// */
//public class FirstDataGatewayBeanTest {
//
//    String processDate = "20171111";
//    String batchId = "";
//
//    @Test
//    public void testgateway() throws DatatypeConfigurationException, ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException, FirstDataException {
//        List<SettleEntity> fdmsDataVisa = getSettleEntityElementsVisa();
//        List<SettleEntity> fdmsDataMaster = getSettleEntityElementsMaster();
//        List<SettleEntity> fdmsDataAmex = getSettleEntityElementsAmex();
//        List<SettleEntity> fdmsDataDiscover = getSettleEntityElementsDiscover();
//        SettleMessageRepository repository = new SettleMessageRepository();
//        FirstDataGatewayBean bean = new FirstDataGatewayBean();
//        SettleXMLHandler handler = new SettleXMLHandler();
//        bean.setSettleXMLHandler(handler);
////        bean.settle(fdmsDataVisa, batchId);
////        bean.settle(fdmsDataMaster, batchId);
////        bean.settle(fdmsDataAmex, batchId);
////        bean.settle(fdmsDataDiscover, batchId);
//
//        assertEquals(true, true);
//
//    }
//
//    public List<SettleEntity> getSettleEntityElementsVisa() throws DatatypeConfigurationException {
//        List<SettleEntity> fdmsData = new ArrayList<SettleEntity>();
//
//        SettleEntity settleEntity = new SettleEntity();
//        settleEntity.setIdentityUUID("c47e4366-fe72-473b-b114-523e8de5641f");
//        settleEntity.setCardType("Visa");
//        settleEntity.setOrderNumber("afdf40c0-7bb0-4e0");
//        settleEntity.setCardToken("4012000033330026");
//        settleEntity.setExpirationDate("0416");
//        settleEntity.setUnit("01");
//        settleEntity.setUnitTotal("02");
//        settleEntity.setFirstName("purna");
//        settleEntity.setLastName("ghadiyam");
//        settleEntity.setAddressLine1("3911 S Walton Walker Blvd");
//        settleEntity.setAddressLine2("Irving");
//        settleEntity.setCity("Dallas");
//        settleEntity.setProvinceCode("TX");
//        settleEntity.setPostalCode("75038");
//        settleEntity.setCountryCode("USA");
//        settleEntity.setResponseReasonCode("100");
//        settleEntity.setResponseDate("123456");
//        settleEntity.setAuthoriztionCode("123456");
//        settleEntity.setAvsResponseCode("12");
//        settleEntity.setTransactionType(TransactionType.Deposit);
//        settleEntity.setPaymentAmount("100");
//        fdmsData.add(settleEntity);
//        return fdmsData;
//    }
//
//    public List<SettleEntity> getSettleEntityElementsMaster() throws DatatypeConfigurationException {
//        List<SettleEntity> fdmsData = new ArrayList<SettleEntity>();
//
//        SettleEntity settleEntity = new SettleEntity();
//        settleEntity.setIdentityUUID("c47e4366-fe72-473b-b114-523e8de5641f");
//        settleEntity.setCardType("Mastercard");
//        settleEntity.setOrderNumber("afdf40c0-7bb0-4e0");
//        settleEntity.setCardToken("4012000033330026");
//        settleEntity.setExpirationDate("0416");
//        settleEntity.setUnit("01");
//        settleEntity.setUnitTotal("02");
//        settleEntity.setFirstName("purna");
//        settleEntity.setLastName("ghadiyam");
//        settleEntity.setAddressLine1("3911 S Walton Walker Blvd");
//        settleEntity.setAddressLine2("Irving");
//        settleEntity.setCity("Dallas");
//        settleEntity.setProvinceCode("TX");
//        settleEntity.setPostalCode("75038");
//        settleEntity.setCountryCode("USA");
//        settleEntity.setResponseReasonCode("100");
//        settleEntity.setResponseDate("123456");
//        settleEntity.setAuthoriztionCode("123456");
//        settleEntity.setAvsResponseCode("12");
//        settleEntity.setTransactionType(TransactionType.Deposit);
//        settleEntity.setPaymentAmount("100");
//        fdmsData.add(settleEntity);
//        return fdmsData;
//    }
//
//    public List<SettleEntity> getSettleEntityElementsAmex() throws DatatypeConfigurationException {
//        List<SettleEntity> fdmsData = new ArrayList<SettleEntity>();
//
//        SettleEntity settleEntity = new SettleEntity();
//        settleEntity.setIdentityUUID("c47e4366-fe72-473b-b114-523e8de5641f");
//        settleEntity.setCardType("Amex");
//        settleEntity.setOrderNumber("afdf40c0-7bb0-4e0");
//        settleEntity.setCardToken("4012000033330026");
//        settleEntity.setExpirationDate("0416");
//        settleEntity.setUnit("01");
//        settleEntity.setUnitTotal("02");
//        settleEntity.setFirstName("purna");
//        settleEntity.setLastName("ghadiyam");
//        settleEntity.setAddressLine1("3911 S Walton Walker Blvd");
//        settleEntity.setAddressLine2("Irving");
//        settleEntity.setCity("Dallas");
//        settleEntity.setProvinceCode("TX");
//        settleEntity.setPostalCode("75038");
//        settleEntity.setCountryCode("USA");
//        settleEntity.setResponseReasonCode("100");
//        settleEntity.setResponseDate("123456");
//        settleEntity.setAuthoriztionCode("123456");
//        settleEntity.setAvsResponseCode("12");
//        settleEntity.setTransactionType(TransactionType.Deposit);
//        settleEntity.setPaymentAmount("100");
//        fdmsData.add(settleEntity);
//        return fdmsData;
//    }
//
//    public List<SettleEntity> getSettleEntityElementsDiscover() throws DatatypeConfigurationException {
//        List<SettleEntity> fdmsData = new ArrayList<SettleEntity>();
//
//        SettleEntity settleEntity = new SettleEntity();
//        settleEntity.setIdentityUUID("c47e4366-fe72-473b-b114-523e8de5641f");
//        settleEntity.setCardType("Discover");
//        settleEntity.setOrderNumber("afdf40c0-7bb0-4e0");
//        settleEntity.setCardToken("4012000033330026");
//        settleEntity.setExpirationDate("0416");
//        settleEntity.setUnit("01");
//        settleEntity.setUnitTotal("02");
//        settleEntity.setFirstName("purna");
//        settleEntity.setLastName("ghadiyam");
//        settleEntity.setAddressLine1("3911 S Walton Walker Blvd");
//        settleEntity.setAddressLine2("Irving");
//        settleEntity.setCity("Dallas");
//        settleEntity.setProvinceCode("TX");
//        settleEntity.setPostalCode("75038");
//        settleEntity.setCountryCode("USA");
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
//}
