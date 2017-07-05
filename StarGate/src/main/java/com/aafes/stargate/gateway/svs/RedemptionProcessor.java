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
import com.svs.svsxml.beans.RedemptionRequest;
import com.svs.svsxml.beans.RedemptionResponse;
import com.svs.svsxml.service.SVSXMLWay;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alugumetlas
 */
@Stateless
public class RedemptionProcessor extends Processor {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RedemptionProcessor.class.getSimpleName());

    @Override
    public void processRequest(Transaction t) {
        try {
            log.info("RedemptionProcessor.processRequest  is started");

            RedemptionRequest redemptionRequest = new RedemptionRequest();
            Amount amount = new Amount();
            amount.setAmount(t.getAmount());
            amount.setCurrency(StarGateConstants.CURRENCY);
            redemptionRequest.setRedemptionAmount(amount);

            Card card = new Card();
            card.setCardCurrency(StarGateConstants.CURRENCY);
            card.setCardNumber(t.getAccount());
            card.setPinNumber(t.getGcpin());
            redemptionRequest.setCard(card);

//           redemptionRequest.setStan(t.getSTAN());
            redemptionRequest.setDate(SvsUtil.formatLocalDateTime());
            redemptionRequest.setRoutingID(StarGateConstants.ROUTING_ID);
            //  redemptionRequest.setTransactionID(t.getRrn()+"0000");

            Merchant merchant = new Merchant();
            merchant.setDivision(StarGateConstants.MERCHANT_DIVISION_NUMBER);
            merchant.setMerchantName(StarGateConstants.MERCHANT_NAME);
            merchant.setMerchantNumber(StarGateConstants.MERCHANT_NUMBER);
            merchant.setStoreNumber(StarGateConstants.STORE_NUMBER);
            redemptionRequest.setMerchant(merchant);

            redemptionRequest.setCheckForDuplicate(StarGateConstants.TRUE);
            if (t.getOrderNumber().length() >= 8 && t.getOrderNumber() != null) {
                redemptionRequest.setInvoiceNumber(t.getOrderNumber().substring(t.getOrderNumber().length() - 8));
            } else {
                redemptionRequest.setInvoiceNumber(t.getOrderNumber());
            }

            log.debug("REQUEST----> Invoice Number " + redemptionRequest.getInvoiceNumber());

            SVSXMLWay sVSXMLWay = SvsUtil.setUserNamePassword();
            RedemptionResponse redemptionResponse = sVSXMLWay.redemption(redemptionRequest);

            if (redemptionResponse != null) {
                log.debug("RESPONSE---->AuthorizationCode " + redemptionResponse.getAuthorizationCode() + "||AMOUNT " + redemptionResponse.getBalanceAmount().getAmount() + "||RETURN  CODE  " + redemptionResponse.getReturnCode().getReturnCode() + "||RETURN  CODE  DISCRIPTION " + redemptionResponse.getReturnCode().getReturnDescription());
                t.setBalanceAmount((long) redemptionResponse.getApprovedAmount().getAmount());
                t.setCurrencycode(StarGateConstants.CURRENCY);

                t.setAccount(redemptionResponse.getCard().getCardNumber());
                t.setExpiration(redemptionResponse.getCard().getEovDate());

                t.setAuthNumber(redemptionResponse.getAuthorizationCode());
                t.setReasonCode(redemptionResponse.getReturnCode().getReturnCode());
                t.setDescriptionField(redemptionResponse.getReturnCode().getReturnDescription());
                t.setTransactionId(redemptionResponse.getTransactionID());
                if (t.getReasonCode().equalsIgnoreCase("01")) {
                    t.setResponseType(ResponseType.APPROVED);
                } else {
                    t.setResponseType(ResponseType.DECLINED);
                }
            }
        } catch (Exception e) {
            log.error(("Error in RedemptionProcessor.processRequest i.e responce is null" + e));
            throw new GatewayException("INTERNAL SERVER ERROR");
        }
        log.debug("rrn number in RedemptionProcessor.processRequest  is :" + t.getRrn());
        log.info("RedemptionProcessor.processRequest  is ended");
    }

}
