/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.svs;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.gateway.Gateway;
import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ganjis
 */
@Stateless
public class SVSGateway extends Gateway {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(SVSGateway.class.getSimpleName());

    @EJB
    private SVSGatewayProcessor svsgp;
    @EJB
    private Configurator configurator;
    
    @Override
    public Transaction processMessage(Transaction t) {

        try {
            boolean validateTransactionFlg = this.validateTransaction(t);
            if (validateTransactionFlg) {
                if (svsgp != null) {
                    t = svsgp.execute(t);
                } else {
                    t.setResponseType(ResponseType.DECLINED);
                    t.setDescriptionField("INTERNAL SERVER ERROR");
                    return t;
                }
            } else {
                LOG.error("Data Validation Failed! " + t.getReasonCode() + ". " + t.getDescriptionField() + ". " + t.getResponseType());
                return t;
            }
        } catch (GatewayException e) {
            t.setResponseType(ResponseType.DECLINED);
            t.setDescriptionField(e.getMessage());
        }

        return t;
    }

    private boolean validateTransaction(Transaction t) throws GatewayException {
        LOG.info("Method validateTransaction started. Class Name SVSGateway");
        if (RequestType.INQUIRY.equalsIgnoreCase(t.getRequestType())
                || RequestType.PREAUTH.equalsIgnoreCase(t.getRequestType())
                || RequestType.FINAL_AUTH.equalsIgnoreCase(t.getRequestType())
                || RequestType.REFUND.equals(t.getRequestType())
                || RequestType.REVERSAL.equals(t.getRequestType())) {
            if (t.getAccount() == null || t.getAccount().trim().isEmpty()) {
                buildErrorResponse(t, configurator.get("INVALID_ACCOUNT_NUMBER"), "INVALID_ACCOUNT_NUMBER");
                return false;
            } 
//            else if (t.getGcpin() == null || t.getGcpin().trim().isEmpty()) {
//                buildErrorResponse(t, "", "PIN NUMBER IS NULL");
//                return false;
//            }
        }


        LOG.info("Method validateTransaction ended. Class Name SVSGateway");
        return true;
    }

    private void buildErrorResponse(Transaction t, String reasonCode, String description) {
        t.setReasonCode(reasonCode);
        t.setResponseType(ResponseType.DECLINED);
        t.setDescriptionField(description);
    }

    public void setSvsgp(SVSGatewayProcessor svsgp) {
        this.svsgp = svsgp;
    }
    
    public Configurator getConfigurator() {
        return configurator;
    }

    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }
}
