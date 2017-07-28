/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.test;

import com.aafes.stargate.authorizer.BaseStrategy;
import com.aafes.stargate.authorizer.BaseStrategyFactory;
import com.aafes.stargate.authorizer.RetailStrategy;
import com.aafes.stargate.authorizer.entity.Facility;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Authorizer;
import com.aafes.stargate.control.CassandraSessionFactory;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.control.TranRepository;
import com.aafes.stargate.dao.FacilityDAO;
import com.aafes.stargate.dao.TransactionDAO;
import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.gateway.GatewayFactory;
import com.aafes.stargate.gateway.vision.simulator.VisionGatewayStub;
import com.aafes.stargate.util.StrategyType;
import com.aafes.starsettler.imported.SettleEntity;
import com.aafes.starsettler.imported.SettleMessageDAO;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.LoggerFactory;
import com.aafes.credit.Message;
import com.aafes.stargate.util.ResponseType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author burangir
 */
public class DecaReversalSaleTest {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(DecaReversalSaleTest.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = DecaReversalSaleTest.this.getClass().getSimpleName();

    Authorizer authorizer;
    Configurator configurator;
    FacilityDAO facilityDAO;
    Facility facility;
    TranRepository tr;
    TransactionDAO td;
    Mapper mapper;
    Mapper mapper1;
    CassandraSessionFactory factory;
    Session session;
    ResultSet resultSet;
    RetailStrategy retailStrategy;
    BaseStrategyFactory bsf;
    GatewayFactory gatewayFactory;
    BaseStrategy bs;
    VisionGatewayStub visionGatewaySimulator;
    SettleMessageDAO settleMessageDAO;

    String requestXMLSale = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><cm:Message TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\" xmlns:cm=\"http://www.aafes.com/credit\"><cm:Header><cm:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</cm:IdentityUUID><cm:LocalDateTime>2017-07-14T13:41:00</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>1234567</cm:OrderNumber><cm:transactionId>10000001</cm:transactionId><cm:termId>12</cm:termId><cm:Comment>Approved</cm:Comment></cm:Header><cm:Request RRN=\"200000000001\"><cm:Media>Milstar</cm:Media><cm:RequestType>Sale</cm:RequestType><cm:InputType>Keyed</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue><cm:TrackData1>%B6019450000289697^Milstar RET0001^2009000000000000100000000000000?</cm:TrackData1><cm:AmountField>2500</cm:AmountField><cm:PlanNumbers><cm:PlanNumber>10001</cm:PlanNumber></cm:PlanNumbers><cm:DescriptionField>SALE</cm:DescriptionField><cm:AddressVerificationService><cm:CardHolderName>John Doe</cm:CardHolderName><cm:BillingAddress1>1222</cm:BillingAddress1><cm:BillingCountryCode>US</cm:BillingCountryCode><cm:BillingZipCode>12345</cm:BillingZipCode><cm:Email>johndoe@kk.com</cm:Email><cm:BillingPhone>1122334455</cm:BillingPhone><cm:ShippingPhone>1122334455</cm:ShippingPhone></cm:AddressVerificationService></cm:Request></cm:Message>";
    String requestXMLReversalSale = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><cm:Message TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\" xmlns:cm=\"http://www.aafes.com/credit\"><cm:Header><cm:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</cm:IdentityUUID><cm:LocalDateTime>2017-07-14T11:08:00</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>1234567</cm:OrderNumber><cm:transactionId>10000001</cm:transactionId><cm:termId>12</cm:termId><cm:Comment>Approved</cm:Comment></cm:Header><cm:Request RRN=\"200000000001\"><cm:Media>Milstar</cm:Media><cm:Reversal>Sale</cm:Reversal><cm:InputType>Keyed</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue><cm:TrackData1>%B6019450000289697^Milstar RET0001^2009000000000000100000000000000?</cm:TrackData1><cm:AmountField>2500</cm:AmountField><cm:PlanNumbers><cm:PlanNumber>10001</cm:PlanNumber></cm:PlanNumbers><cm:DescriptionField>SALE</cm:DescriptionField><cm:AddressVerificationService><cm:CardHolderName>John Doe</cm:CardHolderName><cm:BillingAddress1>1222</cm:BillingAddress1><cm:BillingCountryCode>US</cm:BillingCountryCode><cm:BillingZipCode>12345</cm:BillingZipCode><cm:Email>johndoe@kk.com</cm:Email><cm:BillingPhone>1122334455</cm:BillingPhone><cm:ShippingPhone>1122334455</cm:ShippingPhone></cm:AddressVerificationService></cm:Request></cm:Message>";
    String uuid = "0ee1c509-2c70-4bcd-b261-f94f1fe6c43b";

    @Before
    public void setDataForTesting() {
        authorizer = new Authorizer();
        configurator = new Configurator();
        authorizer.setConfigurator(configurator);

        facilityDAO = mock(FacilityDAO.class);
        facility = new Facility();
        facility.setDeviceType("RPOS");
        facility.setFacility("3740152100");
        facility.setStrategy("Deca");
        facility.setTokenBankName("Deca006");

        when(facilityDAO.get(uuid)).thenReturn(facility);
        authorizer.setFacilityDAO(facilityDAO);

        tr = new TranRepository();
        td = new TransactionDAO();

        factory = new CassandraSessionFactory();
        factory.setSeedHost("localhost");
        factory.connect();

        session = factory.getSession();
        mapper = new MappingManager(session).mapper(Transaction.class);
        td.setMapper(mapper);
        tr.setTransactionDAO(td);
        authorizer.setTranRepository(tr);

        retailStrategy = new RetailStrategy();
        settleMessageDAO = new SettleMessageDAO();
        mapper1 = new MappingManager(session).mapper(SettleEntity.class);
        settleMessageDAO.setMapper(mapper1);
        settleMessageDAO.setCassandraSessionFactory(factory);
        retailStrategy.setSettleMessageDAO(settleMessageDAO);
        retailStrategy.setConfigurator(configurator);
        bsf = new BaseStrategyFactory();
        gatewayFactory = new GatewayFactory();
        gatewayFactory.setEnableStub("true");
        visionGatewaySimulator = new VisionGatewayStub();
        gatewayFactory.setVisionGatewayStub(visionGatewaySimulator);
        bsf.setRetailStrategy(retailStrategy);
        bs = bsf.findStrategy(StrategyType.DECA);
        //bsf.setRetailStrategy(retailStrategy);
        bs.setGatewayFactory(gatewayFactory);

        authorizer.setBaseStrategyFactory(bsf);
    }

    //@Ignore
    @Test
    public void testNoPreAuthorizationForReversal() {
        sMethodName = "testNoPreAuthorizationForReversal";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        Message creditMessage = this.unmarshalCreditMessage(requestXMLReversalSale);
        Message result = authorizer.authorize(creditMessage);
        clearGlobalVariables();
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        assertEquals("NO_AUTHORIZATION_FOUND_FOR_REVERSAL", result.getResponse().get(0).getDescriptionField());
    }

//    @Ignore
    @Test
    public void testProcessRequest() {
        sMethodName = "testProcessRequest";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        Message creditMessage = this.unmarshalCreditMessage(requestXMLSale);
        Message result = authorizer.authorize(creditMessage);
        clearGlobalVariables();
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        assertEquals(ResponseType.APPROVED, result.getResponse().get(0).getResponseType());
    }

    //@Ignore
    @Test
    public void testForReversal() {
        sMethodName = "testForReversal";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);

        String requestXMLSaleLocal = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><cm:Message TypeCode=\"Request\" "
                + "MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\" xmlns:cm=\"http://www.aafes.com/credit\"><cm:Header>"
                + "<cm:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</cm:IdentityUUID><cm:LocalDateTime>2017-07-14T13:41:00"
                + "</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator>"
                + "<cm:OrderNumber>1000001</cm:OrderNumber>"
                + "<cm:transactionId>40000001</cm:transactionId><cm:termId>12</cm:termId><cm:Comment>Approved</cm:Comment></cm:Header>"
                + "<cm:Request RRN=\"300000000001\"><cm:Media>Milstar</cm:Media><cm:RequestType>Sale</cm:RequestType>"
                + "<cm:InputType>Keyed</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account>"
                + "<cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue>"
                + "<cm:TrackData1>%B6019450000289697^Milstar RET0001^2009000000000000100000000000000?</cm:TrackData1>"
                + "<cm:AmountField>25</cm:AmountField><cm:PlanNumbers><cm:PlanNumber>10001</cm:PlanNumber></cm:PlanNumbers>"
                + "<cm:DescriptionField>SALE</cm:DescriptionField><cm:AddressVerificationService>"
                + "<cm:CardHolderName>John Doe</cm:CardHolderName><cm:BillingAddress1>1222</cm:BillingAddress1>"
                + "<cm:BillingCountryCode>US</cm:BillingCountryCode><cm:BillingZipCode>12345</cm:BillingZipCode>"
                + "<cm:Email>johndoe@kk.com</cm:Email><cm:BillingPhone>1122334455</cm:BillingPhone>"
                + "<cm:ShippingPhone>1122334455</cm:ShippingPhone></cm:AddressVerificationService></cm:Request></cm:Message>";
        Message creditMessage = this.unmarshalCreditMessage(requestXMLSaleLocal);
        Message result = authorizer.authorize(creditMessage);

        if (ResponseType.APPROVED.equals(result.getResponse().get(0).getResponseType())) {
            requestXMLReversalSale = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><cm:Message TypeCode=\"Request\" "
                    + "MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\" xmlns:cm=\"http://www.aafes.com/credit\"><cm:Header>"
                    + "<cm:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</cm:IdentityUUID><cm:LocalDateTime>2017-07-14T11:08:00"
                    + "</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator>"
                    + "<cm:OrderNumber>1000001</cm:OrderNumber>"
                    + "<cm:transactionId>40000001</cm:transactionId><cm:termId>12</cm:termId><cm:Comment>Approved</cm:Comment>"
                    + "</cm:Header><cm:Request RRN=\"300000000001\"><cm:Media>Milstar</cm:Media><cm:Reversal>Sale</cm:Reversal>"
                    + "<cm:InputType>Keyed</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account>"
                    + "<cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue>"
                    + "<cm:TrackData1>%B6019450000289697^Milstar RET0001^2009000000000000100000000000000?</cm:TrackData1>"
                    + "<cm:AmountField>25</cm:AmountField><cm:PlanNumbers><cm:PlanNumber>10001</cm:PlanNumber></cm:PlanNumbers>"
                    + "<cm:DescriptionField>SALE</cm:DescriptionField><cm:AddressVerificationService>"
                    + "<cm:CardHolderName>John Doe</cm:CardHolderName><cm:BillingAddress1>1222</cm:BillingAddress1>"
                    + "<cm:BillingCountryCode>US</cm:BillingCountryCode><cm:BillingZipCode>12345</cm:BillingZipCode>"
                    + "<cm:Email>johndoe@kk.com</cm:Email><cm:BillingPhone>1122334455</cm:BillingPhone>"
                    + "<cm:ShippingPhone>1122334455</cm:ShippingPhone></cm:AddressVerificationService></cm:Request></cm:Message>";

            creditMessage = this.unmarshalCreditMessage(requestXMLReversalSale);
            result = authorizer.authorize(creditMessage);
            clearGlobalVariables();
            LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
            assertEquals(ResponseType.APPROVED, result.getResponse().get(0).getResponseType());
        } else {
            Assert.fail("Sale Request failed!!! Reversal request not processed!!!");
        }
    }

    //@Ignore
    @Test
    public void testForALreadyReversed() {
        sMethodName = "testForALreadyReversed";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);

        String requestXMLSaleLocal = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><cm:Message TypeCode=\"Request\" "
                + "MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\" xmlns:cm=\"http://www.aafes.com/credit\"><cm:Header>"
                + "<cm:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</cm:IdentityUUID><cm:LocalDateTime>2017-07-14T13:41:00"
                + "</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator>"
                + "<cm:OrderNumber>1000002</cm:OrderNumber>"
                + "<cm:transactionId>40000002</cm:transactionId><cm:termId>12</cm:termId><cm:Comment>Approved</cm:Comment></cm:Header>"
                + "<cm:Request RRN=\"300000000002\"><cm:Media>Milstar</cm:Media><cm:RequestType>Sale</cm:RequestType>"
                + "<cm:InputType>Keyed</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account>"
                + "<cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue>"
                + "<cm:TrackData1>%B6019450000289697^Milstar RET0001^2009000000000000100000000000000?</cm:TrackData1>"
                + "<cm:AmountField>25</cm:AmountField><cm:PlanNumbers><cm:PlanNumber>10001</cm:PlanNumber></cm:PlanNumbers>"
                + "<cm:DescriptionField>SALE</cm:DescriptionField><cm:AddressVerificationService>"
                + "<cm:CardHolderName>John Doe</cm:CardHolderName><cm:BillingAddress1>1222</cm:BillingAddress1>"
                + "<cm:BillingCountryCode>US</cm:BillingCountryCode><cm:BillingZipCode>12345</cm:BillingZipCode>"
                + "<cm:Email>johndoe@kk.com</cm:Email><cm:BillingPhone>1122334455</cm:BillingPhone>"
                + "<cm:ShippingPhone>1122334455</cm:ShippingPhone></cm:AddressVerificationService></cm:Request></cm:Message>";
        Message creditMessage = this.unmarshalCreditMessage(requestXMLSaleLocal);
        Message result = authorizer.authorize(creditMessage);

         if (ResponseType.APPROVED.equals(result.getResponse().get(0).getResponseType())) {
            requestXMLReversalSale = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><cm:Message TypeCode=\"Request\" "
                    + "MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\" xmlns:cm=\"http://www.aafes.com/credit\"><cm:Header>"
                    + "<cm:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</cm:IdentityUUID><cm:LocalDateTime>2017-07-14T11:08:00"
                    + "</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator>"
                    + "<cm:OrderNumber>1000002</cm:OrderNumber>"
                    + "<cm:transactionId>40000002</cm:transactionId><cm:termId>12</cm:termId><cm:Comment>Approved</cm:Comment>"
                    + "</cm:Header><cm:Request RRN=\"300000000002\"><cm:Media>Milstar</cm:Media><cm:Reversal>Sale</cm:Reversal>"
                    + "<cm:InputType>Keyed</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account>"
                    + "<cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue>"
                    + "<cm:TrackData1>%B6019450000289697^Milstar RET0001^2009000000000000100000000000000?</cm:TrackData1>"
                    + "<cm:AmountField>25</cm:AmountField><cm:PlanNumbers><cm:PlanNumber>10001</cm:PlanNumber></cm:PlanNumbers>"
                    + "<cm:DescriptionField>SALE</cm:DescriptionField><cm:AddressVerificationService>"
                    + "<cm:CardHolderName>John Doe</cm:CardHolderName><cm:BillingAddress1>1222</cm:BillingAddress1>"
                    + "<cm:BillingCountryCode>US</cm:BillingCountryCode><cm:BillingZipCode>12345</cm:BillingZipCode>"
                    + "<cm:Email>johndoe@kk.com</cm:Email><cm:BillingPhone>1122334455</cm:BillingPhone>"
                    + "<cm:ShippingPhone>1122334455</cm:ShippingPhone></cm:AddressVerificationService></cm:Request></cm:Message>";

            creditMessage = this.unmarshalCreditMessage(requestXMLReversalSale);
            result = authorizer.authorize(creditMessage);

             if (ResponseType.APPROVED.equals(result.getResponse().get(0).getResponseType())) {
                creditMessage = this.unmarshalCreditMessage(requestXMLReversalSale);
                result = authorizer.authorize(creditMessage);
                clearGlobalVariables();
                LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
                assertEquals("TRANSACTION_ALREADY_REVERSED", result.getResponse().get(0).getDescriptionField());
            } else {
                Assert.fail("First Reversal Request failed!!! Reversal request not resent!!!");
            }
        } else {
            Assert.fail("Sale Request failed!!! Reversal request not processed!!!");
        }
    }

    @Test
    public void testForTransactionAlreadySettled() {
        sMethodName = "testForTransactionAlreadySettled";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);

        String requestXMLSaleLocal = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><cm:Message TypeCode=\"Request\" "
                + "MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\" xmlns:cm=\"http://www.aafes.com/credit\"><cm:Header>"
                + "<cm:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</cm:IdentityUUID><cm:LocalDateTime>2017-07-14T13:41:00"
                + "</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator>"
                + "<cm:OrderNumber>1000003</cm:OrderNumber>"
                + "<cm:transactionId>40000003</cm:transactionId><cm:termId>12</cm:termId><cm:Comment>Approved</cm:Comment></cm:Header>"
                + "<cm:Request RRN=\"300000000003\"><cm:Media>Milstar</cm:Media><cm:RequestType>Sale</cm:RequestType>"
                + "<cm:InputType>Keyed</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account>"
                + "<cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue>"
                + "<cm:TrackData1>%B6019450000289697^Milstar RET0001^2009000000000000100000000000000?</cm:TrackData1>"
                + "<cm:AmountField>25</cm:AmountField><cm:PlanNumbers><cm:PlanNumber>10001</cm:PlanNumber></cm:PlanNumbers>"
                + "<cm:DescriptionField>SALE</cm:DescriptionField><cm:AddressVerificationService>"
                + "<cm:CardHolderName>John Doe</cm:CardHolderName><cm:BillingAddress1>1222</cm:BillingAddress1>"
                + "<cm:BillingCountryCode>US</cm:BillingCountryCode><cm:BillingZipCode>12345</cm:BillingZipCode>"
                + "<cm:Email>johndoe@kk.com</cm:Email><cm:BillingPhone>1122334455</cm:BillingPhone>"
                + "<cm:ShippingPhone>1122334455</cm:ShippingPhone></cm:AddressVerificationService></cm:Request></cm:Message>";
        Message creditMessage = this.unmarshalCreditMessage(requestXMLSaleLocal);
        Message result = authorizer.authorize(creditMessage);

         if (ResponseType.APPROVED.equals(result.getResponse().get(0).getResponseType())) {
            boolean test =  udpateSettleStatus("DONE", this.getSystemDate(), "1000003", this.getSystemDate(), "Milstar", "DP", "40000003", "40000003");
            
            if(test){
                requestXMLReversalSale = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><cm:Message TypeCode=\"Request\" "
                    + "MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\" xmlns:cm=\"http://www.aafes.com/credit\"><cm:Header>"
                    + "<cm:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</cm:IdentityUUID><cm:LocalDateTime>2017-07-14T11:08:00"
                    + "</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator>"
                    + "<cm:OrderNumber>1000003</cm:OrderNumber>"
                    + "<cm:transactionId>40000003</cm:transactionId><cm:termId>12</cm:termId><cm:Comment>Approved</cm:Comment>"
                    + "</cm:Header><cm:Request RRN=\"300000000003\"><cm:Media>Milstar</cm:Media><cm:Reversal>Sale</cm:Reversal>"
                    + "<cm:InputType>Keyed</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account>"
                    + "<cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue>"
                    + "<cm:TrackData1>%B6019450000289697^Milstar RET0001^2009000000000000100000000000000?</cm:TrackData1>"
                    + "<cm:AmountField>25</cm:AmountField><cm:PlanNumbers><cm:PlanNumber>10001</cm:PlanNumber></cm:PlanNumbers>"
                    + "<cm:DescriptionField>SALE</cm:DescriptionField><cm:AddressVerificationService>"
                    + "<cm:CardHolderName>John Doe</cm:CardHolderName><cm:BillingAddress1>1222</cm:BillingAddress1>"
                    + "<cm:BillingCountryCode>US</cm:BillingCountryCode><cm:BillingZipCode>12345</cm:BillingZipCode>"
                    + "<cm:Email>johndoe@kk.com</cm:Email><cm:BillingPhone>1122334455</cm:BillingPhone>"
                    + "<cm:ShippingPhone>1122334455</cm:ShippingPhone></cm:AddressVerificationService></cm:Request></cm:Message>";
                creditMessage = this.unmarshalCreditMessage(requestXMLReversalSale);
                result = authorizer.authorize(creditMessage);
                clearGlobalVariables();
                LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
                assertEquals("TRANSACTION_ALREADY_SETTLED", result.getResponse().get(0).getDescriptionField());
            }else Assert.fail("Data Updationg failed in table starsettler.settlemessages!!! Reversal request not processed!!!");
        } else {
            Assert.fail("Sale Request failed!!! Reversal request not processed!!!");
        }
    }

    private Message unmarshalCreditMessage(String content) {
        Message request = new Message();
        try {
            StringReader reader = new StringReader(content);
            JAXBContext jc = JAXBContext.newInstance(Message.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            JAXBContext jaxbContext = JAXBContext.newInstance(Message.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            request = (Message) jaxbUnmarshaller.unmarshal(reader);
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }
        return request;
    }

    public void clearGlobalVariables() {
        authorizer = null;
        configurator = null;
        facilityDAO = null;
        facility = null;
        tr = null;
        td = null;
        mapper = null;
        factory = null;
        session = null;
        resultSet = null;
        retailStrategy = null;
        bsf = null;
    }

    public boolean udpateSettleStatus(String settlestatus, String receiveddate, String ordernumber, String settledate, String cardtype, 
            String transactiontype, String clientlineid, String transactionid) {
        sMethodName = "udpateSettleStatus";
        boolean tokenValidateFlg = false;
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        try {
            if (settlestatus != null && receiveddate != null && ordernumber != null &&  settledate != null &&  cardtype  != null
            &&  transactiontype != null &&  clientlineid != null &&  transactionid  != null ) {
                String updateQuery = "";
                ResultSet resultSet = null;
                try {
                    updateQuery = "update starsettler.settlemessages"
                            + " set settlestatus = '"+settlestatus+"'"
                            + " where receiveddate = '"+receiveddate+"'"
                            + " and ordernumber = '"+ordernumber+"'" 
                            + " and settledate = '"+settledate+"'" 
                            + " and cardtype = '"+cardtype+"'" 
                            + " and transactiontype = '"+transactiontype+"'" 
                            + " and clientlineid = '"+clientlineid+"'" 
                            + " and transactionid = '"+transactionid+"';";

                    resultSet = session.execute(updateQuery);

                    if (resultSet != null) {
                        tokenValidateFlg = true;
//                    } else {
//                        LOG.error("Data Udpatation failed ! settlestatus, receiveddate, ordernumber, settledate, "
//                                + "cardtype, transactiontype, clientlineid, transactionid may be null");
                    }
                } catch (Exception ex) {
                    throw new GatewayException("INTERNAL SYSTEM ERROR");
                } finally {
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occured in " + sMethodName + ". Exception  : " + e.getMessage());
            throw new GatewayException("GatewayException-->INTERNAL SYSTEM ERROR during update the token");
        }
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        return tokenValidateFlg;
    }
    private String getSystemDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String ts = dateFormat.format(date);
        return ts;
    }

//    private void setInitialData(){
//        Message msg = new Message();
//        Request request  = new Request();
//        Header head = new Header();
//        
//        msg.setTypeCode(uuid);
//    }
    
}
