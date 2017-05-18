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
import com.aafes.stargate.util.ResponseType;
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
    private double approvedAmount;
    private PreAuthResponse preAuthResponseObj = null;
    private PreAuthRequest preAuthRequest = null;
    private SVSXMLWay sVSXMLWay = null;

    public void preAuth(Transaction t) {
        sMethodName = "preAuth";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        try {
            sVSXMLWay = SvsUtil.setUserNamePassword();
            preAuthRequest = new PreAuthRequest();

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
            // utilized when checkForDuplicate is FALSE. checkForDuplicate defauly is TRUE (SVSXMLSpecReviewV1.2.docx)
            //preAuthRequest.setStan(t.getSTAN()); 
            preAuthRequest.setTransactionID(t.getTransactionId());
            preAuthRequest.setCheckForDuplicate(StarGateConstants.TRUE);

            processPreAuthRequest(t);
            
        } catch (Exception e) {
            LOGGER.error("Exception occured in " + sMethodName + ". Exception  : " + e.getMessage());
            t.setReasonCode("");
            t.setResponseType(ResponseType.DECLINED);
            t.setDescriptionField("INTERNAL SYSTEM ERROR");
        }
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
    }
    
    private void processPreAuthRequest(Transaction t){
        sMethodName = "processPreAuthRequest";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        try{
            preAuthResponseObj = sVSXMLWay.preAuth(preAuthRequest);
            if(preAuthResponseObj != null){
                if(preAuthResponseObj.getReturnCode() != null){
                    LOGGER.info("ReturnCode : " + String.valueOf(preAuthResponseObj.getReturnCode().getReturnCode()));
                    t.setReasonCode(preAuthResponseObj.getReturnCode().getReturnCode());
               
                    LOGGER.info("ReturnDescription : " + String.valueOf(preAuthResponseObj.getReturnCode().getReturnDescription()));
                    t.setDescriptionField(preAuthResponseObj.getReturnCode().getReturnDescription());
                }
                
                if(preAuthResponseObj.getApprovedAmount() != null){
                    approvedAmount = preAuthResponseObj.getApprovedAmount().getAmount();
                    //LOGGER.info("ApprovedAmount : " + String.valueOf(approvedAmount));
                    //LOGGER.info("Currency : " + preAuthResponseObj.getApprovedAmount().getCurrency());
                    t.setAmount((long) approvedAmount);
                    t.setCurrencycode(preAuthResponseObj.getApprovedAmount().getCurrency());
                    t.setAmtPreAuthorized((long) approvedAmount);
                }
                
                if(preAuthResponseObj.getBalanceAmount() != null){
                    //LOGGER.info("BalanceAmount : " + String.valueOf(preAuthResponseObj.getBalanceAmount().getAmount()));
                    t.setBalanceAmount((long) preAuthResponseObj.getBalanceAmount().getAmount());
                    //LOGGER.info("Currency : " + preAuthResponseObj.getBalanceAmount().getCurrency());
                }
                
                LOGGER.info("AuthorizationCode : " + preAuthResponseObj.getAuthorizationCode());
                if(preAuthResponseObj.getReturnCode() != null) LOGGER.info("ReturnCode : " + preAuthResponseObj.getReturnCode().getReturnCode());

                if(preAuthResponseObj.getCard() != null){
                    //LOGGER.info("CardNumber : " + preAuthResponseObj.getCard().getCardNumber());
                    t.setCardSequenceNumber(preAuthResponseObj.getCard().getCardNumber());
                    //LOGGER.info("CardHolderName : " + preAuthResponseObj.getCard().getCardNumber());
                    //LOGGER.info("CardCurrency : " + preAuthResponseObj.getCard().getCardCurrency());
                    //LOGGER.info("CardExpiration : " + preAuthResponseObj.getCard().getCardExpiration());
                    //LOGGER.info("CardTrackOne : " + preAuthResponseObj.getCard().getCardTrackOne());
                    //LOGGER.info("CardTrackTwo : " + preAuthResponseObj.getCard().getCardTrackTwo());
                    //LOGGER.info("EovDate : " + preAuthResponseObj.getCard().getEovDate());
                    //LOGGER.info("PinNumber : " + preAuthResponseObj.getCard().getPinNumber());
                } 
                //LOGGER.info("ConversionRate : " + preAuthResponseObj.getConversionRate());
                //LOGGER.info("Stan : " + preAuthResponseObj.getStan());
                //LOGGER.info("TransactionID : " +  preAuthResponseObj.getTransactionID());
                //LOGGER.info("Sku : " + preAuthResponseObj.getSku());
                
                t.setAuthoriztionCode(preAuthResponseObj.getAuthorizationCode());
                t.setSTAN(preAuthResponseObj.getStan());
            }else{
                LOGGER.error("Response Object is NULL " + sMethodName + " " + CLASS_NAME);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occured in " + sMethodName + ". Exception  : " + e.getMessage());
            t.setReasonCode("");
            t.setResponseType(ResponseType.DECLINED);
            t.setDescriptionField("INTERNAL SYSTEM ERROR");
        }
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
    }
}
