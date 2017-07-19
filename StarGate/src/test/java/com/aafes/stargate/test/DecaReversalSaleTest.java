/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.test;

import com.aafes.credit.Message;
import com.aafes.stargate.authorizer.BaseStrategy;
import com.aafes.stargate.authorizer.BaseStrategyFactory;
import com.aafes.stargate.authorizer.RetailStrategy;
import com.aafes.stargate.authorizer.entity.Facility;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.boundary.CreditMessageResource;
import com.aafes.stargate.control.Authorizer;
import com.aafes.stargate.control.CassandraSessionFactory;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.control.TranRepository;
import com.aafes.stargate.dao.FacilityDAO;
import com.aafes.stargate.dao.TransactionDAO;
import com.aafes.stargate.gateway.GatewayFactory;
import com.aafes.stargate.gateway.vision.simulator.VisionGatewayStub;
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
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.LoggerFactory;

/**
 *
 * @author burangir
 */
public class DecaReversalSaleTest {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(DecaReversalSaleTest.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = DecaReversalSaleTest.this.getClass().getSimpleName();
    CreditMessageResource creditMessageResource = null;

    String responseXML = "";

    String requestXMLSale = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><cm:Message TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\" xmlns:cm=\"http://www.aafes.com/credit\"><cm:Header><cm:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</cm:IdentityUUID><cm:LocalDateTime>2017-07-14T13:41:00</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>1234567</cm:OrderNumber><cm:transactionId>10000001</cm:transactionId><cm:termId>12</cm:termId><cm:Comment>Approved</cm:Comment></cm:Header><cm:Request RRN=\"200000000001\"><cm:Media>Milstar</cm:Media><cm:RequestType>Sale</cm:RequestType><cm:InputType>Keyed</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue><cm:TrackData1>%B6019450000289697^Milstar RET0001^2009000000000000100000000000000?</cm:TrackData1><cm:AmountField>2500</cm:AmountField><cm:PlanNumbers><cm:PlanNumber>10001</cm:PlanNumber></cm:PlanNumbers><cm:DescriptionField>SALE</cm:DescriptionField><cm:AddressVerificationService><cm:CardHolderName>John Doe</cm:CardHolderName><cm:BillingAddress1>1222</cm:BillingAddress1><cm:BillingCountryCode>US</cm:BillingCountryCode><cm:BillingZipCode>12345</cm:BillingZipCode><cm:Email>johndoe@kk.com</cm:Email><cm:BillingPhone>1122334455</cm:BillingPhone><cm:ShippingPhone>1122334455</cm:ShippingPhone></cm:AddressVerificationService></cm:Request></cm:Message>";
    String requestXMLReversalSale = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><cm:Message TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\" xmlns:cm=\"http://www.aafes.com/credit\"><cm:Header><cm:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</cm:IdentityUUID><cm:LocalDateTime>2017-07-14T11:08:00</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>1234567</cm:OrderNumber><cm:transactionId>10000001</cm:transactionId><cm:termId>12</cm:termId><cm:Comment>Approved</cm:Comment></cm:Header><cm:Request RRN=\"200000000001\"><cm:Media>Milstar</cm:Media><cm:Reversal>Sale</cm:Reversal><cm:InputType>Keyed</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue><cm:TrackData1>%B6019450000289697^Milstar RET0001^2009000000000000100000000000000?</cm:TrackData1><cm:AmountField>2500</cm:AmountField><cm:PlanNumbers><cm:PlanNumber>10001</cm:PlanNumber></cm:PlanNumbers><cm:DescriptionField>SALE</cm:DescriptionField><cm:AddressVerificationService><cm:CardHolderName>John Doe</cm:CardHolderName><cm:BillingAddress1>1222</cm:BillingAddress1><cm:BillingCountryCode>US</cm:BillingCountryCode><cm:BillingZipCode>12345</cm:BillingZipCode><cm:Email>johndoe@kk.com</cm:Email><cm:BillingPhone>1122334455</cm:BillingPhone><cm:ShippingPhone>1122334455</cm:ShippingPhone></cm:AddressVerificationService></cm:Request></cm:Message>";
    String uuid = "0ee1c509-2c70-4bcd-b261-f94f1fe6c43b";

   @Ignore
    @Test
    public void testNoPreAuthorizationForReversal() {
        sMethodName = "testNoPreAuthorizationForReversal";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        Message creditMessage = this.unmarshalCreditMessage(requestXMLReversalSale);
        Authorizer authorizer = new Authorizer();
        Configurator configurator = new Configurator();
        authorizer.setConfigurator(configurator);

        FacilityDAO facilityDAO = mock(FacilityDAO.class);
        Facility facility = new Facility();
        facility.setDeviceType("RPOS");
        facility.setFacility("3740152100");
        facility.setStrategy("Deca");
        facility.setTokenBankName("Deca006");

        when(facilityDAO.get(uuid)).thenReturn(facility);
        authorizer.setFacilityDAO(facilityDAO);

        TranRepository tr = new TranRepository();
        TransactionDAO td = new TransactionDAO();
        Mapper mapper;
        CassandraSessionFactory factory = new CassandraSessionFactory();
        factory.setSeedHost("localhost");
        factory.connect();
        Session session = null;
        ResultSet resultSet = null;
        session = factory.getSession();
        mapper = new MappingManager(session).mapper(Transaction.class);
        td.setMapper(mapper);
        tr.setTransactionDAO(td);
        authorizer.setTranRepository(tr);

        RetailStrategy retailStrategy = new RetailStrategy();
        BaseStrategyFactory bsf = new BaseStrategyFactory();
        bsf.setRetailStrategy(retailStrategy);

        authorizer.setBaseStrategyFactory(bsf);

        Message result = authorizer.authorize(creditMessage);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        assertEquals("NO_AUTHORIZATION_FOUND_FOR_REVERSAL", result.getResponse().get(0).getDescriptionField());
    }

    
    @Test
    public void testProcessRequest() {
        sMethodName = "testProcessRequest";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        Message creditMessage = this.unmarshalCreditMessage(requestXMLSale);
        Authorizer authorizer = new Authorizer();
        Configurator configurator = new Configurator();
        authorizer.setConfigurator(configurator);

        FacilityDAO facilityDAO = mock(FacilityDAO.class);
        Facility facility = new Facility();
        facility.setDeviceType("RPOS");
        facility.setFacility("3740152100");
        facility.setStrategy("Deca");
        facility.setTokenBankName("Deca006");

        when(facilityDAO.get(uuid)).thenReturn(facility);
        authorizer.setFacilityDAO(facilityDAO);

        TranRepository tr = new TranRepository();
        TransactionDAO td = new TransactionDAO();
        Mapper mapper;
        CassandraSessionFactory factory = new CassandraSessionFactory();
        factory.setSeedHost("localhost");
        factory.connect();
        Session session = null;
        ResultSet resultSet = null;
        session = factory.getSession();
        mapper = new MappingManager(session).mapper(Transaction.class);
        td.setMapper(mapper);
        tr.setTransactionDAO(td);
        authorizer.setTranRepository(tr);

        RetailStrategy retailStrategy = new RetailStrategy();
        VisionGatewayStub vgs = new VisionGatewayStub();
        GatewayFactory gatewayFactory = new GatewayFactory();
        mapper = new MappingManager(session).mapper(Transaction.class);
        SettleMessageDAO settleMessageDAO = new SettleMessageDAO();
        settleMessageDAO.setCassandraSessionFactory(factory);
        mapper = new MappingManager(session).mapper(SettleEntity.class);
        settleMessageDAO.setMapper(mapper);
        retailStrategy.setSettleMessageDAO(settleMessageDAO);
        gatewayFactory.setVisionGatewayStub(vgs);
        gatewayFactory.setEnableStub("true");
        BaseStrategyFactory baseStrategyFactory = new BaseStrategyFactory();
        BaseStrategy baseStrategy = retailStrategy;
        baseStrategyFactory.setRetailStrategy(retailStrategy);
        baseStrategy.setGatewayFactory(gatewayFactory);

        authorizer.setBaseStrategyFactory(baseStrategyFactory);

        Message result = authorizer.authorize(creditMessage);

        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        assertEquals("SALE", result.getResponse().get(0).getDescriptionField());
    }

    @Ignore
    @Test
    public void testForReversal() {
        sMethodName = "testForReversal";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        Message creditMessage = this.unmarshalCreditMessage(requestXMLReversalSale);
         Authorizer authorizer = new Authorizer();
        Configurator configurator = new Configurator();
        authorizer.setConfigurator(configurator);

        FacilityDAO facilityDAO = mock(FacilityDAO.class);
        Facility facility = new Facility();
        facility.setDeviceType("RPOS");
        facility.setFacility("3740152100");
        facility.setStrategy("Deca");
        facility.setTokenBankName("Deca006");

        when(facilityDAO.get(uuid)).thenReturn(facility);
        authorizer.setFacilityDAO(facilityDAO);

        TranRepository tr = new TranRepository();
        TransactionDAO td = new TransactionDAO();
        Mapper mapper;
        CassandraSessionFactory factory = new CassandraSessionFactory();
        factory.setSeedHost("localhost");
        factory.connect();
        Session session = null;
        ResultSet resultSet = null;
        session = factory.getSession();
        mapper = new MappingManager(session).mapper(Transaction.class);
        td.setMapper(mapper);
        tr.setTransactionDAO(td);
        authorizer.setTranRepository(tr);

        RetailStrategy retailStrategy = new RetailStrategy();
        VisionGatewayStub vgs = new VisionGatewayStub();
        GatewayFactory gatewayFactory = new GatewayFactory();
        mapper = new MappingManager(session).mapper(Transaction.class);
        SettleMessageDAO settleMessageDAO = new SettleMessageDAO();
        settleMessageDAO.setCassandraSessionFactory(factory);
        mapper = new MappingManager(session).mapper(SettleEntity.class);
        settleMessageDAO.setMapper(mapper);
        retailStrategy.setSettleMessageDAO(settleMessageDAO);
        gatewayFactory.setVisionGatewayStub(vgs);
        gatewayFactory.setEnableStub("true");
        BaseStrategyFactory baseStrategyFactory = new BaseStrategyFactory();
        BaseStrategy baseStrategy = retailStrategy;
        baseStrategyFactory.setRetailStrategy(retailStrategy);
        baseStrategy.setGatewayFactory(gatewayFactory);

        authorizer.setBaseStrategyFactory(baseStrategyFactory);

        Message result = authorizer.authorize(creditMessage);

        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        assertEquals("NO_AUTHORIZATION_FOUND_FOR_REVERSAL", result.getResponse().get(0).getDescriptionField());
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
}