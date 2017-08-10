/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.credit.Message;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.authorizer.entity.TransactionFuelProdGroup;
import com.aafes.stargate.authorizer.entity.TransactionNonFuelProductGroup;
import com.aafes.stargate.control.AuthorizerException;
import com.aafes.stargate.control.CassandraSessionFactory;
import com.aafes.stargate.control.TranRepository;
import com.aafes.stargate.dao.TransactionDAO;
import com.aafes.stargate.util.RequestType;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import org.slf4j.LoggerFactory;

/**
 *
 * @author burangir
 */
public class TestWexDBChanges {
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(TestWexDBChanges.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = TestWexDBChanges.this.getClass().getSimpleName();
    private static String maskAccount = "true";
    static TranRepository tranRepository;
    static TranRepository tr;
    static TransactionDAO td;
    static Mapper mapper;
    static Mapper mapper1;
    static CassandraSessionFactory factory;
    static Session session;
    static ResultSet resultSet;
    
    
    public static void main(String args[]){
        
        tr = new TranRepository();
        td = new TransactionDAO();

        factory = new CassandraSessionFactory();
        factory.setSeedHost("localhost");
        factory.connect();

        session = factory.getSession();
        mapper = new MappingManager(session).mapper(Transaction.class);
        td.setMapper(mapper);
        tr.setTransactionDAO(td);
        
        String preAuth = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><cm:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:cm='http://www.aafes.com/credit' xsi:schemaLocation='http://www.aafes.com/credit file:///D:/Users/burangir/Downloads/wildfly-10.1.0.Final/wildfly-10.1.0.Final/standalone/configuration/CreditMessageGSA.xsd' TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\"><cm:Header><cm:IdentityUUID>eacbc625-6fef-479e-8738-92adcfed7c65</cm:IdentityUUID> <cm:LocalDateTime>2017-07-02T09:04:01</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>54163254</cm:OrderNumber><cm:transactionId>66324154</cm:transactionId><cm:termId>23</cm:termId></cm:Header><cm:Request RRN=\"TkFwxJKiaTwf\"><cm:Media>WEX</cm:Media> <cm:RequestType>PreAuth</cm:RequestType> <cm:InputType>Swiped</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue> <cm:TrackData2>6900460000000000001=20095004100210123</cm:TrackData2><cm:AmountField>50.62</cm:AmountField><cm:WEXRequestData>    <cm:CardSeqNumber>12345</cm:CardSeqNumber>    <cm:ServiceCode>S</cm:ServiceCode><cm:CATFlag>1</cm:CATFlag><cm:PromptDetailCount>1</cm:PromptDetailCount><cm:DriverId>12365</cm:DriverId><cm:Odometer>36079</cm:Odometer><cm:VehicleId>9213</cm:VehicleId><cm:RestrictCode>01</cm:RestrictCode><cm:ProdDetailCount>1</cm:ProdDetailCount><cm:FuelProdGroup><cm:PricePerUnit>2.099</cm:PricePerUnit><cm:Quantity>8.106</cm:Quantity><cm:FuelProdCode>001</cm:FuelProdCode><cm:FuelDollarAmount>17.01</cm:FuelDollarAmount></cm:FuelProdGroup></cm:WEXRequestData><cm:pumpNmbr>23</cm:pumpNmbr> <cm:DescriptionField>PreAuth</cm:DescriptionField><cm:origAuthCode>130362</cm:origAuthCode><cm:AmtPreAuthorized>75.00</cm:AmtPreAuthorized> </cm:Request></cm:Message>";
        Transaction t = new Transaction();
        Message creditMessage = unmarshalCreditMessage(preAuth);
        t = mapRequest(t, creditMessage);
        encryptValues(t);
        LOG.info("Saving transaction....." + t.getRrn());
        tranRepository = new TranRepository();
        //tranRepository.save(t);
        try{
            List newList = new ArrayList();
            newList.add("0,0,0,0");
            t.setFuelProductGroup(newList);
            t.setNonFuelProductGroup(newList);
            Statement st = new SimpleStatement("INSERT INTO stargate.transactions (identityuuid,rrn,requesttype,account,accounttypetype,actioncode,"
                    + "amount,amountsign,amtpreauthorized,authhour,authnumber,authoriztioncode,avsresponsecode,balanceamount,"
                    + "billingaddress1,billingaddress2,billingcountrycode,billingphone,billingzipcode,billpaymentindicator,"
                    + "cardholdername,cardpresence,cardreferenceid,cardseqnumber,cardsequencenumber,catflag,comment,contact,"
                    + "csvresponsecode,currencycode,customerid,datanumber,deptnumber,descriptionfield,divisionnumber,downpayment,"
                    + "driverid,email,encryptalgo,encryptedpayload,encryptmgmt,encrypttrack,essoloadamount,expiration,facility,"
                    + "facility10,facility7,fee,fueldolleramount,fuelprice,fuelprodcode,fuelproductgroup,inputcode,inputtype,"
                    + "jobvaluenumber,ksn,licencenumber,localdatetime,media,merchantorg,milstarnumber,modifiedacctvalue,"
                    + "nonfuelamount,nonfuelprodcode,nonfuelproductgroup,nonfuelqty,numberofattempts,odometer,ordernumber,"
                    + "origaccttype,origauthcode,originalorder,originalrequesttype,origrrn,origtransid,pan,partialamount,paymenttype,"
                    + "pinblock,plannumber,priceperunit,proddetailcount,productcode,promptdetailcount,pumpnmbr,pumpprice,qtypumped,"
                    + "quantity,rationamt,reasoncode,requestauthdatetime,requestxmldatetime,responseauthdatetime,responsedate,"
                    + "responsetype,responsexmldatetime,restrictcode,reversal,sequencenumber,servicecode,settleamt,settleindicator,"
                    + "settlerq,settlers,shippingaddress,shippingcountrycode,shippingphone,shippingzipcode,skunumber,stan,"
                    + "telephonetype,termid,tokenid,traceid,track1,track2,transactionid,transactiontype,unitmeas,unitofmeas,upc,"
                    + "userid,vehicleid,voidflag,zipcode)"
                    + "VALUES ('0ee1c509-2c70-4bcd-b261-f94f1fe6c43b','2000','Sale','4508','Pan','',250000,'',0,'17071917','','','',"
                    + "0,'1222','','US','1122334455','12345','','JohnDoe',null,'','','','','Approved','','','','','','','SALE','','','',"
                    + "'johndoe@kk.com','','','',null,0,null,'3740152100','','','',0,0,'1',?,'Keyed','Keyed','','','',"
                    + "'2017-07-1917:24:49.897','Milstar','','','',null,'0',?,0,null,'','1234567','','','','',null,'','Pan',0,"
                    + "'','','10001',0,'','',0,'',0,0,0,'100','','2017-07-1917:21:49.185','2017-07-1917:21:49.185','','','',"
                    + "'2017-07-1917:22:23.064','','Reversal','','',0,'TRUE','','','','','1122334455','','','','','12',"
                    + "'90BE73G6XPW1LE84508','',null,null,'10000001','','','','','','','',null);", 
                    t.getFuelProductGroup(), t.getNonFuelProductGroup()); 
            ResultSet rs = session.execute(st);
            
             try {
            String query = "SELECT * FROM stargate.transactions "
                    + "WHERE identityuuid = '0ee1c509-2c70-4bcd-b261-f94f1fe6c43b' and rrn = '2000' "
                    + "and requesttype = 'Sale' ALLOW FILTERING;";
            resultSet = session.execute(query);
            while (!resultSet.isExhausted()) {
            Row row = resultSet.one();
            System.out.println("ROE :" +  row);


        }
        } catch (Exception ex) {
            LOG.error("Error while creating cross site request token " + ex.getMessage());
        } finally {
        }
            
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            session.close();
            factory.close();
        }
    }
    
    private static Transaction mapRequest(Transaction transaction, Message requestMessage) {
        LOG.info("Authorizer.mapRequest method started");
        String[] decimalPart;
        transaction.setRequestXmlDateTime(getSystemDateTime());

        Message.Header header = requestMessage.getHeader();

        // Mapping Header Fields
        if (header.getIdentityUUID() != null) {
            transaction.setIdentityUuid(header.getIdentityUUID());
        }
        transaction.setLocalDateTime(formatLocalDateTime(header.
                getLocalDateTime()));
        boolean settleIndicator = header.isSettleIndicator();
        if (settleIndicator) {
            transaction.setSettleIndicator("true");
        } else {
            transaction.setSettleIndicator("false");
        }
        transaction.setOrderNumber(header.getOrderNumber());
        transaction.setTransactionId(header.getTransactionId());
        if (header.getTermId() != null) {
            transaction.setTermId(header.getTermId());
        }
        transaction.setComment(header.getComment());
        transaction.setCustomerId(header.getCustomerID());

        // Mapping Request Fields
        if (requestMessage.getRequest() != null && requestMessage.getRequest().size() > 1) {
            LOG.error("AuthorizerException due to Multiple requests");
            throw new AuthorizerException("MULTIPLE_REQUESTS");
        }
        Message.Request request = requestMessage.getRequest().get(0);
        transaction.setRrn(request.getRRN());
        transaction.setMedia(request.getMedia());
        if (request.getRequestType() != null && !request.getRequestType().value().isEmpty()) {
            transaction.setRequestType(request.getRequestType().value());
        }
        if (request.getReversal() != null && !request.getReversal().value().isEmpty()) {
            transaction.setReversal(request.getReversal().value());
        }
        if (request.getVoid() != null && !request.getVoid().value().isEmpty()) {

            transaction.setVoidFlag(request.getVoid().value());
        }

        transaction.setAccount(request.getAccount());
        if (request.getPan() != null) {
            if (request.getPan().value().equalsIgnoreCase("PAN")) {
                transaction.setAccountTypeType(request.getPan().value());
                transaction.setPan(request.getPan().value());
            } else {
                LOG.error("AuthorizerException due to invalid Token tag or not  PAN tag value PAN");
                throw new AuthorizerException("INVALID_PAN_TAG");
            }

        }

        if (request.getToken() != null) {
            transaction.setTokenId(request.getToken().value());
            if (request.getToken().value().equalsIgnoreCase("TOKEN")) {
                transaction.setAccountTypeType(request.getToken().value());
                transaction.setTokenId(request.getAccount());
            } else {
                LOG.error("AuthorizerException due to invalid Token tag or not  Token tag value TOKEN");
                throw new AuthorizerException("INVALID_TOKEN_TAG");
            }
        }
        if (request.getEncryptedPayload() != null) {
            transaction.setEncryptedPayLoad(request.getEncryptedPayload().value());
        }
        transaction.setCvv(request.getCardVerificationValue());
        transaction.setKsn(request.getKSN());
        transaction.setPinBlock(request.getPinBlock());
        if (request.getExpiration() != null) {
            //TODO : check for valid expiration date
            String exp = request.getExpiration().toString();
            if (exp != null && exp.length() == 4) {
                String month = exp.substring(2, 4);
                if (Integer.parseInt(month) > 12 || Integer.parseInt(month) < 1) {
                    LOG.error("AuthorizerException due to invalid Expiration date");
                    throw new AuthorizerException("INVALID_EXPIRATION_DATE");
                }
            } else {
                LOG.error("AuthorizerException due to invalid Expiration date");
                throw new AuthorizerException("INVALID_EXPIRATION_DATE");
            }
            transaction.setExpiration(request.getExpiration().toString());
        }
        //TODO : check amount handling in MPG
        try {
            BigDecimal amt;
            amt = request.getAmountField();
            if (amt != null) {
                amt = amt.movePointRight(2);
                if (amt.longValueExact() <= 9999999) {
                    if (transaction.getRequestType() != null
                            && !transaction.getRequestType().trim().isEmpty()
                            && !transaction.getRequestType().equalsIgnoreCase(RequestType.REFUND)) {
                        if (amt.longValueExact() < 0) {
                            LOG.error("AuthorizerException due to invalid amount");
                            throw new AuthorizerException("INVALID_AMOUNT");
                        }
                    }
                    transaction.setAmount(amt.longValueExact());
                } else {
                    LOG.error("AuthorizerException due to invalid amount");
                    throw new AuthorizerException("INVALID_AMOUNT");
                }
            }
        } catch (ArithmeticException e) {
            LOG.error("AuthorizerException due to invalid amount" + e.getMessage());
            throw new AuthorizerException("INVALID_AMOUNT");
        }
        transaction.setGcpin(request.getGCpin());
        transaction.setInputType(request.getInputType());
        transaction.setDescriptionField(request.getDescriptionField());
        transaction.setTrack1(request.getTrackData1());
        transaction.setTrack2(request.getTrackData2());
        transaction.setEncryptTrack(request.getEncryptTrack());
        if (request.getPlanNumbers() != null
                && request.getPlanNumbers().getPlanNumber() != null
                && request.getPlanNumbers().getPlanNumber().get(0) != null) {
            transaction.setPlanNumber(request.getPlanNumbers().getPlanNumber().get(0).toString());
        }

        if (request.getPumpNmbr() != null) {
            transaction.setPumpNmbr(request.getPumpNmbr().toString());
        }
        if (request.getWEXRequestData() != null) {
            Message.Request.WEXRequestData wexReqPayAtPump = request.getWEXRequestData();
            if (transaction.getDriverId() != null) {
                transaction.setDriverId(wexReqPayAtPump.getDriverId().toString());
            }
            if (wexReqPayAtPump.getRestrictCode() != null) {
                transaction.setRestrictCode(wexReqPayAtPump.getRestrictCode().toString());
            }
            
            /* NEW FIELDS ADDED IN CLASS AFTER MODIFICATIONS IN CreditMessageGSA.XSD - start */
            if(wexReqPayAtPump.getFuelProdGroup() != null && wexReqPayAtPump.getFuelProdGroup().size() > 0){
                TransactionFuelProdGroup fuelProGroupObj;
                List<TransactionFuelProdGroup> fuelProdDataList = new ArrayList<>();
                List<Message.Request.WEXRequestData.FuelProdGroup> list = wexReqPayAtPump.getFuelProdGroup();
                for(Message.Request.WEXRequestData.FuelProdGroup tmp : list){
                    fuelProGroupObj = new TransactionFuelProdGroup();
                    if(tmp.getQuantity() != null) fuelProGroupObj.setFuelQuantity(tmp.getQuantity().get(0));
                    if(tmp.getFuelDollarAmount() != null) fuelProGroupObj.setFuelDollarAmount(tmp.getFuelDollarAmount().get(0));
                    if(tmp.getPricePerUnit() != null) fuelProGroupObj.setFuelPricePerUnit(tmp.getPricePerUnit().get(0));
                    if(tmp.getFuelProdCode() != null) fuelProGroupObj.setFuelProductCode(tmp.getFuelProdCode().get(0));
                    
                    fuelProdDataList.add(fuelProGroupObj);
                    fuelProGroupObj = null;
                }
                transaction.setFuelProductGroup(fuelProdDataList);
                fuelProdDataList = null;
            }
            
            List<TransactionNonFuelProductGroup> nonFuelProdDataList = new ArrayList<>();
            
            if(wexReqPayAtPump.getNonFuelProductGroup() != null && wexReqPayAtPump.getNonFuelProductGroup().size() > 0){
                TransactionNonFuelProductGroup nonFuelProGroupObj;
                List<Message.Request.WEXRequestData.NonFuelProductGroup> list = wexReqPayAtPump.getNonFuelProductGroup();
                for(Message.Request.WEXRequestData.NonFuelProductGroup tmp : list){
                    nonFuelProGroupObj = new TransactionNonFuelProductGroup();
                    if(tmp.getNonFuelQty() != null) nonFuelProGroupObj.setNonFuelQuantity(tmp.getNonFuelQty().get(0));
                    if(tmp.getNonFuelAmount() != null) nonFuelProGroupObj.setNonFuelAmount(tmp.getNonFuelAmount().get(0));
                    if(tmp.getNonFuelPricePerUnit() != null) nonFuelProGroupObj.setNonFuelPricePerUnit(tmp.getNonFuelPricePerUnit().get(0));
                    if(tmp.getNonFuelProdCode() != null) nonFuelProGroupObj.setNonFuelProductCode(tmp.getNonFuelProdCode().get(0));
                    
                    nonFuelProdDataList.add(nonFuelProGroupObj);
                    nonFuelProGroupObj = null;
                }
            }
                transaction.setNonFuelProductGroup(nonFuelProdDataList);
                nonFuelProdDataList = null;
            
            /* NEW FIELDS ADDED IN CLASS AFTER MODIFICATIONS IN CreditMessageGSA.XSD - end */
            
            if (wexReqPayAtPump.getVehicleId() != null) {
                transaction.setVehicleId(wexReqPayAtPump.getVehicleId().toString());
            }
            if (wexReqPayAtPump.getLicenseNumber() != null) {
                transaction.setLicenceNumber(wexReqPayAtPump.getLicenseNumber());
            }
            if (wexReqPayAtPump.getDeptNumber() != null) {
                transaction.setDeptNumber(wexReqPayAtPump.getDeptNumber().toString());
            }
            transaction.setJobValueNumber(wexReqPayAtPump.getJobValueNumber());
            transaction.setDataNumber(wexReqPayAtPump.getDataNumber());
            transaction.setUserId(wexReqPayAtPump.getUserId());
            if (wexReqPayAtPump.getProdDetailCount() != null) {
                transaction.setProdDetailCount(wexReqPayAtPump.getProdDetailCount().toString());
            }
            
            if(wexReqPayAtPump.getServiceCode() != null && wexReqPayAtPump.getServiceCode().size() > 0)
                transaction.setServiceCode(wexReqPayAtPump.getServiceCode().get(0));
            
            if(wexReqPayAtPump.getOdometer() != null){
                transaction.setOdoMeter(wexReqPayAtPump.getOdometer());
            }
            
             if(wexReqPayAtPump.getCardSeqNumber()!= null){
                transaction.setCardSeqNumber(wexReqPayAtPump.getCardSeqNumber());
            }
        }
        Message.Request.AddressVerificationService addressVerServc = request.getAddressVerificationService();
        if (addressVerServc != null) {
            transaction.setCardHolderName(addressVerServc.getCardHolderName());
            transaction.setBillingAddress1(addressVerServc.getBillingAddress1());
            transaction.setBillingAddress2(addressVerServc.getBillingAddress2());
            transaction.setBillingCountryCode(addressVerServc.getBillingCountryCode());
            transaction.setShippingCountryCode(addressVerServc.getShippingCountryCode());
            transaction.setShippingAddress(addressVerServc.getShippingAddress1());
            transaction.setShippingAddress(addressVerServc.getShippingAddress2());
            transaction.setBillingZipCode(addressVerServc.getBillingZipCode());
            transaction.setShippingZipCode(addressVerServc.getShippingZipCode());
            try {
                if (addressVerServc.getBillingPhone() != null) {
                    transaction.setBillingPhone(addressVerServc.getBillingPhone().toString());
                }
                if (addressVerServc.getShippingPhone() != null) {
                    transaction.setShippingPhone(addressVerServc.getShippingPhone().toString());
                }
            } catch (NumberFormatException e) {
                LOG.error("NumberFormatException-->AuthorizerException due to invalid phone number or format ");
                throw new AuthorizerException("INVALID_PHONE_NUM");
            }
            transaction.setEmail(addressVerServc.getEmail());
        }
        transaction.setZipCode(request.getZipCode());
        transaction.setUpc(request.getUPC());
        transaction.setEncryptMgmt(request.getEncryptMgmt());
        transaction.setEncryptAlgo(request.getEncryptAlgorithm());
        transaction.setSettleRq(request.getSettleRq());
        transaction.setOriginalOrder(request.getOriginalOrder());
        transaction.setOrigTransId(request.getOrigTransId());
        transaction.setOrigAuthCode(request.getOrigAuthCode());
        BigDecimal amtPreAuth;
        amtPreAuth = request.getAmtPreAuthorized();
        if (amtPreAuth != null) {
            amtPreAuth = amtPreAuth.movePointRight(2);
            long n = amtPreAuth.longValueExact();
            transaction.setAmtPreAuthorized(n);
        }
        transaction.setPaymentType(request.getPymntType());
        if (getAuthTime() != null) {
            String authHour = getAuthTime().substring(0, 8);
            transaction.setAuthHour(authHour);
        }
        // Adding origininal rrn, ordernuber etc
        if (request.getOriginalOrder() != null && !request.getOriginalOrder().isEmpty()) {

            transaction.setOriginalOrder(request.getOriginalOrder());
        }

        if (request.getOrigRRN() != null && !request.getOrigRRN().isEmpty()) {
            transaction.setOrigRRN(request.getOrigRRN().get(0));
        }

        if (request.getOrigTransId() != null && !request.getOrigTransId().isEmpty()) {

            transaction.setOrigTransId(request.getOrigTransId());
        }

        if (request.getOrigAuthCode() != null && !request.getOrigAuthCode().isEmpty()) {

            transaction.setOrigAuthCode(request.getOrigAuthCode());
        }
        LOG.debug("RRN number in class Authorizer..method mapRequest :" + transaction.getRrn());
        LOG.info("Authorizer.mapRequest method ended");
        return transaction;
    }
    
    
    private static String getSystemDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date();
        String ts = dateFormat.format(date);
        return ts;
    }

    private static String getAuthTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = new Date();
        String ts = dateFormat.format(date);
        return ts;
    }

    private static String formatLocalDateTime(XMLGregorianCalendar in) {
        String ts = in.toString();      //2016-11-07T08:54:06
        String out = ts.substring(2, 4)
                + ts.substring(5, 7)
                + ts.substring(8, 10)
                + ts.substring(11, 13)
                + ts.substring(14, 16)
                + ts.substring(17, 19);
        return out;                     //161107085406
    }
    
    private static void encryptValues(Transaction t) {
        String account = t.getAccount();
        account = account.replaceAll("\\w(?=\\w{4})", "");
        t.setAccount(account);
    }
    
    private static Message unmarshalCreditMessage(String content) {
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