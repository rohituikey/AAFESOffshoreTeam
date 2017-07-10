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
import com.svs.svsxml.beans.IssueVirtualGiftCardRequest;
import com.svs.svsxml.beans.IssueVirtualGiftCardResponse;
import com.svs.svsxml.beans.Merchant;
import com.svs.svsxml.service.SVSXMLWay;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author singha
 */
@Stateless
public class SVSIssueProcessor extends Processor {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PreAuthorizationProcessor.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = SVSIssueProcessor.this.getClass().getSimpleName();

    @Override
    public void processRequest(Transaction t) {
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
            request.setTransactionID(t.getRrn() + "0000");
            request.setCheckForDuplicate(StarGateConstants.TRUE);

            double amt = t.getAmount();
            amt = amt / 100;
            Amount amount = new Amount();
            amount.setAmount(amt);
            amount.setCurrency(StarGateConstants.CURRENCY);
            request.setIssueAmount(amount);

            IssueVirtualGiftCardResponse response = sVSXMLWay.issueVirtualGiftCard(request);

            t.setAmtPreAuthorized((long) (response.getApprovedAmount().getAmount() * 100));
            t.setAuthNumber(response.getAuthorizationCode());
            t.setBalanceAmount((long) (response.getBalanceAmount().getAmount() * 100));
            t.setReasonCode(response.getReturnCode().getReturnCode());
            t.setAccount(response.getCard().getCardNumber());
            t.setExpiration(response.getCard().getCardExpiration());
            t.setGcpin(response.getCard().getPinNumber().length() < 4 ? appendZeroForFourDigitPin(response.getCard().getPinNumber()) : response.getCard().getPinNumber());
            t.setDescriptionField(response.getReturnCode().getReturnDescription());
            t.setReasonCode(response.getReturnCode().getReturnCode());
            t.setDescriptionField(response.getReturnCode().getReturnDescription());

            if (t.getReasonCode().equalsIgnoreCase("01")) {
                t.setResponseType(ResponseType.APPROVED);
            } else {
                t.setResponseType(ResponseType.DECLINED);
            }
            LOGGER.debug("ReturnDescription : " + String.valueOf(response.getReturnCode().getReturnDescription()));

        } catch (Exception e) {
            LOGGER.error("Exception occured in " + sMethodName + ".ewsponce is null Exception  : " + e.getMessage());
            throw new GatewayException("INTERNAL_SERVER_ERROR");
        }
        LOGGER.debug("rrn number in  SVSIssueProcessor."+sMethodName+" is : " + t.getRrn());
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
    }

    private String appendZeroForFourDigitPin(String gcPin) {
        int noOfZeroesToBeAppended = 4 - gcPin.length();
        for (int i = 0; i < noOfZeroesToBeAppended; i++) {
            gcPin = "0" + gcPin;
        }
        return gcPin;

    }

}
