/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.authorizer;

import com.aafes.starsettler.imported.SettleEntity;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Authorizer;
import com.aafes.stargate.control.AuthorizerException;
import com.aafes.stargate.gateway.Gateway;
import com.aafes.stargate.gateway.GatewayFactory;
import com.aafes.stargate.util.InputType;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
import com.aafes.stargate.util.StarGateConstants;
import com.aafes.stargate.util.StrategyType;
import com.aafes.starsettler.imported.SettleConstant;
import com.aafes.starsettler.imported.SettleMessageDAO;
import com.aafes.starsettler.imported.SettleStatus;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author suryadevaral
 */
@Stateless
public class RetailStrategy extends BaseStrategy {

    @EJB
    private SettleMessageDAO settleMessageDAO;
    
    SettleEntity settleEntity;
    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(Authorizer.class.getSimpleName());

    @Override
    public Transaction processRequest(Transaction t) {

        try {

            boolean retailFieldsValid = this.validateRetailFields(t);
            if (!retailFieldsValid) {
                LOG.info("Invalid fields");
                return t;
            }

            // Send transaction to Gateway. 
            Gateway gateway = super.pickGateway(t);
            if (gateway != null) {
                t = gateway.processMessage(t);
            }
            

            if (t.getReversal().equalsIgnoreCase(SettleConstant.TRUE) && t.getSettleIndicator().equalsIgnoreCase(SettleConstant.TRUE)) {
                settleEntity = findSettleEntity(t);
                if (settleEntity != null && settleEntity.getSettlestatus().equalsIgnoreCase(SettleStatus.Ready_to_settle)) {
                    updateSettel(settleEntity);
                } else if (settleEntity != null && settleEntity.getSettlestatus().equalsIgnoreCase(SettleStatus.Done)) {
                    t.setTransactiontype(StarGateConstants.REFUND); //Update the transactio type 
                    t.setRequestType(StarGateConstants.REFUND);
                    if (gateway != null) {
                        t = gateway.processMessage(t);  //calling refund 
                    }

                } else {
                    t.setReasonCode(StarGateConstants.NO_PRIOR_AUTHORIZATION_ERROR);
                    t.setResponseType(ResponseType.DECLINED);
                    t.setDescriptionField(StarGateConstants.DESC);

                }

            }

            //if Authorized, save in settle message repository to settle
            if ((t.getReasonCode().equalsIgnoreCase("000")) && (t.getSettleIndicator().equalsIgnoreCase("true"))) {
                saveToSettle(t);
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
    
    private SettleEntity findSettleEntity(Transaction t){
            return settleMessageDAO.find( t.getLocalDateTime(),t.getOrderNumber(),t.getLocalDateTime(),t.getMedia(),t.getTransactiontype(),t.getCustomerId(),t.getTransactionId());
       
    }
    /**
     * 
     * @param t 
     */
    private void saveToSettle(Transaction t){
    List<SettleEntity> settleEntityList = new ArrayList<SettleEntity>();
    settleEntity = new SettleEntity();
    
    settleEntity.setTransactionId(t.getTransactionId());
    settleEntity.setReceiveddate(this.getSystemDate());
    settleEntity.setOrderNumber(t.getOrderNumber());
    settleEntity.setSettleDate(this.getSystemDate());
    //Card Type not available    
    settleEntity.setTransactionType(t.getTransactiontype());
    //ClientLineId not available  
    settleEntity.setClientLineId(t.getTransactionId());
    settleEntity.setIdentityUUID(t.getIdentityUuid());
    //LineId not available 
    //ShipId not available 
    settleEntity.setRrn(t.getRrn());
    settleEntity.setPaymentAmount(Long.toString(t.getAmount()));
    
    
    //where to map t.getLocalDateTime()
    
    settleEntity.setSettlestatus(SettleStatus.Ready_to_settle);
     
    
    settleEntityList.add(settleEntity);
    settleMessageDAO.save(settleEntityList);
    }

    
    private void updateSettel(SettleEntity settleEntity){
        settleEntity.setSettlestatus(SettleConstant.Not_to_settle);
        settleMessageDAO.update(settleEntity);
    }

    
    private boolean validateRetailFields(Transaction t) {

         LOG.info("Validating fields");
        //Validate Swiped Transaction
        if (t.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
             LOG.info("Validating fields if Swiped");
            if ((t.getTrack2() == null || t.getTrack2().trim().isEmpty())
                    && (t.getTrack1() == null || t.getTrack1().trim().isEmpty())) {
                LOG.error("Track 1&2 data is null");
                t.setResponseType(ResponseType.DECLINED);
                t.setDescriptionField("TRACK DATA REQUIRED");
                return false;
            }
        }

        //Validate Keyed Transaction
        if (t.getInputType().equalsIgnoreCase(InputType.KEYED)) {
            
            if (t.getAccount()== null || t.getAccount().trim().isEmpty()){
                t.setResponseType(ResponseType.DECLINED);
                t.setDescriptionField("ACCOUNT NO. REQUIRED");
                return false;
            }
            
            else if(t.getExpiration()== null || t.getExpiration().trim().isEmpty()) {
                LOG.info("Expiration date: " +t.getExpiration());
             t.setResponseType(ResponseType.DECLINED);
                t.setDescriptionField("EXPIRATION DATE REQUIRED");
                return false;
        }
        }
        
        //Handle Void and Reversal Transactions
        if((t.getReversal() != null && !t.getReversal().trim().isEmpty())
                || (t.getVoidFlag() != null && !t.getVoidFlag().trim().isEmpty())) {
            t.setRequestType(RequestType.REFUND);
          
        }
        // validate fields here
        String mediaType = t.getMedia();
        String PlanNbr = t.getPlanNumber();
//        String mediaTypeFromAccount = GetMediaTypeByAccountNbr.getCardType(t.getAccount());
//
//        // Validate Account Number
//        if (!mediaType.equalsIgnoreCase(mediaTypeFromAccount)) {
//            t.setResponseType(ResponseType.DECLINED);
//            t.setDescriptionField("INVALID ACCOUNT");
//            return false;
//        }
        // Milstar transaction should have a Plan Number
        if (t.getMedia().equalsIgnoreCase(MediaType.MIL_STAR)) {
            if (PlanNbr.equalsIgnoreCase("") || PlanNbr.trim().isEmpty()) {
                t.setResponseType(ResponseType.DECLINED);
                t.setDescriptionField("INVALID CREDIT PLAN");
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
    
    private String getSystemDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String ts = dateFormat.format(date);
        return ts;
    }
    
    
//public static void main(String str[]){
//    BaseStrategy RetailStrategy = new RetailStrategy();
//    Transaction t = new Transaction();
//    t.setStrategy(StrategyType.MPG);
//    t.setTransactionId("1258");
//    t.setOrderNumber("3641258");
//    t.setTransactiontype("Sale");
//    t.setIdentityUuid("302aa1e2-22f0-4a3d-a41c-2c4339d847c4");
//    t.setRrn("hbXQUJ6pGth7");
//    t.setAmount((long)1.00);
//    t.setSettleIndicator("true");
//    t.setReversal("true");
//    t.setLocalDateTime("2017-02-05T09:10:01");
//    t.setCustomerId("01");
//    t.setAccount("6019440000000321");
//    RetailStrategy.setGatewayFactory(new GatewayFactory());
//    RetailStrategy.processRequest(t);
//    
//
//}
    
}
