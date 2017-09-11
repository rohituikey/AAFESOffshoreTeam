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
import com.aafes.stargate.util.WexConstants;
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
    @EJB
    private WexSettleMessagesDao wexSettleMessagesDao;
    Transaction storedTran = null;
    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(WEXStrategy.class.getSimpleName());
    private Transaction t;

    @Override
    public Transaction processRequest(Transaction transaction) {
        LOG.info("WEXStrategy.processRequest Entry ... " + transaction.getRrn());
        t = transaction;
        try {

            boolean wexFieldsValid = this.validate(t);
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

    private boolean validate(Transaction t) {
        LOG.info("Validating fields in WEXtrategy");
        if (t.getRequestType() != null && (t.getRequestType().equalsIgnoreCase(RequestType.PREAUTH)
                || t.getRequestType().equalsIgnoreCase(RequestType.FINAL_AUTH))) {
            return wEXValidator.validatePreAuthAndFinalAuth(t);
        } 
        else if (t.getRequestType() != null && t.getRequestType().equalsIgnoreCase(RequestType.SALE)) {
            return wEXValidator.validateSale(t);
        } 
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

    private void saveToWexSettle(Transaction t) {
        LOG.info("WexStrategy.saveToWexSettle method is started");
        List<WexSettleEntity> wexSettleEntityList = new ArrayList();
        WexSettleEntity wexSettleEntity = new WexSettleEntity();

        wexSettleEntity.setAmount(Long.toString(t.getAmount()));
        wexSettleEntity.setAuthRef(t.getAuthNumber());
        if(t.getTransactiontype()== null)  wexSettleEntity.setTransactionCode(WexConstants.TRANSTYPEFINALANDSALE);
        else wexSettleEntity.setTransactionCode(t.getTransactiontype());
        wexSettleEntity.setTransactionId(t.getTransactionId());
        wexSettleEntity.setAppName(configurator.get("APPLICATIONNAME"));
        wexSettleEntity.setTransactionType(t.getRequestType());
        if(wexSettleEntity.getTransactionType() == null)
        wexSettleEntity.setTransactionType(RequestType.FINAL_AUTH);
        wexSettleEntity.setAppVersion(configurator.get("APPLICATIONVERSION"));
        wexSettleEntity.setTid(t.getTid());
        wexSettleEntity.setCardTrack(t.getTrack2());
        wexSettleEntity.setCatFlag(t.getCatFlag());
        wexSettleEntity.setDriverId(t.getDriverId());
        wexSettleEntity.setOdometer(t.getOdoMeter());
        wexSettleEntity.setVehicleId(t.getVehicleId());
        wexSettleEntity.setService(t.getServiceCode());
        wexSettleEntity.setProduct(t.getProducts());
        wexSettleEntity.setOrderNumber(t.getOrderNumber());
        wexSettleEntity.setReceivedDate(this.getSystemDate());
        wexSettleEntity.setPumpNumber(t.getPumpNmbr());
        wexSettleEntity.setTransactionTime(t.getLocalDateTime());
        wexSettleEntity.setSettleStatus(SettleStatus.Ready_to_settle);
        //wexSettleEntity.setSettelmentDate(t.getLocalDateTime().substring(0,11));
        //wexSettleEntity.setSettelmentDate(t.getLocalDateTime().substring(11,22));
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
