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
import com.aafes.stargate.util.InputType;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alugumetlas
 */
@Stateless
public class WEXStrategy extends BaseStrategy {

    @EJB
    private Configurator configurator;
    Transaction storedTran = null;
    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(WEXStrategy.class.getSimpleName());

    @Override
    public Transaction processRequest(Transaction t) {
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
        } catch (AuthorizerException e) {
            buildErrorResponse(t, "", e.getMessage());
            return t;
        } catch (Exception e) {
            buildErrorResponse(t, "", e.getMessage());
            return t;
        }
        LOG.debug("rrn number in WEXStrategy.processRequest is :  " + t.getRrn());
        LOG.info("WEXStrategy.processRequest is ended");
        return t;
    }

    private boolean validateTransactions(Transaction t) {

        WEXValidator wEXValidator = new WEXValidator();

        LOG.info("Validating fields in WEXtrategy");
        String accountNumber = t.getAccount().substring(0, 5);
        if ((t.getAccount() == null || t.getAccount().trim().isEmpty())
                && (accountNumber.equals("690046") || accountNumber.equals("707138"))) {
            this.buildErrorResponse(t, "INVALID_ACCOUNT_NUMBER", "INVALID CARD NUMBER FOR WEX");
            return false;
        }
        if ((!t.getRequestType().trim().isEmpty() || t.getRequestType() != null)
                && (t.getRequestType().equalsIgnoreCase(RequestType.PREAUTH) 
                || t.getRequestType().equalsIgnoreCase(RequestType.FINAL_AUTH))) {

            return wEXValidator.validateForPreAuthAndFinalAuth(t);

        }
        //sale request validation
        if (!t.getRequestType().trim().isEmpty() || t.getRequestType() != null || t.getRequestType().equalsIgnoreCase(RequestType.SALE)) {
            return wEXValidator.validateSale(t);
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

}
