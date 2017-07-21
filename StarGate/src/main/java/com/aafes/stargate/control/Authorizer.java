package com.aafes.stargate.control;

import com.aafes.credit.AccountTypeType;
import com.aafes.credit.AddressVerificationResponseType;
import com.aafes.credit.Message;
import com.aafes.credit.Message.Header;
import com.aafes.credit.Message.Request;
import com.aafes.credit.Message.Response;
import com.aafes.stargate.dao.FacilityDAO;
import com.aafes.stargate.authorizer.BaseStrategy;
import com.aafes.stargate.authorizer.BaseStrategyFactory;
import com.aafes.stargate.authorizer.entity.Facility;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.dao.TransactionDAO;
import com.aafes.stargate.gateway.fdms.FDMSStub;
import com.aafes.stargate.tokenizer.TokenBusinessService;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.xml.datatype.XMLGregorianCalendar;
import org.slf4j.LoggerFactory;

/**
 * This class contains business methods for performing all gateway requests.
 *
 * @author ganjis
 */
@Stateless
public class Authorizer {

    @EJB
    private TranRepository tranRepository;
    @EJB
    private FacilityDAO facilityDAO;
    @EJB
    private BaseStrategyFactory baseStrategyFactory;
    @EJB
    private TokenBusinessService tokenBusinessService;
    @EJB
    private Configurator configurator;
    @Inject
    private String maskAccount;
    @Inject
    private String enableFDMSStub;
    @EJB
    private FDMSStub fDMSStub;
    @EJB
    private TransactionDAO transactionDAO;
    private boolean isDuplicateTransaction = false;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Authorizer.class.getSimpleName());

    public Message authorize(Message cm) {

        LOG.info("Authorizer.authorize method started");
        Transaction t = new Transaction();
        boolean bTokenCall = false;
        boolean bDoneMApreq = false;
        try {
            mapRequest(t, cm);
            bDoneMApreq = true;
            findFacility(t);
            if (t.getFacility() == null || t.getFacility().isEmpty()
                    || t.getStrategy() == null || t.getStrategy().isEmpty()) {
                LOG.info("UUID not found in facmapper .ResponseType " + t.getRrn());
                t.setReasonCode(configurator.get("INVALID_UUID"));
                t.setDescriptionField("INVALID_UUID");
                t.setResponseType(ResponseType.DECLINED);
                mapResponse(t, cm);
                return cm;
            }

            /**
             * Remove if condition after Junit test case modified for Authorizer
             */
            if (tokenBusinessService != null
                    && t.getAccountTypeType() != null
                    && t.getAccountTypeType().equalsIgnoreCase(AccountTypeType.TOKEN.value())
                    && !t.getRequestType().equalsIgnoreCase(RequestType.ISSUE)) {
                bTokenCall = tokenBusinessService.lookUpAccount(t);
                if (!bTokenCall) {
                    LOG.info("Token Not found.ResponseType" + t.getRrn());
                    t.setReasonCode(configurator.get("TOKEN_NOTFOUND"));
                    t.setResponseType(ResponseType.DECLINED);
                    t.setDescriptionField("TOKEN_NOTFOUND");
                    mapResponse(t, cm);
                    return cm;
                }
            }
            Transaction storedTran = null;
            if (t.getReversal() != null
                    && (t.getReversal().equalsIgnoreCase(RequestType.SALE)
                    || t.getReversal().equalsIgnoreCase(RequestType.REFUND))) {
                LOG.info("Reversal........");
                storedTran = tranRepository.find(t.getIdentityUuid(), t.getRrn(), RequestType.REVERSAL);

                if (storedTran != null && storedTran.getResponseType().equalsIgnoreCase(ResponseType.APPROVED)) {
                    storedTran.setReasonCode(configurator.get("TRANSACTION_ALREADY_REVERSED"));
                    storedTran.setResponseType(ResponseType.DECLINED);
                    storedTran.setDescriptionField("TRANSACTION_ALREADY_REVERSED");
                } else {
                    storedTran = null;
                }
            } else {
                storedTran = tranRepository.find(t.getIdentityUuid(), t.getRrn(), t.getRequestType());
            }

            if (storedTran == null) {

                isDuplicateTransaction = false;
                /**
                 * FDMS Stub for specific UUIDs and credit cards.
                 */
                LOG.info("enableFDMSStub : " + enableFDMSStub);
                if (enableFDMSStub != null
                        && enableFDMSStub.equalsIgnoreCase("true")) {
                    boolean bFoundCard = fDMSStub.StoreAndReturnResponse(t);
                    if (bFoundCard) {
                        LOG.info("Found matched record.");
                        encryptValues(t);
                        tranRepository.save(t);
                        mapResponse(t, cm);
                        return cm;
                    } else {
                        LOG.info("No record found.");
                    }
                }
                LOG.info("Transaction not found. So processing to find Strategy " + t.getRrn());
                BaseStrategy baseStrategy = baseStrategyFactory.findStrategy(t.getStrategy());
                if (baseStrategy != null) {
                    Transaction authTran = checkReversalTransaction(t);
                    Transaction authTranCancel = checkTransactionCancel(t);
//                    if ((MediaType.MIL_STAR.equalsIgnoreCase(t.getMedia())
//                            || MediaType.GIFT_CARD.equalsIgnoreCase(t.getMedia()))
//                            && (t.getReversal() != null
//                            && t.getReversal().equalsIgnoreCase(RequestType.REVERSAL))) {
//                        LOG.info("Don't call Vision / SVS for MilStar / GiftCard Reversals." + t.getRrn());
//                    } else {
//                        t = baseStrategy.processRequest(t);
//                    }
                    t = baseStrategy.processRequest(t);
                    mapResponse(t, cm);
                    t.setResponseXmlDateTime(getSystemDateTime());
                    if (t.getReversal() != null
                            && t.getReversal().equalsIgnoreCase(RequestType.REVERSAL)
                            && ResponseType.APPROVED.equalsIgnoreCase(t.getResponseType())) {
                        LOG.info("Saving and updating transaction.....");
                        authTran.setReversal(RequestType.REVERSAL);
                        tranRepository.saveAndUpdate(t, authTran);
                    } else if (t.getReversal() != null
                            && t.getRequestType().equals(RequestType.TRNCANCEL)
                            && ResponseType.APPROVED.equalsIgnoreCase(t.getResponseType())) {
                        LOG.info("Saving and updating transaction.....");
                        tranRepository.saveAndUpdate(t, authTranCancel);
                    } else {
                        LOG.info("Saving transaction....." + t.getRrn());
                        encryptValues(t);
                        tranRepository.save(t);
                    }
                } else {
                    // TODO: Confirm response description
                    t.setReasonCode(configurator.get("INVALID_UUID"));
                    t.setDescriptionField("INVALID_UUID");
                    t.setResponseType(ResponseType.DECLINED);
                    mapResponse(t, cm);
                    return cm;
                }
            } else {
                LOG.info("Transaction found. So replying from the cache..." + t.getRrn());
                isDuplicateTransaction = true;
                mapResponse(storedTran, cm);
            }
        } catch (AuthorizerException e) {
            LOG.error("Exception caught" + e.toString());
            t.setReasonCode(configurator.get(e.getMessage()));
            t.setDescriptionField(e.getMessage());
            t.setResponseType(ResponseType.DECLINED);
            mapResponse(t, cm);
            return cm;
        } catch (ProcessingException e) {
            LOG.error("Exception caught" + e.toString());
            t.setReasonCode(configurator.get("TOKENIZER_CONNECTION_ERROR"));
            t.setDescriptionField("TOKENIZER_CONNECTION_ERROR");
            t.setResponseType(ResponseType.DECLINED);
            mapResponse(t, cm);
            return cm;
        } catch (Exception e) {
            LOG.error("Exception caught" + e.toString() + "  INTERNAL_SERVER_ERROR");
            t.setReasonCode(configurator.get("INTERNAL_SERVER_ERROR"));
            t.setDescriptionField("INTERNAL_SERVER_ERROR");
            if (!bTokenCall && bDoneMApreq) {
                t.setReasonCode(configurator.get("TOKENIZER_CONNECTION_ERROR"));
                t.setDescriptionField("TOKENIZER_CONNECTION_ERROR");
            }
            t.setResponseType(ResponseType.DECLINED);
            if (cm.getResponse() != null) {
                cm.getResponse().clear();
            }
            mapResponse(t, cm);
            return cm;
        }
        LOG.info("Authorizer.authorize method exit");
        return cm;
    }

    private Transaction checkReversalTransaction(Transaction t) {

        Transaction authTran = null;

        if (t.getReversal() != null
                && (t.getReversal().equalsIgnoreCase(RequestType.SALE)
                || t.getReversal().equalsIgnoreCase(RequestType.REFUND))) {

            LOG.info("Reversal request......." + t.getRrn());

            if (MediaType.GIFT_CARD.equalsIgnoreCase(t.getMedia())) {

                LOG.info("Gift Card Reversal request.......");

                authTran = tranRepository.find(t.getIdentityUuid(),
                        t.getRrn(), RequestType.FINAL_AUTH);
            } else {
                LOG.info("FDMS / Vision Reversal request.......");

                authTran = tranRepository.find(t.getIdentityUuid(),
                        t.getRrn(), t.getReversal());
            }

            if (authTran == null) {
                throw new AuthorizerException("NO_AUTHORIZATION_FOUND_FOR_REVERSAL");
            }

            //OrderNumber, CardNumber, Mop, Amount
            if (authTran.getOrderNumber() != null
                    && t.getOrderNumber() != null
                    && (!authTran.getOrderNumber().equals(t.getOrderNumber()))) {
                LOG.info("Order Number is not matching.......");
                throw new AuthorizerException("NO_AUTHORIZATION_FOUND_FOR_REVERSAL");
            }

//            if(authTran.getAccount()!=null
//                    && t.getAccount()!=null
//                    && (!authTran.getAccount().equals(t.getAccount()))  ){
//                LOG.info("Card Number is not matching.......");
//                throw new AuthorizerException("NO_AUTHORIZATION_FOUND_FOR_REVERSAL");
//            }
            if (authTran.getMedia() != null
                    && t.getMedia() != null
                    && (!authTran.getMedia().equals(t.getMedia()))) {
                LOG.info("MOP is not matching.......");
                throw new AuthorizerException("NO_AUTHORIZATION_FOUND_FOR_REVERSAL");
            }

            if (authTran.getAmount() != t.getAmount()) {
                LOG.info("Amount is not matching.......");
                throw new AuthorizerException("NO_AUTHORIZATION_FOUND_FOR_REVERSAL");
            }

            t.setResponseDate(authTran.getResponseDate());
            t.setAuthNumber(authTran.getAuthNumber());
            t.setReversal(RequestType.REVERSAL);
            t.setRequestType(RequestType.REVERSAL);

            if (MediaType.MIL_STAR.equalsIgnoreCase(t.getMedia())) {
                LOG.info("Vision Reversal Response.......");

                t.setReasonCode("000");
                t.setResponseType(ResponseType.APPROVED);
            }

            if (MediaType.GIFT_CARD.equalsIgnoreCase(t.getMedia())) {
                LOG.info("Gift Card Reversal Response.......");

                t.setReasonCode("100");
                t.setResponseType(ResponseType.APPROVED);
            }

        } else {

            LOG.info("Auth request.......");
        }
        return authTran;

    }

    private void encryptValues(Transaction t) {
        if ("true".equalsIgnoreCase(this.maskAccount)
                && t.getAccount() != null
                && !t.getAccount().trim().isEmpty()) {
            String account = t.getAccount();
            account = account.replaceAll("\\w(?=\\w{4})", "");
            t.setAccount(account);

//            String expiryDate = t.getExpiration();
//            String cardNumber = t.getAccount();
//
//            String baseDir = System.getProperty("jboss.server.config.dir");
//            String keysPath = baseDir + "/crypto/keys";
//            String logPath = baseDir + "/crypto.log4j.properties";
//
//            Encryptor encryptor = new Encryptor(keysPath, logPath);
//
//            expiryDate = encryptor.encrypt(expiryDate);
//            cardNumber = encryptor.encrypt(cardNumber);
//            
//            t.setExpiration(expiryDate);
//            t.setAccount(cardNumber);
        }

    }

    private void findFacility(Transaction t) {
        LOG.info("Authorizer.findFacility method started");
        String uuid = t.getIdentityUuid();
        Facility facility = facilityDAO.get(uuid);
        if (facility != null) {
            t.setFacility(facility.getFacility());
            t.setStrategy(facility.getStrategy());
            t.setDeviceType(facility.getDeviceType());
            t.setTokenBankName(facility.getTokenBankName());
        }
        LOG.info("Authorizer.findFacility method ended");
    }

    // Map the inbound Message to a Transaction.
    private Transaction mapRequest(Transaction transaction, Message requestMessage) {
        LOG.info("Authorizer.mapRequest method started");
        transaction.setRequestXmlDateTime(this.getSystemDateTime());

        Header header = requestMessage.getHeader();

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
        Request request = requestMessage.getRequest().get(0);
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
//        if (request.getEssoRequestData() != null) {
//            BigDecimal pumpAmt;
//            pumpAmt = request.getEssoRequestData().getPumpPrice();
//            if (pumpAmt != null) {
//                pumpAmt = pumpAmt.movePointRight(2);
//                long n = pumpAmt.longValueExact();
//                transaction.setPumpPrice(n);
//            }
//            if (request.getEssoRequestData().getProductCode() != null) {
//                transaction.setProductCode(request.getEssoRequestData().getProductCode().toString());
//            }
//            if (request.getEssoRequestData().getOdometer() != null) {
//                transaction.setOdoMeter(request.getEssoRequestData().getOdometer().toString());
//            }
//            if (request.getEssoRequestData().getRationAmt() != null) {
//                transaction.setRationAmt(request.getEssoRequestData().getRationAmt().toString());
//            }
//            if (request.getEssoRequestData().getUnitMeas() != null) {
//                transaction.setUnitMeas(request.getEssoRequestData().getUnitMeas().toString());
//            }
//        }

//        BigDecimal essoAmt;
//        essoAmt = request.getEssoLoadAmt();
//        if (essoAmt != null) {
//            essoAmt = essoAmt.movePointRight(2);
//            long n = essoAmt.longValueExact();
//            transaction.setEssoLoadAmount(n);
//        }
//        if (request.getPumpNmbr() != null) {
//            transaction.setPumpNmbr(request.getPumpNmbr().toString());
//        }
//        if (request.getWEXRequestPayAtPumpData() != null) {
//            Request.WEXRequestPayAtPumpData wexReqPayAtPump = request.getWEXRequestPayAtPumpData();
//            if (wexReqPayAtPump.getDriverId() != null) {
//                transaction.setDriverId(wexReqPayAtPump.getDriverId().toString());
//            }
//            if (wexReqPayAtPump.getRestrictCode() != null) {
//                transaction.setRestrictCode(wexReqPayAtPump.getRestrictCode().toString());
//            }
//            if (wexReqPayAtPump.getQtyPumped() != null) {
//                BigDecimal qtyPumped;
//                qtyPumped = wexReqPayAtPump.getQtyPumped();
//                qtyPumped = qtyPumped.movePointRight(2);
//                long n = qtyPumped.longValueExact();
//                transaction.setQtyPumped(n);
//            }
//            if (wexReqPayAtPump.getFuelPrice() != null) {
//                BigDecimal fuelPrice;
//                fuelPrice = wexReqPayAtPump.getFuelPrice();
//                fuelPrice = fuelPrice.movePointRight(2);
//                long n = fuelPrice.longValueExact();
//                transaction.setFuelPrice(n);
//            }
//            if (wexReqPayAtPump.getFuelProdCode() != null) {
//                transaction.setFuelProdCode(wexReqPayAtPump.getFuelProdCode().toString());
//            }
//            if (wexReqPayAtPump.getUnitOfMeas() != null) {
//                transaction.setUnitOfMeas(wexReqPayAtPump.getUnitOfMeas().toString());
//            }
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
////          TODO:  transaction.setContact(request);
//            if (wexReqPayAtPump.getProdDetailCount() != null) {
//                transaction.setProdDetailCount(wexReqPayAtPump.getProdDetailCount().toString());
//            }
//            transaction.setServiceCode(wexReqPayAtPump.getServiceCode());
//
//        }
        Request.AddressVerificationService addressVerServc = request.getAddressVerificationService();
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

    private String getSystemDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date();
        String ts = dateFormat.format(date);
        return ts;
    }

    private String getAuthTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = new Date();
        String ts = dateFormat.format(date);
        return ts;
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

    private void mapResponse(Transaction t, Message cm) {
        LOG.info("Authorizer.mapResponse method started");
        Response response = new Response();
        cm.getRequest().clear();
        cm.getHeader().setComment(t.getComment());
        response.setReasonCode(t.getReasonCode());
        response.setResponseType(t.getResponseType());
        response.setDescriptionField(t.getDescriptionField());
        response.setRRN(t.getRrn());

        if (ResponseType.APPROVED.equalsIgnoreCase(t.getResponseType())) {
            // We add these fields only if it is approved
            response.setMedia(t.getMedia());
            response.setAuthNumber(t.getAuthNumber());
            if (t.getPlanNumber() != null && !t.getPlanNumber().trim().isEmpty() && t.getMedia().equalsIgnoreCase(MediaType.MIL_STAR)) {
                response.setPlanNumber(String.valueOf(BigInteger.valueOf(Long.valueOf(t.getPlanNumber()))));
            }
            if (t.getMilstarNumber() != null && !t.getMilstarNumber().trim().isEmpty()) {
                response.setMilstarNumber(t.getMilstarNumber());
            }
            if (t.getMedia().equalsIgnoreCase(MediaType.MIL_STAR) || t.getMedia().equalsIgnoreCase(MediaType.GIFT_CARD)) {
                response.setBalanceAmount(BigDecimal.valueOf(t.getBalanceAmount()).movePointLeft(2));
                if (t.getRequestType().equalsIgnoreCase(RequestType.PREAUTH)) {
                    response.setAmtPreAuthorized(BigDecimal.valueOf(t.getAmtPreAuthorized()).movePointLeft(2));
                }
            }
            if (response.getCardReferenceID() != null) {
                response.setCardReferenceID(String.valueOf(BigInteger.valueOf(Long.valueOf(t.getCardReferenceID()))));
            }
//            if (t.getMedia().equalsIgnoreCase(MediaType.MIL_STAR)) {
//                response.setPartialAmount(BigDecimal.valueOf(t.getPartialAmount()).movePointLeft(2));
//            }
            response.setOrigReqType(t.getRequestType());
            if ("Pan".equalsIgnoreCase(t.getPan())) {
                response.setOrigAcctType("Pan");
                if (tokenBusinessService != null
                        && !isDuplicateTransaction
                        && !t.getRequestType().equalsIgnoreCase(RequestType.ISSUE)
                        && (t.getTokenId() == null || t.getTokenId().trim().isEmpty())) {
                    try {
                        tokenBusinessService.issueToken(t);
                    } catch (ProcessingException e) {
                        LOG.error("Cannot generate token. Token Service Error " + e);
                    }
                }
                if (t.getTokenId() != null && !t.getTokenId().trim().isEmpty()) {
                    response.setModifiedAcctValue(t.getTokenId());
                }

                //response.setAmtPreAuthorized(BigDecimal.valueOf(t.getAmtPreAuthorized()).movePointLeft(2));
            }
            if (t.getMedia().equalsIgnoreCase(MediaType.GIFT_CARD) && t.getRequestType().equalsIgnoreCase(RequestType.ISSUE)) {
                Response.VrtGcAcctInfo GcInfo = new Response.VrtGcAcctInfo();
                GcInfo.setVrtGcAcctNbr(new BigInteger(String.valueOf(t.getAccount())));
                GcInfo.setVrtGCPin(t.getGcpin());
                response.setVrtGcAcctInfo(GcInfo);
            }
            if (t.getSettleRs() != null && !t.getSettleRs().trim().isEmpty()) {
                response.setSettleRs(t.getSettleRs());
                response.setSettleAmt(BigDecimal.valueOf(t.getSettleAmt()));
            }
            if (t.getMedia().equalsIgnoreCase(MediaType.VISA)
                    || t.getMedia().equalsIgnoreCase(MediaType.MASTER)
                    || t.getMedia().equalsIgnoreCase(MediaType.DISCOVER)
                    || t.getMedia().equalsIgnoreCase(MediaType.AMEX)) {
                Response.AddressVerificationResponse avr = new Response.AddressVerificationResponse();
                avr.setBillingZipCode(AddressVerificationResponseType.fromValue(t.getAvsResponseCode()));
                response.setAddressVerificationResponse(avr);
            }
        }

        //TODO ::
//        Response.WEXResponsePayAtPumpData wexRespData = new Response.WEXResponsePayAtPumpData();
//        wexRespData.setAmtPreAuthorized(BigDecimal.valueOf(t.getAmtPreAuthorized()));
        cm.getResponse().add(response);
        LOG.debug("RRN number in Authorizer.mapResponse method is :" + t.getRrn());
        LOG.info("Authorizer.mapResponse method ended ");
    }

    public void setTranRepository(TranRepository tranRepository) {
        this.tranRepository = tranRepository;
    }

    public void setFacilityDAO(FacilityDAO facilityDAO) {
        this.facilityDAO = facilityDAO;
    }

    public void setMaskAccount(String maskAccount) {
        this.maskAccount = maskAccount;
    }

    /**
     * @param configurator the configurator to set
     */
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }

    /**
     * @param tokenBusinessService the tokenBusinessService to set
     */
    public void setTokenBusinessService(TokenBusinessService tokenBusinessService) {
        this.tokenBusinessService = tokenBusinessService;
    }

    public void setEnableFDMSStub(String enableFDMSStub) {
        this.enableFDMSStub = enableFDMSStub;
    }

    public void setfDMSStub(FDMSStub fDMSStub) {
        this.fDMSStub = fDMSStub;
    }

    /**
     * @param baseStrategyFactory the baseStrategyFactory to set
     */
    public void setBaseStrategyFactory(BaseStrategyFactory baseStrategyFactory) {
        this.baseStrategyFactory = baseStrategyFactory;
    }

    public TransactionDAO getTransactionDAO() {
        return transactionDAO;
    }

    public void setTransactionDAO(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    /**
     *
     * @param t
     * @return
     */
    private Transaction checkTransactionCancel(Transaction t) {

        Transaction authTran = null;
        if (t.getRequestType() != null & t.getRequestType().equalsIgnoreCase(RequestType.TRNCANCEL)) {

            authTran = tranRepository.find(t.getIdentityUuid(), t.getOrigRRN(), t.getDescriptionField());

            if (authTran == null || !(authTran.getOrderNumber().equals(t.getOriginalOrder()))) {

                throw new AuthorizerException("NO_AUTHORIZATION_FOUND_FOR_CANCELATION");
            }

        }

        return authTran;
    }
}
