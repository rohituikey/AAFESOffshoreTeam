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
import com.svs.svsxml.beans.IssueGiftCardRequest;
import com.svs.svsxml.beans.IssueGiftCardResponse;
import com.svs.svsxml.beans.Merchant;
import com.svs.svsxml.service.SVSXMLWay;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author uikuyr
 */
@Stateless
public class SVSIssueGiftCardProcessor extends Processor {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PreAuthorizationProcessor.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = SVSIssueGiftCardProcessor.this.getClass().getSimpleName();

    @Override
    public void processRequest(Transaction transaction) {
        try {
            sMethodName = "sVSIssueGiftCard";
            LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);

            IssueGiftCardRequest request = new IssueGiftCardRequest();

            Amount amount = new Amount();
            amount.setAmount(transaction.getAmount());
            amount.setCurrency(transaction.getCurrencycode());
            request.setIssueAmount(amount);

            Card card = new Card();
            card.setPinNumber(transaction.getGcpin());
            card.setCardNumber(transaction.getAccount());
            card.setCardCurrency(transaction.getCurrencycode());
            card.setCardTrackOne(transaction.getTrack1());
            card.setCardTrackTwo(transaction.getTrack2());
            request.setCard(card);

            request.setDate(SvsUtil.formatLocalDateTime());
            request.setInvoiceNumber(transaction.getOrderNumber().substring(transaction.getOrderNumber().length() - 8));
            Merchant merchant = new Merchant();
            merchant.setDivision(StarGateConstants.MERCHANT_DIVISION_NUMBER);
            merchant.setMerchantName(StarGateConstants.MERCHANT_NAME);
            merchant.setMerchantNumber(StarGateConstants.MERCHANT_NUMBER);
            merchant.setStoreNumber(StarGateConstants.STORE_NUMBER);
            request.setMerchant(merchant);

            request.setRoutingID(StarGateConstants.ROUTING_ID);
            request.setStan(transaction.getSTAN());
            request.setTransactionID(transaction.getTransactionId());
            SVSXMLWay svsXMLWay = SvsUtil.setUserNamePassword();
            IssueGiftCardResponse response = svsXMLWay.issueGiftCard(request);
            transaction.setReasonCode(response.getReturnCode().getReturnCode());
            transaction.setDescriptionField(response.getReturnCode().getReturnDescription());
            if (transaction.getReasonCode().equalsIgnoreCase("01")) {
                transaction.setResponseType(ResponseType.APPROVED);
            } else {
                transaction.setResponseType(ResponseType.DECLINED);
            }
            transaction.setAuthoriztionCode(response.getAuthorizationCode());
            transaction.setAmount((long) response.getApprovedAmount().getAmount());
            transaction.setCurrencycode(response.getApprovedAmount().getCurrency());
            transaction.setBalanceAmount((long) response.getBalanceAmount().getAmount());
            //where to take the follwing fields
            response.getCard().getCardExpiration();
            response.getCard().getEovDate();
            response.getConversionRate();
            response.getIncentiveNumber();
            
            LOGGER.info("ReturnDescription : " + String.valueOf(response.getReturnCode().getReturnDescription()));
        } catch (Exception e) {
            LOGGER.error("Exception occured in " + sMethodName + ". Exception  : " + e.getMessage());
            throw new GatewayException("INTERNAL_SERVER_ERROR");
        }
    }
}
