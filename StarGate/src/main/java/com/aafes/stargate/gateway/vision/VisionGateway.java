package com.aafes.stargate.gateway.vision;

import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.gateway.Gateway;
import com.aafes.stargate.util.InputType;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

@Stateless
public class VisionGateway extends Gateway {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(VisionGateway.class.getSimpleName());

    @EJB
    private VisionPlusProcessor vpp;
    @EJB
    private Configurator configurator;

    @Override
    public Transaction processMessage(Transaction t) {

        try {
            this.validateTransaction(t);
            if (vpp != null) {
                t = vpp.authorize(t);
            } else {
                t.setReasonCode(configurator.get("INTERNAL_SERVER_ERROR"));
                t.setResponseType(ResponseType.DECLINED);
                t.setDescriptionField("INTERNAL_SERVER_ERROR");
                return t;
            }
        } catch (GatewayException e) {
            t.setReasonCode(configurator.get(e.getMessage()));
            t.setResponseType(ResponseType.DECLINED);
            t.setDescriptionField(e.getMessage());
        }

        return t;
    }

    private void validateTransaction(Transaction t) throws GatewayException {
        
        // Add all validations here
        if (!t.getMedia().equalsIgnoreCase(MediaType.MIL_STAR)
                && !t.getMedia().equalsIgnoreCase(MediaType.ESSO)) {
            throw new GatewayException("UNSUPPORTED_CARD_TYPE");
        }

        if (! ( Validator.isCreditCard(t.getAccount())
                && t.getAccount().length() <= 19 ) ) {
            throw new GatewayException("INVALID_ACCOUNT_NUMBER");
        }

        if (t.getRequestType() == null 
                || t.getRequestType().length() == 0 
                || t.getRequestType().equals("") ) {
            throw new GatewayException("INVALID_REQUEST_TYPE");
        }

        if (!Validator.isSignAmount(Long.toString(t.getAmount()).trim())) {
            throw new GatewayException("INVALID_AMOUNT");
        } else if (t.getAmountSign().equalsIgnoreCase("-")
                && !t.getRequestType().equalsIgnoreCase(RequestType.REFUND)) {
            throw new GatewayException("INVALID_AMOUNT");
        }
        
         if (!Validator.isNumberOnly(t.getLocalDateTime())) {
             throw new GatewayException("INVALID_LOCAL_DATETIME");
         }
         
         if (!Validator.isExp(t.getExpiration())) {
              throw new GatewayException("INVALID_EXPIRATION_DATE");
         }
         
          if (t.getInputType() == null 
                  || t.getInputType().length() < 0) {
               throw new GatewayException("INVALID_INPUT_TYPE");
          }
          
           if (t.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
            if ((t.getTrack2() == null || t.getTrack2().
                    length() == 0)
                    && (t.getTrack1() == null || t.
                    getTrack1().length() == 0)) {
                throw new GatewayException("INVALID_TRACK_DATA");
            }
           }
           
            if(!(t.getFacility() != null && t.getFacility().length() >= 8)) {
                throw new GatewayException("INVALID_FACILITY");
            }
            
            if (t.getMedia().equalsIgnoreCase(MediaType.MIL_STAR)
                    && (t.getPlanNumber().equalsIgnoreCase("") || t.getPlanNumber().trim().length() == 0)) {
               throw new GatewayException("INVALID_PLAN_NUMBER");
           }
            
            if (t.getCvv() == null 
                    || t.getCvv().equalsIgnoreCase("") 
                    || t.getCvv().length() <= 2) {
                throw new GatewayException("INVALID_CVV");
            }
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
    
}
