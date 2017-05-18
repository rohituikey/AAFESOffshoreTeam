/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * REQUEST SHOULD HAVE FOLLOWING FIELDS AS PER DOCUMENT SHARED ON 17-MAY-2017
    <requestedAmount>			
	amount          Mandatory
	currency	Mandatory
    </requestedAmount>			
    <card>			
        cardNumber	Mandatory
        pinNumber	Mandatory
        cardTrackOne	Non-Mandatory
        cardTrackTwo	Non-Mandatory
    </card>			
    date                Mandatory
    invoiceNumber	Mandatory
    <merchant>			
        merchantName	Mandatory
        merchantNumber	Mandatory
        storeNumber	Mandatory
        division	Non-Mandatory
    </merchant>			
    routingID           Mandatory
    stan                Non-Mandatory
    transactionID	Mandatory
    checkForDuplicate	Mandatory
 */
package com.aafes.stargate.gateway.svs;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.util.StarGateConstants;
import com.aafes.stargate.util.SvsUtil;
import com.svs.svsxml.beans.Amount;
import com.svs.svsxml.beans.Card;
import com.svs.svsxml.beans.Merchant;
import com.svs.svsxml.beans.PreAuthRequest;
import com.svs.svsxml.beans.PreAuthResponse;
import com.svs.svsxml.service.SVSXMLWay;
import org.slf4j.LoggerFactory;

/**
 * @author burangir
 * Used to place a reserve on funds on a card for a fixed period of time or until a Pre-Authorization Completion message is approved.
 * This message type should always be followed by a Pre-Authorization Completion transaction.
 */
public class PreAuthorizationProcessor {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PreAuthorizationProcessor.class.getSimpleName());

    String sMethodName = "";
    final String CLASS_NAME = PreAuthorizationProcessor.this.getClass().getSimpleName();
    boolean validationErrFlg = false;
    double approvedAmount;

    public void preAuth(Transaction t) {
        sMethodName = "preAuth";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        try {
            SVSXMLWay sVSXMLWay = SvsUtil.setUserNamePassword();
            PreAuthRequest preAuthRequest = new PreAuthRequest();

            Amount amountObj = new Amount();
            if(String.valueOf(t.getAmount()) != null){
                amountObj.setAmount(t.getAmount());
                LOGGER.info("Pre-Auth Amount : " + String.valueOf(t.getAmount()));
            } else validationErrFlg = true;
            
            if(t.getCurrencycode() != null) amountObj.setCurrency(t.getCurrencycode());
            else validationErrFlg = true;
            preAuthRequest.setRequestedAmount(amountObj);
            
            Card card = new Card();
            card.setCardCurrency(t.getCurrencycode());
            card.setCardNumber(t.getAccount());
            card.setPinNumber(t.getGcpin());
            card.setCardTrackOne(t.getTrack1());
            card.setCardTrackTwo(t.getTrack2());
            preAuthRequest.setCard(card);
            
            preAuthRequest.setDate(t.getLocalDateTime());
            // GET LAST EIGHT DIGIT OF ORDER NUMBER
            preAuthRequest.setInvoiceNumber(t.getOrderNumber().substring((t.getOrderNumber().length() - 8))); 
            
            //The second subfield is 4 digits to be used identify the PIN.. Each Sub-field should be right justified left zero filled
            if(t.getGcpin() != null && t.getGcpin().trim().length() == 4){
                t.setGcpin("0000" + t.getGcpin());
            }
            
            Merchant merchant = new Merchant();
            merchant.setMerchantName(t.getMerchantOrg());
            merchant.setMerchantNumber(StarGateConstants.MERCHANT_NUMBER);
            merchant.setStoreNumber(StarGateConstants.STORE_NUMBER);
            merchant.setDivision(t.getDivisionnumber());
            preAuthRequest.setMerchant(merchant);
            
            preAuthRequest.setRoutingID(StarGateConstants.ROUTING_ID);
            preAuthRequest.setStan(t.getSTAN());
            preAuthRequest.setTransactionID(t.getTransactionId());
            preAuthRequest.setCheckForDuplicate(StarGateConstants.CHECK_FOR_DUPLICATE);

            PreAuthResponse preAuthResponseObj = sVSXMLWay.preAuth(preAuthRequest);

            if(preAuthResponseObj != null){
                if(preAuthResponseObj.getReturnCode() != null){
                    LOGGER.info("ReturnCode : " + String.valueOf(preAuthResponseObj.getReturnCode().getReturnCode()));
                    t.setReasonCode(preAuthResponseObj.getReturnCode().getReturnCode());
               
                    LOGGER.info("ReturnDescription : " + String.valueOf(preAuthResponseObj.getReturnCode().getReturnDescription()));
                    t.setDescriptionField(preAuthResponseObj.getReturnCode().getReturnDescription());
                }
                
                if(preAuthResponseObj.getApprovedAmount() != null){
                    approvedAmount = preAuthResponseObj.getApprovedAmount().getAmount();
                    LOGGER.info("ApprovedAmount : " + String.valueOf(approvedAmount));
                    LOGGER.info("Currency : " + preAuthResponseObj.getApprovedAmount().getCurrency());
                    t.setAmount((long) approvedAmount);
                    t.setCurrencycode(preAuthResponseObj.getApprovedAmount().getCurrency());
                    t.setAmtPreAuthorized((long) approvedAmount);
                }
                
                if(preAuthResponseObj.getBalanceAmount() != null){
                    LOGGER.info("BalanceAmount : " + String.valueOf(preAuthResponseObj.getBalanceAmount().getAmount()));
                    t.setBalanceAmount((long) preAuthResponseObj.getBalanceAmount().getAmount());
                    LOGGER.info("Currency : " + preAuthResponseObj.getBalanceAmount().getCurrency());
                }
                
                LOGGER.info("AuthorizationCode : " + preAuthResponseObj.getAuthorizationCode());
                if(preAuthResponseObj.getReturnCode() != null) LOGGER.info("ReturnCode : " + preAuthResponseObj.getReturnCode().getReturnCode());

                if(preAuthResponseObj.getCard() != null){
                    LOGGER.info("CardNumber : " + preAuthResponseObj.getCard().getCardNumber());
                    t.setCardSequenceNumber(preAuthResponseObj.getCard().getCardNumber());
                    LOGGER.info("CardHolderName : " + preAuthResponseObj.getCard().getCardNumber());
                    LOGGER.info("CardCurrency : " + preAuthResponseObj.getCard().getCardCurrency());
                    LOGGER.info("CardExpiration : " + preAuthResponseObj.getCard().getCardExpiration());
                    LOGGER.info("CardTrackOne : " + preAuthResponseObj.getCard().getCardTrackOne());
                    LOGGER.info("CardTrackTwo : " + preAuthResponseObj.getCard().getCardTrackTwo());
                    LOGGER.info("EovDate : " + preAuthResponseObj.getCard().getEovDate());
                    LOGGER.info("PinNumber : " + preAuthResponseObj.getCard().getPinNumber());
                } 
                LOGGER.info("ConversionRate : " + preAuthResponseObj.getConversionRate());
                LOGGER.info("Stan : " + preAuthResponseObj.getStan());
                LOGGER.info("TransactionID : " +  preAuthResponseObj.getTransactionID());
                LOGGER.info("Sku : " + preAuthResponseObj.getSku());
                
                t.setAuthoriztionCode(preAuthResponseObj.getAuthorizationCode());
                t.setSTAN(preAuthResponseObj.getStan());
            }else{
                LOGGER.error("Response Object is NULL " + sMethodName + " " + CLASS_NAME);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occured in " + sMethodName + ". Exception  : " + e.getMessage());
            e.printStackTrace();
        }
//        GiftCard giftCard = svsdao.find(t.getAccount(), t.getGcpin());
//
//        if (giftCard == null) {
//            this.handleError(t);
//            return;
//        }
//
//        long gcAmount = 0;
//
//        if (giftCard.getBalanceAmount() != null) {
//            gcAmount = Long.parseLong(giftCard.getBalanceAmount());
//        }
//
//        if (t.getAmount() <= gcAmount) {
//            t.setResponseType(ResponseType.APPROVED);
//            t.setReasonCode("100");
//            t.setAmtPreAuthorized(t.getAmount());
//            t.setBalanceAmount(gcAmount - t.getAmount());
//        } else {
//            t.setResponseType(ResponseType.DECLINED);
//            t.setDescriptionField("INSUFFICIENT_FUNDS");
//            t.setReasonCode("202");
//        }
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
    }
}
