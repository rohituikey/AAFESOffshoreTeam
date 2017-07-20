/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.authorizer;

import com.aafes.starsettler.imported.SettleEntity;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.AuthorizerException;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.gateway.Gateway;
import com.aafes.stargate.gateway.vision.FutureVisionGateway;
import com.aafes.stargate.timeout.TimeoutProcessor;
import com.aafes.stargate.tokenizer.TokenBusinessService;
import com.aafes.stargate.util.InputType;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
import com.aafes.stargate.util.TransactionType;
import com.aafes.starsettler.imported.SettleConstant;
import com.aafes.starsettler.imported.SettleMessageDAO;
import com.aafes.starsettler.imported.SettleStatus;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.ProcessingException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author suryadevaral
 */
@Stateless
public class RetailStrategy extends BaseStrategy {

    

    @EJB
    private SettleMessageDAO settleMessageDAO;
    @EJB
    private Configurator configurator;
    @EJB
    private TokenBusinessService tokenBusinessService;
    @EJB
    private TimeoutProcessor timeoutProcessor;
    @EJB
    private FutureVisionGateway futureVisionGateway;

    SettleEntity settleEntity;
    Transaction storedTran = null;
    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(RetailStrategy.class.getSimpleName());

    @Override
    public Transaction processRequest(Transaction t) {
        LOG.info("REtailStrategy.processRequest Entry ... " + t.getRrn());
        try {

            boolean retailFieldsValid = this.validateRetailFields(t);
            LOG.info("retailFieldsValid " + retailFieldsValid + "..." + t.getRrn());

            if (!retailFieldsValid) {
                LOG.info("Invalid fields");
                return t;
            }

            // Send transaction to Gateway. 
            /* ADDED/MODIFIED TO CHECK IF TRANSACTION ALREADY SETTLED - START */
            Gateway gateway = super.pickGateway(t);
            if (t.getReversal() != null && t.getReversal().equalsIgnoreCase(RequestType.SALE)) {
                t.setRequestType(RequestType.REFUND);
                if (gateway != null) {
                    t = gateway.processMessage(t);  //calling refund 
                }

                settleEntity = findSettleEntity(t);
                if (settleEntity != null && settleEntity.getSettlestatus().equalsIgnoreCase(SettleStatus.Ready_to_settle)) {
                    updateSettle(settleEntity);
                } else if (settleEntity == null) {
                    this.buildErrorResponse(t, "NO_SETTLEMENT_FOUND_FOR_REVERSAL", "NO_SETTLEMENT_FOUND_FOR_REVERSAL");
                    return t;
                }
            } else if (t.getRequestType() != null && RequestType.TRNCANCEL.equalsIgnoreCase(t.getRequestType())) {
                Transaction trns = t;
                trns.setOrderNumber(t.getOrderNumber());
                trns.setRrn(t.getOrigRRN());
                trns.setRequestType(t.getDescriptionField());
                t = checkTransactionSettlementStatus(trns, gateway);
                return t;
            } else if (t.getReversal() != null && t.getReversal().equalsIgnoreCase(RequestType.REVERSAL)) {
                t = checkTransactionSettlementStatus(t, gateway);
                return t;
            } else if (t.getReversal() != null && t.getReversal().equalsIgnoreCase(RequestType.REFUND)) {
                t = checkTransactionSettlementStatus(t, gateway);
                return t;
            } else {
                if (gateway != null) {
                    t = gateway.processMessage(t);
                }
            }
            /* ADDED/MODIFIED TO CHECK IF TRANSACTION ALREADY SETTLED - END */
            //if Authorized, save in settle message repository to settle
            if (t.getReversal() != null && !t.getReversal().equalsIgnoreCase(RequestType.REVERSAL)
                    && ResponseType.APPROVED.equalsIgnoreCase(t.getResponseType())) {
                getToken(t);
                saveToSettle(t);
            }
            /* CODE MODIFIED FOR Deca Reversal Sale - END */
        } catch (AuthorizerException e) {
            buildErrorResponse(t, "", e.getMessage());
            return t;
        } catch (Exception e) {
            buildErrorResponse(t, "", e.getMessage());
            return t;
        }
        LOG.debug("rrn number in REtailStrategy.processRequest is :  " + t.getRrn());
        LOG.info("REtailStrategy.processRequest is ended");
        return t;
    }

    private SettleEntity findSettleEntity(Transaction t) {
        SettleEntity settleEntity = settleMessageDAO.find(t.getIdentityUuid(), t.getOrderNumber(), t.getRrn(), t.getTransactionId());
        return settleEntity;
    }

    private void getToken(Transaction t) {
        if ("Pan".equalsIgnoreCase(t.getPan())) {

            if (tokenBusinessService != null
                    && !t.getRequestType().equalsIgnoreCase(RequestType.ISSUE)) {
                try {
                    tokenBusinessService.issueToken(t);
                } catch (ProcessingException e) {
                    LOG.error("Cannot generate token. Token Service Error");
                }

            }
        }
    }

    /**
     *
     * @param t
     */
    private void saveToSettle(Transaction t) {
        LOG.info("RetailStrategy.saveTOSettle method is started");
        List<SettleEntity> settleEntityList = new ArrayList<SettleEntity>();
        settleEntity = new SettleEntity();

        settleEntity.setTransactionId(t.getTransactionId());
        settleEntity.setReceiveddate(this.getSystemDate());
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
        settleMessageDAO.save(settleEntityList);
        LOG.debug("rrn number in RetailStrategy.saveTOSettle is: " + t.getRrn());
        LOG.info("RetailStrategy.saveTOSettle method is ended");

    }

    private void updateSettle(SettleEntity settleEntity) {
        settleEntity.setSettlestatus(SettleConstant.Not_to_settle);
        settleMessageDAO.update(settleEntity);
    }

    private boolean validateRetailFields(Transaction t) {

        LOG.info("Validating fields in Retailstrategy");
        //Validate Swiped Transaction
        if (t.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
            LOG.info("Validating fields if Swiped");
            if ((t.getTrack2() == null || t.getTrack2().trim().isEmpty())
                    && (t.getTrack1() == null || t.getTrack1().trim().isEmpty())) {
                this.buildErrorResponse(t, "INVALID_TRACK_DATA", "INVALID_TRACK_DATA");
                return false;
            }
        }

        //Validate Keyed Transaction
        if (t.getInputType().equalsIgnoreCase(InputType.KEYED)) {

            if (t.getAccount() == null || t.getAccount().trim().isEmpty()) {
                this.buildErrorResponse(t, "INVALID_ACCOUNT_NUMBER", "INVALID_ACCOUNT_NUMBER");
                return false;
            } else if (t.getExpiration() == null || t.getExpiration().trim().isEmpty()) {
                this.buildErrorResponse(t, "INVALID_EXPIRATION_DATE", "INVALID_EXPIRATION_DATE");
                return false;
            }
        }

        //Handle Void and Reversal Transactions
//        if ((t.getReversal() != null && !t.getReversal().trim().isEmpty())
//                || (t.getVoidFlag() != null && !t.getVoidFlag().trim().isEmpty())) {
//            t.setRequestType(RequestType.REFUND);
//
//        }
        // validate fields here
        String mediaType = t.getMedia();
        String PlanNbr = t.getPlanNumber();
//        String mediaTypeFromAccount = GetMediaTypeByAccountNbr.getCardType(t.getAccount());
//
//        // Validate Account Number
//        if (!mediaType.equalsIgnoreCase(mediaTypeFromAccount)) {
//            t.setResponseType(ResponseType.DECLINED);
//            t.setDescriptionField("INVALID ACCOUNT");
//            return false;
//        }
        // Milstar transaction should have a Plan Number
        if (t.getMedia().equalsIgnoreCase(MediaType.MIL_STAR)) {
            if (PlanNbr == null || PlanNbr.equalsIgnoreCase("") || PlanNbr.trim().isEmpty()) {
                this.buildErrorResponse(t, "INVALID_PLAN_NUMBER", "INVALID_PLAN_NUMBER");
                return false;
            }
        }

        if (t.getSettleIndicator() == null || !t.getSettleIndicator().equalsIgnoreCase(SettleConstant.TRUE)) {
            this.buildErrorResponse(t, "INVALID_SETTLE_INDICATOR", "INVALID_SETTLE_INDICATOR");
            return false;
        }
        LOG.info("validation ended in retailStrategy ");
        return true;
    }

    private void buildErrorResponse(Transaction t, String reasonCode, String description) {
        t.setReasonCode(configurator.get(reasonCode));
        t.setResponseType(ResponseType.DECLINED);
        t.setDescriptionField(description);
        LOG.error("Exception/Error occured. reasonCode:" + reasonCode + " .description" + description);
    }

    private String getSystemDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String ts = dateFormat.format(date);
        return ts;
    }

    public TimeoutProcessor getTimeoutProcessor() {
        return timeoutProcessor;
    }

    public void setTimeoutProcessor(TimeoutProcessor timeoutProcessor) {
        this.timeoutProcessor = timeoutProcessor;
    }

    public void setFutureVisionGateway(FutureVisionGateway futureVisionGateway) {
        this.futureVisionGateway = futureVisionGateway;
    }

    /* ADDED COMMON CODE TO CHECK IF SETTLEMENT STATUS - START */
    public Transaction checkTransactionSettlementStatus(Transaction t, Gateway gateway) {
        settleEntity = findSettleEntity(t);
        if (settleEntity == null) {
            LOG.error("NO_ENTITY_FOUND_FOR_SETTELMENT");
            throw new AuthorizerException("NO_ENTITY_FOUND_FOR_SETTELMENT");
        } else if (settleEntity.getSettlestatus().equalsIgnoreCase(SettleStatus.Done)) {
            this.buildErrorResponse(t, "TRANSACTION_ALREADY_SETTLED", "TRANSACTION_ALREADY_SETTLED");
            return t;
        }
        t = gateway.processMessage(t);  //calling refund 
        if (ResponseType.APPROVED.equalsIgnoreCase(t.getResponseType())) {
            settleEntity.setSettlestatus(SettleConstant.Not_to_settle);
            updateSettle(settleEntity);
        }
        return t;
    }
    /* ADDED COMMON CODE TO CHECK IF SETTLEMENT STATUS - START */
    
    public void setSettleMessageDAO(SettleMessageDAO settleMessageDAO) {
        this.settleMessageDAO = settleMessageDAO;
    } 
    
    /**
     * @param configurator the configurator to set
     */
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }
}