/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.credit.Message;
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
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.LoggerFactory;

/**
 *
 * @author burangir
 */
public class WexRefundTransactionTest {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WexRefundTransactionTest.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = WexRefundTransactionTest.this.getClass().getSimpleName();
    
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
    WEXStrategy wexStrategy;
    WexGateway wexGateway;
    WEXProcessor wexProcessor;
    WEXValidator wexValidator;
    BaseStrategyFactory bsf;
    GatewayFactory gatewayFactory;
    BaseStrategy bs;
    SettleMessageDAO settleMessageDAO;
    
    String requestXMLRefund = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><cm:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:cm='http://www.aafes.com/credit' xsi:schemaLocation='http://www.aafes.com/credit file:///D:/Users/burangir/Downloads/wildfly-10.1.0.Final/wildfly-10.1.0.Final/standalone/configuration/CreditMessageGSA.xsd' TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\"><cm:Header><cm:IdentityUUID>eacbc625-6fef-479e-8738-92adcfed7c65</cm:IdentityUUID><cm:LocalDateTime>2017-07-02T09:04:01</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>54163254</cm:OrderNumber><cm:transactionId>66324154</cm:transactionId><cm:termId>23</cm:termId></cm:Header><cm:Request RRN=\"TkFwxJKiaTwf\"><cm:Media>WEX</cm:Media><cm:RequestType>Refund</cm:RequestType><cm:InputType>Swiped</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue><cm:TrackData2>6900460000000000001=20095004100210123</cm:TrackData2><cm:AmountField>-9.00</cm:AmountField><cm:WEXRequestData><cm:CardSeqNumber>12345</cm:CardSeqNumber><cm:ServiceCode>S</cm:ServiceCode><cm:CATFlag>1</cm:CATFlag><cm:DriverId>12365</cm:DriverId><cm:Odometer>36079</cm:Odometer><cm:VehicleId>9213</cm:VehicleId><cm:RestrictCode>01</cm:RestrictCode><cm:ProdDetailCount>1</cm:ProdDetailCount><cm:NonFuelPricePerUnit>9.00</cm:NonFuelPricePerUnit><cm:NonFuelQty>1.00</cm:NonFuelQty><cm:NonFuelProdCode>102</cm:NonFuelProdCode><cm:NonFuelAmount>-9.00</cm:NonFuelAmount><cm:LicenseNumber>1212</cm:LicenseNumber><cm:DeptNumber>1</cm:DeptNumber><cm:JobValueNumber>1</cm:JobValueNumber><cm:DataNumber>12</cm:DataNumber><cm:UserId>121</cm:UserId></cm:WEXRequestData><cm:pumpNmbr>23</cm:pumpNmbr><cm:DescriptionField>Refund</cm:DescriptionField><cm:origAuthCode>130362</cm:origAuthCode><cm:AmtPreAuthorized>75.00</cm:AmtPreAuthorized></cm:Request></cm:Message>";
    //String requestXMLReversalSale = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><cm:Message TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\" xmlns:cm=\"http://www.aafes.com/credit\"><cm:Header><cm:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</cm:IdentityUUID><cm:LocalDateTime>2017-07-14T11:08:00</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>1234567</cm:OrderNumber><cm:transactionId>10000001</cm:transactionId><cm:termId>12</cm:termId><cm:Comment>Approved</cm:Comment></cm:Header><cm:Request RRN=\"200000000001\"><cm:Media>Milstar</cm:Media><cm:Reversal>Sale</cm:Reversal><cm:InputType>Keyed</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue><cm:TrackData1>%B6019450000289697^Milstar RET0001^2009000000000000100000000000000?</cm:TrackData1><cm:AmountField>2500</cm:AmountField><cm:PlanNumbers><cm:PlanNumber>10001</cm:PlanNumber></cm:PlanNumbers><cm:DescriptionField>SALE</cm:DescriptionField><cm:AddressVerificationService><cm:CardHolderName>John Doe</cm:CardHolderName><cm:BillingAddress1>1222</cm:BillingAddress1><cm:BillingCountryCode>US</cm:BillingCountryCode><cm:BillingZipCode>12345</cm:BillingZipCode><cm:Email>johndoe@kk.com</cm:Email><cm:BillingPhone>1122334455</cm:BillingPhone><cm:ShippingPhone>1122334455</cm:ShippingPhone></cm:AddressVerificationService></cm:Request></cm:Message>";
    String uuid = "eacbc625-6fef-479e-8738-92adcfed7c65";
    
    @Before
    public void setDataForTesting() {
        authorizer = new Authorizer();
        configurator = new Configurator();
        authorizer.setConfigurator(configurator);

        facilityDAO = mock(FacilityDAO.class);
        facility = new Facility();
        facility.setDeviceType("RPOS");
        facility.setFacility("3740152100");
        facility.setStrategy("Wex");
        facility.setTokenBankName("Wex006");

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

        wexStrategy = new WEXStrategy();
        wexValidator = new WEXValidator();
        wexProcessor = new WEXProcessor();
        wexGateway = new WexGateway();
        wexGateway.setwEXProcessor(wexProcessor);
        settleMessageDAO = new SettleMessageDAO();
        mapper1 = new MappingManager(session).mapper(SettleEntity.class);
        settleMessageDAO.setMapper(mapper1);
        settleMessageDAO.setCassandraSessionFactory(factory);
        wexStrategy.setSettleMessageDAO(settleMessageDAO);
        wexStrategy.setConfigurator(configurator);
        wexStrategy.setwEXValidator(wexValidator);
        bsf = new BaseStrategyFactory();
        gatewayFactory = new GatewayFactory();
        gatewayFactory.setEnableStub("true");
        gatewayFactory.setWexGateway(wexGateway);
        bsf.setwEXStrategy(wexStrategy);
        bs = bsf.findStrategy(StrategyType.WEX);
        //bsf.setRetailStrategy(retailStrategy);
        bs.setGatewayFactory(gatewayFactory);

        authorizer.setBaseStrategyFactory(bsf);
    }
    
    @Ignore
    @Test
    public void testNoAuthorizationFoundForRefundRequest() {
        sMethodName = "testNoAuthorizationFoundForRefundRequest";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        Message creditMessage = this.unmarshalCreditMessage(requestXMLRefund);
        Message result = authorizer.authorize(creditMessage);
        clearGlobalVariables();
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        assertEquals("NO_AUTHORIZATION_FOUND_FOR_REFUND", result.getResponse().get(0).getDescriptionField());
    }
    
    //@Ignore
    @Test
    public void testSuccessRefundRequest() {
        sMethodName = "testSuccessRefundRequest";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        Message creditMessage = this.unmarshalCreditMessage(requestXMLRefund);
        Message result = authorizer.authorize(creditMessage);
        clearGlobalVariables();
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        assertEquals("100", result.getResponse().get(0).getReasonCode());
    }
    
    @Ignore
    @Test
    public void testDeclineRefundRequest() {
        sMethodName = "testDeclineRefundRequest";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        Message creditMessage = this.unmarshalCreditMessage(requestXMLRefund);
        Message result = authorizer.authorize(creditMessage);
        clearGlobalVariables();
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        assertEquals("200", result.getResponse().get(0).getReasonCode());
    }
    
    @Ignore
    @Test
    public void testTramsactionAlreadyRefunded() {
        sMethodName = "testTramsactionAlreadyRefunded";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        Message creditMessage = this.unmarshalCreditMessage(requestXMLRefund);
        Message result = authorizer.authorize(creditMessage);
        clearGlobalVariables();
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        assertEquals("TRANSACTION_ALREADY_REFUNDED", result.getResponse().get(0).getDescriptionField());
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
        wexStrategy = null;
        bsf = null;
    }
    
}
