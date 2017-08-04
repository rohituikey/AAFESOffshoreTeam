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
import com.aafes.stargate.control.AuthorizerException;
import com.aafes.stargate.control.CassandraSessionFactory;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.control.TranRepository;
import com.aafes.stargate.dao.FacilityDAO;
import com.aafes.stargate.dao.TransactionDAO;
import com.aafes.stargate.gateway.GatewayFactory;
import com.aafes.stargate.gateway.vision.simulator.VisionGatewayStub;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.StrategyType;
import com.aafes.stargate.util.TransactionType;
import com.aafes.starsettler.imported.SettleEntity;
import com.aafes.starsettler.imported.SettleMessageDAO;
import com.aafes.starsettler.imported.SettleStatus;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
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
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author singha
 */
public class WEXSaleRequestTest {

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
    BaseStrategyFactory bsf;
    GatewayFactory gatewayFactory;
    BaseStrategy bs;
    WexGateway wexGateway;
    SettleMessageDAO settleMessageDAO;
    Transaction t;
    String uuid;
    String requestXML;
    Message creditMessage;
    List<SettleEntity> SettleEntityList;

    @Before
    public void setUp() {
        t = new Transaction();
        uuid = "0ee1c509-2c70-4bcd-b261-f94f1fe6c43b";
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
                + " <cm:OrderNumber>9876567</cm:OrderNumber>\n"
                + " <cm:transactionId>10000001</cm:transactionId>\n"
                + " <cm:termId>12</cm:termId>\n"
                + "<cm:Comment>Approved</cm:Comment>\n"
                + "</cm:Header>\n"
                + "<cm:Request RRN=\"200000000001\">\n"
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
                + "<cm:OriginalOrder>9876567</cm:OriginalOrder>\n"
                + "     <cm:DescriptionField>Sale</cm:DescriptionField>\n"
                + "     <cm:origRRN>200000000001</cm:origRRN>\n"
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
        creditMessage = this.unmarshalCreditMessage(requestXML);
        t = mapRequest(creditMessage);
        SettleEntityList = mapToSettle(t);
        factory = new CassandraSessionFactory();
        factory.setSeedHost("localhost");
        factory.connect();

        session = factory.getSession();
        mapper = new MappingManager(session).mapper(Transaction.class);
        tr = new TranRepository();
        td = new TransactionDAO();
        td.setMapper(mapper);
        tr.setTransactionDAO(td);
        td.setCassandraSessionFactory(factory);
        tr.setTransactionDAO(td);
        settleMessageDAO = new SettleMessageDAO();
        Mapper mapper2 = new MappingManager(session).mapper(SettleEntity.class);
        settleMessageDAO.setCassandraSessionFactory(factory);
        settleMessageDAO.setMapper(mapper2);

//        transaction.setMedia("WEX");
//        transaction.setRequestType("Sale");
//        transaction.setInputType("Swiped");
//        transaction.setPan("Pan");
//        transaction.setIdentityUuid("eacbc625-6fef-479e-8738-92adcfed7c65");
//        transaction.setLocalDateTime("2017-07-02T09:04:01");
//        transaction.setSettleIndicator("true");
//        transaction.setOrderNumber("54163254");
//        transaction.setTransactionId("66324154");
//        transaction.setTermId("23");
//        transaction.setAccount("6006496628299904508");
//        transaction.setExpiration("2103");
//        transaction.setCvv("837");
//        transaction.setTrack2("6900460000000000001=20095004100210123");
//        transaction.setAmount((long)50.62);
//        transaction.setCardSeqNumber("12345");
//        transaction.setServiceCode("S");
//        transaction.setCatFlag("1");
//        transaction.setDriverId("12365");
//        transaction.setOdoMeter("36079");
//        transaction.setVehicleId("9213");
//        transaction.setRestrictCode("01");
//        transaction.setProdDetailCount("1");
//        transaction.setFuelProdCode("001");
//        transaction.setQuantity(BigDecimal.valueOf(24.118));
//        transaction.setFuelPrice((long)24.11);
//        transaction.setPricePerUnit(BigDecimal.valueOf(2.099));
//        transaction.setFuelDollerAmount(BigDecimal.valueOf(50.62));
//        transaction.setNonFuelqty(BigDecimal.valueOf(21.11));
//        transaction.setNonFuelProdCode("12");
//        transaction.setNonFuelAmount(BigDecimal.valueOf(12));
//        transaction.setLicenceNumber("1212");
//        transaction.setDeptNumber("1");
//        transaction.setJobValueNumber("1");
//        transaction.setDataNumber("12");
//        transaction.setUserId("121");
//        transaction.setPumpNmbr("23");
//        transaction.setDescriptionField("Sale");
//        transaction.setOrigAuthCode("130362");
//        transaction.setAmtPreAuthorized((long)75.00);
//        
    }

    @Test
    public void testSaleApproved() {

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

    private void setAllDependencies() {
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
        settleMessageDAO = new SettleMessageDAO();
        mapper1 = new MappingManager(session).mapper(SettleEntity.class);
        settleMessageDAO.setMapper(mapper1);
        settleMessageDAO.setCassandraSessionFactory(factory);
       // wexStrategy.setSettleMessageDAO(settleMessageDAO);
        //wexStrategy.setConfigurator(configurator);
        bsf = new BaseStrategyFactory();
        gatewayFactory = new GatewayFactory();
        gatewayFactory.setEnableStub("true");
        wexGateway = new WexGateway();
   //     gatewayFactory.setWexGateway(wexGateway);
     //   bsf.setWexStrategy(wexStrategy);
        bs = bsf.findStrategy(StrategyType.DECA);
        //bsf.setRetailStrategy(retailStrategy);
        bs.setGatewayFactory(gatewayFactory);

        authorizer.setBaseStrategyFactory(bsf);
    }

    private Transaction mapRequest(Message requestMessage) {

        Transaction transaction = new Transaction();
        String[] decimalPart;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date();
        String ts = dateFormat.format(date);
        transaction.setRequestXmlDateTime(ts);

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
                throw new AuthorizerException("INVALID_PAN_TAG");
            }

        }

        if (request.getToken() != null) {
            transaction.setTokenId(request.getToken().value());
            if (request.getToken().value().equalsIgnoreCase("TOKEN")) {
                transaction.setAccountTypeType(request.getToken().value());
                transaction.setTokenId(request.getAccount());
            } else {
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
                    throw new AuthorizerException("INVALID_EXPIRATION_DATE");
                }
            } else {
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
                            throw new AuthorizerException("INVALID_AMOUNT");
                        }
                    }
                    transaction.setAmount(amt.longValueExact());
                } else {
                    throw new AuthorizerException("INVALID_AMOUNT");
                }
            }
        } catch (ArithmeticException e) {
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
            if (wexReqPayAtPump.getQtyPumped() != null) {
                BigDecimal qtyPumped;
                String strQtyPumped;
                long n = 0L;
                if (wexReqPayAtPump.getQtyPumped().size() > 0) {
                    qtyPumped = (BigDecimal) wexReqPayAtPump.getQtyPumped().get(0);
                    strQtyPumped = String.valueOf(qtyPumped);
                    if (null != strQtyPumped && strQtyPumped.contains(".")) {
                        decimalPart = strQtyPumped.split("\\.");
                        if (decimalPart[1] != null && decimalPart[1].length() > 0) {
                            qtyPumped = qtyPumped.movePointRight(decimalPart[1].length());
                        }
                    }
                    n = qtyPumped.longValueExact();
                }
                transaction.setQtyPumped(n);
            }
            if (wexReqPayAtPump.getFuelPrice() != null) {
                BigDecimal fuelPrice;
                String strFuelPrice;
                long n = 0L;
                if (wexReqPayAtPump.getFuelPrice().size() > 0) {
                    fuelPrice = (BigDecimal) wexReqPayAtPump.getFuelPrice().get(0);
                    strFuelPrice = String.valueOf(fuelPrice);
                    if (null != strFuelPrice && strFuelPrice.contains(".")) {
                        decimalPart = strFuelPrice.split("\\.");
                        if (decimalPart[1].length() > 0) {
                            fuelPrice = fuelPrice.movePointRight(decimalPart[1].length());
                        }
                    }
                    n = fuelPrice.longValueExact();
                }
                transaction.setFuelPrice(n);
            }
            if (wexReqPayAtPump.getFuelProdCode() != null
                    && wexReqPayAtPump.getFuelProdCode().size() > 0 && wexReqPayAtPump.getFuelProdCode().get(0) != null) {
                transaction.setFuelProdCode(wexReqPayAtPump.getFuelProdCode().get(0).toString());
            }

            //added lines for new fields mapping starts here
            if (wexReqPayAtPump.getNonFuelProdCode() != null && wexReqPayAtPump.getNonFuelProdCode().size() > 0
                    && wexReqPayAtPump.getNonFuelProdCode().get(0) != null) {
                transaction.setNonFuelProdCode(wexReqPayAtPump.getNonFuelProdCode().get(0).toString());
            }
            if (wexReqPayAtPump.getCATFlag() != null && wexReqPayAtPump.getCATFlag().size() > 0) {
                transaction.setCatFlag(wexReqPayAtPump.getCATFlag().get(0));
            }
            if (wexReqPayAtPump.getPricePerUnit() != null) {
                transaction.setPricePerUnit(wexReqPayAtPump.getPricePerUnit());
            }
            if (wexReqPayAtPump.getFuelDollarAmount() != null && wexReqPayAtPump.getFuelDollarAmount().size() > 0) {
                transaction.setFuelDollerAmount(wexReqPayAtPump.getFuelDollarAmount().get(0));
            }
            //added lines for new fields mapping ends here

//            if (wexReqPayAtPump.getUnitOfMeas() != null) {
            //                transaction.setUnitOfMeas(wexReqPayAtPump.getUnitOfMeas().toString());
            //            }
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
//          TODO:  transaction.setContact(request);
            if (wexReqPayAtPump.getProdDetailCount() != null) {
                transaction.setProdDetailCount(wexReqPayAtPump.getProdDetailCount().toString());
            }

            if (wexReqPayAtPump.getServiceCode() != null && wexReqPayAtPump.getServiceCode().size() > 0) {
                transaction.setServiceCode(wexReqPayAtPump.getServiceCode().get(0));
            }

            if (wexReqPayAtPump.getNonFuelAmount() != null) {
                BigDecimal nonFuelPrice = new BigDecimal("0");
                String strNonFuelPrice;
                long n = 0L;
                if (wexReqPayAtPump.getNonFuelAmount().size() > 0) {
                    nonFuelPrice = (BigDecimal) wexReqPayAtPump.getNonFuelAmount().get(0);
                    strNonFuelPrice = String.valueOf(nonFuelPrice);
                    if (null != strNonFuelPrice && strNonFuelPrice.contains(".")) {
                        decimalPart = String.valueOf(nonFuelPrice).split("\\.");
                        if (decimalPart[1] != null && decimalPart[1].length() > 0) {
                            nonFuelPrice = nonFuelPrice.movePointRight(decimalPart[1].length());
                        }
                        n = nonFuelPrice.longValueExact();
                    }
                }
                transaction.setNonFuelAmount(nonFuelPrice);
            }

            if (wexReqPayAtPump.getOdometer() != null) {
                transaction.setOdoMeter(wexReqPayAtPump.getOdometer());
            }

            if (wexReqPayAtPump.getCardSeqNumber() != null) {
                transaction.setCardSeqNumber(wexReqPayAtPump.getCardSeqNumber());
            }

            if (wexReqPayAtPump.getQuantity() != null) {
                BigDecimal quantity = new BigDecimal("0");
                String strQuantity;
                long n = 0L;
                if (wexReqPayAtPump.getQuantity().size() > 0) {
                    quantity = (BigDecimal) wexReqPayAtPump.getQuantity().get(0);
                    strQuantity = String.valueOf(quantity);
                    if (null != strQuantity && strQuantity.contains(".")) {
                        decimalPart = String.valueOf(quantity).split("\\.");
                        if (decimalPart[1] != null && decimalPart[1].length() > 0) {
                            quantity = quantity.movePointRight(decimalPart[1].length());
                        }
                        n = quantity.longValueExact();
                    }
                }
                transaction.setQuantity(quantity);
            }

            if (wexReqPayAtPump.getNonFuelQty() != null) {
                BigDecimal nonFuelQty = new BigDecimal("0");
                String strNonFuelQty;
                long n = 0L;
                if (wexReqPayAtPump.getNonFuelQty().size() > 0) {
                    nonFuelQty = (BigDecimal) wexReqPayAtPump.getNonFuelQty().get(0);
                    strNonFuelQty = String.valueOf(nonFuelQty);
                    if (null != strNonFuelQty && strNonFuelQty.contains(".")) {
                        decimalPart = String.valueOf(nonFuelQty).split("\\.");
                        if (decimalPart[1] != null && decimalPart[1].length() > 0) {
                            nonFuelQty = nonFuelQty.movePointRight(decimalPart[1].length());
                        }
                        n = nonFuelQty.longValueExact();
                    }
                }
                transaction.setNonFuelqty(nonFuelQty);
            }
        }
        //*Uncommented from 502 to 551 and modified some code
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
        return transaction;
    }

    private String formatLocalDateTime(XMLGregorianCalendar in) {
        String ts = in.toString();      //2016-11-07T08:54:06
        String out = ts.substring(2, 4)
                + ts.substring(5, 7)
                + ts.substring(8, 10)
                + ts.substring(11, 13)
                + ts.substring(14, 16)
                + ts.substring(17, 19);
        return out;                     //161107085406
    }

    private List<SettleEntity> mapToSettle(Transaction t) {

        List<SettleEntity> settleEntityList = new ArrayList<SettleEntity>();
        SettleEntity settleEntity = new SettleEntity();

        settleEntity.setTransactionId(t.getTransactionId());
        settleEntity.setReceiveddate(getSystemDate());
        settleEntity.setOrderNumber(t.getOrderNumber());
        settleEntity.setSettleDate(this.getSystemDate());
        settleEntity.setOrderDate(this.getSystemDate());
        //Card Type not available    
        settleEntity.setTransactionType(t.getTransactiontype());
        //ClientLineId not available  
        settleEntity.setClientLineId(t.getTransactionId());
        settleEntity.setIdentityUUID(t.getIdentityUuid());
        //LineId not available 
        //ShipId not available 
        settleEntity.setRrn(t.getRrn());
        settleEntity.setPaymentAmount(Long.toString(t.getAmount()));
        //where to map t.getLocalDateTime()
        settleEntity.setSettlestatus(SettleStatus.Ready_to_settle);
        settleEntity.setCardType(t.getMedia());
        settleEntity.setSettlePlan(t.getPlanNumber());
        settleEntity.setAuthNum(t.getAuthNumber());

        if (t.getAmount() < 0) {
            settleEntity.setTransactionType(TransactionType.Refund);
        } else if (t.getAmount() >= 0) {
            settleEntity.setTransactionType(TransactionType.Deposit);
        }
        if (t.getTokenId() != null && !t.getTokenId().trim().isEmpty()) {
            settleEntity.setCardToken(t.getTokenId());
            settleEntity.setTokenBankName(t.getTokenBankName());
        }
        settleEntityList.add(settleEntity);
        return settleEntityList;
    }

    private String getSystemDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String ts = dateFormat.format(date);
        return ts;
    }

}
