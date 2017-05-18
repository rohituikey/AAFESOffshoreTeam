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
import com.svs.svsxml.beans.IssueVirtualGiftCardRequest;
import com.svs.svsxml.beans.IssueVirtualGiftCardResponse;
import com.svs.svsxml.beans.Merchant;
import com.svs.svsxml.service.SVSXMLWay;
import com.svs.svsxml.service.SVSXMLWayService;
import java.util.Map;
import javax.xml.ws.BindingProvider;
import org.slf4j.LoggerFactory;

/**
 *
 * @author singha
 */
public class SVSIssue {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PreAuthorizationProcessor.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = SVSIssue.this.getClass().getSimpleName();

    public void issueGiftCard(Transaction t) {

        sMethodName = "issueGiftCard";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        try {
            SVSXMLWay sVSXMLWay = SvsUtil.setUserNamePassword();

            IssueVirtualGiftCardRequest request = new IssueVirtualGiftCardRequest();
            request.setDate(SvsUtil.formatLocalDateTime());

            request.setInvoiceNumber(t.getOrderNumber().substring(t.getOrderNumber().length() - 8));
            Merchant merchant = new Merchant();
            merchant.setDivision(StarGateConstants.MERCHANT_DIVISION_NUMBER);
            merchant.setMerchantName(StarGateConstants.MERCHANT_NAME);
            merchant.setMerchantNumber(StarGateConstants.MERCHANT_NUMBER);
            merchant.setStoreNumber(StarGateConstants.STORE_NUMBER);
            request.setMerchant(merchant);
            request.setRoutingID(StarGateConstants.ROUTING_ID);
            request.setTransactionID(t.getTransactionId());
            request.setCheckForDuplicate(StarGateConstants.TRUE);

            Amount amount = new Amount();
            amount.setAmount(t.getAmount());
            amount.setCurrency(StarGateConstants.CURRENCY);
            request.setIssueAmount(amount);

            IssueVirtualGiftCardResponse response = sVSXMLWay.issueVirtualGiftCard(request);

            t.setAmtPreAuthorized((long) response.getApprovedAmount().getAmount());
            t.setAuthoriztionCode(response.getAuthorizationCode());
            t.setBalanceAmount((long) response.getBalanceAmount().getAmount());
            t.setReasonCode(response.getReturnCode().getReturnCode());
            t.setCardReferenceID(response.getCard().getCardNumber());
            t.setExpiration(response.getCard().getCardExpiration());

            t.setDescriptionField(response.getReturnCode().getReturnDescription());
            t.setReasonCode(response.getReturnCode().getReturnCode());
            t.setDescriptionField(response.getReturnCode().getReturnDescription());

            if (response.getBalanceAmount() != null) {
                t.setBalanceAmount((long) response.getBalanceAmount().getAmount());
            }
            LOGGER.info("ReturnDescription : " + String.valueOf(response.getReturnCode().getReturnDescription()));

        } catch (Exception e) {
            LOGGER.error("Exception occured in " + sMethodName + ". Exception  : " + e.getMessage());
            throw new GatewayException("INERNAL SERVER ERROR");
        }
    }
     
}
