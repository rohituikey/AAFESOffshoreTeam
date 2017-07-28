///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.aafes.starsettler.control;
//
//import com.aafes.starsettler.dao.FacilityDAO;
//import com.aafes.starsettler.dao.SettleMessageDAO;
//import com.aafes.starsettler.dao.TransactionDAO;
//import com.aafes.starsettler.entity.Facility;
//import com.aafes.starsettler.entity.SettleEntity;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.xml.datatype.DatatypeConfigurationException;
//import org.junit.After;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import org.junit.Before;
//import org.junit.Test;
//
//
///**
// *
// * @author ghadiyamp
// */
//public class CassandraSessionFactoryTest {
//    
//    private CassandraSessionFactory csf;
//   
//     @Before
//    public void setUp()
//    {
//         Properties p = new Properties();
//        try {
//            p.load(new FileReader(new File("src/main/resources/starsettler.properties")));
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(CassandraSessionFactoryTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(CassandraSessionFactoryTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//         csf = new CassandraSessionFactory();
//         csf.setSeedHost(p.getProperty("com.aafes.starsettler.control.CassandraSessionFactory.seedHost"));
//    }
//    
//     @Test
//     public void testFacility() {
//         
//         FacilityDAO facilityDAO = new FacilityDAO();
//         
//         facilityDAO.setCassandraSessionFactory(csf);
//         facilityDAO.postConstruct();
//         Facility inputFacility = new Facility();
//         inputFacility.setFacility("123456789");
//         inputFacility.setStrategy("Ecomm");
//         inputFacility.setUuid("This is a test UUID");
//         facilityDAO.save(inputFacility);
//         Facility outputFacility = facilityDAO.get("This is a test UUID");
//         assertEquals(outputFacility.getFacility(),inputFacility.getFacility());
//         csf.close();
//     }
//     
//     @Test
//     public void testTransaction() {
//         
//         TransactionDAO  transactionDAO = new TransactionDAO();
//         transactionDAO.setCassandraSessionFactory(csf);
//         transactionDAO.postConstruct();
//         SettleEntity settleEntity = new SettleEntity();
//         settleEntity.setResponseDate("20170403");
//         settleEntity.setResponseReasonCode("100");
//         settleEntity.setAuthoriztionCode("100");
//         transactionDAO.find(settleEntity);
//         
////         Transaction inputTransaction = new Transaction();
////         inputTransaction.setIdentityUuid("c47e4366-fe72-473b-b114-523e8de5641f");
////         String authHour ="";
////         if (getSystemDateTime() != null) {
////            authHour = getSystemDateTime().substring(0, 8);
////            inputTransaction.setAuthHour(authHour);
////        }
////         String orderNumber = "123458";
////         inputTransaction.setOrderNumber(orderNumber);
////         inputTransaction.setAccount("123456789");
////         TranRepository repository = new TranRepository();
////         repository.setMaskAccount("true");
////         repository.setTransactionDAO(transactionDAO);
////         repository.save(inputTransaction);
////         Transaction outputTransaction = repository.find(authHour,orderNumber);
////         assertEquals(outputTransaction.getIdentityUuid(),inputTransaction.getIdentityUuid());
//         
//           assertEquals(true, true);
//         
//     }
//     
//     @Test
//    public void SettleMessageRepository() throws Exception {
//        
//        SettleMessageDAO settleMessageDAO = new SettleMessageDAO();
//        SettleMessageRepository repository = new SettleMessageRepository();
//        FacilityDAO dao = new FacilityDAO();
//       List<SettleEntity> settleEntityList = new ArrayList<SettleEntity>();
//       TransactionDAO tranDao = new TransactionDAO();
//       SettleEntity settleEntity = new SettleEntity();
//       repository.setFacilityDAO(dao);
//       repository.setSettleMessageDAO(settleMessageDAO);
//       repository.setTransactionDAO(tranDao);
//       String Processdate = "";
//       String SettleStatus = "READY";
//        repository.save(settleEntityList);
//         repository.updateBatchRef(settleEntityList, Processdate);
//        repository.updateFdmsData(settleEntityList, SettleStatus);
//        repository.updateStatus(settleEntityList, SettleStatus);
//       
//         assertEquals(true, true);
//        
//        
//                
//    }
//     
//     
//     @Test
//    public void getFDMSData() throws Exception {
//        List<SettleEntity> fdmsData = new ArrayList<SettleEntity>();
//        SettleMessageDAO repository = new SettleMessageDAO();
//        repository.setCassandraSessionFactory(csf);
//        repository.postConstruct();
//        String Processdate = "";
//        String SettleStatus = "READY";
//        repository.save(fdmsData);
//        repository.updateBatchRef(fdmsData, Processdate);
//        repository.updateFdmsData(fdmsData, SettleStatus);
//        repository.updateStatus(fdmsData, SettleStatus);
//        repository.getBatchId();
//        repository.getFDMSData(Processdate, SettleStatus);
//         System.out.println("repository.getFDMSData(Processdate, SettleStatus)"+repository.getFDMSData(Processdate, SettleStatus));
//        repository.getVisionData(Processdate, SettleStatus);
//        repository.getAll(Processdate, SettleStatus);
//        
//         assertEquals(true, true);
//
//    }
//
//    public List<SettleEntity> getSettleEntityElements() throws DatatypeConfigurationException {
//       List<SettleEntity> fdmsData = new ArrayList<SettleEntity>();
//       
//       SettleEntity settleEntity = new SettleEntity();
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
//        settleEntity.setSettlestatus("Ready");
//        settleEntity.setSettleDate("20170411");
//        settleEntity.setSequenceId("123");
//        
//        
//        fdmsData.add(settleEntity);
//
//        
//       
//        return fdmsData;
//    }
//    
//    
//    @After
//     public void closeConnection()
//     {
//         if(csf != null) {
//             csf.close();
//         }
//     }
//    
//}
