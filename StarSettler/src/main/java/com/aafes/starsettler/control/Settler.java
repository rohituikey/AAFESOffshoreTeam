package com.aafes.starsettler.control;

import com.aafes.starsettler.dao.FacilityDAO;
import com.aafes.starsettler.entity.CommandMessage;
import com.aafes.starsettler.entity.*;
import com.aafes.starsettler.tokenizer.TokenEndPointService;
import com.aafes.starsettler.util.CardType;
import com.aafes.starsettler.util.ResponseType;
import com.aafes.starsettler.util.SettleStatus;
import com.aafes.starsettler.util.TransactionType;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import generated.Settlement;
import generated.Settlement.Appeasements.Appeasement;
import generated.Settlement.Shipment.Lines.Shipped;
import generated.Settlement.Shipment.Shipping;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import org.slf4j.LoggerFactory;

/**
 * This class contains business methods for performing all gateway requests.
 *
 * @author ganjis
 */
@Stateless
public class Settler {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(Settler.class.getSimpleName());

    @EJB
    private Configurator configurator;
    @EJB
    private SettleMessageRepository settleRepository;
    @EJB
    private TokenEndPointService tokenEndPointService;

    @EJB
    private SettleFactory settleFactory;

    public Settlement saveForSettle(Settlement settleMessage) throws JAXBException {

        List<SettleEntity> settleEntityList = new ArrayList<SettleEntity>();
        try {
            // This method will receive the settle message from settle message resource
            // Do required validations 
            // Convert to Settle Entity Bean
            validateMessage(settleMessage);
            mapRequest(settleMessage, settleEntityList);
            validateSettleList(settleEntityList);
            //Added the below code for Defect -->745 
            //Desc : Binary Vision File Settlement requests with same primary key in database are 
            //being overwritten instead of being rejected
            boolean recordDuplicate = settleRepository.validateDuplicateRecords(settleEntityList);
            if (recordDuplicate) {
                Settlement.Response response = new Settlement.Response();
                response.setDescriptionField("DUPLICATE_RECORD");
                response.setReasonCode(configurator.get("DUPLICATE_RECORD"));
                response.setResponseType(ResponseType.FAILED);
                settleMessage.getResponse().add(response);
            } else {
                String tokenBankName = findFacility(settleMessage.getIdentityUUID());
                findAuthorizationCodes(settleEntityList, tokenBankName);
                // Pass the entity to Setlle Repository to save
                settleRepository.save(settleEntityList);

                // Covert to settle message
                Settlement.Response response = new Settlement.Response();
                response.setDescriptionField(ResponseType.SUCCESS);
                response.setReasonCode(configurator.get(ResponseType.SUCCESS));
                response.setResponseType(ResponseType.SUCCESS);
                settleMessage.getResponse().add(response);
            }

        } catch (JAXBException e) {
            throw new JAXBException(e.getMessage());
        } catch (SettlerException e) {
            Settlement.Response response = new Settlement.Response();
            response.setDescriptionField(e.getMessage());
            response.setReasonCode(configurator.get(e.getMessage()));
            response.setResponseType(ResponseType.FAILED);
            settleMessage.getResponse().add(response);
        } catch (Exception e) {
            Settlement.Response response = new Settlement.Response();
            response.setDescriptionField("INTERNAL SERVER ERROR");
            response.setReasonCode(configurator.get("INTERNAL_SERVER_ERROR"));
            response.setResponseType(ResponseType.FAILED);
            settleMessage.getResponse().add(response);
        }

        return settleMessage;
    }

    public String commandSettle(CommandMessage commandMessage) {

        String response = "Success";
        try {

            String settleType = commandMessage.getSettlerType();
            String processDate = commandMessage.getProcessDate();
            BaseSettler baseSettler = settleFactory.findSettler(settleType);
            String identityUUID = commandMessage.getIdentityuuid();
            baseSettler.run(identityUUID, processDate);

        } catch (Exception e) {
            response = e.getMessage();
        }

        return response;
    }

    private String findFacility(String uuid) throws SettlerException {
        try {

            Facility facility = settleRepository.getFacility(uuid);
            if (facility == null || facility.getFacility() == null
                    || facility.getFacility().trim().isEmpty()
                    || facility.getTokenBankName() == null
                    || facility.getTokenBankName().trim().isEmpty()) {
                throw new SettlerException("INVALID_UUID");
            } else {
                return facility.getTokenBankName();
            }
        } catch (Exception e) {
            throw new SettlerException("INVALID_UUID");
        }
    }

    private void findAuthorizationCodes(List<SettleEntity> settleEntityList, String tokenBankname) throws SettlerException {

        boolean isTokenPresent = false;
        for (SettleEntity settleEntity : settleEntityList) {
            settleEntity.setTokenBankName(tokenBankname);
            isTokenPresent = findToken(settleEntity);
            if (!isTokenPresent) {
                throw new SettlerException("TOKEN_NOTFOUND");
            }
            String cardType = settleEntity.getCardType();
            if (cardType.equalsIgnoreCase(CardType.AMEX)
                    || cardType.equalsIgnoreCase(CardType.VISA)
                    || cardType.equalsIgnoreCase(CardType.DISCOVER)
                    || cardType.equalsIgnoreCase(CardType.MASTER)) {
                AuthorizationCodes ac = settleRepository.findAuthorizationCodes(settleEntity);
                settleEntity.setResponseReasonCode(ac.getResponseReasonCode());
                settleEntity.setResponseDate(ac.getResponseDate());
                settleEntity.setAuthoriztionCode(ac.getAuthoriztionCode());
                settleEntity.setAvsResponseCode(ac.getAvsResponseCode());
                settleEntity.setCsvResponseCode(ac.getCsvResponseCode());
            }
            
        }
    }

    private boolean findToken(SettleEntity se) {

        try {
            if (tokenEndPointService != null) {
                String accountNbr = tokenEndPointService.lookupAccount(se);
                if (accountNbr == null || accountNbr.trim().isEmpty()) {
                    return false;
                }
            }
        } catch (Exception e) {
            LOG.info("Error while calling tokenizer for token : " + se.getCardToken());
            LOG.error(e.toString());
            return false;
        }

        return true;
    }
//         private void validateDuplicateRecords(List<SettleEntity> settleEntityList) {
//         
//       for (SettleEntity settleEntity : settleEntityList) {
//            String cardType = settleEntity.getCardType();
//            if (cardType.equalsIgnoreCase(CardType.AMEX)
//                    || cardType.equalsIgnoreCase(CardType.VISA)
//                    || cardType.equalsIgnoreCase(CardType.DISCOVER)
//                    || cardType.equalsIgnoreCase(CardType.MASTER)) {
//                SettleEntity settleMessage = settleRepository.validateDuplicateRecords(settleEntity);
//                settleEntity.setResponseReasonCode(settleMessage.get.getResponseReasonCode());
//                settleEntity.setResponseDate(ac.getResponseDate());
//                settleEntity.setAuthoriztionCode(ac.getAuthoriztionCode());
//                settleEntity.setAvsResponseCode(ac.getAvsResponseCode());
//                settleEntity.setCsvResponseCode(ac.getCsvResponseCode());
//            }
//
//        for (Row rs : result) {
//            Settlement.Response response = new Settlement.Response();
//            response.setDescriptionField("DUPLICATE_RECORD");
//            response.setReasonCode(configurator.get("DUPLICATE_RECORD"));
//            response.setResponseType(ResponseType.DECLINED);
//            settleMessage.getResponse().add(response);
//            }
//     }

    private void mapRequest(Settlement settleMessage, List<SettleEntity> settleEntityList) throws JAXBException, SettlerException {

        // TODO
        // Map all request fields from settle Message to settle entity bean here
        if (settleMessage.getShipment() != null) {
            if (settleMessage.getShipment().getLines() != null) {
                Shipped lineItem = settleMessage.getShipment().getLines().getShipped();
                if (lineItem.getPayments() != null) {
                    List<Shipped.Payments.Payment> paymentsList = lineItem.getPayments().getPayment();
                    if (paymentsList != null && !paymentsList.isEmpty()) {
                        for (Shipped.Payments.Payment payment : paymentsList) {
                            SettleEntity settleEntity = new SettleEntity();
                            settleEntity.setIdentityUUID(settleMessage.getIdentityUUID());
                            this.mapLineHeader(lineItem, settleEntity);
                            this.mapLinePaymentNode(payment, settleEntity);
                            settleEntity.setReceiveddate(this.getSystemDateTime());
                            settleEntity.setSettlestatus(SettleStatus.Ready_to_settle);
                            settleEntityList.add(settleEntity);
                        }
                    }
                } else {
                    throw new SettlerException("INVALID_REQUEST");
                }

            } else if (settleMessage.getShipment().getShipping() != null) {
                Shipping shipping = settleMessage.getShipment().getShipping();

                if (shipping.getPayments() != null) {
                    List<Shipping.Payments.Payment> paymentsList = shipping.getPayments().getPayment();
                    if (paymentsList != null && !paymentsList.isEmpty()) {
                        for (Shipping.Payments.Payment payment : paymentsList) {
                            SettleEntity settleEntity = new SettleEntity();
                            settleEntity.setIdentityUUID(settleMessage.getIdentityUUID());
                            settleEntity.setClientLineId(shipping.getClientLineId());
                            try {
                                BigDecimal amount;
                                amount = shipping.getAmount();
                                if (amount != null) {
                                    amount = amount.movePointRight(2);
                                    if (amount.longValueExact() <= 9999999 && amount.longValueExact() >= -9999999) {
                                        settleEntity.setShippingAmount(Long.toString(amount.longValueExact()));
                                    } else {
                                        throw new SettlerException("INVALID_AMOUNT");
                                    }
                                }
                            } catch (ArithmeticException e) {
                                throw new SettlerException("INVALID_AMOUNT");
                            }
                            this.mapShippingPaymentNode(payment, settleEntity);
                            settleEntity.setReceiveddate(this.getSystemDateTime());
                            settleEntity.setSettlestatus(SettleStatus.Ready_to_settle);
                            settleEntityList.add(settleEntity);
                        }
                    }
                } else {
                    throw new SettlerException("INVALID_REQUEST");
                }
            } else {
                throw new SettlerException("INVALID_REQUEST");
            }
        } else if (settleMessage.getAppeasements() != null) {
            Appeasement appeasement = settleMessage.getAppeasements().getAppeasement();
            if (appeasement.getPayments() != null) {
                Appeasement.Payments.Payment payment = appeasement.getPayments().getPayment();
                if (payment != null) {
                    SettleEntity settleEntity = new SettleEntity();
                    settleEntity.setIdentityUUID(settleMessage.getIdentityUUID());
                    settleEntity.setClientLineId(appeasement.getClientLineId());
                    settleEntity.setAppeasementCode(appeasement.getCode());
                    if (appeasement.getDate() != null) {
                        String appeasementDate = appeasement.getDate().toString();
                        if (appeasementDate != null && this.isValidDate(appeasementDate)) {
                            settleEntity.setAppeasementDate(appeasement.getDate().toString());

                        } else {
                            throw new JAXBException("INVALID XML");
                        }

                    }
                    settleEntity.setAppeasementDescription(appeasement.getDescription());
                    settleEntity.setAppeasementReference(appeasement.getReference());
                    this.mapAppeasementPaymentNode(payment, settleEntity);
                    settleEntity.setReceiveddate(this.getSystemDateTime());
                    settleEntity.setSettlestatus(SettleStatus.Ready_to_settle);
                    settleEntityList.add(settleEntity);
                }
            } else {
                throw new SettlerException("INVALID_REQUEST");
            }
        } else {
            throw new SettlerException("INVALID_REQUEST");
        }

    }

    private void mapLineHeader(Shipped lineItem, SettleEntity settleEntity) throws SettlerException {
        settleEntity.setClientLineId(lineItem.getClientLineId());
        if (lineItem.getLineId() != null) {
            settleEntity.setLineId(lineItem.getLineId().toString());
        }
        if (lineItem.getShipId() != null) {
            settleEntity.setShipId(lineItem.getShipId().toString());
        }
        settleEntity.setCrc(lineItem.getCrc());
        if (lineItem.getQuantity() != null) {
            settleEntity.setQuantity(lineItem.getQuantity().toString());
        }

        try {
            BigDecimal unitCost;
            unitCost = lineItem.getUnitCost();
            if (unitCost != null) {
                unitCost = unitCost.movePointRight(2);
                if (unitCost.longValueExact() <= 9999999 && unitCost.longValueExact() >= -9999999) {
                    settleEntity.setUnitCost(Long.toString(unitCost.longValueExact()));
                } else {
                    throw new SettlerException("INVALID_AMOUNT");
                }
            }
            BigDecimal unitDiscount;
            unitDiscount = lineItem.getUnitDiscount();
            if (unitDiscount != null) {
                unitDiscount = unitDiscount.movePointRight(2);
                if (unitDiscount.longValueExact() <= 9999999 && unitDiscount.longValueExact() >= -9999999) {
                    settleEntity.setUnitDiscount(Long.toString(unitDiscount.longValueExact()));
                } else {
                    throw new SettlerException("INVALID_AMOUNT");
                }
            }
        } catch (ArithmeticException e) {
            throw new SettlerException("INVALID_AMOUNT");
        }
        if (lineItem.getUnit() != null) {
            settleEntity.setUnit(lineItem.getUnit().toString());
        }
        if (lineItem.getUnitTotal() != null) {
            settleEntity.setUnitTotal(lineItem.getUnitTotal().toString());
        }
        settleEntity.setCouponCode(lineItem.getCouponCode());
    }

    private void mapLinePaymentNode(Shipped.Payments.Payment payment, SettleEntity settleEntity) throws JAXBException, SettlerException {
        settleEntity.setCardType(payment.getType());
        try {
            BigDecimal amount;
            amount = payment.getAmount();
            if (amount != null) {
                amount = amount.movePointRight(2);
                if (amount.longValueExact() <= 9999999 && amount.longValueExact() >= -9999999) {
                    settleEntity.setPaymentAmount(Long.toString(amount.longValueExact()));
                    if (amount.longValueExact() < 0) {
                        settleEntity.setTransactionType(TransactionType.Refund);
                    } else if (amount.longValueExact() >= 0) {
                        settleEntity.setTransactionType(TransactionType.Deposit);
                    }
                } else {
                    throw new SettlerException("INVALID_AMOUNT");
                }
            }
        } catch (ArithmeticException e) {
            throw new SettlerException("INVALID_AMOUNT");
        }
        if (payment.getTransactionId() != null) {
            settleEntity.setTransactionId(payment.getTransactionId().toString());
        }
        settleEntity.setOrderNumber(payment.getOrderNumber());
        if (payment.getOrderDate() != null) {
            String orderDate = payment.getOrderDate().toString();
            if (orderDate != null && this.isValidDate(orderDate)) {
                settleEntity.setOrderDate(payment.getOrderDate().toString());
            } else {
                throw new JAXBException("INVALID XML");
            }

        }
        if (payment.getShipDate() != null) {
            String shipDate = payment.getShipDate().toString();
            if (shipDate != null && this.isValidDate(shipDate)) {
                settleEntity.setShipDate(payment.getShipDate().toString());
            } else {
                throw new JAXBException("INVALID XML");
            }

        }
        if (payment.getSettleDate() != null) {
            String settleDate = payment.getSettleDate().toString();
            if (settleDate != null && this.isValidDate(settleDate)) {
                settleEntity.setSettleDate(payment.getSettleDate().toString());
            } else {
                throw new JAXBException("INVALID XML");
            }

        }

        settleEntity.setCardReferene(payment.getCardReference());
        settleEntity.setCardToken(payment.getCardToken());

        if (payment.getExpirationDate() != null && !payment.getType().equalsIgnoreCase(CardType.GIFT_CARD)) {
            //check for valid expiration date
            String exp = payment.getExpirationDate().toString();
            if (exp != null && exp.length() == 4) {
                String month = exp.substring(2, 4);
                if (Integer.parseInt(month) > 12 || Integer.parseInt(month) < 1) {
                    throw new JAXBException("INVALID XML");
                }
            } else {
                throw new JAXBException("INVALID XML");
            }
            settleEntity.setExpirationDate(payment.getExpirationDate().toString());
        }
        settleEntity.setAuthNum(payment.getAuthNum());
        String settlePlan = "";
        if (payment.getRequestPlan() != null) {
            settleEntity.setRequestPlan(payment.getRequestPlan().toString());
            settlePlan = settleEntity.getRequestPlan();
        }
        if (payment.getResponsePlan() != null) {
            settleEntity.setResponsePlan(payment.getResponsePlan().toString());
            settlePlan = settleEntity.getResponsePlan();
        }
        if (payment.getQualifiedPlan() != null) {
            settleEntity.setQualifiedPlan(payment.getQualifiedPlan().toString());
            settlePlan = settleEntity.getQualifiedPlan();
        }
        settleEntity.setSettlePlan(settlePlan);
        settleEntity.setRrn(payment.getRRN());
        if (payment.getCustomer() != null) {
            Shipped.Payments.Payment.Customer.Contact contact = payment.getCustomer().getContact();
            if (contact != null) {
                settleEntity.setFirstName(contact.getFirstName());
                settleEntity.setMiddleName(contact.getMiddleName());
                settleEntity.setLastName(contact.getLastName());
                if (contact.getHomePhone() != null) {
                    settleEntity.setHomePhone(contact.getHomePhone().toString());
                }
                settleEntity.setEmail(contact.getEmail());
            }
            Shipped.Payments.Payment.Customer.Address address = payment.getCustomer().getAddress();
            if (address != null) {
                if (address.getLine() != null && !address.getLine().isEmpty()) {

                    for (Shipped.Payments.Payment.Customer.Address.Line line : address.getLine()) {
                        if (line.getSequence() == null || line.getSequence().intValueExact() > 2 || line.getSequence().intValueExact() < 1) {
                            throw new SettlerException("INVALID_ADDRESS_SEQUENCE");
                        }
                        if (line.getSequence().intValueExact() == 1) {
                            if (line.getValue() != null
                                    && line.getValue().length() > 200) {
                                throw new JAXBException("INVALID XML");
                            }
                            settleEntity.setAddressLine1(line.getValue());
                        }
                        if (line.getSequence().intValueExact() == 2) {
                            if (line.getValue() != null
                                    && line.getValue().length() > 200) {
                                throw new JAXBException("INVALID XML");
                            }
                            settleEntity.setAddressLine2(line.getValue());
                        }
//                        if (line.getSequence().intValueExact() == 3) {
//                            if (line.getValue() != null
//                                    && line.getValue().length() > 200) {
//                                throw new JAXBException("INVALID XML");
//                            }
////                            settleEntity.setAddressLine3(line.getValue());
//                        }
                    }
                }
                settleEntity.setCity(address.getCity());
                settleEntity.setProvinceCode(address.getProvinceCode());
                settleEntity.setPostalCode(address.getPostalCode());
                settleEntity.setCountryCode(address.getCountryCode());

            }
        }
    }

    private void mapShippingPaymentNode(Shipping.Payments.Payment payment, SettleEntity settleEntity) throws JAXBException, SettlerException {

        settleEntity.setCardType(payment.getType());
        try {
            BigDecimal amount;
            amount = payment.getAmount();
            if (amount != null) {
                amount = amount.movePointRight(2);
                if (amount.longValueExact() <= 9999999 && amount.longValueExact() >= -9999999) {
                    settleEntity.setPaymentAmount(Long.toString(amount.longValueExact()));
                    if (amount.longValueExact() < 0) {
                        settleEntity.setTransactionType(TransactionType.Refund);
                    } else if (amount.longValueExact() >= 0) {
                        settleEntity.setTransactionType(TransactionType.Deposit);
                    }
                } else {
                    throw new SettlerException("INVALID_AMOUNT");
                }
            }
        } catch (ArithmeticException e) {
            throw new SettlerException("INVALID_AMOUNT");
        }
        if (payment.getTransactionId() != null) {
            settleEntity.setTransactionId(payment.getTransactionId().toString());
        }
        settleEntity.setOrderNumber(payment.getOrderNumber());
        if (payment.getOrderDate() != null) {
            String orderDate = payment.getOrderDate().toString();
            if (orderDate != null && this.isValidDate(orderDate)) {
                settleEntity.setOrderDate(payment.getOrderDate().toString());
            } else {
                throw new JAXBException("INVALID XML");
            }

        }
        if (payment.getShipDate() != null) {
            String shipDate = payment.getShipDate().toString();
            if (shipDate != null && this.isValidDate(shipDate)) {
                settleEntity.setShipDate(payment.getShipDate().toString());
            } else {
                throw new JAXBException("INVALID XML");
            }

        }
        if (payment.getSettleDate() != null) {
            String settleDate = payment.getSettleDate().toString();
            if (settleDate != null && this.isValidDate(settleDate)) {
                settleEntity.setSettleDate(payment.getSettleDate().toString());
            } else {
                throw new JAXBException("INVALID XML");
            }

        }

        settleEntity.setCardReferene(payment.getCardReference());
        settleEntity.setCardToken(payment.getCardToken());

        if (payment.getExpirationDate() != null && !payment.getType().equalsIgnoreCase(CardType.GIFT_CARD)) {
            //check for valid expiration date
            String exp = payment.getExpirationDate().toString();
            if (exp != null && exp.length() == 4) {
                String month = exp.substring(2, 4);
                if (Integer.parseInt(month) > 12 || Integer.parseInt(month) < 1) {
                    throw new JAXBException("INVALID XML");
                }
            } else {
                throw new JAXBException("INVALID XML");
            }
            settleEntity.setExpirationDate(payment.getExpirationDate().toString());
        }
        settleEntity.setAuthNum(payment.getAuthNum());
        String settlePlan = "";
        if (payment.getRequestPlan() != null) {
            settleEntity.setRequestPlan(payment.getRequestPlan().toString());
            settlePlan = settleEntity.getRequestPlan();
        }
        if (payment.getResponsePlan() != null) {
            settleEntity.setResponsePlan(payment.getResponsePlan().toString());
            settlePlan = settleEntity.getResponsePlan();
        }
        if (payment.getQualifiedPlan() != null) {
            settleEntity.setQualifiedPlan(payment.getQualifiedPlan().toString());
            settlePlan = settleEntity.getQualifiedPlan();
        }
        settleEntity.setSettlePlan(settlePlan);
        settleEntity.setRrn(payment.getRRN());
        if (payment.getCustomer() != null) {
            Shipping.Payments.Payment.Customer.Contact contact = payment.getCustomer().getContact();
            if (contact != null) {

                settleEntity.setFirstName(contact.getFirstName());
                settleEntity.setMiddleName(contact.getMiddleName());
                settleEntity.setLastName(contact.getLastName());
                if (contact.getHomePhone() != null) {
                    settleEntity.setHomePhone(contact.getHomePhone().toString());
                }
                settleEntity.setEmail(contact.getEmail());
            }
            Shipping.Payments.Payment.Customer.Address address = payment.getCustomer().getAddress();
            if (address != null) {
                if (address.getLine() != null && !address.getLine().isEmpty()) {

                    for (Shipping.Payments.Payment.Customer.Address.Line line : address.getLine()) {
                        if (line.getSequence() == null || line.getSequence().intValueExact() > 2 || line.getSequence().intValueExact() < 1) {
                            throw new SettlerException("INVALID_ADDRESS_SEQUENCE");
                        }
                        if (line.getSequence().intValueExact() == 1) {
                            if (line.getValue() != null
                                    && line.getValue().length() > 200) {
                                throw new JAXBException("INVALID XML");
                            }
                            settleEntity.setAddressLine1(line.getValue());
                        }
                        if (line.getSequence().intValueExact() == 2) {
                            if (line.getValue() != null
                                    && line.getValue().length() > 200) {
                                throw new JAXBException("INVALID XML");
                            }
                            settleEntity.setAddressLine2(line.getValue());
                        }
//                        if (line.getSequence().intValueExact() == 3) {
//                            if (line.getValue() != null
//                                    && line.getValue().length() > 200) {
//                                throw new JAXBException("INVALID XML");
//                            }
////                            settleEntity.setAddressLine3(line.getValue());
//                        }
                    }

                }
                settleEntity.setCity(address.getCity());
                settleEntity.setProvinceCode(address.getProvinceCode());
                settleEntity.setPostalCode(address.getPostalCode());
                settleEntity.setCountryCode(address.getCountryCode());

            }
        }
    }

    private void mapAppeasementPaymentNode(Appeasement.Payments.Payment payment, SettleEntity settleEntity) throws JAXBException, SettlerException {
        settleEntity.setCardType(payment.getType());
        try {
            BigDecimal amount;
            amount = payment.getAmount();
            if (amount != null) {
                amount = amount.movePointRight(2);
                if (amount.longValueExact() <= 9999999 && amount.longValueExact() >= -9999999) {
                    settleEntity.setPaymentAmount(Long.toString(amount.longValueExact()));
                    if (amount.longValueExact() < 0) {
                        settleEntity.setTransactionType(TransactionType.Refund);
                    } else if (amount.longValueExact() >= 0) {
                        settleEntity.setTransactionType(TransactionType.Deposit);
                    }
                } else {
                    throw new SettlerException("INVALID_AMOUNT");
                }
            }
        } catch (ArithmeticException e) {
            throw new SettlerException("INVALID_AMOUNT");
        }
        if (payment.getTransactionId() != null) {
            settleEntity.setTransactionId(payment.getTransactionId().toString());
        }
        settleEntity.setOrderNumber(payment.getOrderNumber());
        if (payment.getOrderDate() != null) {
            String orderDate = payment.getOrderDate().toString();
            if (orderDate != null && this.isValidDate(orderDate)) {
                settleEntity.setOrderDate(payment.getOrderDate().toString());
            } else {
                throw new JAXBException("INVALID XML");
            }

        }
        if (payment.getShipDate() != null) {
            String shipDate = payment.getShipDate().toString();
            if (shipDate != null && this.isValidDate(shipDate)) {
                settleEntity.setShipDate(payment.getShipDate().toString());
            } else {
                throw new JAXBException("INVALID XML");
            }

        }
        if (payment.getSettleDate() != null) {
            String settleDate = payment.getSettleDate().toString();
            if (settleDate != null && this.isValidDate(settleDate)) {
                settleEntity.setSettleDate(payment.getSettleDate().toString());
            } else {
                throw new JAXBException("INVALID XML");
            }

        }

        settleEntity.setCardReferene(payment.getCardReference());
        settleEntity.setCardToken(payment.getCardToken());

        if (payment.getExpirationDate() != null && !payment.getType().equalsIgnoreCase(CardType.GIFT_CARD) ) {
            //check for valid expiration date
            String exp = payment.getExpirationDate().toString();
            if (exp != null && exp.length() == 4) {
                String month = exp.substring(2, 4);
                if (Integer.parseInt(month) > 12 || Integer.parseInt(month) < 1) {
                    throw new JAXBException("INVALID XML");
                }
            } else {
                throw new JAXBException("INVALID XML");
            }
            settleEntity.setExpirationDate(payment.getExpirationDate().toString());
        }
        settleEntity.setAuthNum(payment.getAuthNum());
        String settlePlan = "";
        if (payment.getRequestPlan() != null) {
            settleEntity.setRequestPlan(payment.getRequestPlan().toString());
            settlePlan = settleEntity.getRequestPlan();
        }
        if (payment.getResponsePlan() != null) {
            settleEntity.setResponsePlan(payment.getResponsePlan().toString());
            settlePlan = settleEntity.getResponsePlan();
        }
        if (payment.getQualifiedPlan() != null) {
            settleEntity.setQualifiedPlan(payment.getQualifiedPlan().toString());
            settlePlan = settleEntity.getQualifiedPlan();
        }
        settleEntity.setSettlePlan(settlePlan);
        settleEntity.setRrn(payment.getRRN());
        if (payment.getCustomer() != null) {
            Appeasement.Payments.Payment.Customer.Contact contact = payment.getCustomer().getContact();
            if (contact != null) {
                settleEntity.setFirstName(contact.getFirstName());
                //settleEntity.setMiddleName(contact.getMiddleName());
                settleEntity.setLastName(contact.getLastName());
                if (contact.getHomePhone() != null) {
                    settleEntity.setHomePhone(contact.getHomePhone().toString());
                }
                settleEntity.setEmail(contact.getEmail());
            }
            Appeasement.Payments.Payment.Customer.Address address = payment.getCustomer().getAddress();
            if (address != null) {
                if (address.getLine() != null && !address.getLine().isEmpty()) {

                    for (Appeasement.Payments.Payment.Customer.Address.Line line : address.getLine()) {
                        if (line.getSequence() == null || line.getSequence().intValueExact() > 2 || line.getSequence().intValueExact() < 1) {
                            throw new SettlerException("INVALID_ADDRESS_SEQUENCE");
                        }
                        if (line.getSequence().intValueExact() == 1) {
                            if (line.getValue() != null
                                    && line.getValue().length() > 200) {
                                throw new JAXBException("INVALID XML");
                            }
                            settleEntity.setAddressLine1(line.getValue());
                        }
                        if (line.getSequence().intValueExact() == 2) {
                            if (line.getValue() != null
                                    && line.getValue().length() > 200) {
                                throw new JAXBException("INVALID XML");
                            }
                            settleEntity.setAddressLine2(line.getValue());
                        }
//                        if (line.getSequence().intValueExact() == 3) {
//                            if (line.getValue() != null
//                                    && line.getValue().length() > 200) {
//                                throw new JAXBException("INVALID XML");
//                            }
////                            settleEntity.setAddressLine3(line.getValue());
//                        }
                    }

                }
                settleEntity.setCity(address.getCity());
                settleEntity.setProvinceCode(address.getProvinceCode());
                settleEntity.setPostalCode(address.getPostalCode());
                settleEntity.setCountryCode(address.getCountryCode());

            }
        }
    }

    private void validateSettleList(List<SettleEntity> settleEntityList) throws SettlerException {

        boolean lookForTotal = false;
        long total = 0;
        int creditCount = 0;

        if ((settleEntityList.get(0).getUnitCost() != null && !settleEntityList.get(0).getUnitCost().isEmpty())
                || (settleEntityList.get(0).getShippingAmount() != null && !settleEntityList.get(0).getShippingAmount().isEmpty())) {
            lookForTotal = true;
        }

        for (SettleEntity settleEntity : settleEntityList) {
            if (lookForTotal) {
                try {
                    total = total + Long.parseLong(settleEntity.getPaymentAmount());
                } catch (ArithmeticException e) {
                    throw new SettlerException("INVALID_AMOUNT");
                }
            }
            if (settleEntity.getCardType().equals(CardType.MIL_STAR)) {
                if ((settleEntity.getRequestPlan() == null || settleEntity.getRequestPlan().trim().isEmpty())
                        && (settleEntity.getResponsePlan() == null || settleEntity.getResponsePlan().trim().isEmpty())
                        && (settleEntity.getQualifiedPlan() == null || settleEntity.getQualifiedPlan().trim().isEmpty())) {
                    throw new SettlerException("PLAN_NUMBER_REQUIRED");
                }
                if (settleEntity.getOrderNumber() != null && settleEntity.getOrderNumber().trim().length() > 10) {
                    throw new SettlerException("INVALID_ORDER_NUMBER");
                }
            }

            // TODO : Check for ATT & WEX etc ..,
            if (settleEntity.getCardType().equals(CardType.MIL_STAR)
                    || settleEntity.getCardType().equals(CardType.AMEX)
                    || settleEntity.getCardType().equals(CardType.VISA)
                    || settleEntity.getCardType().equals(CardType.DISCOVER)
                    || settleEntity.getCardType().equals(CardType.MASTER)) {
                creditCount++;
            }

            if (creditCount > 1) {
                throw new SettlerException("MULTIPLE_CREDIT_CARDS");
            }

            if (settleEntity.getOrderNumber() == null
                    || settleEntity.getOrderNumber().trim().isEmpty()) {
                throw new SettlerException("INVALID_ORDER_NUMBER");
            }

            if (settleEntity.getCardType().equals(CardType.AMEX)
                    || settleEntity.getCardType().equals(CardType.VISA)
                    || settleEntity.getCardType().equals(CardType.DISCOVER)
                    || settleEntity.getCardType().equals(CardType.MASTER)) {
                if (settleEntity.getOrderNumber() != null
                        && !settleEntity.getOrderNumber().trim().isEmpty()) {
                    if (settleEntity.getOrderNumber().length() > 22) {
                        throw new SettlerException("INVALID_ORDER_NUMBER");
                    }
                }
            }

        }

        if (lookForTotal) {
            if (settleEntityList.get(0).getUnitCost() != null && !settleEntityList.get(0).getUnitCost().trim().isEmpty()) {
                long unitCost = Long.parseLong(settleEntityList.get(0).getUnitCost());
                long unitDiscount = 0;
                if (settleEntityList.get(0).getUnitDiscount() != null
                        && !settleEntityList.get(0).getUnitDiscount().trim().isEmpty()) {
                    unitDiscount = Long.parseLong(settleEntityList.get(0).getUnitDiscount());
                }
                if ((unitCost - unitDiscount) != total) {
                    throw new SettlerException("INVALID_UNIT_COST");
                }
            } else if (settleEntityList.get(0).getShippingAmount() != null && !settleEntityList.get(0).getShippingAmount().trim().isEmpty()) {
                long shippingAmount = Long.parseLong(settleEntityList.get(0).getShippingAmount());
                if (shippingAmount != total) {
                    throw new SettlerException("INVALID_SHIPPING_AMOUNT");
                }
            }

        }

        if (settleEntityList.get(0).getUnit() != null
                && !settleEntityList.get(0).getUnit().trim().isEmpty()) {
            if (settleEntityList.get(0).getUnitTotal() != null
                    && !settleEntityList.get(0).getUnitTotal().trim().isEmpty()) {
                if ((Integer.parseInt(settleEntityList.get(0).getUnitTotal()) < 1)
                        || (Integer.parseInt(settleEntityList.get(0).getUnit()) < 1)
                        || (Integer.parseInt(settleEntityList.get(0).getUnit()) > Integer.parseInt(settleEntityList.get(0).getUnitTotal()))) {
                    throw new SettlerException("INVALID_UNIT_COUNT");
                }
            }

        }

    }

    private void validateMessage(Settlement requestMessage) throws JAXBException {
        if (requestMessage.getShipment() != null) {
            if (requestMessage.getShipment().getLines() != null
                    && requestMessage.getShipment().getShipping() != null) {
                throw new JAXBException("Invalid XML");
            }
        }
    }

    private String getSystemDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String ts = dateFormat.format(date);
        return ts;
    }

    private boolean isValidDate(String date) {
        try {
            DateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
            format.setLenient(false);
            format.parse(date);
            return true;
        } catch (ParseException p) {
            return false;
        }
    }

    /**
     * @param settleFactory the settleFactory to set
     */
    public void setSettleFactory(SettleFactory settleFactory) {
        this.settleFactory = settleFactory;
    }

    /**
     * @param settleRepository the settleRepository to set
     */
    public void setSettleRepository(SettleMessageRepository settleRepository) {
        this.settleRepository = settleRepository;
    }

    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }

}
