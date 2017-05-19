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
import com.svs.svsxml.beans.BalanceInquiryRequest;
import com.svs.svsxml.beans.BalanceInquiryResponse;
import com.svs.svsxml.beans.Card;
import com.svs.svsxml.beans.Merchant;
import com.svs.svsxml.service.SVSXMLWay;
import com.svs.svsxml.service.SVSXMLWayService;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alugumetlas
 */
public class BalanceInquiryProcessor {

    private static final org.slf4j.Logger log
            = LoggerFactory.getLogger(BalanceInquiryProcessor.class.getSimpleName());

    public void processBalanceInquiry(Transaction t) {
        log.info("Method...... " + "processBalanceInquiry.......");

        SVSXMLWay sVSXMLWay = SvsUtil.setUserNamePassword();

        BalanceInquiryRequest balanceInquiryRequest = new BalanceInquiryRequest();

        Amount amount = new Amount();
        amount.setAmount(t.getAmount());
        amount.setCurrency(t.getCurrencycode());
        balanceInquiryRequest.setAmount(amount);

        Card card = new Card();
        card.setCardCurrency(t.getCurrencycode());
        card.setCardNumber(t.getAccount());
        card.setPinNumber(t.getGcpin());
        balanceInquiryRequest.setCard(card);

        Merchant merchant = new Merchant();
        merchant.setDivision(t.getDivisionnumber());
        merchant.setMerchantName(StarGateConstants.MERCHANT_NAME);
        merchant.setMerchantNumber(StarGateConstants.MERCHANT_NUMBER);
        merchant.setStoreNumber(StarGateConstants.STORE_NUMBER);
        balanceInquiryRequest.setMerchant(merchant);

        balanceInquiryRequest.setCheckForDuplicate(StarGateConstants.TRUE);
        balanceInquiryRequest.setTransactionID(t.getTransactionId());
        balanceInquiryRequest.setDate(t.getLocalDateTime());
        log.info(t.getOrderNumber().substring(t.getOrderNumber().length()-8));
         
        balanceInquiryRequest.setRoutingID(StarGateConstants.ROUTING_ID);
        balanceInquiryRequest.setStan(t.getSTAN());

        BalanceInquiryResponse balanceInquiryResponse = sVSXMLWay.balanceInquiry(balanceInquiryRequest);
        try {
            if (balanceInquiryResponse != null) {
                log.info("Method...... " + "processBalanceInquiry.Response......");
                t.setAuthoriztionCode(balanceInquiryResponse.getAuthorizationCode());
                if (balanceInquiryResponse.getBalanceAmount() != null) {
                    t.setAmount((long) balanceInquiryResponse.getBalanceAmount().getAmount());
                    t.setCurrencycode(balanceInquiryResponse.getBalanceAmount().getCurrency());
                }

                t.setSTAN(balanceInquiryResponse.getStan());

                if (balanceInquiryResponse.getCard() != null) {
                    t.setCardSequenceNumber(balanceInquiryResponse.getCard().getCardNumber());
                    t.setExpiration(balanceInquiryResponse.getCard().getCardExpiration());

                }
                if (balanceInquiryResponse.getReturnCode() != null) {
                    t.setReasonCode(balanceInquiryResponse.getReturnCode().getReturnCode());
                    t.setDescriptionField(balanceInquiryResponse.getReturnCode().getReturnDescription());
                }
            }
        } catch (Exception e) {
            System.err.print(e);
        }
    }
}
