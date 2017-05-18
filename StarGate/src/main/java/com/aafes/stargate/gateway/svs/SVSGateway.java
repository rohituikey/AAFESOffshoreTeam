/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.svs;

import com.aafes.stargate.authorizer.entity.Transaction;
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

    @Override
    public Transaction processMessage(Transaction t) {

        try {
            boolean validateTransactionFlg = this.validateTransaction(t);
            if(validateTransactionFlg){
                if (svsgp != null) {
                    t = svsgp.execute(t);
                } else {
                    t.setResponseType(ResponseType.DECLINED);
                    t.setDescriptionField("INTERNAL SERVER ERROR");
                    return t;
                }
            }else{
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

        if(t.getCurrencycode() == null || t.getCurrencycode().trim().isEmpty()){
            buildErrorResponse(t, "", "CURRENCY IS NULL");
            return false;
        }
        
        if(!RequestType.ISSUE.equals(t.getRequestType())){
            if(t.getAccount() == null || t.getAccount().trim().isEmpty()){
                buildErrorResponse(t, "", "CARD NUMBER IS NULL");
                return false;
            } else if(t.getGcpin() == null || t.getGcpin().trim().isEmpty()){
                buildErrorResponse(t, "", "PIN NUMBER IS NULL");
                return false;
            }
        }   
        
        if(t.getLocalDateTime() == null || t.getLocalDateTime().trim().isEmpty()){
            buildErrorResponse(t, "", "TRANSACITON DATE IS NULL");
            return false;
        } else if(t.getOrderNumber() == null || t.getOrderNumber().isEmpty()){
            buildErrorResponse(t, "", "INVOICE NUMBER IS NULL");
            return false;
        } else if(!RequestType.ISSUE.equals(t.getRequestType()) && (t.getTransactionId() == null || t.getTransactionId().trim().isEmpty())){
            buildErrorResponse(t, "", "TRANSACTION ID IS NULL");
            return false;
        } 
        
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

}
