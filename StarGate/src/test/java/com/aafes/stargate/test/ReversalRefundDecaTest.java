//
//package com.aafes.stargate.test;
//
//import com.aafes.credit.Message;
//import com.aafes.stargate.authorizer.BaseStrategy;
//import com.aafes.stargate.authorizer.BaseStrategyFactory;
//import com.aafes.stargate.authorizer.RetailStrategy;
//import com.aafes.stargate.authorizer.entity.Facility;
//import com.aafes.stargate.authorizer.entity.Transaction;
//import com.aafes.stargate.control.Authorizer;
//import com.aafes.stargate.control.CassandraSessionFactory;
//import com.aafes.stargate.control.Configurator;
//import com.aafes.stargate.control.TranRepository;
//import com.aafes.stargate.dao.FacilityDAO;
//import com.aafes.stargate.dao.TransactionDAO;
//import com.aafes.stargate.gateway.GatewayFactory;
//import com.aafes.stargate.gateway.vision.simulator.VisionGatewayStub;
//import com.aafes.stargate.util.RequestType;
//import com.aafes.stargate.util.ResponseType;
//import com.aafes.stargate.util.StrategyType;
//import com.aafes.stargate.util.TransactionType;
//import com.aafes.starsettler.imported.SettleEntity;
//import com.aafes.starsettler.imported.SettleMessageDAO;
//import com.aafes.starsettler.imported.SettleStatus;
//import com.datastax.driver.core.ResultSet;
//import com.datastax.driver.core.Session;
//import com.datastax.driver.mapping.Mapper;
//import com.datastax.driver.mapping.MappingManager;
//import java.io.StringReader;
//import java.math.BigDecimal;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Unmarshaller;
//import javax.xml.datatype.XMLGregorianCalendar;
//import static org.junit.Assert.assertEquals;
//import org.junit.Before;
//import org.junit.Test;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
///**
// *
// * @author alugumetlas
// */
//public class ReversalRefundDecaTest {
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
//    RetailStrategy retailStrategy;
//    BaseStrategyFactory bsf;
//    GatewayFactory gatewayFactory;
//    BaseStrategy bs;
//    VisionGatewayStub visionGatewaySimulator;
//    SettleMessageDAO settleMessageDAO;
//    Transaction t;
//    String uuid;
//    String requestXML;
//    Message creditMessage;
//    List<SettleEntity> SettleEntityList;
//
//    @Before
//    public void setUp() {
//        t = new Transaction();
//        uuid = "0ee1c509-2c70-4bcd-b261-f94f1fe6c43b";
//        requestXML
//                = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
//                + " <cm:Message\n"
//                + "TypeCode=\"Request\"\n "
//                + "MajorVersion=\"3\"\n"
//                + "MinorVersion=\"1\" FixVersion=\"0\"\n"
//                + "xmlns:cm='http://www.aafes.com/credit'>\n"
//                + "<cm:Header> \n"
//                + "<cm:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</cm:IdentityUUID>\n"
//                + "<cm:LocalDateTime>2017-05-31T13:31:33</cm:LocalDateTime>\n"
//                + "<cm:SettleIndicator>true</cm:SettleIndicator>\n"
//                + "<cm:OrderNumber>5000001</cm:OrderNumber>\n"
//                + "<cm:transactionId>60000001</cm:transactionId>\n"
//                + "<cm:termId>12</cm:termId>\n"
//                + "<cm:Comment>Approved</cm:Comment>\n"
//                + "</cm:Header>\n"
//                + "<cm:Request RRN=\"700000000001\">\n"
//                + "<cm:Media>Milstar</cm:Media>\n"
//                + "<cm:Reversal>Refund</cm:Reversal>\n"
//                + "<cm:InputType>Keyed</cm:InputType>\n"
//                + "<cm:Pan>Pan</cm:Pan>\n"
//                + "<cm:Account>6006496628299904508</cm:Account>\n"
//                + "<cm:Expiration>2103</cm:Expiration>\n"
//                + "<cm:CardVerificationValue>837</cm:CardVerificationValue>\n"
//                + "<cm:TrackData1>%B6019450000289697^MILSTARRET0001^2009000000000000100000000000000?</cm:TrackData1>\n"
//                + "<cm:AmountField>2500</cm:AmountField>\n"
//                + "<cm:PlanNumbers>\n"
//                + "<cm:PlanNumber>10001</cm:PlanNumber>\n"
//                + "</cm:PlanNumbers> \n"
//                + "<cm:DescriptionField>REFUND</cm:DescriptionField>\n"
//                + "<cm:AddressVerificationService>\n"
//                + "<cm:CardHolderName>John Doe</cm:CardHolderName>\n"
//                + "<cm:BillingAddress1>1222</cm:BillingAddress1>\n"
//                + "<cm:BillingCountryCode>US</cm:BillingCountryCode>\n"
//                + "<cm:BillingZipCode>12345</cm:BillingZipCode>\n"
//                + "<cm:Email>johndoe@kk.com</cm:Email>\n"
//                + "<cm:BillingPhone>1122334455</cm:BillingPhone>\n"
//                + "<cm:ShippingPhone>1122334455</cm:ShippingPhone>\n"
//                + "</cm:AddressVerificationService>\n"
//                + "</cm:Request>\n"
//                + "</cm:Message>";
//        creditMessage = this.unmarshalCreditMessage(requestXML);
//        t = mapRequest(creditMessage);
//        SettleEntityList = mapToSettle(t);
//        factory = new CassandraSessionFactory();
//        factory.setSeedHost("localhost");
//        factory.connect();
//
//        session = factory.getSession();
//        mapper = new MappingManager(session).mapper(Transaction.class);
//        tr = new TranRepository();
//        td = new TransactionDAO();
//        td.setMapper(mapper);
//        tr.setTransactionDAO(td);
//        td.setCassandraSessionFactory(factory);
//        tr.setTransactionDAO(td);
//        settleMessageDAO = new SettleMessageDAO();
//        Mapper mapper2 = new MappingManager(session).mapper(SettleEntity.class);
//        settleMessageDAO.setCassandraSessionFactory(factory);
//        settleMessageDAO.setMapper(mapper2);
//
//    }
//
//    @Test
//    public void testForNoPriorTransaction() {
//        //  Message creditMessage = this.unmarshalCreditMessage(requestXML);
//        this.setAllDependencies();
//        Message result = authorizer.authorize(creditMessage);
//        assertEquals("NO_AUTHORIZATION_FOUND_FOR_REVERSAL", result.getResponse().get(0).getDescriptionField());
//    }
//
//    @Test
//    public void testProcessRequest() {
//        this.setAllDependencies();
//        settleMessageDAO.save(SettleEntityList);
//        t.setResponseType("APPROVED");
//        t.setComment("APPROVED");
//        t.setRequestType("Refund");
//        t.setReversal("");
//        td.save(t);
//        this.setAllDependencies();
//        Message result = authorizer.authorize(creditMessage);
//        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
//        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
//        assertEquals(ResponseType.APPROVED, result.getResponse().get(0).getResponseType());
//
//    }
//
//    @Test
//    public void testForAlreadySettled() {
//        SettleEntityList.get(0).setSettlestatus("Done");
//        settleMessageDAO.save(SettleEntityList);
//        t.setRequestType(RequestType.REFUND);
//        t.setResponseType("APPROVED");
//        td.save(t);
//        this.setAllDependencies();
//        Message result = authorizer.authorize(creditMessage);
//        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
//        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
//        assertEquals("TRANSACTION_ALREADY_SETTLED", result.getResponse().get(0).getDescriptionField());
//    }
//
//    @Test
//    public void testAllRedayReversed() {
//        t.setResponseType("APPROVED");
//        t.setRequestType(RequestType.REVERSAL);
//        td.save(t);
//        this.setAllDependencies();
//        Message result = authorizer.authorize(creditMessage);
//        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
//        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
//        assertEquals("TRANSACTION_ALREADY_REVERSED", result.getResponse().get(0).getDescriptionField());
//
//    }
//
//    private void setAllDependencies() {
//        authorizer = new Authorizer();
//        configurator = new Configurator();
//        authorizer.setConfigurator(configurator);
//
//        facilityDAO = mock(FacilityDAO.class);
//        facility = new Facility();
//        facility.setDeviceType("RPOS");
//        facility.setFacility("3740152100");
//        facility.setStrategy("Deca");
//        facility.setTokenBankName("Deca006");
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
//        retailStrategy = new RetailStrategy();
//        settleMessageDAO = new SettleMessageDAO();
//        mapper1 = new MappingManager(session).mapper(SettleEntity.class);
//        settleMessageDAO.setMapper(mapper1);
//        settleMessageDAO.setCassandraSessionFactory(factory);
//        retailStrategy.setSettleMessageDAO(settleMessageDAO);
//        retailStrategy.setConfigurator(configurator);
//        bsf = new BaseStrategyFactory();
//        gatewayFactory = new GatewayFactory();
//        gatewayFactory.setEnableStub("true");
//        visionGatewaySimulator = new VisionGatewayStub();
//        gatewayFactory.setVisionGatewayStub(visionGatewaySimulator);
//        bsf.setRetailStrategy(retailStrategy);
//        bs = bsf.findStrategy(StrategyType.DECA);
//        //bsf.setRetailStrategy(retailStrategy);
//        bs.setGatewayFactory(gatewayFactory);
//
//        authorizer.setBaseStrategyFactory(bsf);
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
//    private Transaction mapRequest(Message requestMessage) {
//        Transaction transaction = new Transaction();
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//        Date date = new Date();
//        String ts = dateFormat.format(date);
//        transaction.setRequestXmlDateTime(ts);
//
//        Message.Header header = requestMessage.getHeader();
//
//        // Mapping Header Fields
//        if (header.getIdentityUUID() != null) {
//            transaction.setIdentityUuid(header.getIdentityUUID());
//        }
//        transaction.setLocalDateTime(formatLocalDateTime(header.
//                getLocalDateTime()));
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
//            }
//        }
//        if (request.getToken() != null) {
//            transaction.setTokenId(request.getToken().value());
//            if (request.getToken().value().equalsIgnoreCase("TOKEN")) {
//                transaction.setAccountTypeType(request.getToken().value());
//                transaction.setTokenId(request.getAccount());
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
//            }
//            transaction.setExpiration(request.getExpiration().toString());
//        }
//        //TODO : check amount handling in MPG
//        BigDecimal amt;
//        amt = request.getAmountField();
//        if (amt != null) {
//            amt = amt.movePointRight(2);
//            if (amt.longValueExact() <= 9999999) {
//                if (transaction.getRequestType() != null
//                        && !transaction.getRequestType().trim().isEmpty()
//                        && !transaction.getRequestType().equalsIgnoreCase(RequestType.REFUND)) {
//
//                }
//                transaction.setAmount(amt.longValueExact());
//            }
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
//        Message.Request.AddressVerificationService addressVerServc = request.getAddressVerificationService();
//        if (addressVerServc != null) {
//            transaction.setCardHolderName(addressVerServc.getCardHolderName());
//            transaction.setBillingAddress1(addressVerServc.getBillingAddress1());
//            transaction.setBillingAddress2(addressVerServc.getBillingAddress2());
//            transaction.setBillingCountryCode(addressVerServc.getBillingCountryCode());
//            transaction.setShippingCountryCode(addressVerServc.getShippingCountryCode());
//            transaction.setShippingAddress(addressVerServc.getShippingAddress1());
//            transaction.setShippingAddress(addressVerServc.getShippingAddress2());
//            transaction.setBillingZipCode(addressVerServc.getBillingZipCode());
//            transaction.setShippingZipCode(addressVerServc.getShippingZipCode());
//            try {
//                if (addressVerServc.getBillingPhone() != null) {
//                    transaction.setBillingPhone(addressVerServc.getBillingPhone().toString());
//                }
//                if (addressVerServc.getShippingPhone() != null) {
//                    transaction.setShippingPhone(addressVerServc.getShippingPhone().toString());
//                }
//            } catch (NumberFormatException e) {
//
//            }
//            transaction.setEmail(addressVerServc.getEmail());
//        }
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
//        if (request.getOrigRRN() != null && !request.getOrigRRN().isEmpty()) {
//            transaction.setOrigRRN(request.getOrigRRN().get(0));
//        }
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
//
//    private String formatLocalDateTime(XMLGregorianCalendar in) {
//        String ts = in.toString();      //2016-11-07T08:54:06
//        String out = ts.substring(2, 4)
//                + ts.substring(5, 7)
//                + ts.substring(8, 10)
//                + ts.substring(11, 13)
//                + ts.substring(14, 16)
//                + ts.substring(17, 19);
//        return out;                     //161107085406
//    }
//
//    private List<SettleEntity> mapToSettle(Transaction t) {
//
//        List<SettleEntity> settleEntityList = new ArrayList<SettleEntity>();
//        SettleEntity settleEntity = new SettleEntity();
//
//        settleEntity.setTransactionId(t.getTransactionId());
//        settleEntity.setReceiveddate(getSystemDate());
//        settleEntity.setOrderNumber(t.getOrderNumber());
//        settleEntity.setSettleDate(this.getSystemDate());
//        settleEntity.setOrderDate(this.getSystemDate());
//        //Card Type not available    
//        settleEntity.setTransactionType(t.getTransactiontype());
//        //ClientLineId not available  
//        settleEntity.setClientLineId(t.getTransactionId());
//        settleEntity.setIdentityUUID(t.getIdentityUuid());
//        //LineId not available 
//        //ShipId not available 
//        settleEntity.setRrn(t.getRrn());
//        settleEntity.setPaymentAmount(Long.toString(t.getAmount()));
//        //where to map t.getLocalDateTime()
//        settleEntity.setSettlestatus(SettleStatus.Ready_to_settle);
//        settleEntity.setCardType(t.getMedia());
//        settleEntity.setSettlePlan(t.getPlanNumber());
//        settleEntity.setAuthNum(t.getAuthNumber());
//
//        if (t.getAmount() < 0) {
//            settleEntity.setTransactionType(TransactionType.Refund);
//        } else if (t.getAmount() >= 0) {
//            settleEntity.setTransactionType(TransactionType.Deposit);
//        }
//        if (t.getTokenId() != null && !t.getTokenId().trim().isEmpty()) {
//            settleEntity.setCardToken(t.getTokenId());
//            settleEntity.setTokenBankName(t.getTokenBankName());
//        }
//        settleEntityList.add(settleEntity);
//        return settleEntityList;
//    }
//
//    private String getSystemDate() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = new Date();
//        String ts = dateFormat.format(date);
//        return ts;
//    }
//}
