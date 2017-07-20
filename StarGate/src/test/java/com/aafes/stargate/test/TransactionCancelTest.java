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
import com.aafes.stargate.validatetoken.TokenValidatorService;
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
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author singha
 */
public class TransactionCancelTest {

    private Transaction transaction;
    @Mock
    private TokenValidatorService tokenValidatorService;
    @InjectMocks
    private CreditMessageResource cmr;
    @Mock
    private Authorizer authorizer;

    private String requestXML;
    String uuid;

    @Before
    public void setUp() {
        tokenValidatorService = new TokenValidatorService();
        cmr = new CreditMessageResource();
        transaction = new Transaction();
        requestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + " <cm:Message\n"
                + "TypeCode=\"Request\"\n "
                + "MajorVersion=\"3\"\n"
                + "MinorVersion=\"1\" FixVersion=\"0\"\n"
                + "xmlns:cm='http://www.aafes.com/credit'>\n"
                + "<cm:Header>\n"
                + "<cm:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</cm:IdentityUUID>\n"
                + " <cm:LocalDateTime>2017-05-31T13:31:33</cm:LocalDateTime>\n"
                + " <cm:SettleIndicator>true</cm:SettleIndicator>\n"
                + " <cm:OrderNumber>gbPG1574358</cm:OrderNumber>\n"
                + " <cm:transactionId>6312</cm:transactionId>\n"
                + " <cm:termId>12</cm:termId>\n"
                + "<cm:Comment>Track 1 data only Deca Testing Sale</cm:Comment>\n"
                + "</cm:Header>\n"
                + "<cm:Request RRN=\"gbailendra03\">\n"
                // + "<cm:Request RRN="gbailendra03">\n"
                + "  <cm:Media>Milstar</cm:Media>\n"
                + "  <cm:RequestType>TrnCancel</cm:RequestType>\n"
                + "<cm:InputType>Keyed</cm:InputType>\n"
                + "<cm:Pan>Pan</cm:Pan>\n"
                + "<cm:Account>0006019447240006428</cm:Account>\n"
                + "                <cm:Expiration>2203</cm:Expiration>\n"
                + "     <cm:CardVerificationValue>837</cm:CardVerificationValue>\n"
                + "     <cm:TrackData1>%B6019450000289697^MILSTAR RET0001^2009000000000000100000000000000?</cm:TrackData1>\n"
                + "     <cm:AmountField>0.1</cm:AmountField>\n"
                + "     <cm:PlanNumbers>\n"
                + "         <cm:PlanNumber>10001</cm:PlanNumber>\n"
                + "     </cm:PlanNumbers>\n"
                + "<cm:OriginalOrder>O1PG4851998</cm:OriginalOrder>\n"
                + "     <cm:DescriptionField>SALE</cm:DescriptionField>\n"
                + "     <cm:origRRN>RkNPG3739455</cm:origRRN>\n"
                + "     <cm:AddressVerificationService>\n"
                + "         <cm:CardHolderName>John Doe</cm:CardHolderName>\n"
                + "         <cm:BillingAddress1>1222</cm:BillingAddress1>\n"
                + "      <cm:BillingCountryCode>US</cm:BillingCountryCode>\n"
                + "      <cm:BillingZipCode>12345</cm:BillingZipCode>\n"
                + "      <cm:Email>johndoe@kk.com</cm:Email>\n"
                + "      <cm:BillingPhone>1122334455</cm:BillingPhone>\n"
                + "      <cm:ShippingPhone>1122334455</cm:ShippingPhone>\n"
                + "  </cm:AddressVerificationService>\n"
                + "</cm:Request>\n"
                + "</cm:Message>";
    }

    @Ignore
    @Test
    public void testForTransactionExist() {
        Message creditMessage = this.unmarshalCreditMessage(requestXML);
        this.setAllDependencies();
        Message result = authorizer.authorize(creditMessage);
        assertEquals("100", result.getResponse().get(0).getReasonCode());
    }

    @Ignore
    @Test
    public void testForNoTransactionExist() {
        Message creditMessage = this.unmarshalCreditMessage(requestXML);
        this.setAllDependencies();
        Message result = authorizer.authorize(creditMessage);
        assertEquals("NO_AUTHORIZATION_FOUND_FOR_CANCELATION", result.getResponse().get(0).getDescriptionField());
    }

    private void setAllDependencies() {
        authorizer = new Authorizer();
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

        Mapper mapper2;
        RetailStrategy retailStrategy = new RetailStrategy();
        VisionGatewayStub vgs = new VisionGatewayStub();
        GatewayFactory gatewayFactory = new GatewayFactory();
        mapper2 = new MappingManager(session).mapper(Transaction.class);
        SettleMessageDAO settleMessageDAO = new SettleMessageDAO();
        settleMessageDAO.setCassandraSessionFactory(factory);
        mapper2 = new MappingManager(session).mapper(SettleEntity.class);
        settleMessageDAO.setMapper(mapper2);
        retailStrategy.setSettleMessageDAO(settleMessageDAO);
        retailStrategy.setConfigurator(configurator);
        gatewayFactory.setVisionGatewayStub(vgs);
        gatewayFactory.setEnableStub("true");
        BaseStrategyFactory baseStrategyFactory = new BaseStrategyFactory();
        BaseStrategy baseStrategy = retailStrategy;
        baseStrategyFactory.setRetailStrategy(retailStrategy);
        baseStrategy.setGatewayFactory(gatewayFactory);
        authorizer.setBaseStrategyFactory(baseStrategyFactory);
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
