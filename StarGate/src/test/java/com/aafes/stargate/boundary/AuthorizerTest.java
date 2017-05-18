///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.aafes.stargate.boundary;
//
//import com.aafes.credit.Message;
//import com.aafes.stargate.authorizer.entity.Facility;
//import com.aafes.stargate.authorizer.entity.Transaction;
//import com.aafes.stargate.control.Authorizer;
//import com.aafes.stargate.control.AuthorizerException;
//import com.aafes.stargate.control.Configurator;
//import com.aafes.stargate.control.TranRepository;
//import com.aafes.stargate.dao.FacilityDAO;
//import com.aafes.stargate.gateway.vision.VisionGateway;
//import com.aafes.stargate.util.ResponseType;
//import com.aafes.stargate.util.StrategyType;
//import java.io.StringReader;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import javax.ws.rs.ProcessingException;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Unmarshaller;
//import javax.xml.datatype.DatatypeConfigurationException;
//import static org.junit.Assert.assertEquals;
//import org.junit.Test;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
///**
// *
// * @author ghadiyamp
// */
//public class AuthorizerTest {
//
//    @Test
//    public void testValidInputXMLAuthorize() throws Exception {
//
//        String requestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
//                + "<!-- Example of an MILSTAR Keyed at POS Request  --> \n"
//                + "<cm:Message\n"
//                + "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' \n"
//                + "xmlns:cm='http://www.aafes.com/credit'\n"
//                + "xsi:schemaLocation='http://www.aafes.com/credit file:/home/simulator/CreditMessage12S1.xsd'\n"
//                + "TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"0\" FixVersion=\"0\">\n"
//                + "<cm:Header>\n"
//                + "        <cm:IdentityUUID>c47e4366-fe72-473b-b114-523e8de5641f</cm:IdentityUUID>\n"
//                + "        <cm:LocalDateTime>2016-12-07T10:12:01</cm:LocalDateTime>\n"
//                + "        <cm:SettleIndicator>true</cm:SettleIndicator>\n"
//                + "        <cm:OrderNumber>55548741538</cm:OrderNumber>\n"
//                + "        <cm:transactionId>1536</cm:transactionId>\n"
//                + "        <cm:termId>05</cm:termId>\n"
//                + "</cm:Header>\n"
//                + "<cm:Request RRN=\"fZQX3Uw7e52v\">\n"
//                + "<cm:Media>Milstar</cm:Media>\n"
//                + "<cm:RequestType>Sale</cm:RequestType>\n"
//                + "<cm:InputType>Keyed</cm:InputType>\n"
//                + "<cm:Pan>Pan</cm:Pan>\n"
//                + "<cm:pumpPrice>1234</cm:pumpPrice>\n"
//                + "<cm:Account>60194400000320</cm:Account>\n"
//                + "<cm:voidflag>123</cm:voidflag>\n"
//                + "<cm:Expiration>2112</cm:Expiration>\n"
//                + "<cm:CardVerificationValue>313</cm:CardVerificationValue>\n"
//                + "<cm:AmountField>99999.99</cm:AmountField>\n"
//                + "<cm:PlanNumbers>\n"
//                + "<cm:PlanNumber>10001</cm:PlanNumber>\n"
//                + "</cm:PlanNumbers> \n"
//                + "<cm:ZipCode>12345</cm:ZipCode>\n"
//                + "<cm:DescriptionField>SALE</cm:DescriptionField>\n"
//                + "<cm:AddressVerificationService>"
//                + "<cm:CardHolderName>123</cm:CardHolderName>"
//                + "<cm:BillingAddress>1222</cm:BillingAddress>"
//                + "<cm:BillingCountryCode>US</cm:BillingCountryCode>"
//                + "<cm:BillingZipCode>12345</cm:BillingZipCode>"
//                + "<cm:Email>@</cm:Email>"
//                + "<cm:BillingPhone>1122334455</cm:BillingPhone>"
//                + "</cm:AddressVerificationService>"
//                + "</cm:Request>\n"
//                + "</cm:Message>";
//
//        Message creditMessage = this.unmarshalCreditMessage(requestXML);
//        Authorizer authorizer = new Authorizer();
//
//        FacilityDAO facilityDAO = mock(FacilityDAO.class);
//        Facility facility = new Facility();
//        facility.setFacility("1234567890");
//        facility.setStrategy(StrategyType.ECOMM);
//        when(facilityDAO.get("c47e4366-fe72-473b-b114-523e8de5641f")).
//                thenReturn(facility);
//        authorizer.setFacilityDAO(facilityDAO);
//
//        TranRepository tr = mock(TranRepository.class);
//        Transaction transaction = new Transaction();
//        transaction.setResponseType(ResponseType.APPROVED);
//        transaction.setReasonCode("000");
//        String authHour = getSystemDateTime().substring(0, 8);
//        when(tr.find("c47e4366-fe72-473b-b114-523e8de5641f",
//                         "fZQX3Uw7e52v", "Sale")).thenReturn(transaction);      
//        authorizer.setTranRepository(tr);
//
//        Message result = authorizer.authorize(creditMessage);
//        assertEquals(ResponseType.APPROVED, result.getResponse().get(0).
//                getResponseType());
//    }
//
//    @Test
//    public void testInValidInputXMLAuthorize() throws Exception {
//
//        String requestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
//                + "<!-- Example of an MILSTAR Keyed at POS Request  --> \n"
//                + "<cm:Message\n"
//                + "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' \n"
//                + "xmlns:cm='http://www.aafes.com/credit'\n"
//                + "xsi:schemaLocation='http://www.aafes.com/credit file:/home/simulator/CreditMessage12S1.xsd'\n"
//                + "TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"0\" FixVersion=\"0\">\n"
//                + "<cm:Header>\n"
//                + "        <cm:IdentityUUID>c47e4366-fe72-473b-b114-523e8de5641g</cm:IdentityUUID>\n"
//                + "        <cm:LocalDateTime>2016-12-07T10:12:01</cm:LocalDateTime>\n"
//                + "        <cm:SettleIndicator>false</cm:SettleIndicator>\n"
//                + "        <cm:OrderNumber>55548741536</cm:OrderNumber>\n"
//                + "        <cm:transactionId>1536</cm:transactionId>\n"
//                + "        <cm:termId>05</cm:termId>\n"
//                + "</cm:Header>\n"
//                + "<cm:Request RRN=\"fZQX3Uw7e52v\">\n"
//                + "<cm:Media>Milstar</cm:Media>\n"
//                + "<cm:voidflag></cm:voidflag>\n"
//                + "<cm:pumpPrice></cm:pumpPrice>\n"
//                + "<cm:RequestType>Sale</cm:RequestType>\n"
//                + "<cm:InputType>Keyed</cm:InputType>\n"
//                + "<cm:Pan>Pan</cm:Pan>\n"
//                + "<cm:Account>60194400000320</cm:Account>\n"
//                + "<cm:Expiration>2112</cm:Expiration>\n"
//                + "<cm:CardVerificationValue>313</cm:CardVerificationValue>\n"
//                + "<cm:AmountField>25.00</cm:AmountField>\n"
//                + "<cm:PlanNumbers>\n"
//                + "<cm:PlanNumber>10001</cm:PlanNumber>\n"
//                + "</cm:PlanNumbers> \n"
//                + "<cm:ZipCode>12345</cm:ZipCode>\n"
//                + "<cm:DescriptionField>SALE</cm:DescriptionField>\n"
//                + "<cm:AddressVerificationService>"
//                + "<cm:CardHolderName>123</cm:CardHolderName>"
//                + "<cm:BillingAddress>1222</cm:BillingAddress>"
//                + "<cm:BillingCountryCode>US</cm:BillingCountryCode>"
//                + "<cm:BillingZipCode>12345</cm:BillingZipCode>"
//                + "<cm:Email>@</cm:Email>"
//                + "<cm:BillingPhone>1122334455</cm:BillingPhone>"
//                + "</cm:AddressVerificationService>"
//                + "</cm:Request>\n"
//                + "</cm:Message>";
//
//        Message creditMessage = this.unmarshalCreditMessage(requestXML);
//        Authorizer authorizer = new Authorizer();
//       Configurator configurator = new Configurator();
//       authorizer.setConfigurator(configurator);
//
//      FacilityDAO facilityDAO = mock(FacilityDAO.class);
//
//        when(facilityDAO.get("c47e4366-fe72-473b-b114-523e8de5641g")).
//                thenReturn(null);
//        authorizer.setFacilityDAO(facilityDAO);
//
//
//
//     Message result = authorizer.authorize(creditMessage);
//     assertEquals(ResponseType.DECLINED, result.getResponse().get(0).
//              getResponseType());
//
//    }
//   
//    private Message unmarshalCreditMessage(String content) {
//        Message request = new Message();
//        try {
//            StringReader reader = new StringReader(content);
//            JAXBContext jc = JAXBContext.newInstance(Message.class);
//            Unmarshaller unmarshaller = jc.createUnmarshaller();
//            JAXBContext jaxbContext = JAXBContext.newInstance(Message.class);
//            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//            request = (Message) jaxbUnmarshaller.unmarshal(reader);
//        } catch (JAXBException ex) {
//            System.out.println(ex.toString());
//        }
//        return request;
//    }
//
//    private String getSystemDateTime() {
//        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
//        Date date = new Date();
//        String ts = dateFormat.format(date);
//        return ts;
//    }
//
//    public Transaction getKeyedMILSTARSaleRequestatPOS() throws DatatypeConfigurationException {
//        Transaction transaction = new Transaction();
//        transaction.setIdentityUuid("c47e4366-fe72-473b-b114-523e8de5641f");
//        transaction.setSettleIndicator("false");
//        transaction.setOrderNumber("55548741536");
//        transaction.setTransactionId("1536");
//        transaction.setTermId(05);
//        transaction.setComment("");
//        transaction.setRrn("fZQX3Uw7e52v");
//        transaction.setMedia("MilStar");
//        transaction.setRequestType("Sale");
//        transaction.setInputType("Keyed");
//        transaction.setPan("12121221121212");
//        transaction.setAccount("6019440000000320");
//        transaction.setExpiration("2112");
//        transaction.setCvv("313");
//        transaction.setAmount((long) 25.00);
//        transaction.setCardReferenceID("12345");
//        transaction.setPlanNumber("10001");
//        transaction.setBillingZipCode("12345");
//        transaction.setShippingZipCode("12345");
//        transaction.setDescriptionField("SALE");
//        transaction.setFacility("123456789");
//        transaction.setComment("Approved");
//        return transaction;
//    }
//}
