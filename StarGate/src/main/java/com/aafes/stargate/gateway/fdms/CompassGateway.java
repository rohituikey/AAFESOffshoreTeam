package com.aafes.stargate.gateway.fdms;

import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.gateway.Gateway;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

@Stateless
public class CompassGateway   extends Gateway {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(CompassGateway.class.getSimpleName());

    @EJB
    private CompassGatewayProcessor cgp;
    
//    
//    @Resource
//    private SessionContext context;
    
    @EJB
    private Configurator configurator;
    
    
    private Transaction transaction = new Transaction();
    


    
    
    
    
    
    
    
    
    
    @Override
    public Transaction processMessage(Transaction t) {
        transaction = t;
        try {
            this.validateTransaction(t);
            if (cgp != null) {
                final ExecutorService service = Executors.newSingleThreadExecutor();
                try {
                    final Future<?> f = service.submit(() -> {
                        transaction= cgp.execute(t);
                    });
                    f.get(25, TimeUnit.SECONDS);
                } catch (final TimeoutException e) {
                    transaction.setRequestType(RequestType.REVERSAL);
                    transaction = cgp.execute(transaction);
                    transaction.setResponseType(ResponseType.TIMEOUT);
                    transaction.setDescriptionField("Time out occured");
                    transaction.setReasonCode(configurator.get("TIMEOUT_EXCEPTION"));
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    service.shutdown();
                }
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
