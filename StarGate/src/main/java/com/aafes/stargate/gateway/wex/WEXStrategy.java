/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.starsettler.imported.WexSettleMessagesDao;
import com.aafes.starsettler.imported.WexSettleEntity;
import com.aafes.stargate.authorizer.BaseStrategy;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.AuthorizerException;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.gateway.Gateway;
import com.aafes.stargate.gateway.vision.Validator;
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
    private WexSettleMessagesDao wexSettleMessagesDao;
    @EJB
    private TokenBusinessService tokenBusinessService;
    Transaction storedTran = null;
    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(WEXStrategy.class.getSimpleName());
    private Transaction t;

    @Override
    public Transaction processRequest(Transaction transaction) {
        LOG.info("WEXStrategy.processRequest Entry ... " + transaction.getRrn());
        t = transaction;
        try {

            boolean wexFieldsValid = this.validateTransactions(t);
            LOG.info("WEXFieldsValid " + wexFieldsValid + "..." + t.getRrn());

            if (!wexFieldsValid) {
                LOG.info("Invalid fields");
                return t;
            }
            Gateway gateway = super.pickGateway(t);
            if (gateway != null) {
                t = gateway.processMessage(t);
            }
            //added code to settle the final auth transactions
            if (t.getRequestType() != null
                    && (t.getRequestType().equalsIgnoreCase(RequestType.SALE)
                    || t.getRequestType().equalsIgnoreCase(RequestType.FINAL_AUTH)
                    || RequestType.REFUND.equals(t.getRequestType()))
                    && ResponseType.APPROVED.equalsIgnoreCase(t.getResponseType())
                    ) {
                LOG.info("WEXStrategy.processRequest settlements process");
                getToken(t);
                saveToSettle(t);
                saveToWexSettle(t);
            }
            //ends  here
        } catch (AuthorizerException e) {
            buildErrorResponse(t, "", e.getMessage());
            throw e;
        } catch (Exception e) {
            buildErrorResponse(t, "", e.getMessage());
            throw e;
        }
        LOG.debug("rrn number in WEXStrategy.processRequest is :  " + t.getRrn());
        LOG.info("WEXStrategy.processRequest is ended");
        return t;
    }

    private boolean validateTransactions(Transaction t) {
        LOG.info("Validating fields in WEXtrategy");
        Validator validator = new Validator();

        //PREAUTH/FINAL_AUTH request validation - start
        if (t.getRequestType() != null && (t.getRequestType().equalsIgnoreCase(RequestType.PREAUTH)
                || t.getRequestType().equalsIgnoreCase(RequestType.FINAL_AUTH))) {
            return wEXValidator.validatePreAuthAndFinalAuth(t);
        } //PREAUTH/FINAL_AUTH request validation - end
        //sale request validation - start
        else if (t.getRequestType() != null && t.getRequestType().equalsIgnoreCase(RequestType.SALE)) {
            return wEXValidator.validateSale(t);
        } //sale request validation - end
        // ADDED FOR REFUND REQUEST VALIDATION - START
        else if (t.getRequestType() != null && t.getRequestType().equalsIgnoreCase(RequestType.REFUND)) {
            return wEXValidator.validateRefundRequest(t);
        } // ADDED FOR REFUND REQUEST VALIDATION - END
        else {
            LOG.error("RequestType value is null");
            this.buildErrorResponse(t, "INVALID_REQUEST_TYPE", "INVALID_REQUEST_TYPE");
        }
        LOG.info("validation ended in WEXStrategy ");
        return true;
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
        settleEntity.setAuthreference(t.getAuthNumber());
        if (t.getQuantity() != null) {
            settleEntity.setQuantity(t.getQuantity().toString());
        }
        settleEntity.setProductgroup(t.getProductGroup());
        settleEntity.setProductcode(t.getProductCode());
        settleEntity.setReasonCode(t.getReasonCode());
        settleEntity.setResponseType(t.getResponseType());
        settleEntity.setCatflag(t.getCatFlag());
        settleEntity.setOdometer(t.getOdoMeter());
        settleEntity.setDescriptionField(t.getDriverId());
        settleEntity.setVehicleId(t.getVehicleId());
        settleEntity.setTrackdata2(t.getTrack2());
        settleEntity.setService(t.getServiceCode());
        if(t.getLocalDateTime() != null && !t.getLocalDateTime().isEmpty() && ResponseType.APPROVED.equalsIgnoreCase(t.getResponseType()))
            settleEntity.setTime(t.getLocalDateTime().substring(6,12));
        settleEntity.setPumpNumber(t.getPumpNmbr());
        if (t.getPricePerUnit() != null) {
            settleEntity.setUnitCost(t.getPricePerUnit().toString());
        }
        if(t.getLocalDateTime() != null && !t.getLocalDateTime().isEmpty() && ResponseType.APPROVED.equalsIgnoreCase(t.getResponseType()))
            settleEntity.setDate(t.getLocalDateTime().substring(0, 6));
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

//To save in WexSett
    private void saveToWexSettle(Transaction t) {
        LOG.info("WexStrategy.saveToWexSettle method is started");

        List<WexSettleEntity> wexSettleEntityList = new ArrayList<WexSettleEntity>();
        WexSettleEntity wexSettleEntity = new WexSettleEntity();

        wexSettleEntity.setAmount( Long.toString(t.getAmount()));
        wexSettleEntity.setAuthRef(t.getAuthNumber());
        //wexSettleEntity.setTransactiontype(t.getTransactiontype());
        wexSettleEntity.setTransactiontype("10");
        wexSettleEntity.setTransactionId(t.getTransactionId());
        wexSettleEntity.setAppName("");
        wexSettleEntity.setAppVersion("");

        wexSettleEntity.setTId(t.getTid());
        //wexSettleEntity.setBatchTId(t.getTid());

        wexSettleEntity.setCardTrack(t.getTrack2());
        wexSettleEntity.setCatflag(t.getCatFlag());
        wexSettleEntity.setDriverId(t.getDriverId());
        wexSettleEntity.setOdometer(t.getOdoMeter());
        wexSettleEntity.setVehicleId(t.getVehicleId());
        wexSettleEntity.setService(t.getServiceCode());
        wexSettleEntity.setProduct(t.getProductGroup());
//       if(t.getOrderNumber() == null)
              wexSettleEntity.setOrdernumber("8888");
        wexSettleEntity.setOrderDate(this.getSystemDate());

        wexSettleEntity.setCatflag(t.getCatFlag());
        wexSettleEntity.setSettlestatus(SettleStatus.Ready_to_settle);
        
        wexSettleEntityList.add(wexSettleEntity);
        wexSettleMessagesDao.saveToWex(wexSettleEntityList);
        LOG.debug("rrn number in WexStrategy.saveTOSettle is: " + t.getRrn());
        LOG.info("WexStrategy.saveTOSettle method is ended");

    }

    private String getSystemDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String ts = dateFormat.format(date);
        return ts;
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

    /**
     * @param wEXValidator the wEXValidator to set
     */
    public void setwEXValidator(WEXValidator wEXValidator) {
        this.wEXValidator = wEXValidator;
    }

    public WexSettleMessagesDao getWexSettleMessagesDao() {
        return wexSettleMessagesDao;
    }

    public void setWexSettleMessagesDao(WexSettleMessagesDao wexSettleMessagesDao) {
        this.wexSettleMessagesDao = wexSettleMessagesDao;
    }
    
}
