/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.svs;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.util.ResponseType;
import com.aafes.stargate.util.StarGateConstants;
import com.aafes.stargate.util.SvsUtil;
import com.svs.svsxml.beans.Amount;
import com.svs.svsxml.beans.Card;
import com.svs.svsxml.beans.Merchant;
import com.svs.svsxml.beans.PreAuthCompleteRequest;
import com.svs.svsxml.beans.PreAuthCompleteResponse;
import com.svs.svsxml.service.SVSXMLWay;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author uikuyr this class checks if the transaction is pre authorized or not
 * and then does the final confirmation
 */
@Stateless
public class SVSFinalAuthProcessor extends Processor {

    private static final org.slf4j.Logger LOGGER
            = LoggerFactory.getLogger(SVSFinalAuthProcessor.class.getSimpleName());

    @Override
    public void processRequest(Transaction transaction) {
        LOGGER.info("SVSFinalAuthProcessor.processRequest is started");

        try {
            SVSXMLWay sVSXMLWay = SvsUtil.setUserNamePassword();
            PreAuthCompleteRequest request = new PreAuthCompleteRequest();

            //Card details where card number and pin number are mandatory
            Card card = new Card();
            card.setCardNumber(transaction.getAccount());
            card.setPinNumber(transaction.getGcpin());
            card.setCardTrackOne(transaction.getTrack1());
            card.setCardTrackTwo(transaction.getTrack2());
            request.setCard(card);
            request.setDate(SvsUtil.formatLocalDateTime());

            request.setCheckForDuplicate(StarGateConstants.TRUE);

            //request.setStan(transaction.getSTAN());
            //Merchant details 
            Merchant merchant = new Merchant();
            merchant.setDivision(StarGateConstants.MERCHANT_DIVISION_NUMBER);
            merchant.setMerchantName(StarGateConstants.MERCHANT_NAME);
            merchant.setMerchantNumber(StarGateConstants.MERCHANT_NUMBER);
            merchant.setStoreNumber(StarGateConstants.STORE_NUMBER);
            request.setMerchant(merchant);

            request.setRoutingID(StarGateConstants.ROUTING_ID);
            double amt = transaction.getAmount();
            amt = amt / 100;

            Amount amount = new Amount();
            amount.setCurrency(StarGateConstants.CURRENCY);
            amount.setAmount(amt);
            request.setTransactionAmount(amount);
            request.setTransactionID(transaction.getRrn() + "0000");
            //invoice number should be last 8 digits of order Number
            request.setInvoiceNumber(transaction.getOrderNumber().substring(transaction.getOrderNumber().length() - 8));
            LOGGER.debug("Amount Details : " + amount.getCurrency() + amount.getAmount() + "Transacion ID: " + transaction.getTransactionId() + " Invoice Number : " + request.getInvoiceNumber());

            PreAuthCompleteResponse response = sVSXMLWay.preAuthComplete(request);

            if (null != response.getReturnCode()) {
                LOGGER.debug("Approved Amount Details : " + amount.getCurrency() + response.getApprovedAmount()+"response : Authorization code :" + response.getAuthorizationCode());
                transaction.setReasonCode(response.getReturnCode().getReturnCode());
                if (transaction.getReasonCode().equalsIgnoreCase("01")) {
                    transaction.setResponseType(ResponseType.APPROVED);
                } else {
                    transaction.setResponseType(ResponseType.DECLINED);
                }
                transaction.setDescriptionField(response.getReturnCode().getReturnDescription());
            }
            transaction.setAuthNumber(response.getAuthorizationCode());
            //approved amount
            if (response.getApprovedAmount() != null) {
                transaction.setAmtPreAuthorized((long) (response.getApprovedAmount().getAmount() * 100));
            }
            //balance amount
            if (response.getBalanceAmount() != null) {
                transaction.setBalanceAmount((long) (response.getBalanceAmount().getAmount() * 100));
            }
            transaction.setCurrencycode(StarGateConstants.CURRENCY);
            if (response.getCard().getCardNumber() != null) {
                transaction.setCardReferenceID(response.getCard().getCardNumber());
            }
        } catch (Exception e) {
            LOGGER.error("responce is null and Unexpected exception: " + e.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");

        }
        LOGGER.debug("rrn number in SVSFinalAuthProcessor.processRequest  is :" + transaction.getRrn());
        LOGGER.info("SVSFinalAuthProcessor.processRequest is ended");
    }
}
