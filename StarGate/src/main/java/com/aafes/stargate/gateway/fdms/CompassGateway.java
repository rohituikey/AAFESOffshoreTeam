package com.aafes.stargate.gateway.fdms;

import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.Gateway;
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
public class CompassGateway   extends Gateway {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(CompassGateway.class.getSimpleName());

    @EJB
    private CompassGatewayProcessor cgp;
    
    @Resource
    private SessionContext context;
    
    private Transaction transaction = new Transaction();
    

    public void createTimer(long duration) {
      context.getTimerService().createTimer(duration, "My timer");
      System.out.println("timer");
   }
    
     @Timeout
     public void timeout(Timer timer) {
         
        // processMessage(transaction);
         
        Transaction localTransaction = new Transaction();
        localTransaction = transaction;
        localTransaction.setRequestType(RequestType.REVERSAL);
        transaction = cgp.execute(localTransaction);
        transaction.setResponseType(ResponseType.TIMEOUT);
        transaction.setDescriptionField("Time out occured");
        transaction.setReasonCode("TIMEOUT OCCURED");
     }
    
    @Override
    public Transaction processMessage(Transaction t) {
        transaction = t;
        try {
            this.validateTransaction(t);
            if (cgp != null) {
                 createTimer(25000);
                    //t.setRequestAuthDateTime(ResponseType.TIMEOUT);
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

        return transaction;
    }

    private void validateTransaction(Transaction t) throws GatewayException {
        
       
    }

    public void setCgp(CompassGatewayProcessor cgp) {
        this.cgp = cgp;
    }

    
}
