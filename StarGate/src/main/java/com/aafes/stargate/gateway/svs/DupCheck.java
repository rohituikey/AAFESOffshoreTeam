/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.svs;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.util.ResponseType;
import com.svs.svsxml.beans.PreAuthResponse;
import org.slf4j.LoggerFactory;

/**
 *
 * @author uikuyr
 */
public class DupCheck {
    boolean dupCheckFlag = false;
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PreAuthorizationProcessor.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = DupCheck.this.getClass().getSimpleName();

    
    public boolean handlePreAuthResponse(Transaction t, PreAuthResponse preAuthResponseObj, long timeTaken) {
        sMethodName = "handlePreAuthResponse";
        double approvedAmount;
        String reasonCode = "", reasonDescription = "";

        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        try {
            if (preAuthResponseObj != null) {
                
                if(timeTaken>10){
                    return true;
                }
                
                else if (preAuthResponseObj.getReturnCode() != null) {
                    reasonCode = String.valueOf(preAuthResponseObj.getReturnCode().getReturnCode());
                    reasonDescription = String.valueOf(preAuthResponseObj.getReturnCode().getReturnDescription());

                    LOGGER.info("ReturnCode : " + reasonCode);
                    LOGGER.info("ReturnDescription : " + reasonDescription);

                    if ("".equals(reasonCode)) {
                        dupCheckFlag = true;
                    }

                    if (!dupCheckFlag) {
                        t.setReasonCode(reasonCode);
                        t.setDescriptionField(reasonDescription);
                        if ("01".equalsIgnoreCase(reasonCode)) {
                            t.setResponseType(ResponseType.APPROVED);
                        } else {
                            t.setResponseType(ResponseType.DECLINED);
                        }
                    }
                }

                if (!dupCheckFlag) {
                    if (preAuthResponseObj.getApprovedAmount() != null) {
                        approvedAmount = preAuthResponseObj.getApprovedAmount().getAmount();
                        t.setCurrencycode(preAuthResponseObj.getApprovedAmount().getCurrency());
                        t.setAmtPreAuthorized((long) (approvedAmount * 100));
                    }

                    if (preAuthResponseObj.getBalanceAmount() != null) {
                        t.setBalanceAmount((long) (preAuthResponseObj.getBalanceAmount().getAmount() * 100));
                    }

                    LOGGER.info("AuthorizationCode : " + preAuthResponseObj.getAuthorizationCode());

                    if (preAuthResponseObj.getCard() != null) {
                        t.setCardSequenceNumber(preAuthResponseObj.getCard().getCardNumber());
                    }
                    t.setAuthNumber(preAuthResponseObj.getAuthorizationCode());
                    t.setSTAN(preAuthResponseObj.getStan());
                    t.setTransactionId(preAuthResponseObj.getTransactionID());
                }
            } else {
                LOGGER.error("Response Object is NULL " + sMethodName + " " + CLASS_NAME);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occured in " + sMethodName + ". Exception  : " + e.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        }
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        return dupCheckFlag;
    }
    
}
