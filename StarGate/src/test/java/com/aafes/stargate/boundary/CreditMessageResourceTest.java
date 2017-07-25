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
//import com.aafes.stargate.control.Configurator;
//import com.aafes.stargate.control.TranRepository;
//import com.aafes.stargate.dao.FacilityDAO;
//import com.aafes.stargate.util.ResponseType;
//import com.aafes.stargate.util.StrategyType;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import static org.junit.Assert.assertEquals;
//import org.junit.Test;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
///**
// *
// * @author ghadiyamp
// */
//public class CreditMessageResourceTest {
//
//    public CreditMessageResourceTest() {
//    }
//
//    @Test
//    public void postInvalidXml() throws Exception {
//
//        String requestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
//                + "<!-- Example of an MILSTAR Keyed at POS Request  --> \n"
//                + "<cm:Message\n"
//                + "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' \n"
//                + "xmlns:cm='http://www.aafes.com/credit'\n"
//                + "xsi:schemaLocation='http://www.aafes.com/credit file:/home/simulator/CreditMessageGSA.xsd'\n"
//                + "TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"0\" FixVersion=\"0\">\n"
//                + "<cm:Header>\n"
//                + "        <cm:IdentityUUID></cm:IdentityUUID>\n"
//                + "        <cm:LocalDateTime>2016-12-07T10:12:01</cm:LocalDateTime>\n"
//                + "        <cm:SettleIndicator>false</cm:SettleIndicator>\n"
//                + "        <cm:OrderNumber>55548741536</cm:OrderNumber>\n"
//                + "        <cm:transactionId>1536</cm:transactionId>\n"
//                + "        <cm:termId>05</cm:termId>\n"
//                + "</cm:Header>\n"
//                + "<cm:Request RRN=\"fZQX3Uw7e52v\">\n"
//                + "<cm:Media>Milstar</cm:Media>\n"
//                + "<cm:RequestType>Sale</cm:RequestType>\n"
//                + "<cm:InputType>Keyed</cm:InputType>\n"
//                + "<cm:Pan>Pan</cm:Pan>\n"
//                + "<cm:Account>60194400000320</cm:Account>\n"
//                + "<cm:Expiration>2113</cm:Expiration>\n"
//                + "<cm:CardVerificationValue>313</cm:CardVerificationValue>\n"
//                + "<cm:AmountField>25.00</cm:AmountField>\n"
//                + "<cm:PlanNumbers>\n"
//                + "<cm:PlanNumber>10001</cm:PlanNumber>\n"
//                + "</cm:PlanNumbers> \n"
//                + "<cm:ZipCode>12345</cm:ZipCode>\n"
//                + "<cm:DescriptionField>SALE</cm:DescriptionField>\n"
//                + "</cm:Request>\n"
//                + "</cm:Message>";
//
//        String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ErrorInformation><Error>Invalid XML</Error>"
//                + "</ErrorInformation>";
//        CreditMessageResource creditMessageResource = new CreditMessageResource();
//        String responseFromCM = creditMessageResource.postXml(requestXML);
//
//        assertEquals(expectedResponse, responseFromCM);
//
//    }
//
//    @Test
//    public void postInValidatedXML() throws Exception {
//
//        String requestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
//                + "<!-- Example of an MILSTAR Keyed at POS Request  --> \n"
//                + "<cm:Message\n"
//                + "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' \n"
//                + "xmlns:cm='http://www.aafes.com/credit'\n"
//                + "xsi:schemaLocation='http://www.aafes.com/credit file:/home/simulator/CreditMessageGSA.xsd'\n"
//                + "TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"0\" FixVersion=\"0\">\n"
//                + "<cm:Header>\n"
//                + "        <cm:IdentityUUID></cm:IdentityUUID>\n"
//                + "        <cm:LocalDateTime>2016-12-07T10:12:01</cm:LocalDateTime>\n"
//                + "        <cm:SettleIndicator>false</cm:SettleIndicator>\n"
//                + "        <cm:OrderNumber>55548741536</cm:OrderNumber>\n"
//                + "        <cm:transactionId>1536</cm:transactionId>\n"
//                + "        <cm:termId>05</cm:termId>\n"
//                + "</cm:Header>\n"
//                + "<cm:Request RRN=\"fZQX3Uw7e52v\">\n"
//                + "<cm:Media>Milstar</cm:Media>\n"
//                + "<cm:RequestType>Sale</cm:RequestType>\n"
//                + "<cm:InputType>Keyed</cm:InputType>\n"
//                + "<cm:Pan>Pan</cm:Pan>\n"
//                + "<cm:Account>60194400000320</cm:Account>\n"
//                + "<cm:Expiration>2113</cm:Expiration>\n"
//                + "<cm:CardVerificationValue>313</cm:CardVerificationValue>\n"
//                + "<cm:AmountField>25.00</cm:AmountField>\n"
//                + "<cm:PlanNumbers>\n"
//                + "<cm:PlanNumber>10001</cm:PlanNumber>\n"
//                + "</cm:PlanNumbers> \n"
//                + "<cm:ZipCode>12345</cm:ZipCode>\n"
//                + "<cm:DescriptionField>SALE</cm:DescriptionField>\n"
//                + "</cm:Request>\n";
//
//        String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ErrorInformation><Error>Invalid XML</Error>"
//                + "</ErrorInformation>";
//        CreditMessageResource creditMessageResource = new CreditMessageResource();
//        String responseFromCM = creditMessageResource.postXml(requestXML);
//
//        assertEquals(expectedResponse, responseFromCM);
//
//    }
//
//    @Test
//    public void postValidXml() throws Exception {
//
//        String requestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
//                + "<cm:Message TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"0\" FixVersion=\"0\" xmlns:cm=\"http://www.aafes.com/credit\">\n"
//                + "    <cm:Header>\n"
//                + "        <cm:IdentityUUID>c47e4366-fe72-473b-b114-523e8de5641f</cm:IdentityUUID>\n"
//                + "        <cm:LocalDateTime>2017-01-07T10:12:01</cm:LocalDateTime>\n"
//                + "        <cm:SettleIndicator>false</cm:SettleIndicator>\n"
//                + "        <cm:OrderNumber>AF987123528912</cm:OrderNumber>\n"
//                + "        <cm:transactionId>6740</cm:transactionId>\n"
//                + "        <cm:termId>20</cm:termId>\n"
//                + "        <cm:Comment>Test</cm:Comment>\n"
//                + "        <cm:CustomerID>45017633122</cm:CustomerID>\n"
//                + "    </cm:Header>\n"
//                + "    <cm:Request RRN=\"RRNPG4083672\">\n"
//                + "        <cm:Media>Amex</cm:Media>\n"
//                + "        <cm:RequestType>Sale</cm:RequestType>\n"
//                + "        <cm:InputType>Keyed</cm:InputType>\n"
//                + "        <cm:Pan>Pan</cm:Pan>\n"
//                + "        <cm:Account>379598301271009</cm:Account>\n"
//                + "        <cm:Expiration>2512</cm:Expiration>\n"
//                + "        <cm:CardVerificationValue>837</cm:CardVerificationValue>\n"
//                + "        <cm:AmountField>10.02</cm:AmountField>\n"
//                + "        <cm:PlanNumbers>\n"
//                + "            <cm:PlanNumber>10001</cm:PlanNumber>\n"
//                + "        </cm:PlanNumbers>\n"
//                + "        <cm:DescriptionField>SALE</cm:DescriptionField>\n"
//                + "        <cm:AddressVerificationService>\n"
//                + "            <cm:CardHolderName>Test Amex AAS Code</cm:CardHolderName>\n"
//                + "            <cm:BillingAddress>abcd</cm:BillingAddress>\n"
//                + "            <cm:BillingCountryCode>US</cm:BillingCountryCode>\n"
//                + "            <cm:BillingZipCode>65202</cm:BillingZipCode>\n"
//                + "            <cm:Email>johndoe@johndoe.com</cm:Email>\n"
//                + "            <cm:BillingPhone>8052776424</cm:BillingPhone>\n"
//                + "            <cm:ShippingPhone>00000</cm:ShippingPhone>\n"
//                + "        </cm:AddressVerificationService>\n"
//                + "    </cm:Request>\n"
//                + "</cm:Message>";
//
//        CreditMessageResource creditMessageResource = new CreditMessageResource();
//        Authorizer authorizer = mock(Authorizer.class);
//        when(authorizer.authorize(any())).thenReturn(new Message());
//        String responseFromCM = creditMessageResource.postXml(requestXML);
//        String expectedResponse = "";
//
//        assertEquals(expectedResponse, responseFromCM);
//
//    }
//
//    private String getSystemDateTime() {
//        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
//        Date date = new Date();
//        String ts = dateFormat.format(date);
//        return ts;
//    }
//
//}
