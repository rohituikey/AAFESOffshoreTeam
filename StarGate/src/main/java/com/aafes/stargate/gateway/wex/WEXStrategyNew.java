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
import com.aafes.stargate.gateway.vision.Validator;
import com.aafes.stargate.tokenizer.TokenBusinessService;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
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
public class WEXStrategyNew extends BaseStrategy {

    @EJB
    private Configurator configurator;
    @EJB
    private WEXValidator wEXValidator;

    private WexSettleEntiry wexSettleMessages;
    @EJB
    private WexSettleEntityDAO wexSettleMessagesDAO;
    @EJB
    private TokenBusinessService tokenBusinessService;
    Transaction storedTran = null;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(WEXStrategyNew.class.getSimpleName());
    private Transaction t;

    @Override
    public Transaction processRequest(Transaction transaction) {
        LOG.info("WEXStrategyNew.processRequest Entry ... " + transaction.getRrn());
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
                    && ResponseType.APPROVED.equalsIgnoreCase(t.getResponseType())) {
                LOG.info("WEXStrategyNew.processRequest settlements process");
                getToken(t);
                saveToWexSettleMessages(t);
            }
            //ends  here
        } catch (AuthorizerException e) {
            buildErrorResponse(t, "", e.getMessage());
            throw e;
        } catch (Exception e) {
            buildErrorResponse(t, "", e.getMessage());
            throw e;
        }
        LOG.debug("rrn number in WEXStrategyNew.processRequest is :  " + t.getRrn());
        LOG.info("WEXStrategyNew.processRequest is ended");
        return t;
    }

    private boolean validateTransactions(Transaction t) {
        LOG.info("Validating fields in WEXStrategyNew");
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
        LOG.info("validation ended in WEXStrategyNew ");
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
            if (tokenBusinessService != null && !t.getRequestType().equalsIgnoreCase(RequestType.ISSUE)) {
                try {
                    tokenBusinessService.issueToken(t);
                } catch (ProcessingException e) {
                    LOG.error("Cannot generate token. Token Service Error");
                }
            }
        }
    }

    private void saveToWexSettleMessages(Transaction t) {
        LOG.info("WEXStrategyNew.saveToWexSettleMessages method is started");
        List<WexSettleEntiry> wexSettleMessagesList = new ArrayList();
        wexSettleMessages = new WexSettleEntiry();
        wexSettleMessages.setTransactionFileDate("");
        wexSettleMessages.setTransactionFileTime("");
        wexSettleMessages.setTransactionFileSequence("");
        wexSettleMessages.setBatchTId("");
        wexSettleMessages.setBatchId("");
        wexSettleMessages.setBatchApp("");
        wexSettleMessages.setBatchVersion("");
        wexSettleMessages.setTranscardCode("");
        wexSettleMessages.setTransType(t.getTransactiontype());
        wexSettleMessages.setTransNbr("");
        wexSettleMessages.setTransDate(this.getSystemDate());
        wexSettleMessages.setTransTime(this.getSystemTime());
        wexSettleMessages.setCardTrack(t.getTrack2());
        wexSettleMessages.setPumpCat(t.getPumpNmbr());
        wexSettleMessages.setPumpService(t.getPumpNmbr());
        wexSettleMessages.setPumpNbr(t.getPumpNmbr());
        wexSettleMessages.setPumpAmount(String.valueOf(t.getPumpPrice()));
        wexSettleMessages.setProduct(t.getProductGroup().toString());
        wexSettleMessages.setOdometer(t.getOdoMeter());
        wexSettleMessages.setAmount(String.valueOf(t.getAmount()));
        wexSettleMessages.setAuthRef(t.getAuthNumber());
        wexSettleMessages.setDriverId(t.getDriverId());
        wexSettleMessages.setVehicleId(t.getVehicleId());
        wexSettleMessages.setOrderDate(this.getSystemDate());
        wexSettleMessages.setSequenceId(t.getSequenceNumber());
        wexSettleMessages.setSettleId("");
        wexSettleMessages.setSettlestatus(SettleStatus.Ready_to_settle);
        wexSettleMessages.setTime(this.getSystemTime());
        wexSettleMessages.setCatflag("");
        wexSettleMessages.setService("");
        
        wexSettleMessagesList.add(wexSettleMessages);
        wexSettleMessagesDAO.save(wexSettleMessagesList);
        LOG.debug("rrn number in WEXStrategyNew.saveToWexSettleMessages is: " + t.getRrn());
        LOG.info("WEXStrategyNew.saveToWexSettleMessages method is ended");
    }

    private String getSystemDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String ts = dateFormat.format(date);
        return ts;
    }
    
    private String getSystemTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String ts = dateFormat.format(date);
        return ts;
    }
    
    public void setWexSettleMessages(WexSettleEntiry wexSettleMessages) {
        this.wexSettleMessages = wexSettleMessages;
    }

    public void setWexSettleMessagesDAO(WexSettleEntityDAO wexSettleMessagesDAO) {
        this.wexSettleMessagesDAO = wexSettleMessagesDAO;
    }

    public void setTokenBusinessService(TokenBusinessService tokenBusinessService) {
        this.tokenBusinessService = tokenBusinessService;
    }

    public void setStoredTran(Transaction storedTran) {
        this.storedTran = storedTran;
    }

    public void setT(Transaction t) {
        this.t = t;
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
}
