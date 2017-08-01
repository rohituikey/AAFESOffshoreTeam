/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.stargate.authorizer.BaseStrategy;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.AuthorizerException;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.gateway.Gateway;
import com.aafes.stargate.tokenizer.TokenBusinessService;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
import com.aafes.stargate.util.TransactionType;
import com.aafes.starsettler.imported.SettleEntity;
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
 * @author alugumetlas
 */
@Stateless
public class WEXStrategy extends BaseStrategy {

    @EJB
    private Configurator configurator;
    @EJB
    private WEXValidator wEXValidator;
    
    private SettleEntity settleEntity;
    @EJB
    private SettleMessageDAO settleMessageDAO;
    @EJB
    private TokenBusinessService tokenBusinessService;
    Transaction storedTran = null;
    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(WEXStrategy.class.getSimpleName());

    @Override
    public Transaction processRequest(Transaction t) throws AuthorizerException {
        LOG.info("WEXStrategy.processRequest Entry ... " + t.getRrn());
        try {

            boolean retailFieldsValid = this.validateTransactions(t);
            LOG.info("WEXFieldsValid " + retailFieldsValid + "..." + t.getRrn());

            if (!retailFieldsValid) {
                LOG.info("Invalid fields");
                return t;
            }
            Gateway gateway = super.pickGateway(t);
            if (gateway != null) {
                t = gateway.processMessage(t);
            }
            //added code to settle the final auth transactions
             if (t.getRequestType() != null && !t.getRequestType().equalsIgnoreCase(RequestType.FINAL_AUTH)
                    && ResponseType.APPROVED.equalsIgnoreCase(t.getResponseType())) {
                getToken(t);
                saveToSettle(t);
            }
             //ends  here
        } catch (AuthorizerException e) {
            buildErrorResponse(t, "", e.getMessage());
            throw e;
            //return t;
        } catch (Exception e) {
            buildErrorResponse(t, "", e.getMessage());
            throw e;
            //return t;
        }
        LOG.debug("rrn number in WEXStrategy.processRequest is :  " + t.getRrn());
        LOG.info("WEXStrategy.processRequest is ended");
        return t;
    }

    private boolean validateTransactions(Transaction t) throws AuthorizerException{
        LOG.info("Validating fields in WEXtrategy");
        String accountNumber = "";
        boolean errFlg = false;
        try{
            if (t.getAccount() == null || t.getAccount().trim().isEmpty()){
                errFlg = true;
            }

            if(!errFlg){ 
                accountNumber = t.getAccount().substring(0, 5);
                if(accountNumber.equals("690046") || accountNumber.equals("707138")) {
                    errFlg = true;
                }
            }

            if(errFlg){
                this.buildErrorResponse(t, "INVALID_ACCOUNT_NUMBER", "INVALID CARD NUMBER FOR WEX");
                return false;
            }

            //PREAUTH/FINAL_AUTH request validation - start
            if (t.getRequestType() != null && (t.getRequestType().equalsIgnoreCase(RequestType.PREAUTH) 
                    || t.getRequestType().equalsIgnoreCase(RequestType.FINAL_AUTH))) 
                return wEXValidator.validateForPreAuthAndFinalAuth(t);
            //PREAUTH/FINAL_AUTH request validation - end
            //sale request validation - start
            else if (t.getRequestType() != null && t.getRequestType().equalsIgnoreCase(RequestType.SALE))
                return wEXValidator.validateSale(t);
            //sale request validation - end
            // ADDED FOR REFUND REQUEST VALIDATION - START
            else if (t.getRequestType() != null && t.getRequestType().equalsIgnoreCase(RequestType.REFUND))
                return wEXValidator.validateRefundRequest(t);
            // ADDED FOR REFUND REQUEST VALIDATION - END
            else{
                LOG.error("RequestType value is null");
                this.buildErrorResponse(t, "INVALID_REQUEST_TYPE", "INVALID_REQUEST_TYPE");
            }
            LOG.info("validation ended in WEXStrategy ");
            return true;
        }catch(Exception e){
            throw e;
        }
    }

    private void buildErrorResponse(Transaction t, String reasonCode, String description) {
        t.setReasonCode(configurator.get(reasonCode));
        t.setResponseType(ResponseType.DECLINED);
        t.setDescriptionField(description);
        LOG.error("Exception/Error occured. reasonCode:" + reasonCode + " .description" + description);
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
        private void saveToSettle(Transaction t) {
        LOG.info("WexStrategy.saveTOSettle method is started");
        List<SettleEntity> settleEntityList = new ArrayList<SettleEntity>();
        settleEntity = new SettleEntity();

        settleEntity.setTransactionId(t.getTransactionId());
        settleEntity.setReceiveddate(this.getSystemDate());
        settleEntity.setOrderNumber(t.getOrderNumber());
        settleEntity.setSettleDate(this.getSystemDate());
        settleEntity.setOrderDate(this.getSystemDate());
        settleEntity.setTransactionType(t.getTransactiontype());
        settleEntity.setClientLineId(t.getTransactionId());
        settleEntity.setIdentityUUID(t.getIdentityUuid());
        settleEntity.setRrn(t.getRrn());
        settleEntity.setPaymentAmount(Long.toString(t.getAmount()));

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
        LOG.debug("rrn number in WexStrategy.saveTOSettle is: " + t.getRrn());
        LOG.info("WexStrategy.saveTOSettle method is ended");

    }
        private String getSystemDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String ts = dateFormat.format(date);
        return ts;
    }

}
