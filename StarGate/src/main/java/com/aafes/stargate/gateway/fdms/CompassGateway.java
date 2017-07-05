package com.aafes.stargate.gateway.fdms;

import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.gateway.Gateway;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

@Stateless
public class CompassGateway extends Gateway {
    
    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(CompassGateway.class.getSimpleName());
    
    @EJB
    private CompassGatewayProcessor cgp;
    
    @EJB    
    private Configurator configurator;
    
    @Override
    public Transaction processMessage(Transaction t) {
        LOG.info("CompassGateway.processMessage method is started");
        try {
            if (this.validateTransaction(t)) {
                t = cgp.execute(t);
                validateResponse(t);
            } else {
                t.setResponseType(ResponseType.DECLINED);
                t.setDescriptionField("INTERNAL SERVER ERROR");
                return t;
            }
        } catch (GatewayException e) {
            LOG.error(e.toString()+". ResponseType is :"+ResponseType.DECLINED+". Description is :"+e.getMessage());
            t.setResponseType(ResponseType.DECLINED);
            t.setDescriptionField(e.getMessage());
        }
        LOG.debug(("rrn number in CompassGateway.processMessage is " + t.getRrn()));
        LOG.info("CompassGateway.processMessage method is ended");
        return t;
    }
    
    private boolean validateTransaction(Transaction t) throws GatewayException {
        return true;
    }

//    public void setCgp(CompassGatewayProcessor cgp) {
//        this.cgp = cgp;
//    }
    private void validateResponse(Transaction t) {
        LOG.info("in compassGateway validateResponse method is started");
        if (t.getRequestType().equalsIgnoreCase("SALE") && t.getReasonCode().equalsIgnoreCase("000")) {
            t.setResponseType(ResponseType.TIMEOUT);
            t.setDescriptionField("Connection TimeOut");
        } else if (t.getReasonCode().equals(configurator.get("TIMEOUT_EXCEPTION"))) {
            LOG.info("TIMEOUT_EXCEPTION and requsting for reversal ");
            t.setRequestType(RequestType.REVERSAL);
            InitiateReversal initiateResponse = new InitiateReversal(t);
            Thread triggerResponse = new Thread(initiateResponse);
            triggerResponse.start();
        }
        LOG.info("in compassGateway validateResponse method is ended");        
    }
    
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }
    
}
