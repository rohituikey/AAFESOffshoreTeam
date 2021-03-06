/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.credit.Message;
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
import com.aafes.stargate.gateway.GatewayFactory;
import com.aafes.stargate.gateway.wex.simulator.NBSConnector;

import com.aafes.stargate.util.StrategyType;
import com.aafes.starsettler.imported.SettleEntity;
import com.aafes.starsettler.imported.SettleMessageDAO;
import com.aafes.starsettler.imported.WexSettleEntity;
import com.aafes.starsettler.imported.WexSettleMessagesDao;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import java.io.StringReader;
import java.net.SocketTimeoutException;
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
 * @author alugumetlas
 */
@Ignore
public class TestPreAuthWex {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TestPreAuthWex.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = TestPreAuthWex.this.getClass().getSimpleName();

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
    WEXValidator wexValidator;
    WexSettleMessagesDao wexSettleMessagesDao;
    BaseStrategyFactory bsf;
    GatewayFactory gatewayFactory;
    BaseStrategy bs;
    SettleMessageDAO settleMessageDAO;
    NBSRequestGenerator nBSRequestGenerator;
    WEXProcessor wexProcessor;
    NBSConnector nBSConnector;
    Mapper mapper3;

    String requestXMLPreAuth = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><cm:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:cm='http://www.aafes.com/credit' xsi:schemaLocation='http://www.aafes.com/credit file:///D:/Users/alugumetlas/Downloads/wildfly-10.1.0.Final/wildfly-10.1.0.Final/standalone/configuration/CreditMessageGSA.xsd' TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\"><cm:Header><cm:IdentityUUID>eacbc625-6fef-479e-8738-92adcfed7c65</cm:IdentityUUID><cm:LocalDateTime>2017-07-02T09:04:01</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>54163254</cm:OrderNumber><cm:transactionId>66324154</cm:transactionId><cm:termId>23</cm:termId></cm:Header><cm:Request RRN=\"TkFwxJKiaTwf\"><cm:Media>WEX</cm:Media>  <cm:RequestType>PreAuth</cm:RequestType><cm:InputType>Swiped</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue>  <cm:TrackData2>6006496628299904508=20095004100210123</cm:TrackData2><cm:AmountField>50.62</cm:AmountField><cm:WEXRequestData> <cm:CardSeqNumber>12345</cm:CardSeqNumber><cm:ServiceCode>S</cm:ServiceCode> <cm:CATFlag>1</cm:CATFlag> <cm:PromptDetailCount>1</cm:PromptDetailCount> <cm:DriverId>12365</cm:DriverId> <cm:Odometer>36079</cm:Odometer><cm:VehicleId>9213</cm:VehicleId><cm:RestrictCode>01</cm:RestrictCode><cm:ProdDetailCount>1</cm:ProdDetailCount> <cm:FuelProdGroup>  <cm:PricePerUnit>2.099</cm:PricePerUnit><cm:Quantity>8.106</cm:Quantity><cm:FuelProdCode>001</cm:FuelProdCode><cm:FuelDollarAmount>17.01</cm:FuelDollarAmount></cm:FuelProdGroup></cm:WEXRequestData><cm:pumpNmbr>23</cm:pumpNmbr> <cm:DescriptionField>PreAuth</cm:DescriptionField><cm:origAuthCode>130362</cm:origAuthCode><cm:AmtPreAuthorized>75.00</cm:AmtPreAuthorized></cm:Request></cm:Message>";
    String requestXMLPreAuthKeyed = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><cm:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:cm='http://www.aafes.com/credit' xsi:schemaLocation='http://www.aafes.com/credit file:///D:/Users/alugumetlas/Downloads/wildfly-10.1.0.Final/wildfly-10.1.0.Final/standalone/configuration/CreditMessageGSA.xsd' TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\"><cm:Header><cm:IdentityUUID>eacbc625-6fef-479e-8738-92adcfed7c65</cm:IdentityUUID><cm:LocalDateTime>2017-07-02T09:04:01</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>54163254</cm:OrderNumber><cm:transactionId>66324154</cm:transactionId><cm:termId>23</cm:termId></cm:Header><cm:Request RRN=\"TkFwxJKiaTwf\"><cm:Media>WEX</cm:Media>  <cm:RequestType>PreAuth</cm:RequestType><cm:InputType>Keyed</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue>  <cm:TrackData2>6006496628299904508=20095004100210123</cm:TrackData2><cm:AmountField>50.62</cm:AmountField><cm:WEXRequestData> <cm:CardSeqNumber>12345</cm:CardSeqNumber><cm:ServiceCode>S</cm:ServiceCode> <cm:CATFlag>1</cm:CATFlag> <cm:PromptDetailCount>1</cm:PromptDetailCount> <cm:DriverId>12365</cm:DriverId> <cm:Odometer>36079</cm:Odometer><cm:VehicleId>9213</cm:VehicleId><cm:RestrictCode>01</cm:RestrictCode><cm:ProdDetailCount>1</cm:ProdDetailCount> <cm:FuelProdGroup>  <cm:PricePerUnit>2.099</cm:PricePerUnit><cm:Quantity>8.106</cm:Quantity><cm:FuelProdCode>001</cm:FuelProdCode><cm:FuelDollarAmount>17.01</cm:FuelDollarAmount></cm:FuelProdGroup></cm:WEXRequestData><cm:pumpNmbr>23</cm:pumpNmbr> <cm:DescriptionField>PreAuth</cm:DescriptionField><cm:origAuthCode>130362</cm:origAuthCode><cm:AmtPreAuthorized>75.00</cm:AmtPreAuthorized></cm:Request></cm:Message>";
    String requestXMLPreAuthWithOutFuelProds = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><cm:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:cm='http://www.aafes.com/credit' xsi:schemaLocation='http://www.aafes.com/credit file:///D:/Users/alugumetlas/Downloads/wildfly-10.1.0.Final/wildfly-10.1.0.Final/standalone/configuration/CreditMessageGSA.xsd' TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\"><cm:Header><cm:IdentityUUID>eacbc625-6fef-479e-8738-92adcfed7c65</cm:IdentityUUID><cm:LocalDateTime>2017-07-02T09:04:01</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>54163254</cm:OrderNumber><cm:transactionId>66324154</cm:transactionId><cm:termId>23</cm:termId></cm:Header><cm:Request RRN=\"TkFwxJKiaTwf\"><cm:Media>WEX</cm:Media>  <cm:RequestType>PreAuth</cm:RequestType><cm:InputType>Swiped</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue>  <cm:TrackData2>6006496628299904508=20095004100210123</cm:TrackData2><cm:AmountField>50.62</cm:AmountField><cm:WEXRequestData> <cm:CardSeqNumber>12345</cm:CardSeqNumber><cm:ServiceCode>S</cm:ServiceCode> <cm:CATFlag>1</cm:CATFlag> <cm:PromptDetailCount>1</cm:PromptDetailCount> <cm:DriverId>12365</cm:DriverId> <cm:Odometer>36079</cm:Odometer><cm:VehicleId>9213</cm:VehicleId><cm:RestrictCode>01</cm:RestrictCode><cm:ProdDetailCount>1</cm:ProdDetailCount></cm:WEXRequestData><cm:pumpNmbr>23</cm:pumpNmbr> <cm:DescriptionField>PreAuth</cm:DescriptionField><cm:origAuthCode>130362</cm:origAuthCode><cm:AmtPreAuthorized>75.00</cm:AmtPreAuthorized></cm:Request></cm:Message>";
    String requestXMLPreAuthExceedFuelProds = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><cm:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:cm='http://www.aafes.com/credit' xsi:schemaLocation='http://www.aafes.com/credit file:///D:/Users/alugumetlas/Downloads/wildfly-10.1.0.Final/wildfly-10.1.0.Final/standalone/configuration/CreditMessageGSA.xsd' TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\"><cm:Header><cm:IdentityUUID>eacbc625-6fef-479e-8738-92adcfed7c65</cm:IdentityUUID><cm:LocalDateTime>2017-07-02T09:04:01</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>54163254</cm:OrderNumber><cm:transactionId>66324154</cm:transactionId><cm:termId>23</cm:termId></cm:Header><cm:Request RRN=\"TkFwxJKiaTwf\"><cm:Media>WEX</cm:Media>  <cm:RequestType>PreAuth</cm:RequestType><cm:InputType>Swiped</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue>  <cm:TrackData2>6006496628299904508=20095004100210123</cm:TrackData2><cm:AmountField>50.62</cm:AmountField><cm:WEXRequestData> <cm:CardSeqNumber>12345</cm:CardSeqNumber><cm:ServiceCode>S</cm:ServiceCode> <cm:CATFlag>1</cm:CATFlag> <cm:PromptDetailCount>1</cm:PromptDetailCount> <cm:DriverId>12365</cm:DriverId> <cm:Odometer>36079</cm:Odometer><cm:VehicleId>9213</cm:VehicleId><cm:RestrictCode>01</cm:RestrictCode><cm:ProdDetailCount>2</cm:ProdDetailCount> <cm:FuelProdGroup>  <cm:PricePerUnit>2.099</cm:PricePerUnit><cm:Quantity>8.106</cm:Quantity><cm:FuelProdCode>001</cm:FuelProdCode><cm:FuelDollarAmount>17.01</cm:FuelDollarAmount></cm:FuelProdGroup><cm:FuelProdGroup>  <cm:PricePerUnit>2.099</cm:PricePerUnit><cm:Quantity>8.106</cm:Quantity><cm:FuelProdCode>001</cm:FuelProdCode><cm:FuelDollarAmount>17.01</cm:FuelDollarAmount></cm:FuelProdGroup><cm:FuelProdGroup>  <cm:PricePerUnit>2.099</cm:PricePerUnit><cm:Quantity>8.106</cm:Quantity><cm:FuelProdCode>002</cm:FuelProdCode><cm:FuelDollarAmount>20.01</cm:FuelDollarAmount></cm:FuelProdGroup><cm:NonFuelProductGroup><cm:NonFuelPricePerUnit>9.00</cm:NonFuelPricePerUnit><cm:NonFuelQty>1.00</cm:NonFuelQty><cm:NonFuelProdCode>102</cm:NonFuelProdCode><cm:NonFuelAmount>9.00</cm:NonFuelAmount></cm:NonFuelProductGroup></cm:WEXRequestData><cm:pumpNmbr>23</cm:pumpNmbr> <cm:DescriptionField>PreAuth</cm:DescriptionField><cm:origAuthCode>130362</cm:origAuthCode><cm:AmtPreAuthorized>75.00</cm:AmtPreAuthorized></cm:Request></cm:Message>";
    String requestXMLPreAuthExceedNONFuelProds = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><cm:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:cm='http://www.aafes.com/credit' xsi:schemaLocation='http://www.aafes.com/credit file:///D:/Users/alugumetlas/Downloads/wildfly-10.1.0.Final/wildfly-10.1.0.Final/standalone/configuration/CreditMessageGSA.xsd' TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\"><cm:Header><cm:IdentityUUID>eacbc625-6fef-479e-8738-92adcfed7c65</cm:IdentityUUID><cm:LocalDateTime>2017-07-02T09:04:01</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>54163254</cm:OrderNumber><cm:transactionId>66324154</cm:transactionId><cm:termId>23</cm:termId></cm:Header><cm:Request RRN=\"TkFwxJKiaTwf\"><cm:Media>WEX</cm:Media>  <cm:RequestType>PreAuth</cm:RequestType><cm:InputType>Swiped</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue>  <cm:TrackData2>6006496628299904508=20095004100210123</cm:TrackData2><cm:AmountField>50.62</cm:AmountField><cm:WEXRequestData> <cm:CardSeqNumber>12345</cm:CardSeqNumber><cm:ServiceCode>S</cm:ServiceCode> <cm:CATFlag>1</cm:CATFlag> <cm:PromptDetailCount>1</cm:PromptDetailCount> <cm:DriverId>12365</cm:DriverId> <cm:Odometer>36079</cm:Odometer><cm:VehicleId>9213</cm:VehicleId><cm:RestrictCode>01</cm:RestrictCode><cm:ProdDetailCount>1</cm:ProdDetailCount> <cm:FuelProdGroup>  <cm:PricePerUnit>2.099</cm:PricePerUnit><cm:Quantity>8.106</cm:Quantity><cm:FuelProdCode>001</cm:FuelProdCode><cm:FuelDollarAmount>17.01</cm:FuelDollarAmount></cm:FuelProdGroup><cm:NonFuelProductGroup><cm:NonFuelPricePerUnit>9.00</cm:NonFuelPricePerUnit><cm:NonFuelQty>1.00</cm:NonFuelQty><cm:NonFuelProdCode>102</cm:NonFuelProdCode><cm:NonFuelAmount>9.00</cm:NonFuelAmount></cm:NonFuelProductGroup><cm:NonFuelProductGroup><cm:NonFuelPricePerUnit>9.00</cm:NonFuelPricePerUnit><cm:NonFuelQty>1.00</cm:NonFuelQty><cm:NonFuelProdCode>102</cm:NonFuelProdCode><cm:NonFuelAmount>9.00</cm:NonFuelAmount></cm:NonFuelProductGroup><cm:NonFuelProductGroup><cm:NonFuelPricePerUnit>9.00</cm:NonFuelPricePerUnit><cm:NonFuelQty>1.00</cm:NonFuelQty><cm:NonFuelProdCode>102</cm:NonFuelProdCode><cm:NonFuelAmount>9.00</cm:NonFuelAmount></cm:NonFuelProductGroup><cm:NonFuelProductGroup><cm:NonFuelPricePerUnit>9.00</cm:NonFuelPricePerUnit><cm:NonFuelQty>1.00</cm:NonFuelQty><cm:NonFuelProdCode>102</cm:NonFuelProdCode><cm:NonFuelAmount>9.00</cm:NonFuelAmount></cm:NonFuelProductGroup><cm:NonFuelProductGroup><cm:NonFuelPricePerUnit>9.00</cm:NonFuelPricePerUnit><cm:NonFuelQty>1.00</cm:NonFuelQty><cm:NonFuelProdCode>102</cm:NonFuelProdCode><cm:NonFuelAmount>9.00</cm:NonFuelAmount></cm:NonFuelProductGroup><cm:NonFuelProductGroup><cm:NonFuelPricePerUnit>9.00</cm:NonFuelPricePerUnit><cm:NonFuelQty>1.00</cm:NonFuelQty><cm:NonFuelProdCode>102</cm:NonFuelProdCode><cm:NonFuelAmount>9.00</cm:NonFuelAmount></cm:NonFuelProductGroup></cm:WEXRequestData><cm:pumpNmbr>23</cm:pumpNmbr> <cm:DescriptionField>PreAuth</cm:DescriptionField><cm:origAuthCode>130362</cm:origAuthCode><cm:AmtPreAuthorized>75.00</cm:AmtPreAuthorized></cm:Request></cm:Message>";
    String uuid = "eacbc625-6fef-479e-8738-92adcfed7c65";
    String requestXMLFuelAndNonFuel = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><cm:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:cm='http://www.aafes.com/credit' xsi:schemaLocation='http://www.aafes.com/credit file:///D:/Users/alugumetlas/Downloads/wildfly-10.1.0.Final/wildfly-10.1.0.Final/standalone/configuration/CreditMessageGSA.xsd' TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\"><cm:Header><cm:IdentityUUID>eacbc625-6fef-479e-8738-92adcfed7c65</cm:IdentityUUID><cm:LocalDateTime>2017-07-02T09:04:01</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>54163254</cm:OrderNumber><cm:transactionId>66324154</cm:transactionId><cm:termId>23</cm:termId></cm:Header><cm:Request RRN=\"TkFwxJKiaTwf\"><cm:Media>WEX</cm:Media>  <cm:RequestType>PreAuth</cm:RequestType><cm:InputType>Swiped</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue>  <cm:TrackData2>6006496628299904508=20095004100210123</cm:TrackData2><cm:AmountField>50.62</cm:AmountField><cm:WEXRequestData> <cm:CardSeqNumber>12345</cm:CardSeqNumber><cm:ServiceCode>S</cm:ServiceCode> <cm:CATFlag>1</cm:CATFlag> <cm:PromptDetailCount>1</cm:PromptDetailCount> <cm:DriverId>12365</cm:DriverId> <cm:Odometer>36079</cm:Odometer><cm:VehicleId>9213</cm:VehicleId><cm:RestrictCode>01</cm:RestrictCode><cm:ProdDetailCount>1</cm:ProdDetailCount> <cm:FuelProdGroup>  <cm:PricePerUnit>2.099</cm:PricePerUnit><cm:Quantity>8.106</cm:Quantity><cm:FuelProdCode>001</cm:FuelProdCode><cm:FuelDollarAmount>17.01</cm:FuelDollarAmount></cm:FuelProdGroup><cm:NonFuelProductGroup><cm:NonFuelPricePerUnit>9.00</cm:NonFuelPricePerUnit><cm:NonFuelQty>1.00</cm:NonFuelQty><cm:NonFuelProdCode>102</cm:NonFuelProdCode><cm:NonFuelAmount>9.00</cm:NonFuelAmount></cm:NonFuelProductGroup></cm:WEXRequestData><cm:pumpNmbr>23</cm:pumpNmbr> <cm:DescriptionField>PreAuth</cm:DescriptionField><cm:origAuthCode>130362</cm:origAuthCode><cm:AmtPreAuthorized>75.00</cm:AmtPreAuthorized></cm:Request></cm:Message>";
    String requestXMLPreAuthRejected = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><cm:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:cm='http://www.aafes.com/credit' xsi:schemaLocation='http://www.aafes.com/credit file:///D:/Users/alugumetlas/Downloads/wildfly-10.1.0.Final/wildfly-10.1.0.Final/standalone/configuration/CreditMessageGSA.xsd' TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\"><cm:Header><cm:IdentityUUID>eacbc625-6fef-479e-8738-92adcfed7c65</cm:IdentityUUID><cm:LocalDateTime>2017-07-02T09:04:01</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>54163254</cm:OrderNumber><cm:transactionId>66324154</cm:transactionId><cm:termId>23</cm:termId></cm:Header><cm:Request RRN=\"TkFwxJKiaTwf\"><cm:Media>WEX</cm:Media>  <cm:RequestType>PreAuth</cm:RequestType><cm:InputType>Swiped</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue>  <cm:TrackData2>6006496628299904508=20095004100210000</cm:TrackData2><cm:AmountField>50.62</cm:AmountField><cm:WEXRequestData> <cm:CardSeqNumber>12345</cm:CardSeqNumber><cm:ServiceCode>S</cm:ServiceCode> <cm:CATFlag>1</cm:CATFlag> <cm:PromptDetailCount>1</cm:PromptDetailCount> <cm:DriverId>12365</cm:DriverId> <cm:Odometer>36079</cm:Odometer><cm:VehicleId>9213</cm:VehicleId><cm:RestrictCode>01</cm:RestrictCode><cm:ProdDetailCount>1</cm:ProdDetailCount> <cm:FuelProdGroup>  <cm:PricePerUnit>2.099</cm:PricePerUnit><cm:Quantity>8.106</cm:Quantity><cm:FuelProdCode>001</cm:FuelProdCode><cm:FuelDollarAmount>17.01</cm:FuelDollarAmount></cm:FuelProdGroup></cm:WEXRequestData><cm:pumpNmbr>23</cm:pumpNmbr> <cm:DescriptionField>PreAuth</cm:DescriptionField><cm:origAuthCode>130362</cm:origAuthCode><cm:AmtPreAuthorized>75.00</cm:AmtPreAuthorized></cm:Request></cm:Message>";
    String requestXMLPreAuthCanceled = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><cm:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:cm='http://www.aafes.com/credit' xsi:schemaLocation='http://www.aafes.com/credit file:///D:/Users/alugumetlas/Downloads/wildfly-10.1.0.Final/wildfly-10.1.0.Final/standalone/configuration/CreditMessageGSA.xsd' TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\"><cm:Header><cm:IdentityUUID>eacbc625-6fef-479e-8738-92adcfed7c65</cm:IdentityUUID><cm:LocalDateTime>2017-07-02T09:04:01</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>54163254</cm:OrderNumber><cm:transactionId>66324154</cm:transactionId><cm:termId>23</cm:termId></cm:Header><cm:Request RRN=\"TkFwxJKiaTwf\"><cm:Media>WEX</cm:Media>  <cm:RequestType>PreAuth</cm:RequestType><cm:InputType>Swiped</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue>  <cm:TrackData2>6006496628299904508=20095004100219999</cm:TrackData2><cm:AmountField>50.62</cm:AmountField><cm:WEXRequestData> <cm:CardSeqNumber>12345</cm:CardSeqNumber><cm:ServiceCode>S</cm:ServiceCode> <cm:CATFlag>1</cm:CATFlag> <cm:PromptDetailCount>1</cm:PromptDetailCount> <cm:DriverId>12365</cm:DriverId> <cm:Odometer>36079</cm:Odometer><cm:VehicleId>9213</cm:VehicleId><cm:RestrictCode>01</cm:RestrictCode><cm:ProdDetailCount>1</cm:ProdDetailCount> <cm:FuelProdGroup>  <cm:PricePerUnit>2.099</cm:PricePerUnit><cm:Quantity>8.106</cm:Quantity><cm:FuelProdCode>001</cm:FuelProdCode><cm:FuelDollarAmount>17.01</cm:FuelDollarAmount></cm:FuelProdGroup></cm:WEXRequestData><cm:pumpNmbr>23</cm:pumpNmbr> <cm:DescriptionField>PreAuth</cm:DescriptionField><cm:origAuthCode>130362</cm:origAuthCode><cm:AmtPreAuthorized>75.00</cm:AmtPreAuthorized></cm:Request></cm:Message>";

    @Before
    public void setDataForTesting() {
        authorizer = new Authorizer();
        configurator = new Configurator();
        configurator.postConstruct();
        configurator.load();
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
        wexSettleMessagesDao = new WexSettleMessagesDao();
        nBSConnector = new NBSConnector();
        nBSConnector.setConfigurator(configurator);
        wexProcessor.setConfigurator(configurator);
        wexProcessor.setClientObj(nBSConnector);
        nBSRequestGenerator = new NBSRequestGenerator();
        nBSRequestGenerator.setConfigurator(configurator);
        wexProcessor.setNbsRequestGenerator(nBSRequestGenerator);
        wexGateway.setwEXProcessor(wexProcessor);
        mapper3 = new MappingManager(session).mapper(WexSettleEntity.class);
        wexSettleMessagesDao.setMapper(mapper3);
        wexSettleMessagesDao.setFactory(factory);
        wexStrategy.setWexSettleMessagesDao(wexSettleMessagesDao);
        wexStrategy.setConfigurator(configurator);
        wexValidator.setConfigurator(configurator);
        wexStrategy.setwEXValidator(wexValidator);
        bsf = new BaseStrategyFactory();
        gatewayFactory = new GatewayFactory();
        gatewayFactory.setEnableStub("true");
        gatewayFactory.setWexGateway(wexGateway);
        bsf.setwEXStrategy(wexStrategy);
        bs = bsf.findStrategy(StrategyType.WEX);
        bs.setGatewayFactory(gatewayFactory);
        authorizer.setBaseStrategyFactory(bsf);
        session = intiateSession();
        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
        // insertDataToFacMapper();
    }

    @Ignore
    @Test
    public void testFuelOnlyApproval() {
        sMethodName = "testFuelOnlyApproval";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        Message creditMessage = this.unmarshalCreditMessage(requestXMLPreAuth);
        Message result = authorizer.authorize(creditMessage);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        session = intiateSession();
        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
        clearGlobalVariables();
        assertEquals("100", result.getResponse().get(0).getReasonCode());
    }
    @Ignore
    @Test
    public void testFuelOnlyDeclained() throws SocketTimeoutException {
        sMethodName = "testFuelOnlyDeclained";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        Message creditMessage = this.unmarshalCreditMessage(requestXMLPreAuthCanceled);
        Message result = authorizer.authorize(creditMessage);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        session = intiateSession();
        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
        clearGlobalVariables();
        assertEquals("200", result.getResponse().get(0).getReasonCode());
    }
    @Ignore
    @Test
    public void testApprovalForOneFuelAndOneNonFuel() {
        sMethodName = "testSuccessPreAuthRequest";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        Message creditMessage = this.unmarshalCreditMessage(requestXMLFuelAndNonFuel);
        Message result = authorizer.authorize(creditMessage);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        session = intiateSession();
        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
        clearGlobalVariables();
        assertEquals("100", result.getResponse().get(0).getReasonCode());
    }
    @Ignore
    @Test
    public void testDeclineForOneFuelAndOneNonFuel() {

        sMethodName = "testDeclineForOneFuelAndOneNonFuel";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        Message creditMessage = this.unmarshalCreditMessage(requestXMLPreAuthCanceled);
        Message result = authorizer.authorize(creditMessage);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        session = intiateSession();
        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
        clearGlobalVariables();
        assertEquals("200", result.getResponse().get(0).getReasonCode());

    }
    @Ignore
    @Test
    public void testDeclinePreAuthRequestDueToKeyedTransaction() {
        sMethodName = "testDeclinePreAuthRequestDueToKeyedTransaction";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        intiateSession();
        Message creditMessage = this.unmarshalCreditMessage(requestXMLPreAuthKeyed);
        Message result = authorizer.authorize(creditMessage);
        session = intiateSession();
        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
        clearGlobalVariables();
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        assertEquals("INVALID_INPUT_TYPE", result.getResponse().get(0).getDescriptionField());
    }

    @Ignore
    @Test
    public void testDeclineDueToOnlyNonFuelProducts() {
        sMethodName = "testDeclinePreAuthRequestDueToNoFuelProducts";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        intiateSession();
        Message creditMessage = this.unmarshalCreditMessage(requestXMLPreAuthWithOutFuelProds);
        Message result = authorizer.authorize(creditMessage);
        session = intiateSession();
        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
        clearGlobalVariables();
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        assertEquals("PRODUCT_DETAIL_COUNT_NOT_BE_NULL", result.getResponse().get(0).getDescriptionField());
    }

    @Ignore
    @Test
    public void testForCountExceededForFuelProdCodes() {
        sMethodName = "testForCountExceededForFuelProdCodes";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        intiateSession();
        Message creditMessage = this.unmarshalCreditMessage(requestXMLPreAuthExceedFuelProds);
        Message result = authorizer.authorize(creditMessage);
        session = intiateSession();
        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
        clearGlobalVariables();
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        assertEquals("FUEL_PRODUCT_DETAIL_COUNT_EXCEEDED", result.getResponse().get(0).getDescriptionField());
    }

    @Ignore
    @Test
    public void testForCountExceededForNonFuelProdCodes() {
        sMethodName = "testForCountExceededForNonFuelProdCodes";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        intiateSession();
        Message creditMessage = this.unmarshalCreditMessage(requestXMLPreAuthExceedNONFuelProds);
        Message result = authorizer.authorize(creditMessage);
        session = intiateSession();
        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
        clearGlobalVariables();
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        assertEquals("NONFUEL_PRODUCT_DETAIL_COUNT_EXCEEDED", result.getResponse().get(0).getDescriptionField());
    }
    @Ignore
    @Test
    public void testForRejectedResponseFromNBS() throws SocketTimeoutException {
        sMethodName = "testForRejectedResponseFromNBS";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        Message creditMessage = this.unmarshalCreditMessage(requestXMLPreAuthRejected);
        Message result = authorizer.authorize(creditMessage);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        session = intiateSession();
        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
        clearGlobalVariables();
        assertEquals("939", result.getResponse().get(0).getReasonCode());
    }

    //@Ignore
    @Test
    public void testForCanceledResponseFromNBS() throws SocketTimeoutException {
        sMethodName = "testForCanceledResponseFromNBS";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        Message creditMessage = this.unmarshalCreditMessage(requestXMLPreAuthCanceled);
        Message result = authorizer.authorize(creditMessage);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        session = intiateSession();
        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
        clearGlobalVariables();
        assertEquals("941", result.getResponse().get(0).getReasonCode());
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
//    private void insertDataToFacMapper() {
//        Statement st = new SimpleStatement("insert into stargate.facmapper(uuid,devicetype,facility,strategy,tokenbankname) values('0ee1c509-2c70-4bcd-b261-f94f1fe6c43b','3858000123','i','WEX','WEX001')");
//        ResultSet rs = session.execute(st);
//        session.close();
//    }

    private Session intiateSession() {
        if (factory == null) {
            factory = new CassandraSessionFactory();
            factory.setSeedHost("localhost");
            factory.connect();
        }
        if (session == null) {
            session = factory.getSession();
        }
        return session;
    }

//    private Transaction mapRequest(Transaction transaction, Message requestMessage) {
//        String[] decimalPart;
//        transaction.setRequestXmlDateTime(SvsUtil.formatLocalDateTime());
//
//        Message.Header header = requestMessage.getHeader();
//
//        // Mapping Header Fields
//        if (header.getIdentityUUID() != null) {
//            transaction.setIdentityUuid(header.getIdentityUUID());
//        }
//        transaction.setLocalDateTime(SvsUtil.formatLocalDateTime());
//        boolean settleIndicator = header.isSettleIndicator();
//        if (settleIndicator) {
//            transaction.setSettleIndicator("true");
//        } else {
//            transaction.setSettleIndicator("false");
//        }
//        transaction.setOrderNumber(header.getOrderNumber());
//        transaction.setTransactionId(header.getTransactionId());
//        if (header.getTermId() != null) {
//            transaction.setTermId(header.getTermId());
//        }
//        transaction.setComment(header.getComment());
//        transaction.setCustomerId(header.getCustomerID());
//
//        // Mapping Request Fields
//        if (requestMessage.getRequest() != null && requestMessage.getRequest().size() > 1) {
//            throw new AuthorizerException("MULTIPLE_REQUESTS");
//        }
//        Message.Request request = requestMessage.getRequest().get(0);
//        transaction.setRrn(request.getRRN());
//        transaction.setMedia(request.getMedia());
//        if (request.getRequestType() != null && !request.getRequestType().value().isEmpty()) {
//            transaction.setRequestType(request.getRequestType().value());
//        }
//        if (request.getReversal() != null && !request.getReversal().value().isEmpty()) {
//            transaction.setReversal(request.getReversal().value());
//        }
//        if (request.getVoid() != null && !request.getVoid().value().isEmpty()) {
//
//            transaction.setVoidFlag(request.getVoid().value());
//        }
//
//        transaction.setAccount(request.getAccount());
//        if (request.getPan() != null) {
//            if (request.getPan().value().equalsIgnoreCase("PAN")) {
//                transaction.setAccountTypeType(request.getPan().value());
//                transaction.setPan(request.getPan().value());
//            } else {
//                throw new AuthorizerException("INVALID_PAN_TAG");
//            }
//
//        }
//
//        if (request.getToken() != null) {
//            transaction.setTokenId(request.getToken().value());
//            if (request.getToken().value().equalsIgnoreCase("TOKEN")) {
//                transaction.setAccountTypeType(request.getToken().value());
//                transaction.setTokenId(request.getAccount());
//            } else {
//                throw new AuthorizerException("INVALID_TOKEN_TAG");
//            }
//        }
//        if (request.getEncryptedPayload() != null) {
//            transaction.setEncryptedPayLoad(request.getEncryptedPayload().value());
//        }
//        transaction.setCvv(request.getCardVerificationValue());
//        transaction.setKsn(request.getKSN());
//        transaction.setPinBlock(request.getPinBlock());
//        if (request.getExpiration() != null) {
//            //TODO : check for valid expiration date
//            String exp = request.getExpiration().toString();
//            if (exp != null && exp.length() == 4) {
//                String month = exp.substring(2, 4);
//                if (Integer.parseInt(month) > 12 || Integer.parseInt(month) < 1) {
//                    throw new AuthorizerException("INVALID_EXPIRATION_DATE");
//                }
//            } else {
//                throw new AuthorizerException("INVALID_EXPIRATION_DATE");
//            }
//            transaction.setExpiration(request.getExpiration().toString());
//        }
//        //TODO : check amount handling in MPG
//        try {
//            BigDecimal amt;
//            amt = request.getAmountField();
//            if (amt != null) {
//                amt = amt.movePointRight(2);
//                if (amt.longValueExact() <= 9999999) {
//                    if (transaction.getRequestType() != null
//                            && !transaction.getRequestType().trim().isEmpty()
//                            && !transaction.getRequestType().equalsIgnoreCase(RequestType.REFUND)) {
//                        if (amt.longValueExact() < 0) {
//                            throw new AuthorizerException("INVALID_AMOUNT");
//                        }
//                    }
//                    transaction.setAmount(amt.longValueExact());
//                } else {
//                    throw new AuthorizerException("INVALID_AMOUNT");
//                }
//            }
//        } catch (ArithmeticException e) {
//            throw new AuthorizerException("INVALID_AMOUNT");
//        }
//        transaction.setGcpin(request.getGCpin());
//        transaction.setInputType(request.getInputType());
//        transaction.setDescriptionField(request.getDescriptionField());
//        transaction.setTrack1(request.getTrackData1());
//        transaction.setTrack2(request.getTrackData2());
//        transaction.setEncryptTrack(request.getEncryptTrack());
//        if (request.getPlanNumbers() != null
//                && request.getPlanNumbers().getPlanNumber() != null
//                && request.getPlanNumbers().getPlanNumber().get(0) != null) {
//            transaction.setPlanNumber(request.getPlanNumbers().getPlanNumber().get(0).toString());
//        }
//
//        if (request.getPumpNmbr() != null) {
//            transaction.setPumpNmbr(request.getPumpNmbr().toString());
//        }
//        if (request.getWEXRequestData() != null) {
//            Message.Request.WEXRequestData wexReqPayAtPump = request.getWEXRequestData();
//            if (transaction.getDriverId() != null) {
//                transaction.setDriverId(wexReqPayAtPump.getDriverId().toString());
//            }
//            if (wexReqPayAtPump.getRestrictCode() != null) {
//                transaction.setRestrictCode(wexReqPayAtPump.getRestrictCode().toString());
//            }
//
//            /* NEW FIELDS ADDED IN CLASS AFTER MODIFICATIONS IN CreditMessageGSA.XSD - start */
//            if (wexReqPayAtPump.getFuelProdGroup() != null && wexReqPayAtPump.getFuelProdGroup().size() > 0) {
//                StringBuilder prodCodeDetailsStr = null;
//                List<String> ProdDataList = new ArrayList<>();
//                List<Message.Request.WEXRequestData.FuelProdGroup> list = wexReqPayAtPump.getFuelProdGroup();
//                //list.size()>2 throws exception
//                for (Message.Request.WEXRequestData.FuelProdGroup tmp : list) {
//                    prodCodeDetailsStr = new StringBuilder();
//                    prodCodeDetailsStr.append(tmp.getFuelProdCode());
//                    prodCodeDetailsStr.append(":");
//                    prodCodeDetailsStr.append(tmp.getQuantity());
//                    prodCodeDetailsStr.append(":");
//                    prodCodeDetailsStr.append(tmp.getPricePerUnit());
//                    prodCodeDetailsStr.append(":");
//                    prodCodeDetailsStr.append(tmp.getFuelDollarAmount());
//                    ProdDataList.add(prodCodeDetailsStr.toString());
//                    prodCodeDetailsStr = null;
//                }
//                List<Message.Request.WEXRequestData.NonFuelProductGroup> nList = wexReqPayAtPump.getNonFuelProductGroup();
//                //  //list.size()>4 throws exception
//                for (Message.Request.WEXRequestData.NonFuelProductGroup tmp : nList) {
//                    prodCodeDetailsStr = new StringBuilder();
//                    prodCodeDetailsStr.append(tmp.getNonFuelProdCode());
//                    prodCodeDetailsStr.append(":");
//                    prodCodeDetailsStr.append(tmp.getNonFuelQty());
//                    prodCodeDetailsStr.append(":");
//                    prodCodeDetailsStr.append(tmp.getNonFuelPricePerUnit());
//                    prodCodeDetailsStr.append(":");
//                    prodCodeDetailsStr.append(tmp.getNonFuelAmount());
//                    ProdDataList.add(prodCodeDetailsStr.toString());
//                    prodCodeDetailsStr = null;
//                }
//                transaction.setProducts(ProdDataList);
//                ProdDataList = null;
//            }
//
//            if (wexReqPayAtPump.getVehicleId() != null) {
//                transaction.setVehicleId(wexReqPayAtPump.getVehicleId().toString());
//            }
//            if (wexReqPayAtPump.getLicenseNumber() != null) {
//                transaction.setLicenceNumber(wexReqPayAtPump.getLicenseNumber());
//            }
//            if (wexReqPayAtPump.getDeptNumber() != null) {
//                transaction.setDeptNumber(wexReqPayAtPump.getDeptNumber().toString());
//            }
//            transaction.setJobValueNumber(wexReqPayAtPump.getJobValueNumber());
//            transaction.setDataNumber(wexReqPayAtPump.getDataNumber());
//            transaction.setUserId(wexReqPayAtPump.getUserId());
//            if (wexReqPayAtPump.getProdDetailCount() != null) {
//                transaction.setProdDetailCount(wexReqPayAtPump.getProdDetailCount().toString());
//            }
//
//            if (wexReqPayAtPump.getServiceCode() != null && wexReqPayAtPump.getServiceCode().size() > 0) {
//                transaction.setServiceCode(wexReqPayAtPump.getServiceCode().get(0));
//            }
//
//            if (wexReqPayAtPump.getOdometer() != null) {
//                transaction.setOdoMeter(wexReqPayAtPump.getOdometer());
//            }
//
//            if (wexReqPayAtPump.getCardSeqNumber() != null) {
//                transaction.setCardSeqNumber(wexReqPayAtPump.getCardSeqNumber());
//            }
//            transaction.setPromptDetailCount(wexReqPayAtPump.getPromptDetailCount());
//        }
//        //*Uncommented from 502 to 551 and modified some code
//
//        transaction.setZipCode(request.getZipCode());
//        transaction.setUpc(request.getUPC());
//        transaction.setEncryptMgmt(request.getEncryptMgmt());
//        transaction.setEncryptAlgo(request.getEncryptAlgorithm());
//        transaction.setSettleRq(request.getSettleRq());
//        transaction.setOriginalOrder(request.getOriginalOrder());
//        transaction.setOrigTransId(request.getOrigTransId());
//        transaction.setOrigAuthCode(request.getOrigAuthCode());
//        BigDecimal amtPreAuth;
//        amtPreAuth = request.getAmtPreAuthorized();
//        if (amtPreAuth != null) {
//            amtPreAuth = amtPreAuth.movePointRight(2);
//            long n = amtPreAuth.longValueExact();
//            transaction.setAmtPreAuthorized(n);
//        }
//        transaction.setPaymentType(request.getPymntType());
//        // Adding origininal rrn, ordernuber etc
//        if (request.getOriginalOrder() != null && !request.getOriginalOrder().isEmpty()) {
//
//            transaction.setOriginalOrder(request.getOriginalOrder());
//        }
//
//        if (request.getOrigRRN() != null && !request.getOrigRRN().isEmpty()) {
//            transaction.setOrigRRN(request.getOrigRRN().get(0));
//        }
//
//        if (request.getOrigTransId() != null && !request.getOrigTransId().isEmpty()) {
//
//            transaction.setOrigTransId(request.getOrigTransId());
//        }
//
//        if (request.getOrigAuthCode() != null && !request.getOrigAuthCode().isEmpty()) {
//
//            transaction.setOrigAuthCode(request.getOrigAuthCode());
//        }
//        return transaction;
//    }

}
