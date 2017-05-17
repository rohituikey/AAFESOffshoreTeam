/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.authorizer;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Authorizer;
import com.aafes.stargate.control.AuthorizerException;
import com.aafes.stargate.control.CustInfo;
import com.aafes.stargate.control.MQServ;
import com.aafes.stargate.gateway.Gateway;
import static com.aafes.stargate.gateway.vision.Common.convertStackTraceToString;
import com.aafes.stargate.util.GetMediaTypeByAccountNbr;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.ResponseCodes;
import com.aafes.stargate.util.ResponseType;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ganjis
 */
@Stateless
public class EcommStrategy extends BaseStrategy {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(Authorizer.class.getSimpleName());

    @Override
    public Transaction processRequest(Transaction t) {

        try {

            boolean ecommFieldsValid = this.validateECommFields(t);
            if (!ecommFieldsValid) {
                return t;
            }
            if (t.getMedia().equals(MediaType.MIL_STAR)) {
                CIDValidation(t);
            }

            //Send transaction to Gateway
            Gateway gateway = super.pickGateway(t);
            if (gateway != null) {
                t = gateway.processMessage(t);
                // check response from t, if declined flip plan number and send it again for authorization
                if (t.getMedia().equalsIgnoreCase(MediaType.MIL_STAR)
                        && t.getResponseType().equalsIgnoreCase(ResponseType.DECLINED)
                        && "20001".equalsIgnoreCase(t.getPlanNumber())
                        && ("364".equalsIgnoreCase(t.getReasonCode())
                        || "190".equalsIgnoreCase(t.getReasonCode())
                        || "023".equalsIgnoreCase(t.getReasonCode()))) {
                    t.setPlanNumber("10001");
                    t = gateway.processMessage(t);
                }
            }

        } catch (AuthorizerException e) {
            buildErrorResponse(t, "", e.getMessage());
            return t;
        } catch (Exception e) {
            buildErrorResponse(t, "", e.getMessage());
            return t;
        }
        return t;
    }

    public boolean validateECommFields(Transaction t) {

        // Validate only fields which are required for ECOMM 
        // Settle Indicator is always false
        if ("true".equalsIgnoreCase(t.getSettleIndicator())) {
            buildErrorResponse(t, "", "INVALID SETTLE INDICATOR");
            return false;
        }

//        if (t.getReversal() != null && !t.getReversal().isEmpty()) {
//            //TODO : Remove below line when handling reversals
//            buildErrorResponse(t, "", "REVERSAL IS NOT SUPPORTED");
//            return false;
//        }
        if (t.getVoidFlag() != null && !t.getVoidFlag().isEmpty()) {
            //TODO : Remove below line when handling voids
            buildErrorResponse(t, "", "VOID IS NOT SUPPORTED");
            return false;
        }

        if (t.getOrderNumber() != null && !t.getOrderNumber().isEmpty()
                && t.getOrderNumber().length() > 22) {
            //TODO : Remove below line when handling voids
            buildErrorResponse(t, "", "INVALID ORDER NUMBER");
            return false;
        }

        // Bin Range     
        String mediaType = t.getMedia();
        String PlanNbr = t.getPlanNumber();
//        String mediaTypeFromAccount = GetMediaTypeByAccountNbr.getCardType(t.getAccount());
//        if (!mediaType.equalsIgnoreCase(mediaTypeFromAccount)) {
//            buildErrorResponse(t, ResponseCodes.INVALID_ACCOUNT, "INVALID ACCOUNT");
//            return false;
//        }
        // For Milstar, Plan Number should be there
        if (t.getMedia().equalsIgnoreCase(MediaType.MIL_STAR)) {
            if (PlanNbr.equalsIgnoreCase("") || PlanNbr.trim().isEmpty()) {
                buildErrorResponse(t, ResponseCodes.INVALID_CREDIT_PLAN, "INVALID CREDIT PLAN");
                return false;
            }
        }

        // AVS Verification
        if (t.getZipCode() == null || t.getZipCode().trim().isEmpty()) {
            if (t.getBillingZipCode() == null
                    || t.getBillingZipCode().trim().isEmpty()) {
                buildErrorResponse(t, ResponseCodes.INVALID_ADDRESS, "INVALID ADDRESS");
                return false;
            }
            if (t.getBillingAddress() == null
                    || t.getBillingAddress().trim().isEmpty()) {
                buildErrorResponse(t, ResponseCodes.INVALID_ADDRESS, "INVALID ADDRESS");
                return false;
            }
            if (t.getBillingCountryCode() == null
                    || t.getBillingCountryCode().trim().isEmpty()) {
                buildErrorResponse(t, ResponseCodes.INVALID_ADDRESS, "INVALID ADDRESS");
                return false;
            }

            if (t.getCardHolderName() == null
                    || t.getCardHolderName().trim().isEmpty()) {
                buildErrorResponse(t, ResponseCodes.INVALID_ADDRESS, "INVALID ADDRESS");
                return false;
            }
        }

        return true;
    }

    private void buildErrorResponse(Transaction t, String reasonCode, String description) {
        t.setReasonCode(reasonCode);
        t.setResponseType(ResponseType.DECLINED);
        t.setDescriptionField(description);
    }

    private void CIDValidation(Transaction t) {

        boolean authorizedForMilstar = true;
        //If we fail to perform this check let the authorization go through. 
        try {
            LOG.debug("Calling custInfo ");

            CustInfo custInfo = new CustInfo();
            String ssn = custInfo.callCustomerLookup(t.getCustomerId());
            if (ssn != null && !ssn.equals("")) {
                MQServ mqServ = new MQServ();
                authorizedForMilstar = mqServ.callMatch(ssn, t.getAccount());
            } else {
                throw new AuthorizerException("Unable to lookup the ssn for cid " + t.getCustomerId());
            }
        } catch (Exception ce) {
            String longDescription = "Unable to perform the milstar cid lookup " + ce.getMessage() + ".";
            LOG.warn(longDescription);
            LOG.error(convertStackTraceToString(ce));
            t.setComment("Unable to perform the milstar cid lookup");

        }

        if (!authorizedForMilstar) {
            throw new AuthorizerException("Customer ID is not authorized to use the milstar card"); //buildResponseMessage(message, rrn, 'D', "995", "NOT ALLOWD", "Customer ID is not authorized to use the milstar card");                                 
        }

    }
}
