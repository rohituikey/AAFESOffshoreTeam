/* To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * REQUEST SHOULD HAVE FOLLOWING FIELDS AS PER DOCUMENT SVSXMLSpecReviewV1.2.docx SHARED ON 17-MAY-2017
    <?xml version="1.0" encoding="UTF-8"?>
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns1="http://service.svsxml.svs.com" 
    xmlns:ns2="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
   <SOAP-ENV:Header>
      <ns2:Security SOAP-ENV:mustUnderstand="1">
         <ns2:UsernameToken>
            <ns2:Username>*** Username***</ns2:Username>
            <ns2:Password>***Password***</ns2:Password>
         </ns2:UsernameToken>
      </ns2:Security>
   </SOAP-ENV:Header>
   <SOAP-ENV:Body>
      <ns1:balanceInquiry>
         <request>
            <date>2017-01-13T15:37:01</date>
            <invoiceNumber>9999</invoiceNumber>
            <merchant>
               <merchantName>IT-D VP OFFICE</merchantName>
               <merchantNumber>061571</merchantNumber>
               <storeNumber>F00500</storeNumber>
               <division />
            </merchant>
            <routingID>6006491571000000000</routingID>
            <stan />
            <transactionID>7115060003730886</transactionID>
            <checkForDuplicate>true</checkForDuplicate>
            <card>
               <cardCurrency>USD</cardCurrency>
               <cardNumber>6006496628299904508</cardNumber>
               <pinNumber>2496</pinNumber>
               <cardExpiration />
            </card>
            <amount>
               <amount>0.00</amount>
               <currency>USD</currency>
            </amount>
         </request>
      </ns1:balanceInquiry>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
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
import com.svs.svsxml.beans.PreAuthRequest;
import com.svs.svsxml.beans.PreAuthResponse;
import com.svs.svsxml.service.SVSXMLWay;
import java.math.BigDecimal;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 * @author burangir
 * Used to place a reserve on funds on a card for a fixed period of time or until a Pre-Authorization Completion message is approved.
 * This message type should always be followed by a Pre-Authorization Completion transaction.
 */
@Stateless
public class PreAuthorizationProcessor extends Processor{
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PreAuthorizationProcessor.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = PreAuthorizationProcessor.this.getClass().getSimpleName();


    @Override
    public void processRequest(Transaction t) {
       PreAuthResponse preAuthResponseObj;
        PreAuthRequest preAuthRequest;
        SVSXMLWay sVSXMLWay;
        double approvedAmount;
        
        sMethodName = "preAuth";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        try {
            sVSXMLWay = SvsUtil.setUserNamePassword();
            preAuthRequest = new PreAuthRequest();

            double amt = t.getAmount();
            amt = amt/100;
                    
            Amount amountObj = new Amount();
            amountObj.setAmount(amt);
            amountObj.setCurrency(StarGateConstants.CURRENCY);
            preAuthRequest.setRequestedAmount(amountObj);
            
            Card card = new Card();
            card.setCardCurrency(StarGateConstants.CURRENCY);
            card.setCardNumber(t.getAccount());
            
            //The second subfield is 4 digits to be used identify the PIN.. Each Sub-field should be right justified left zero filled
            if(t.getGcpin() != null && t.getGcpin().trim().length() == 4){
                t.setGcpin("0000" + t.getGcpin());
            }
            
            card.setPinNumber(t.getGcpin());
           // card.setCardTrackOne(t.getTrack1());
           // card.setCardTrackTwo(t.getTrack2());
            preAuthRequest.setCard(card);
            
            preAuthRequest.setDate(SvsUtil.formatLocalDateTime());
            // GET LAST EIGHT DIGIT OF ORDER NUMBER
            if(t.getOrderNumber() != null && t.getOrderNumber().length() >= 8)
                preAuthRequest.setInvoiceNumber(t.getOrderNumber().substring((t.getOrderNumber().length() - 8))); 
            
            Merchant merchant = new Merchant();
            merchant.setMerchantName(StarGateConstants.MERCHANT_NAME);
            merchant.setMerchantNumber(StarGateConstants.MERCHANT_NUMBER);
            merchant.setStoreNumber(StarGateConstants.STORE_NUMBER);
            merchant.setDivision(StarGateConstants.MERCHANT_DIVISION_NUMBER);
            preAuthRequest.setMerchant(merchant);
            
            preAuthRequest.setRoutingID(StarGateConstants.ROUTING_ID);
            // utilized when checkForDuplicate is FALSE. checkForDuplicate defauly is TRUE (SVSXMLSpecReviewV1.2.docx)
            //preAuthRequest.setStan(t.getSTAN()); 
            preAuthRequest.setTransactionID(t.getRrn() + "0000");
            preAuthRequest.setCheckForDuplicate(StarGateConstants.TRUE);

           preAuthResponseObj = sVSXMLWay.preAuth(preAuthRequest);
           if(preAuthResponseObj != null){
                if(preAuthResponseObj.getReturnCode() != null){
                    LOGGER.info("ReturnCode : " + String.valueOf(preAuthResponseObj.getReturnCode().getReturnCode()));
                    LOGGER.info("ReturnDescription : " + String.valueOf(preAuthResponseObj.getReturnCode().getReturnDescription()));
                    
                    t.setReasonCode(preAuthResponseObj.getReturnCode().getReturnCode());
                    t.setDescriptionField(preAuthResponseObj.getReturnCode().getReturnDescription());
                   // t.setResponseType(preAuthResponseObj.getReturnCode().getReturnDescription());
                    if(t.getReasonCode().equalsIgnoreCase("01"))
                    {
                        t.setResponseType(ResponseType.APPROVED);
                    } else {
                        t.setResponseType(ResponseType.DECLINED);
                    }
                }
                
                if(preAuthResponseObj.getApprovedAmount() != null){
                    approvedAmount = preAuthResponseObj.getApprovedAmount().getAmount();
                  //  t.setAmount((long) approvedAmount);
                    t.setCurrencycode(preAuthResponseObj.getApprovedAmount().getCurrency());
                    t.setAmtPreAuthorized((long) (approvedAmount*100));
                }
                
                if(preAuthResponseObj.getBalanceAmount() != null)
                    t.setBalanceAmount((long) (preAuthResponseObj.getBalanceAmount().getAmount()*100));
               
                LOGGER.debug("AuthorizationCode : " + preAuthResponseObj.getAuthorizationCode());

                if(preAuthResponseObj.getCard() != null) t.setCardSequenceNumber(preAuthResponseObj.getCard().getCardNumber());
                t.setAuthNumber(preAuthResponseObj.getAuthorizationCode());
                t.setSTAN(preAuthResponseObj.getStan());
                t.setTransactionId(preAuthResponseObj.getTransactionID());
            }else LOGGER.error("Response Object is NULL " + sMethodName + " " + CLASS_NAME);
            
        } catch (Exception e) {
            LOGGER.error("Exception occured in " + sMethodName + ".responce is null Exception  : " + e.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        }LOGGER.debug("rrn number is--"+t.getRrn());
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
    }
}