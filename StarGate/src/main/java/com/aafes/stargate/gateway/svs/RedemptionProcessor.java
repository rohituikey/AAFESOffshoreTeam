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
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alugumetlas
 */
@Stateless
public class RedemptionProcessor extends Processor {

    private static final org.slf4j.Logger log
            = LoggerFactory.getLogger(RedemptionProcessor.class.getSimpleName());

    public void processRequest(Transaction t) {
        log.info("Method...... " + "processRedemptionRequest.......");

        SVSXMLWay sVSXMLWay = SvsUtil.setUserNamePassword();
        
 //       <ns1:RedemptionRequest>
//         <request>
//            <date>2016-10-21T10:10:56</date>
//            <invoiceNumber>9999</invoiceNumber>
//            <merchant>
//               <merchantName>IT-D VP OFFICE</merchantName>
//               <merchantNumber>061571</merchantNumber>
//               <storeNumber>F00500</storeNumber>
//               <division />
//            </merchant>
//            <routingID>6006491571000000000</routingID>
//            <stan>112233</stan>
//            <checkForDuplicate>false</checkForDuplicate>
//            <card>
//               <cardCurrency>USD</cardCurrency>
//               <cardNumber>6006491572010001514</cardNumber>
//               <pinNumber>5196</pinNumber>
//               <cardExpiration />
//            </card>
//            <redemptionAmount>
//               <amount>229.00</amount>
//               <currency>USD</currency


        RedemptionRequest redemptionRequest = new RedemptionRequest();
        Amount amount = new Amount();
        amount.setAmount(t.getAmount());
        amount.setCurrency(StarGateConstants.CURRENCY);
        redemptionRequest.setRedemptionAmount(amount);

        //t.getTermId();
        Card card = new Card();
        card.setCardCurrency(StarGateConstants.CURRENCY);
        card.setCardNumber(t.getAccount());
        card.setPinNumber("0000" + t.getGcpin());
        card.setCardTrackOne(t.getTrack1());
        redemptionRequest.setCard(card);
        
        redemptionRequest.setStan(t.getSTAN());
        redemptionRequest.setCheckForDuplicate(StarGateConstants.TRUE);
        //redemptionRequest.setTransactionID(t.getTransactionId());
        redemptionRequest.setDate(SvsUtil.formatLocalDateTime());
        if (t.getOrderNumber() != null && t.getOrderNumber().length() >= 8) {
            redemptionRequest.setInvoiceNumber(t.getOrderNumber().substring(t.getOrderNumber().length() - 8));
        } else {
            redemptionRequest.setInvoiceNumber(t.getOrderNumber());
        }
        redemptionRequest.setStan(t.getSTAN());
        redemptionRequest.setRoutingID(StarGateConstants.ROUTING_ID);

        Merchant merchant = new Merchant();
        merchant.setMerchantName(StarGateConstants.MERCHANT_NAME);
        merchant.setMerchantNumber(StarGateConstants.MERCHANT_NUMBER);
        merchant.setStoreNumber(StarGateConstants.STORE_NUMBER);
        redemptionRequest.setMerchant(merchant);

        log.info("REQUEST---->AuthorizationCode " + t.getAuthoriztionCode() + "||TransactionId " + t.getTransactionId() + "||Invoice Number " + redemptionRequest.getInvoiceNumber());

        RedemptionResponse redemptionResponse = sVSXMLWay.redemption(redemptionRequest);

        try {
            log.info("RESPONSE---->AuthorizationCode " + redemptionResponse.getAuthorizationCode() + "||AMOUNT " + redemptionResponse.getBalanceAmount().getAmount() + "||RETURN  CODE  " + redemptionResponse.getReturnCode().getReturnCode() + "||RETURN  CODE  DISCRIPTION " + redemptionResponse.getReturnCode().getReturnDescription());
            if (redemptionRequest != null) {
                t.setAmount((long) redemptionResponse.getBalanceAmount().getAmount());
                t.setCurrencycode(StarGateConstants.CURRENCY);

                t.setAuthNumber(redemptionResponse.getAuthorizationCode());
                t.setCardSequenceNumber(redemptionResponse.getCard().getCardNumber());
                t.setExpiration(redemptionResponse.getCard().getCardExpiration());

                t.setReasonCode(redemptionResponse.getReturnCode().getReturnCode());
                t.setDescriptionField(redemptionResponse.getReturnCode().getReturnDescription());
            }
        } catch (GatewayException e) {

        }

    }

}
