/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.svs;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.util.StarGateConstants;
import com.aafes.stargate.util.SvsUtil;
import com.svs.svsxml.beans.Amount;
import com.svs.svsxml.beans.Card;
import com.svs.svsxml.beans.Merchant;
import com.svs.svsxml.beans.RedemptionRequest;
import com.svs.svsxml.beans.RedemptionResponse;
import com.svs.svsxml.service.SVSXMLWay;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alugumetlas
 */
public class RedemptionProcessor {
     private static final org.slf4j.Logger log
            = LoggerFactory.getLogger(RedemptionProcessor.class.getSimpleName());

    public void processRedemptionRequest(Transaction t) {
        log.info("Method...... " + "processRedemptionRequest.......");

        SVSXMLWay sVSXMLWay = SvsUtil.setUserNamePassword();
       
//<redemptionAmount>
//amount-m
//currency-m
//</redemptionAmount>
//<card>
//cardNumber-m
//pinNumber-m
//cardTrackOne-n
//cardTrackTwo-n
//</card>
//date-m
//invoiceNumber-m
//<merchant>
//merchantName-m
//merchantNumber-m
//storeNumber
//division
//</merchant>
//routingID-m
//stan-m
//transactionID-n
//checkForDuplicate-,m

        
        RedemptionRequest redemptionRequest = new RedemptionRequest();
        Amount amount = new Amount();
        amount.setAmount(t.getAmount());
        amount.setCurrency(StarGateConstants.CURRENCY);
        redemptionRequest.setRedemptionAmount(amount);
        
        Card card = new Card();
        card.setCardCurrency(StarGateConstants.CURRENCY);
        card.setCardNumber(t.getAccount());
        card.setPinNumber("0000"+t.getGcpin());
        redemptionRequest.setCard(card);
        
        redemptionRequest.setCheckForDuplicate(StarGateConstants.TRUE);
        redemptionRequest.setTransactionID(t.getTransactionId());
        redemptionRequest.setDate(SvsUtil.formatLocalDateTime());
        redemptionRequest.setInvoiceNumber(t.getOrderNumber().substring(t.getOrderNumber().length() - 8));
//        redemptionRequest.setStan(t.getSTAN());
        redemptionRequest.setRoutingID(StarGateConstants.ROUTING_ID);
        
        Merchant merchant = new Merchant();
        merchant.setDivision(StarGateConstants.MERCHANT_DIVISION_NUMBER);
        merchant.setMerchantName(StarGateConstants.MERCHANT_NAME);
        merchant.setMerchantNumber(StarGateConstants.MERCHANT_NUMBER);
        merchant.setStoreNumber(StarGateConstants.STORE_NUMBER);
        redemptionRequest.setMerchant(merchant);
        
        log.info("REQUEST---->AuthorizationCode " + t.getAuthoriztionCode() + "||TransactionId " + t.getTransactionId() + "||Invoice Number " + t.getOrderNumber().substring(t.getOrderNumber().length() - 8));

        
        RedemptionResponse redemptionResponse = sVSXMLWay.redemption(redemptionRequest);
        
         try {
            log.info("RESPONSE---->AuthorizationCode " + redemptionResponse.getAuthorizationCode() + "||AMOUNT " + redemptionResponse.getBalanceAmount().getAmount() + "||RETURN  CODE  " + redemptionResponse.getReturnCode().getReturnCode() + "||RETURN  CODE  DISCRIPTION " + redemptionResponse.getReturnCode().getReturnDescription());
            if(redemptionRequest != null)
            {
                 t.setAmount((long) redemptionResponse.getBalanceAmount().getAmount());
                    t.setCurrencycode(StarGateConstants.CURRENCY);

                    t.setCardSequenceNumber(redemptionResponse.getCard().getCardNumber());
                    t.setExpiration(redemptionResponse.getCard().getCardExpiration());

                    t.setReasonCode(redemptionResponse.getReturnCode().getReturnCode());
                    t.setDescriptionField(redemptionResponse.getReturnCode().getReturnDescription());
            }
         }
         catch(GatewayException e){
             
         }
        
        
        
    }

}
