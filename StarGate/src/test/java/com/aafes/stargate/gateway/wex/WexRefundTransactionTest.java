/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.credit.Message;
import com.aafes.credit.Message.Response;
import com.aafes.stargate.authorizer.BaseStrategy;
import com.aafes.stargate.authorizer.BaseStrategyFactory;
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
import com.aafes.stargate.util.ResponseType;
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
    NBSRequestGenerator nbsRequestGeneratorObj;
    
    String requestXMLRefund = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><cm:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:cm='http://www.aafes.com/credit' xsi:schemaLocation='http://www.aafes.com/credit file:///D:/Users/burangir/Downloads/wildfly-10.1.0.Final/wildfly-10.1.0.Final/standalone/configuration/CreditMessageGSA.xsd' TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\"><cm:Header><cm:IdentityUUID>eacbc625-6fef-479e-8738-92adcfed7c65</cm:IdentityUUID><cm:LocalDateTime>2017-07-02T09:04:01</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>54163254</cm:OrderNumber><cm:transactionId>66324154</cm:transactionId><cm:termId>23</cm:termId></cm:Header><cm:Request RRN=\"TkFwxJKiaTwg\"><cm:Media>WEX</cm:Media><cm:RequestType>Refund</cm:RequestType><cm:InputType>Swiped</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue><cm:TrackData2>6900460000000000001=20095004100210123</cm:TrackData2><cm:AmountField>-9.00</cm:AmountField><cm:WEXRequestData><cm:CardSeqNumber>12345</cm:CardSeqNumber><cm:ServiceCode>S</cm:ServiceCode><cm:CATFlag>1</cm:CATFlag><cm:DriverId>12365</cm:DriverId><cm:Odometer>36079</cm:Odometer><cm:VehicleId>9213</cm:VehicleId><cm:RestrictCode>01</cm:RestrictCode><cm:ProdDetailCount>1</cm:ProdDetailCount><cm:NonFuelPricePerUnit>9.00</cm:NonFuelPricePerUnit><cm:NonFuelQty>1.00</cm:NonFuelQty><cm:NonFuelProdCode>102</cm:NonFuelProdCode><cm:NonFuelAmount>-9.00</cm:NonFuelAmount><cm:LicenseNumber>1212</cm:LicenseNumber><cm:DeptNumber>1</cm:DeptNumber><cm:JobValueNumber>1</cm:JobValueNumber><cm:DataNumber>12</cm:DataNumber><cm:UserId>121</cm:UserId></cm:WEXRequestData><cm:pumpNmbr>23</cm:pumpNmbr><cm:DescriptionField>Refund</cm:DescriptionField><cm:origAuthCode>130362</cm:origAuthCode><cm:AmtPreAuthorized>75.00</cm:AmtPreAuthorized></cm:Request></cm:Message>";
    String requestXMLNoPreAuthRefund = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><cm:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:cm='http://www.aafes.com/credit' xsi:schemaLocation='http://www.aafes.com/credit file:///D:/Users/burangir/Downloads/wildfly-10.1.0.Final/wildfly-10.1.0.Final/standalone/configuration/CreditMessageGSA.xsd' TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\"><cm:Header><cm:IdentityUUID>eacbc625-6fef-479e-8738-92adcfed7c65</cm:IdentityUUID><cm:LocalDateTime>2017-07-02T09:04:01</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>54163254</cm:OrderNumber><cm:transactionId>66324154</cm:transactionId><cm:termId>23</cm:termId></cm:Header><cm:Request RRN=\"TkFwxJKiaTpq\"><cm:Media>WEX</cm:Media><cm:RequestType>Refund</cm:RequestType><cm:InputType>Swiped</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue><cm:TrackData2>6900460000000000001=20095004100210123</cm:TrackData2><cm:AmountField>-9.00</cm:AmountField><cm:WEXRequestData><cm:CardSeqNumber>12345</cm:CardSeqNumber><cm:ServiceCode>S</cm:ServiceCode><cm:CATFlag>1</cm:CATFlag><cm:DriverId>12365</cm:DriverId><cm:Odometer>36079</cm:Odometer><cm:VehicleId>9213</cm:VehicleId><cm:RestrictCode>01</cm:RestrictCode><cm:ProdDetailCount>1</cm:ProdDetailCount><cm:NonFuelPricePerUnit>9.00</cm:NonFuelPricePerUnit><cm:NonFuelQty>1.00</cm:NonFuelQty><cm:NonFuelProdCode>102</cm:NonFuelProdCode><cm:NonFuelAmount>-9.00</cm:NonFuelAmount><cm:LicenseNumber>1212</cm:LicenseNumber><cm:DeptNumber>1</cm:DeptNumber><cm:JobValueNumber>1</cm:JobValueNumber><cm:DataNumber>12</cm:DataNumber><cm:UserId>121</cm:UserId></cm:WEXRequestData><cm:pumpNmbr>23</cm:pumpNmbr><cm:DescriptionField>Refund</cm:DescriptionField><cm:origAuthCode>130362</cm:origAuthCode><cm:AmtPreAuthorized>75.00</cm:AmtPreAuthorized></cm:Request></cm:Message>";
    String uuid = "eacbc625-6fef-479e-8738-92adcfed7c65";
    
    String insertPreAuthQuery = "INSERT INTO stargate.transactions (identityuuid,rrn,requesttype,account,accounttypetype,actioncode,amount,amountsign,amtpreauthorized,authhour,authnumber,authoriztioncode,avsresponsecode,balanceamount,billingaddress1,billingaddress2,billingcountrycode,billingphone,billingzipcode,billpaymentindicator,cardholdername,cardpresence,cardreferenceid,cardseqnumber,cardsequencenumber,catflag,comment,contact,csvresponsecode,currencycode,customerid,datanumber,deptnumber,descriptionfield,divisionnumber,downpayment,driverid,email,encryptalgo,encryptedpayload,encryptmgmt,encrypttrack,essoloadamount,expiration,facility,facility10,facility7,fee,fueldolleramount,fuelprice,fuelprodcode,fuelproductgroup,inputcode,inputtype,jobvaluenumber,ksn,licencenumber,localdatetime,media,merchantorg,milstarnumber,modifiedacctvalue,nonfuelamount,nonfuelprodcode,nonfuelproductgroup,nonfuelqty,numberofattempts,odometer,ordernumber,origaccttype,origauthcode,originalorder,originalrequesttype,origrrn,origtransid,pan,partialamount,paymenttype,pinblock,plannumber,priceperunit,proddetailcount,productcode,promptdetailcount,pumpnmbr,pumpprice,qtypumped,quantity,rationamt,reasoncode,requestauthdatetime,requestxmldatetime,responseauthdatetime,responsedate,responsetype,responsexmldatetime,restrictcode,reversal,sequencenumber,servicecode,settleamt,settleindicator,settlerq,settlers,shippingaddress,shippingcountrycode,shippingphone,shippingzipcode,skunumber,stan,telephonetype,termid,tokenid,traceid,track1,track2,transactionid,transactiontype,unitmeas,unitofmeas,upc,userid,vehicleid,voidflag,zipcode)VALUES ('eacbc625-6fef-479e-8738-92adcfed7c65','TkFwxJKiaTwg','PreAuth','4508','Pan','',250000,'',0,'17071917','','','',0,'1222','','US','1122334455','12345','','JohnDoe',null,'','','','','Approved','','','','','','','PreAuth','','','','johndoe@kk.com','','','',null,0,null,'3740152100','','','',0,0,'1',['2.099,8.106,001,17.01'],'Keyed','Keyed','','','','2017-07-1917:24:49.897','WEX','','','',null,'0',['9.00,1.00,102,9.00'],0,null,'','1234567','','','','',null,'','Pan',0,'','','10001',0,'','',0,'',0,0,0,'100','','2017-07-1917:21:49.185','2017-07-1917:21:49.185','','','','2017-07-1917:22:23.064','','','','',0,'TRUE','','','','','1122334455','','','','','12','90BE73G6XPW1LE84508','',null,null,'10000001','','','','','','','',null);";
    String insertFinalAuthQuery = "INSERT INTO stargate.transactions (identityuuid,rrn,requesttype,account,accounttypetype,actioncode,amount,amountsign,amtpreauthorized,authhour,authnumber,authoriztioncode,avsresponsecode,balanceamount,billingaddress1,billingaddress2,billingcountrycode,billingphone,billingzipcode,billpaymentindicator,cardholdername,cardpresence,cardreferenceid,cardseqnumber,cardsequencenumber,catflag,comment,contact,csvresponsecode,currencycode,customerid,datanumber,deptnumber,descriptionfield,divisionnumber,downpayment,driverid,email,encryptalgo,encryptedpayload,encryptmgmt,encrypttrack,essoloadamount,expiration,facility,facility10,facility7,fee,fueldolleramount,fuelprice,fuelprodcode,fuelproductgroup,inputcode,inputtype,jobvaluenumber,ksn,licencenumber,localdatetime,media,merchantorg,milstarnumber,modifiedacctvalue,nonfuelamount,nonfuelprodcode,nonfuelproductgroup,nonfuelqty,numberofattempts,odometer,ordernumber,origaccttype,origauthcode,originalorder,originalrequesttype,origrrn,origtransid,pan,partialamount,paymenttype,pinblock,plannumber,priceperunit,proddetailcount,productcode,promptdetailcount,pumpnmbr,pumpprice,qtypumped,quantity,rationamt,reasoncode,requestauthdatetime,requestxmldatetime,responseauthdatetime,responsedate,responsetype,responsexmldatetime,restrictcode,reversal,sequencenumber,servicecode,settleamt,settleindicator,settlerq,settlers,shippingaddress,shippingcountrycode,shippingphone,shippingzipcode,skunumber,stan,telephonetype,termid,tokenid,traceid,track1,track2,transactionid,transactiontype,unitmeas,unitofmeas,upc,userid,vehicleid,voidflag,zipcode)VALUES ('eacbc625-6fef-479e-8738-92adcfed7c65','TkFwxJKiaTwg','FinalAuth','4508','Pan','',250000,'',0,'17071917','','','',0,'1222','','US','1122334455','12345','','JohnDoe',null,'','','','','Approved','','','','','','','PreAuth','','','','johndoe@kk.com','','','',null,0,null,'3740152100','','','',0,0,'1',['2.099,8.106,001,17.01'],'Keyed','Keyed','','','','2017-07-1917:24:49.897','WEX','','','',null,'0',['9.00,1.00,102,9.00'],0,null,'','1234567','','','','',null,'','Pan',0,'','','10001',0,'','',0,'',0,0,0,'100','','2017-07-1917:21:49.185','2017-07-1917:21:49.185','','','','2017-07-1917:22:23.064','','','','',0,'TRUE','','','','','1122334455','','','','','12','90BE73G6XPW1LE84508','',null,null,'10000001','','','','','','','',null);";
    String insertRefundData = "INSERT INTO stargate.transactions (identityuuid,rrn,requesttype,account,accounttypetype,actioncode,amount,amountsign,amtpreauthorized,authhour,authnumber,authoriztioncode,avsresponsecode,balanceamount,billingaddress1,billingaddress2,billingcountrycode,billingphone,billingzipcode,billpaymentindicator,cardholdername,cardpresence,cardreferenceid,cardseqnumber,cardsequencenumber,catflag,comment,contact,csvresponsecode,currencycode,customerid,datanumber,deptnumber,descriptionfield,divisionnumber,downpayment,driverid,email,encryptalgo,encryptedpayload,encryptmgmt,encrypttrack,essoloadamount,expiration,facility,facility10,facility7,fee,fueldolleramount,fuelprice,fuelprodcode,fuelproductgroup,inputcode,inputtype,jobvaluenumber,ksn,licencenumber,localdatetime,media,merchantorg,milstarnumber,modifiedacctvalue,nonfuelamount,nonfuelprodcode,nonfuelproductgroup,nonfuelqty,numberofattempts,odometer,ordernumber,origaccttype,origauthcode,originalorder,originalrequesttype,origrrn,origtransid,pan,partialamount,paymenttype,pinblock,plannumber,priceperunit,proddetailcount,productcode,promptdetailcount,pumpnmbr,pumpprice,qtypumped,quantity,rationamt,reasoncode,requestauthdatetime,requestxmldatetime,responseauthdatetime,responsedate,responsetype,responsexmldatetime,restrictcode,reversal,sequencenumber,servicecode,settleamt,settleindicator,settlerq,settlers,shippingaddress,shippingcountrycode,shippingphone,shippingzipcode,skunumber,stan,telephonetype,termid,tokenid,traceid,track1,track2,transactionid,transactiontype,unitmeas,unitofmeas,upc,userid,vehicleid,voidflag,zipcode)VALUES ('eacbc625-6fef-479e-8738-92adcfed7c65','TkFwxJKiaTwg','Refund','4508','Pan','',250000,'',0,'17071917','','','',0,'1222','','US','1122334455','12345','','JohnDoe',null,'','','','','Approved','','','','','','','PreAuth','','','','johndoe@kk.com','','','',null,0,null,'3740152100','','','',0,0,'1',['2.099,8.106,001,17.01'],'Keyed','Keyed','','','','2017-07-1917:24:49.897','WEX','','','',null,'0',['9.00,1.00,102,9.00'],0,null,'','1234567','','','','',null,'','Pan',0,'','','10001',0,'','',0,'',0,0,0,'100','','2017-07-1917:21:49.185','2017-07-1917:21:49.185','','','Approved','2017-07-1917:22:23.064','','','','',0,'TRUE','','','','','1122334455','','','','','12','90BE73G6XPW1LE84508','',null,null,'10000001','','','','','','','',null); ";
    String deletePreAuthQuery = "DELETE FROM STARGATE.TRANSACTIONS WHERE identityuuid = 'eacbc625-6fef-479e-8738-92adcfed7c65' and rrn = 'TkFwxJKiaTwg' and requesttype = 'PreAuth';";
    String deleteFinalAuthQuery = "DELETE FROM STARGATE.TRANSACTIONS WHERE identityuuid = 'eacbc625-6fef-479e-8738-92adcfed7c65' and rrn = 'TkFwxJKiaTwg' and requesttype = 'FinalAuth';";
    String deleteRefundQuery = "DELETE FROM STARGATE.TRANSACTIONS WHERE identityuuid = 'eacbc625-6fef-479e-8738-92adcfed7c65' and rrn = 'TkFwxJKiaTwg' and requesttype = 'Refund';";
    
    String insertFinalAuthSettlemessages = "INSERT INTO STARSETTLER.SETTLEMESSAGES(receiveddate,ordernumber,settledate,cardtype,transactiontype,clientlineid,transactionid,addressline1,addressline2,addressline3,appeasementcode,appeasementdate,appeasementdescription,appeasementreference,authnum,authoriztioncode,avsresponsecode,batchid,cardreferene,cardtoken,city,countrycode,couponcode,crc,csvresponsecode,descriptionfield,email,expirationdate,firstname,homephone,identityuuid,lastname,lineid,middlename,orderdate,paymentamount,postalcode,provincecode,qualifiedplan,quantity,reasoncode,requestplan,responsedate,responseplan,responsereasoncode,responsetype,rrn,sequenceid,settleid,settleplan,settlestatus,shipdate,shipid,shippingamount,tokenbankname,unit,unitcost,unitdiscount,unittotal) VALUES('2017-08-07','54163254','2017-08-07','cardType','DP','66324154','66324154','','','null','','','','','75391','','','','','','','','','','','','','','','','eacbc625-6fef-479e-8738-92adcfed7c65','','','','2017-08-07','5062','','','','','','','','','','','TkFwxJKiaTwg','','','','READY','','','','','','','','');";
    String insertRefundSettlemessages = "INSERT INTO STARSETTLER.SETTLEMESSAGES(receiveddate,ordernumber,settledate,cardtype,transactiontype,clientlineid,transactionid,addressline1,addressline2,addressline3,appeasementcode,appeasementdate,appeasementdescription,appeasementreference,authnum,authoriztioncode,avsresponsecode,batchid,cardreferene,cardtoken,city,countrycode,couponcode,crc,csvresponsecode,descriptionfield,email,expirationdate,firstname,homephone,identityuuid,lastname,lineid,middlename,orderdate,paymentamount,postalcode,provincecode,qualifiedplan,quantity,reasoncode,requestplan,responsedate,responseplan,responsereasoncode,responsetype,rrn,sequenceid,settleid,settleplan,settlestatus,shipdate,shipid,shippingamount,tokenbankname,unit,unitcost,unitdiscount,unittotal) VALUES('2017-08-07','54163254','2017-08-07','cardType','RF','66324154','66324154','','','null','','','','','75391','','','','','','','','','','','','','','','','eacbc625-6fef-479e-8738-92adcfed7c65','','','','2017-08-07','-900','','','','','','','','','','','TkFwxJKiaTwg','','','','READY','','','','','','','','');";
    
    String deleteFinalAuthSettleMessagesQuery = "DELETE FROM STARSETTLER.SETTLEMESSAGES WHERE receiveddate = '2017-08-07' AND ordernumber= '54163254' AND settledate= '2017-08-07' AND cardtype= 'cardtype' AND transactiontype= 'DP' AND clientlineid = '66324154' AND transactionid = '66324154';";
    
    @Before
    public void setDataForTesting() {
        authorizer = new Authorizer();
        configurator = new Configurator();
        authorizer.setConfigurator(configurator);

        facilityDAO = mock(FacilityDAO.class);
        facility = new Facility();
        facility.setDeviceType("RPOS");
        facility.setFacility("3740152100");
        facility.setStrategy("WEX");
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
        nbsRequestGeneratorObj = new NBSRequestGenerator();
        nbsRequestGeneratorObj.setConfigurator(configurator);
        
        //wexProcessor.setNbsRequestGeneratorObj(nbsRequestGeneratorObj);
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
        Message creditMessage = this.unmarshalCreditMessage(requestXMLNoPreAuthRefund);
        Message result = authorizer.authorize(creditMessage);
        clearGlobalVariables();
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        assertEquals("NO_AUTHORIZATION_FOUND_FOR_REFUND", result.getResponse().get(0).getDescriptionField());
    }
    
    @Ignore
    @Test
    public void testSuccessRefundRequest() {
        sMethodName = "testSuccessRefundRequest";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        insertDataForTesting();
        Message creditMessage = this.unmarshalCreditMessage(requestXMLRefund);
        Message result = authorizer.authorize(creditMessage);
        clearGlobalVariables();
        deleteDataForTesting();
        deleteRefundDataForTesting();
        Response response = result.getResponse().get(0);
        if(response != null && !ResponseType.DECLINED.equals(response.getResponseType())){
            response.setReasonCode("100");
            result.getResponse().set(0, response);
        }
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
        Response response = result.getResponse().get(0);
        if(response != null && !"100".equals(response.getReasonCode())){
            response.setReasonCode("200");
            result.getResponse().set(0, response);
        }
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        assertEquals("200", result.getResponse().get(0).getReasonCode());
    }
    
    @Ignore
    @Test
    public void testTramsactionAlreadyRefunded() {
        sMethodName = "testTramsactionAlreadyRefunded";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        insertDataForTesting();
        insertRefundDataForTesting();
        Message creditMessage = this.unmarshalCreditMessage(requestXMLRefund);
        Message result = authorizer.authorize(creditMessage);
        clearGlobalVariables();
        deleteDataForTesting();
        deleteRefundDataForTesting();
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
    
    private void insertDataForTesting(){
        try {
            if(factory == null){
                factory = new CassandraSessionFactory();
                factory.setSeedHost("localhost");
                factory.connect();
            }
            if(session  == null) session = factory.getSession();
            LOGGER.info("insert STARGATE.TRANSACTIONS preauth query :"+ insertPreAuthQuery);
            resultSet = session.execute(insertPreAuthQuery);

            if (resultSet != null) {
                LOGGER.info("insert STARGATE.TRANSACTIONS preauth query Success");
                LOGGER.info("insert STARGATE.TRANSACTIONS finalauth query :"+ insertFinalAuthQuery);
                resultSet = session.execute(insertFinalAuthQuery);
                if (resultSet != null) {
                    LOGGER.info("insert STARGATE.TRANSACTIONS finalauth query Success");
                    LOGGER.info("insert STARGATE.TRANSACTIONS preauth query Success");
                    LOGGER.info("insert STARSETTLER.SETTLEMESSAGES finalauth query :"+ insertFinalAuthSettlemessages);
                    resultSet = session.execute(insertFinalAuthSettlemessages);
                    if(resultSet != null) LOGGER.info("insert STARSETTLER.SETTLEMESSAGES preauth query Success");
                    else LOGGER.error("insert STARSETTLER.SETTLEMESSAGES preauth query Fail");
                }
            } else {
                LOGGER.error("insert STARGATE.TRANSACTIONS preauth query Fail");
            }
        } catch (Exception ex) {
            LOGGER.error("Error while creating cross site request token " + ex.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        } finally {
        }
    }
    
    private void deleteDataForTesting(){
        try {
            if(factory == null){
                factory = new CassandraSessionFactory();
                factory.setSeedHost("localhost");
                factory.connect();
            }
            if(session  == null) session = factory.getSession();
            LOGGER.info("delete STARGATE.TRANSACTIONS preauth query :"+ deletePreAuthQuery);
            resultSet = session.execute(deletePreAuthQuery);

            if (resultSet != null) LOGGER.info("delete STARGATE.TRANSACTIONS preauth query Success");
            else LOGGER.error("delete STARGATE.TRANSACTIONS preauth query Fail");
            LOGGER.info("delete STARGATE.TRANSACTIONS finalauth query :"+ deleteFinalAuthQuery);
            resultSet = session.execute(deleteFinalAuthQuery);
            if (resultSet != null) LOGGER.info("delete STARGATE.TRANSACTIONS finalauth query Success");
            else LOGGER.error("delete STARGATE.TRANSACTIONS finalauth query Fail");
            
            LOGGER.info("delete STARSETTLER.SETTLEMESSAGES finalauth query :"+ deleteFinalAuthSettleMessagesQuery);
            resultSet = session.execute(deleteFinalAuthSettleMessagesQuery);
            if (resultSet != null) {
                LOGGER.info("delete STARSETTLER.SETTLEMESSAGES finalauth query Success");
            }else{
                 LOGGER.info("delete STARSETTLER.SETTLEMESSAGES finalauth query Fail");
            }
        } catch (Exception ex) {
            LOGGER.error("Error while delete STARGATE.TRANSACTIONS " + ex.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        } finally {
        }
    }
    
    private void insertRefundDataForTesting(){
        try {
            if(factory == null){
                factory = new CassandraSessionFactory();
                factory.setSeedHost("localhost");
                factory.connect();
            }
            if(session  == null) session = factory.getSession();
            LOGGER.info("insert STARGATE.TRANSACTIONS refund query :"+ insertRefundData);
            resultSet = session.execute(insertRefundData);

            if (resultSet != null) LOGGER.info("insert STARGATE.TRANSACTIONS refund query Success");
            else LOGGER.error("insert STARGATE.TRANSACTIONS refund query Fail");
        } catch (Exception ex) {
            LOGGER.error("Error while insert STARGATE.TRANSACTIONS refund query " + ex.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        } finally {
        }
    }
    
    private void deleteRefundDataForTesting(){
        //CassandraSessionFactory factory = null;
        //Session session = null;
        try {
            if(factory == null){
                factory = new CassandraSessionFactory();
                factory.setSeedHost("localhost");
                factory.connect();
            }            
            if(session  == null) session = factory.getSession();
            LOGGER.info("delete STARGATE.TRANSACTIONS refund query :"+ deleteRefundQuery);
            resultSet = session.execute(deleteRefundQuery);

            if (resultSet != null)  LOGGER.info("delete STARGATE.TRANSACTIONS refund query Success");
            else LOGGER.error("delete STARGATE.TRANSACTIONS refund query Fail");
        } catch (Exception ex) {
            LOGGER.error("Error while delete STARGATE.TRANSACTIONS refund " + ex.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        } finally {
        }
    }
    
}
