/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.svs;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.util.StarGateConstants;
import com.aafes.stargate.util.SvsUtil;
import com.svs.svsxml.beans.Amount;
import com.svs.svsxml.beans.Card;
import com.svs.svsxml.beans.Merchant;
import com.svs.svsxml.beans.PreAuthCompleteRequest;
import com.svs.svsxml.beans.PreAuthCompleteResponse;
import com.svs.svsxml.service.SVSXMLWay;
import org.slf4j.LoggerFactory;

/**
 *
 * @author uikuyr this class checks if the transaction is pre authorized or not
 * and then does the final confirmation
 */
public class SVSFinalAuth {

    private static final org.slf4j.Logger LOGGER
            = LoggerFactory.getLogger(SVSFinalAuth.class.getSimpleName());

    public void completePreAuth(Transaction transaction) {

        LOGGER.info("Gift Card Request Type : " + transaction.getRequestType());

        try {
            SVSXMLWay sVSXMLWay = SvsUtil.setUserNamePassword();
            PreAuthCompleteRequest request = new PreAuthCompleteRequest();

            //Card details where card number and pin number are mandatory
            Card card = new Card();
            card.setCardNumber(transaction.getAccount());
            card.setPinNumber(transaction.getGcpin());
            card.setCardTrackOne(transaction.getTrack1());
            card.setCardTrackTwo(transaction.getTrack2());
            LOGGER.info("Card Details are : Card number :" + card.getCardNumber() + " Git card pin number would be : " + card.getPinNumber());
            request.setCard(card);
            LOGGER.info("Date " + "2017-05-14T15:37:01" + " Stan: " + transaction.getSTAN());
            request.setDate(transaction.getLocalDateTime());

            //Default it to FALSE. Boolean value present in the SOAP request which specifies the desired SVS 
//        webservices processing style; DUP-CHECK processing is used when checkForDuplicate=TRUE, REVERSAL
//            processing when checkForDuplicate=FALSE
            request.setCheckForDuplicate(StarGateConstants.CHECK_FOR_DUPLICATE);
//        utilized when checkForDuplicate is FALSE
//Systems Trace Audit Number. 
            request.setStan(transaction.getSTAN());

            //Merchant details 
            Merchant merchant = new Merchant();
            merchant.setDivision(transaction.getDivisionnumber());
            merchant.setMerchantName(transaction.getMerchantOrg());
            merchant.setMerchantNumber(StarGateConstants.MERCHANT_NUMBER);
            merchant.setStoreNumber(StarGateConstants.STORE_NUMBER);
            request.setMerchant(merchant);
            LOGGER.info("merchant details: division :" + merchant.getDivision() + " name : " + merchant.getMerchantName() + " merchant number : " + merchant.getMerchantNumber() + " Store Number : " + merchant.getStoreNumber());

            request.setRoutingID(StarGateConstants.ROUTING_ID);
            Amount amount = new Amount();
            amount.setCurrency(StarGateConstants.CURRENCY);
            amount.setAmount(transaction.getAmount());
            request.setTransactionAmount(amount);
            request.setTransactionID(transaction.getTransactionId());
            //invoice number should be last 8 digits of order Number
            
            if(transaction.getOrderNumber().length()<8){
                int zeroesToBeAppended = 8-transaction.getOrderNumber().length();
                for(int i=0 ; i<zeroesToBeAppended ; i++){
                    transaction.setOrderNumber("0"+ transaction.getOrderNumber());
                }
            }
            request.setInvoiceNumber(transaction.getOrderNumber().substring(transaction.getOrderNumber().length() - 8));
            
            LOGGER.info("Amount Details : " + amount.getCurrency() + amount.getAmount());
            LOGGER.info("Transacion ID: " + transaction.getTransactionId());

            PreAuthCompleteResponse response = sVSXMLWay.preAuthComplete(request);

            if (null != response.getAuthorizationCode() && null != response.getReturnCode().getReturnCode() && null != response.getReturnCode().getReturnDescription()) {
                transaction.setAuthoriztionCode(response.getAuthorizationCode());
                transaction.setReasonCode(response.getReturnCode().getReturnCode());
                transaction.setDescriptionField(response.getReturnCode().getReturnDescription());
            }

            LOGGER.info("response : Authorization code :" + response.getAuthorizationCode());
        } catch (Exception e) {
            LOGGER.error("Unexpected exception: " + e.getMessage());
        }
    }
}
