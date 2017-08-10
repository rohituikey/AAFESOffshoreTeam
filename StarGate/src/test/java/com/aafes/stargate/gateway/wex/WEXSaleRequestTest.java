///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.aafes.stargate.gateway.wex;
//
//import com.aafes.credit.Message;
//import com.aafes.stargate.authorizer.BaseStrategy;
//import com.aafes.stargate.authorizer.BaseStrategyFactory;
//import com.aafes.stargate.authorizer.entity.Facility;
//import com.aafes.stargate.authorizer.entity.Transaction;
//import com.aafes.stargate.control.Authorizer;
//import com.aafes.stargate.control.CassandraSessionFactory;
//import com.aafes.stargate.control.Configurator;
//import com.aafes.stargate.control.TranRepository;
//import com.aafes.stargate.dao.FacilityDAO;
//import com.aafes.stargate.dao.TransactionDAO;
//import com.aafes.stargate.gateway.GatewayException;
//import com.aafes.stargate.gateway.GatewayFactory;
//import com.aafes.stargate.util.StrategyType;
//import com.aafes.starsettler.imported.SettleEntity;
//import com.aafes.starsettler.imported.SettleMessageDAO;
//import com.datastax.driver.core.ResultSet;
//import com.datastax.driver.core.Session;
//import com.datastax.driver.mapping.Mapper;
//import com.datastax.driver.mapping.MappingManager;
//import java.io.StringReader;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Unmarshaller;
//import static org.junit.Assert.assertEquals;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//import org.slf4j.LoggerFactory;
//
///**
// *
// * @author singha
// */
//public class WEXSaleRequestTest {
//
//    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WexRefundTransactionTest.class.getSimpleName());
//    String sMethodName = "";
//    final String CLASS_NAME = WEXSaleRequestTest.this.getClass().getSimpleName();
//
//    Authorizer authorizer;
//    Configurator configurator;
//    FacilityDAO facilityDAO;
//    Facility facility;
//    TranRepository tr;
//    TransactionDAO td;
//    Mapper mapper;
//    Mapper mapper1;
//    CassandraSessionFactory factory;
//    Session session;
//    ResultSet resultSet;
//    WEXStrategy wexStrategy;
//    WexGateway wexGateway;
//    WEXProcessor wexProcessor;
//    WEXValidator wexValidator;
//    BaseStrategyFactory bsf;
//    GatewayFactory gatewayFactory;
//    BaseStrategy bs;
//    SettleMessageDAO settleMessageDAO;
//    NBSRequestGenerator nbsRequestGeneratorObj;
//
//    String requestXMLSale = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><cm:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:cm='http://www.aafes.com/credit' xsi:schemaLocation='http://www.aafes.com/credit file:///D:/Users/burangir/Downloads/wildfly-10.1.0.Final/wildfly-10.1.0.Final/standalone/configuration/CreditMessageGSA.xsd' TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\"><cm:Header><cm:IdentityUUID>eacbc625-6fef-479e-8738-92adcfed7c65</cm:IdentityUUID><cm:LocalDateTime>2017-07-02T09:04:01</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>54163254</cm:OrderNumber><cm:transactionId>66324154</cm:transactionId><cm:termId>23</cm:termId></cm:Header><cm:Request RRN=\"TkFwxJKiaTwg\"><cm:Media>WEX</cm:Media><cm:RequestType>Sale</cm:RequestType><cm:InputType>Swiped</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue><cm:TrackData2>6900460000000000001=20095004100210123</cm:TrackData2><cm:AmountField>-9.00</cm:AmountField><cm:WEXRequestData><cm:CardSeqNumber>12345</cm:CardSeqNumber><cm:ServiceCode>S</cm:ServiceCode><cm:CATFlag>1</cm:CATFlag><cm:DriverId>12365</cm:DriverId><cm:Odometer>36079</cm:Odometer><cm:VehicleId>9213</cm:VehicleId><cm:RestrictCode>01</cm:RestrictCode><cm:ProdDetailCount>1</cm:ProdDetailCount><cm:NonFuelPricePerUnit>9.00</cm:NonFuelPricePerUnit><cm:NonFuelQty>1.00</cm:NonFuelQty><cm:NonFuelProdCode>102</cm:NonFuelProdCode><cm:NonFuelAmount>-9.00</cm:NonFuelAmount><cm:LicenseNumber>1212</cm:LicenseNumber><cm:DeptNumber>1</cm:DeptNumber><cm:JobValueNumber>1</cm:JobValueNumber><cm:DataNumber>12</cm:DataNumber><cm:UserId>121</cm:UserId></cm:WEXRequestData><cm:pumpNmbr>23</cm:pumpNmbr><cm:DescriptionField>Refund</cm:DescriptionField><cm:origAuthCode>130362</cm:origAuthCode><cm:AmtPreAuthorized>75.00</cm:AmtPreAuthorized></cm:Request></cm:Message>";
//    String requestXMLNoSale = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><cm:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:cm='http://www.aafes.com/credit' xsi:schemaLocation='http://www.aafes.com/credit file:///D:/Users/burangir/Downloads/wildfly-10.1.0.Final/wildfly-10.1.0.Final/standalone/configuration/CreditMessageGSA.xsd' TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\"><cm:Header><cm:IdentityUUID>eacbc625-6fef-479e-8738-92adcfed7c65</cm:IdentityUUID><cm:LocalDateTime>2017-07-02T09:04:01</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>54163254</cm:OrderNumber><cm:transactionId>66324154</cm:transactionId><cm:termId>23</cm:termId></cm:Header><cm:Request RRN=\"TkFwxJKiaTpq\"><cm:Media>WEX</cm:Media><cm:RequestType>Sale</cm:RequestType><cm:InputType>Swiped</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue><cm:TrackData2>6900460000000000001=20095004100210123</cm:TrackData2><cm:AmountField>-9.00</cm:AmountField><cm:WEXRequestData><cm:CardSeqNumber>12345</cm:CardSeqNumber><cm:ServiceCode>S</cm:ServiceCode><cm:CATFlag>1</cm:CATFlag><cm:DriverId>12365</cm:DriverId><cm:Odometer>36079</cm:Odometer><cm:VehicleId>9213</cm:VehicleId><cm:RestrictCode>01</cm:RestrictCode><cm:ProdDetailCount>1</cm:ProdDetailCount><cm:NonFuelPricePerUnit>9.00</cm:NonFuelPricePerUnit><cm:NonFuelQty>1.00</cm:NonFuelQty><cm:NonFuelProdCode>102</cm:NonFuelProdCode><cm:NonFuelAmount>-9.00</cm:NonFuelAmount><cm:LicenseNumber>1212</cm:LicenseNumber><cm:DeptNumber>1</cm:DeptNumber><cm:JobValueNumber>1</cm:JobValueNumber><cm:DataNumber>12</cm:DataNumber><cm:UserId>121</cm:UserId></cm:WEXRequestData><cm:pumpNmbr>23</cm:pumpNmbr><cm:DescriptionField>Refund</cm:DescriptionField><cm:origAuthCode>130362</cm:origAuthCode><cm:AmtPreAuthorized>75.00</cm:AmtPreAuthorized></cm:Request></cm:Message>";
//    String uuid = "eacbc625-6fef-479e-8738-92adcfed7c65";
//
//    String insertSale = "INSERT INTO STARGATE.TRANSACTIONS(identityuuid,rrn,requesttype,account,accounttypetype,actioncode,amount,amountsign,amtpreauthorized,authhour,authnumber,authoriztioncode,avsresponsecode,balanceamount,billingaddress1,billingaddress2,billingcountrycode,billingphone,billingzipcode,billpaymentindicator,cardholdername,cardpresence,cardreferenceid,cardseqnumber,cardsequencenumber,catflag,comment,contact,csvresponsecode,currencycode,customerid,datanumber,deptnumber,descriptionfield,divisionnumber,downpayment,driverid,email,encryptalgo,encryptedpayload,encryptmgmt,encrypttrack,essoloadamount,expiration,facility,facility10,facility7,fee,fueldolleramount,fuelprice,fuelprodcode,inputcode,inputtype,jobvaluenumber,ksn,licencenumber,localdatetime,media,merchantorg,milstarnumber,modifiedacctvalue,nonfuelamount,nonfuelprodcode,nonfuelqty,numberofattempts,odometer,ordernumber,origaccttype,origauthcode,originalorder,originalrequesttype,origrrn,origtransid,pan,partialamount,paymenttype,pinblock,plannumber,priceperunit,proddetailcount,productcode,pumpnmbr,pumpprice,qtypumped,quantity,rationamt,reasoncode,requestauthdatetime,requestxmldatetime,responseauthdatetime,responsedate,responsetype,responsexmldatetime,restrictcode,reversal,sequencenumber,servicecode,settleamt,settleindicator,settlerq,settlers,shippingaddress,shippingcountrycode,shippingphone,shippingzipcode,skunumber,stan,telephonetype,termid,tokenid,traceid,track1,track2,transactionid,transactiontype,unitmeas,unitofmeas,upc,userid,vehicleid,voidflag,zipcode)values('eacbc625-6fef-479e-8738-92adcfed7c65','TkFwxJKiaTwg','PreAuth','4508','Pan','',5062,'',7500,'17080715','75391','','',0,'','','','','','','','null','','12345','','1','null','','','','null','12','1','PreAuth','','','12365','','null','','null','null',0,'null','3740152100','','','',50.62,2411,'1','','Swiped','1','null','1212','15031','cardType','','','',12,'12',2111,'null','36079','54163254','','130362','null','','null','null','Pan',0,'null','null','',2.099,'7896','98563','23',0,24118,24118,'','100','','2017-08-0715:02:19.935','','','Approved','2017-08-0715:03:14.550','1','','','S',0,'TRUE','null','','','','','','','','','23','','','null','null','66324154','','','','null','121','9213','','null');";
//    String deleteSaleQuery = "DELETE FROM STARGATE.TRANSACTIONS WHERE identityuuid = 'eacbc625-6fef-479e-8738-92adcfed7c65' and rrn = 'TkFwxJKiaTwg' and requesttype = 'PreAuth';";
//
//    String insertSaleSettlemessages = "INSERT INTO STARSETTLER.SETTLEMESSAGES(receiveddate,ordernumber,settledate,cardtype,transactiontype,clientlineid,transactionid,addressline1,addressline2,addressline3,appeasementcode,appeasementdate,appeasementdescription,appeasementreference,authnum,authoriztioncode,avsresponsecode,batchid,cardreferene,cardtoken,city,countrycode,couponcode,crc,csvresponsecode,descriptionfield,email,expirationdate,firstname,homephone,identityuuid,lastname,lineid,middlename,orderdate,paymentamount,postalcode,provincecode,qualifiedplan,quantity,reasoncode,requestplan,responsedate,responseplan,responsereasoncode,responsetype,rrn,sequenceid,settleid,settleplan,settlestatus,shipdate,shipid,shippingamount,tokenbankname,unit,unitcost,unitdiscount,unittotal) VALUES('2017-08-07','54163254','2017-08-07','cardType','RF','66324154','66324154','','','null','','','','','75391','','','','','','','','','','','','','','','','eacbc625-6fef-479e-8738-92adcfed7c65','','','','2017-08-07','-900','','','','','','','','','','','TkFwxJKiaTwg','','','','READY','','','','','','','','');";
//    String deleteSaleSettleMessagesQuery = "DELETE FROM STARSETTLER.SETTLEMESSAGES WHERE receiveddate = '2017-08-07' AND ordernumber= '54163254' AND settledate= '2017-08-07' AND cardtype= 'cardtype' AND transactiontype= 'DP' AND clientlineid = '66324154' AND transactionid = '66324154';";
//
//    @Before
//    public void setDataForTesting() {
//        authorizer = new Authorizer();
//        configurator = new Configurator();
//        authorizer.setConfigurator(configurator);
//
//        facilityDAO = mock(FacilityDAO.class);
//        facility = new Facility();
//        facility.setDeviceType("RPOS");
//        facility.setFacility("3740152100");
//        facility.setStrategy("WEX");
//        facility.setTokenBankName("Wex006");
//
//        when(facilityDAO.get(uuid)).thenReturn(facility);
//        authorizer.setFacilityDAO(facilityDAO);
//
//        tr = new TranRepository();
//        td = new TransactionDAO();
//
//        factory = new CassandraSessionFactory();
//        factory.setSeedHost("localhost");
//        factory.connect();
//
//        session = factory.getSession();
//        mapper = new MappingManager(session).mapper(Transaction.class);
//        td.setMapper(mapper);
//        tr.setTransactionDAO(td);
//        authorizer.setTranRepository(tr);
//
//        wexStrategy = new WEXStrategy();
//        wexValidator = new WEXValidator();
//        wexProcessor = new WEXProcessor();
//        wexGateway = new WexGateway();
//        nbsRequestGeneratorObj = new NBSRequestGenerator();
//
//        wexProcessor.setNbsRequestGeneratorObj(nbsRequestGeneratorObj);
//        nbsRequestGeneratorObj.setApplicationName("AUTHREQ");
//        nbsRequestGeneratorObj.setApplicationVersion("2");
//        nbsRequestGeneratorObj.setDaylightSavingsTimeAtSiteOne("1");
//        nbsRequestGeneratorObj.setCaptureOnlyRequest("C");
//        nbsRequestGeneratorObj.setSessionTypeAuth("A");
//        nbsRequestGeneratorObj.setTransTypePreAuth("8");
//        nbsRequestGeneratorObj.setTransTypeFinalAndSale("10");
//        nbsRequestGeneratorObj.setTransTypeRefund("30");
//        nbsRequestGeneratorObj.setCardTypeWex("WI");
//        nbsRequestGeneratorObj.setServiceType("S");
//
//        wexGateway.setwEXProcessor(wexProcessor);
//        settleMessageDAO = new SettleMessageDAO();
//        mapper1 = new MappingManager(session).mapper(SettleEntity.class);
//        settleMessageDAO.setMapper(mapper1);
//        settleMessageDAO.setCassandraSessionFactory(factory);
//        wexStrategy.setSettleMessageDAO(settleMessageDAO);
//        wexStrategy.setConfigurator(configurator);
//        wexStrategy.setwEXValidator(wexValidator);
//        bsf = new BaseStrategyFactory();
//        gatewayFactory = new GatewayFactory();
//        gatewayFactory.setEnableStub("true");
//        gatewayFactory.setWexGateway(wexGateway);
//        bsf.setwEXStrategy(wexStrategy);
//        bs = bsf.findStrategy(StrategyType.WEX);
//        //bsf.setRetailStrategy(retailStrategy);
//        bs.setGatewayFactory(gatewayFactory);
//
//        authorizer.setBaseStrategyFactory(bsf);
//    }
//
//    @Ignore
//    @Test
//    public void testSuccessSalerRequest() {
//        sMethodName = "testSuccessSalerRequest";
//        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
//        // insertDataForTesting();
//        Message creditMessage = this.unmarshalCreditMessage(requestXMLSale);
//        Message result = authorizer.authorize(creditMessage);
//        clearGlobalVariables();
//        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
//        deleteDataForTesting();
//        assertEquals("100", result.getResponse().get(0).getReasonCode());
//    }
//
//    @Ignore
//    @Test
//    public void testDeclineSaleRequest() {
//        sMethodName = "testDeclineSaleRequest";
//        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
//        Message creditMessage = this.unmarshalCreditMessage(requestXMLSale);
//        Message result = authorizer.authorize(creditMessage);
//        clearGlobalVariables();
//        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
//        assertEquals("200", result.getResponse().get(0).getReasonCode());
//    }
//
//    @Ignore
//    @Test
//    public void testTransactionAlreadyExist() {
//        sMethodName = "testTramsactionAlreadyExist";
//        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
//        Message creditMessage = this.unmarshalCreditMessage(requestXMLSale);
//        Message result = authorizer.authorize(creditMessage);
//        clearGlobalVariables();
//        deleteDataForTesting();
//        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
//        assertEquals("TRANSACTION_ALREADY_EXIST", result.getResponse().get(0).getDescriptionField());
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
//    public void clearGlobalVariables() {
//        authorizer = null;
//        configurator = null;
//        facilityDAO = null;
//        facility = null;
//        tr = null;
//        td = null;
//        mapper = null;
//        factory = null;
//        session = null;
//        resultSet = null;
//        wexStrategy = null;
//        bsf = null;
//    }
//
//    private void deleteDataForTesting() {
//        try {
//            if (factory == null) {
//                factory = new CassandraSessionFactory();
//                factory.setSeedHost("localhost");
//                factory.connect();
//            }
//            if (session == null) {
//                session = factory.getSession();
//            }
//            LOGGER.info("delete STARGATE.TRANSACTIONS Sale query :" + deleteSaleQuery);
//            resultSet = session.execute(deleteSaleQuery);
//
//            if (resultSet != null) {
//                LOGGER.info("delete STARGATE.TRANSACTIONS Sale query Success");
//            } else {
//                LOGGER.error("delete STARGATE.TRANSACTIONS Sale query Fail");
//            }
//
//        } catch (Exception ex) {
//            LOGGER.error("Error while delete STARGATE.TRANSACTIONS " + ex.getMessage());
//            throw new GatewayException("INTERNAL SYSTEM ERROR");
//        } finally {
//        }
//    }
//}
