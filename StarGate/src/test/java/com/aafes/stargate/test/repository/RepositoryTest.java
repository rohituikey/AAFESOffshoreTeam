///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.aafes.stargate.test.repository;
//
//import com.aafes.stargate.authorizer.entity.Facility;
//import com.aafes.stargate.authorizer.entity.Transaction;
//import com.aafes.stargate.control.CassandraSessionFactory;
//import com.aafes.stargate.control.TranRepository;
//import com.aafes.stargate.dao.FacilityDAO;
//import com.aafes.stargate.dao.TransactionDAO;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Properties;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.junit.After;
//import static org.junit.Assert.assertEquals;
//import org.junit.Before;
//import org.junit.Test;
//
///**
// *
// * @author ganjis
// */
//public class RepositoryTest {
//    
//    private CassandraSessionFactory csf;
//   
//    @Before
//    public void setUp()
//    {
//         Properties p = new Properties();
//        try {
//            p.load(new FileReader(new File("src/main/resources/stargate.properties")));
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(RepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(RepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//         csf = new CassandraSessionFactory();
//         csf.setSeedHost(p.getProperty("com.aafes.stargate.control.CassandraSessionFactory.seedHost"));
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
//         Transaction inputTransaction = new Transaction();
//         inputTransaction.setIdentityUuid("c47e4366-fe72-473b-b114-523e8de5641f");
//         inputTransaction.setRrn("fZQX3Uw7e52v");
//         inputTransaction.setRequestType("Sale");
//         String authHour ="";
//         if (getSystemDateTime() != null) {
//            authHour = getSystemDateTime().substring(0, 8);
//            inputTransaction.setAuthHour(authHour);
//        }
//         String orderNumber = "123458";
//         inputTransaction.setOrderNumber(orderNumber);
//         inputTransaction.setAccount("123456789");
//         TranRepository repository = new TranRepository();
//         repository.setTransactionDAO(transactionDAO);
//         repository.save(inputTransaction);
//         repository.saveAndUpdate(inputTransaction, inputTransaction);
//         Transaction outputTransaction = repository.find("c47e4366-fe72-473b-b114-523e8de5641f","fZQX3Uw7e52v", "Sale");
//         assertEquals(outputTransaction.getIdentityUuid(),inputTransaction.getIdentityUuid());
//     }
//     
//     @After
//     public void closeConnection()
//     {
//         if(csf != null) {
//             csf.close();
//         }
//     }
//     
//     private String getSystemDateTime() {
//        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
//        Date date = new Date();
//        String ts = dateFormat.format(date);
//        return ts;
//    }
//}
