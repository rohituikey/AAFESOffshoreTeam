package com.aafes.stargate.gateway.fdms;

import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.Gateway;
import com.aafes.stargate.util.ResponseType;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import org.slf4j.LoggerFactory;

@Stateless
public class CompassGateway   extends Gateway {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(CompassGateway.class.getSimpleName());

    @EJB
    private CompassGatewayProcessor cgp;
    
    @Resource
    private SessionContext context;
    
    @Timeout

    

    public void createTimer(long duration) {
      context.getTimerService().createTimer(2500, "My timer");
   }
    
    @Override
    public Transaction processMessage(Transaction t) {

        try {
            this.validateTransaction(t);
            if (cgp != null) {
                t = cgp.execute(t);
            } else {
                t.setResponseType(ResponseType.DECLINED);
                t.setDescriptionField("INTERNAL SERVER ERROR");
                return t;
            }
        } catch (GatewayException e) {
            t.setResponseType(ResponseType.DECLINED);
            t.setDescriptionField(e.getMessage());
        }

        return t;
    }

    private void validateTransaction(Transaction t) throws GatewayException {
        
       
    }

    public void setCgp(CompassGatewayProcessor cgp) {
        this.cgp = cgp;
    }

    
}
