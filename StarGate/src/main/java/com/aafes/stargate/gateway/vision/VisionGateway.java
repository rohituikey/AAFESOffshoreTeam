package com.aafes.stargate.gateway.vision;

import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.dao.TransactionDAO;
import com.aafes.stargate.gateway.Gateway;
import com.aafes.stargate.util.InputType;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import org.slf4j.LoggerFactory;

@Stateless
public class VisionGateway extends Gateway {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(VisionGateway.class.getSimpleName());

    @EJB
    private VisionPlusProcessor vpp;
    @EJB
    private Configurator configurator;
    @Resource
    private SessionContext context;
    private Transaction t;

    private long expirationTime = 25000;

    @Timeout
    public void programmaticTimeout(Timer timer) {
        if (null == t || null == t.getReasonCode() || t.getReasonCode().isEmpty() || t.getReasonCode().isEmpty()) {
            t.setResponseType(ResponseType.TIMEOUT);
            t.setReasonCode(configurator.get("TIMEOUT_EXCEPTION"));
            LOG.info("programmaticTimeout and is TIMEOUT_EXCEPTION");
        }
        timer.cancel();
    }

    @Override
    public Transaction processMessage(Transaction transaction) {
        t = transaction;
        LOG.info("visiongateway process message method started");
        try {
            this.validateTransaction(t);
            if (vpp != null) {
                context.getTimerService().createTimer(expirationTime, "time Expired");
                t = vpp.authorize(t);
            } else {
                LOG.error("INTERNAL_SERVER_ERROR");
                t.setReasonCode(configurator.get("INTERNAL_SERVER_ERROR"));
                t.setResponseType(ResponseType.DECLINED);
                t.setDescriptionField("INTERNAL_SERVER_ERROR");
                return t;
            }
        } catch (GatewayException e) {
            LOG.error(e.toString());
            t.setReasonCode(configurator.get(e.getMessage()));
            t.setResponseType(ResponseType.DECLINED);
            t.setDescriptionField(e.getMessage());
        } catch (Exception e) {
            LOG.error(e.toString() + "INTERNAL_SERVER_ERROR");
            t.setReasonCode(configurator.get("INTERNAL_SERVER_ERROR"));
            t.setResponseType(ResponseType.DECLINED);
            t.setDescriptionField("INTERNAL_SERVER_ERROR");
        }
        t.setResponseType(ResponseType.APPROVED);
        t.setAuthNumber("123456");
        validateResponse(t);
        LOG.debug("rrn number is "+t.getRrn());
        LOG.info("visiongateway process message method ended");
        return t;
    }

    private void validateTransaction(Transaction t) throws GatewayException {
        LOG.info("VisionGateway.validateTransaction method is satrted");
        // Add all validations here
        if (!t.getMedia().equalsIgnoreCase(MediaType.MIL_STAR)
                && !t.getMedia().equalsIgnoreCase(MediaType.ESSO)) {
            LOG.error("UNSUPPORTED_CARD_TYPE");
            throw new GatewayException("UNSUPPORTED_CARD_TYPE");
        }

        if (!(Validator.isCreditCard(t.getAccount())
                && t.getAccount().length() <= 19)) {
            LOG.error("INVALID_ACCOUNT_NUMBER");
            throw new GatewayException("INVALID_ACCOUNT_NUMBER");
        }

        if (t.getRequestType() == null
                || t.getRequestType().length() == 0
                || t.getRequestType().equals("")) {
            LOG.error("INVALID_REQUEST_TYPE");
            throw new GatewayException("INVALID_REQUEST_TYPE");
        }

        if (!Validator.isSignAmount(Long.toString(t.getAmount()).trim())) {
            LOG.error("INVALID_AMOUNT");
            throw new GatewayException("INVALID_AMOUNT");
        } else if (t.getAmountSign().equalsIgnoreCase("-")
                && !t.getRequestType().equalsIgnoreCase(RequestType.REFUND)) {
            LOG.error("INVALID_AMOUNT");
            throw new GatewayException("INVALID_AMOUNT");
        }

        if (!Validator.isNumberOnly(t.getLocalDateTime())) {
            LOG.error("INVALID_LOCAL_DATETIME");
            throw new GatewayException("INVALID_LOCAL_DATETIME");
        }

        if (!Validator.isExp(t.getExpiration())) {
            LOG.error("INVALID_EXPIRATION_DATE");
            throw new GatewayException("INVALID_EXPIRATION_DATE");
        }

        if (t.getInputType() == null
                || t.getInputType().length() < 0) {
            LOG.error("INVALID_INPUT_TYPE");
            throw new GatewayException("INVALID_INPUT_TYPE");
        }

        if (t.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
            if ((t.getTrack2() == null || t.getTrack2().
                    length() == 0)
                    && (t.getTrack1() == null || t.
                    getTrack1().length() == 0)) {
                LOG.error("INVALID_TRACK_DATA");
                throw new GatewayException("INVALID_TRACK_DATA");
            }
        }

        if (!(t.getFacility() != null && t.getFacility().length() >= 8)) {
            LOG.error("INVALID_FACILITY");
            throw new GatewayException("INVALID_FACILITY");
        }

        if (t.getMedia().equalsIgnoreCase(MediaType.MIL_STAR)
                && (t.getPlanNumber().equalsIgnoreCase("") || t.getPlanNumber().trim().length() == 0)) {
            LOG.error("INVALID_PLAN_NUMBER");
            throw new GatewayException("INVALID_PLAN_NUMBER");
        }

//            if (t.getCvv() == null 
//                    || t.getCvv().equalsIgnoreCase("") 
//                    || t.getCvv().length() <= 2) {
//                throw new GatewayException("INVALID_CVV");
//            }
        LOG.debug("rrn number is."+t.getRrn());
        LOG.info("VisionGateway.validateTransaction method is ended");

    }

    public void setVpp(VisionPlusProcessor visionPlusProcessor) {
        this.vpp = visionPlusProcessor;
    }

    /**
     * @param configurator the configurator to set
     */
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }

    private void validateResponse(Transaction t) {
        LOG.info("VisionGateWay.validateResponce is started");
        if (("SALE".equalsIgnoreCase(t.getRequestType())) && t.getResponseType().equalsIgnoreCase(ResponseType.DECLINED)
                && ("130".equalsIgnoreCase(t.getReasonCode())
                || "99".equalsIgnoreCase(t.getReasonCode())
                || "97".equalsIgnoreCase(t.getReasonCode()))) {
            t.setResponseType(ResponseType.TIMEOUT);
            t.setDescriptionField("Connection TimeOut");
        }
        LOG.info("VisionGateWay.validateResponce is started");
    }
}
