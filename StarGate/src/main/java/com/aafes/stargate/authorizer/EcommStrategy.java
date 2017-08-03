/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.authorizer;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.AuthorizerException;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.control.CustInfo;
import com.aafes.stargate.control.MQServ;
import com.aafes.stargate.gateway.Gateway;
import static com.aafes.stargate.gateway.vision.Common.convertStackTraceToString;
import com.aafes.stargate.stub.CIDValidationStub;
import com.aafes.stargate.timeout.TimeoutProcessor;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ganjis
 */
@Stateless
public class EcommStrategy extends BaseStrategy {

    @EJB
    private Configurator configurator;

    @Inject
    private String enableStub;

    @EJB
    private TimeoutProcessor timeoutProcessor;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(EcommStrategy.class.getSimpleName());

    @Override
    public Transaction processRequest(Transaction t) {
        LOG.info("EcommStrategy.processRequest Entry ..."+t.getRrn());
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
            LOG.error(e.getClass() + e.getMessage() +"..."+t.getRrn());
            buildErrorResponse(t, "", e.getMessage());
            return t;
        } catch (Exception e) {
            LOG.error(e.getMessage() +"..."+t.getRrn());
            buildErrorResponse(t, "", e.getMessage());
            return t;
        }
       
       LOG.info("EcommStrategy.processRequest Exit ..."+t.getRrn());
        return t;
    }

    public boolean validateECommFields(Transaction t) {
        LOG.info("EcommStrategy.validateECommFields method started..."+t.getRrn());
        // Validate only fields which are required for ECOMM 
        // Settle Indicator is always false
        if ("true".equalsIgnoreCase(t.getSettleIndicator())) {
            buildErrorResponse(t, configurator.get("INVALID_SETTLE_INDICATOR"), "INVALID_SETTLE_INDICATOR");
            LOG.error("INVALID_SETTLE_INDICATOR ..."+t.getRrn());
            return false;
        }

        if (t.getVoidFlag() != null && !t.getVoidFlag().isEmpty()) {
            //TODO : Remove below line when handling voids
            buildErrorResponse(t, configurator.get("VOID_IS_NOT_SUPPORTED"), "VOID_IS_NOT_SUPPORTED");
            LOG.error("VOID_IS_NOT_SUPPORTED ..."+t.getRrn());
            return false;
        }
        if (t.getOrderNumber() != null && !t.getOrderNumber().isEmpty()
                && t.getOrderNumber().length() > 22) {
            //TODO : Remove below line when handling voids
            buildErrorResponse(t, configurator.get("INVALID_ORDER_NUMBER"), "INVALID_ORDER_NUMBER");
            LOG.error("INVALID_ORDER_NUMBER ..."+t.getRrn());
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
                buildErrorResponse(t, configurator.get("INVALID_CREDIT_PLAN"), "INVALID_CREDIT_PLAN");
                return false;
            }
            if (!RequestType.SALE.equalsIgnoreCase(t.getRequestType())) {
                if (t.getReversal() == null || t.getReversal().trim().isEmpty()) {
                    buildErrorResponse(t, configurator.get("INVALID_REQUEST_TYPE"), "INVALID_REQUEST_TYPE");
                    return false;
                }
            }
        } else if (t.getMedia().equalsIgnoreCase(MediaType.GIFT_CARD)) {
            if (!t.getRequestType().matches("(?i)" + RequestType.ISSUE
                    + "|" + RequestType.PREAUTH + "|"
                    + "|" + RequestType.FINAL_AUTH + "|"
                    + "|" + RequestType.INQUIRY + "|"
                    + "|" + RequestType.REFUND)) {
                buildErrorResponse(t, configurator.get("INVALID_REQUEST_TYPE"), "INVALID_REQUEST_TYPE");
                return false;
            }
        } else if (t.getMedia().equalsIgnoreCase(MediaType.VISA)
                || t.getMedia().equalsIgnoreCase(MediaType.AMEX)
                || t.getMedia().equalsIgnoreCase(MediaType.DISCOVER)
                || t.getMedia().equalsIgnoreCase(MediaType.MASTER)) {
            if (!RequestType.SALE.equalsIgnoreCase(t.getRequestType())) {
                if (t.getReversal() == null || t.getReversal().trim().isEmpty()) {
                    buildErrorResponse(t, configurator.get("INVALID_REQUEST_TYPE"), "INVALID_REQUEST_TYPE");
                    return false;
                }
            }
        }
        // AVS Verification
        if (!t.getMedia().equalsIgnoreCase(MediaType.GIFT_CARD)) {
            if (t.getZipCode() == null || t.getZipCode().trim().isEmpty()) {
                if (t.getBillingZipCode() == null
                        || t.getBillingZipCode().trim().isEmpty()) {
                    buildErrorResponse(t, configurator.get("INVALID_ADDRESS"), "INVALID_ADDRESS");
                    return false;
                }
                if (t.getBillingAddress1() == null
                        || t.getBillingAddress1().trim().isEmpty()) {
                    buildErrorResponse(t, configurator.get("INVALID_ADDRESS"), "INVALID_ADDRESS");
                    return false;
                }
                if (t.getBillingCountryCode() == null
                        || t.getBillingCountryCode().trim().isEmpty()) {
                    buildErrorResponse(t, configurator.get("INVALID_ADDRESS"), "INVALID_ADDRESS");
                    return false;
                }
                if (t.getCardHolderName() == null
                        || t.getCardHolderName().trim().isEmpty()) {
                    buildErrorResponse(t, configurator.get("INVALID_ADDRESS"), "INVALID_ADDRESS");
                    return false;
                }
            }
        }
        LOG.info("EcommStrategy.validateECommFields Exit ..."+t.getRrn());
        return true;
    }

    private void buildErrorResponse(Transaction t, String reasonCode, String description) {
        t.setReasonCode(reasonCode);
        t.setResponseType(ResponseType.DECLINED);
        t.setDescriptionField(description);
    }

    private void CIDValidation(Transaction t) {
        LOG.info("EcommStrategy.CIDValidation Entry ..."+t.getRrn());

        boolean authorizedForMilstar = true;
        if (enableStub != null && enableStub.trim().equalsIgnoreCase("true")) //
        {
            authorizedForMilstar = CIDValidationStub.validateStub(t);
        } else {
            //If we fail to perform this check let the authorization go through.
            try {
                LOG.info("Calling custInfo ");
                CustInfo custInfo = new CustInfo();
                String ssn = custInfo.callCustomerLookup(t.getCustomerId());
                if (ssn != null && !ssn.equals("")) {
                    MQServ mqServ = new MQServ();
                    authorizedForMilstar = mqServ.callMatch(ssn, t.getAccount());
                } else {
                    LOG.error("Unable to lookup the ssn for cid ..."+t.getRrn());
                    throw new AuthorizerException("Unable to lookup the ssn for cid " + t.getCustomerId());
                }
            } catch (Exception ce) {
                LOG.warn("Unable to perform the milstar cid lookup " + ce.getMessage() + "."+t.getRrn());
                LOG.error(convertStackTraceToString(ce));
                t.setComment("Unable to perform the milstar cid lookup");
            }
        }
        if (!authorizedForMilstar) {
            throw new AuthorizerException("Customer ID is not authorized to use the milstar card"); //buildResponseMessage(message, rrn, 'D', "995", "NOT ALLOWD", "Customer ID is not authorized to use the milstar card");                                 
        }
       LOG.info("EcommStrategy.CIDValidation Exit ..."+t.getRrn());

    }

    /**
     * @param configurator the configurator to set
     */
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }

    public void setEnableStub(String enableStub) {
        this.enableStub = enableStub;
    }

    public TimeoutProcessor getTimeoutProcessor() {
        return timeoutProcessor;
    }

    public void setTimeoutProcessor(TimeoutProcessor timeoutProcessor) {
        this.timeoutProcessor = timeoutProcessor;
    }

}