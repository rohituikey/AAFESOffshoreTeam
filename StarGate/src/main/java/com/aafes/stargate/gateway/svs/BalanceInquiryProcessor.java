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
import com.svs.svsxml.beans.BalanceInquiryRequest;
import com.svs.svsxml.beans.BalanceInquiryResponse;
import com.svs.svsxml.beans.Card;
import com.svs.svsxml.beans.Merchant;
import com.svs.svsxml.service.SVSXMLWay;
import javax.ejb.Stateless;
import javax.enterprise.inject.Stereotype;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alugumetlas
 */
@Stateless
public class BalanceInquiryProcessor extends Processor {

    private static final org.slf4j.Logger log
            = LoggerFactory.getLogger(BalanceInquiryProcessor.class.getSimpleName());


    @Override
    public void processRequest(Transaction t) {
        log.info("Method...... " + "processBalanceInquiry.......");

        SVSXMLWay sVSXMLWay = SvsUtil.setUserNamePassword();

        BalanceInquiryRequest balanceInquiryRequest = new BalanceInquiryRequest();

        Amount amount = new Amount();
        amount.setAmount(t.getAmount());
        amount.setCurrency(StarGateConstants.CURRENCY);
        balanceInquiryRequest.setAmount(amount);

        Card card = new Card();
        card.setCardCurrency(StarGateConstants.CURRENCY);
        card.setCardNumber(t.getAccount());
        card.setPinNumber(t.getGcpin());
        balanceInquiryRequest.setCard(card);

        Merchant merchant = new Merchant();
        merchant.setDivision(StarGateConstants.MERCHANT_DIVISION_NUMBER);
        merchant.setMerchantName(StarGateConstants.MERCHANT_NAME);
        merchant.setMerchantNumber(StarGateConstants.MERCHANT_NUMBER);
        merchant.setStoreNumber(StarGateConstants.STORE_NUMBER);
        balanceInquiryRequest.setMerchant(merchant);
//       log.info("$$$$$$$$$$-------"+t.getPinBlock());

        balanceInquiryRequest.setCheckForDuplicate(StarGateConstants.TRUE);
        balanceInquiryRequest.setTransactionID(t.getRrn() + "0000");
        balanceInquiryRequest.setDate(SvsUtil.formatLocalDateTime());
        
        balanceInquiryRequest.setInvoiceNumber(t.getOrderNumber().substring(t.getOrderNumber().length() - 8));

        balanceInquiryRequest.setRoutingID(StarGateConstants.ROUTING_ID);
//        balanceInquiryRequest.setStan(t.getSTAN());
        log.info("REQUEST---->AuthorizationCode " + t.getAuthoriztionCode() + "||TransactionId " + t.getTransactionId() + "||Invoice Number " + t.getOrderNumber().substring(t.getOrderNumber().length() - 8));

        BalanceInquiryResponse balanceInquiryResponse = sVSXMLWay.balanceInquiry(balanceInquiryRequest);
        try {
            log.info("RESPONSE---->AuthorizationCode " + balanceInquiryResponse.getAuthorizationCode() + "||AMOUNT " + balanceInquiryResponse.getBalanceAmount().getAmount() + "||RETURN  CODE  " + balanceInquiryResponse.getReturnCode().getReturnCode() + "||RETURN CODE DISCRIPTION" +  balanceInquiryResponse.getReturnCode().getReturnDescription());

            if (balanceInquiryResponse != null) {

                t.setAuthNumber(balanceInquiryResponse.getAuthorizationCode());

                if (balanceInquiryResponse.getBalanceAmount() != null) {
                    t.setBalanceAmount((long) (balanceInquiryResponse.getBalanceAmount().getAmount()*100));
                    t.setCurrencycode(StarGateConstants.CURRENCY);

                    t.setAccount(balanceInquiryResponse.getCard().getCardNumber());
                    t.setExpiration(balanceInquiryResponse.getCard().getCardExpiration());

                    t.setReasonCode(balanceInquiryResponse.getReturnCode().getReturnCode());
                    t.setDescriptionField(balanceInquiryResponse.getReturnCode().getReturnDescription());
                    if(t.getReasonCode().equalsIgnoreCase("01"))
                    {
                        t.setResponseType(ResponseType.APPROVED);
                    } else {
                        t.setResponseType(ResponseType.DECLINED);
                    }
                }
            }
        } catch (GatewayException e) {
            log.error(("Error in processBalanceInquiry method responce" + e));
            throw new GatewayException("INTERNAL SERVER ERROR");
        }
    }
}
