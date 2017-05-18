/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.svs;

import com.aafes.stargate.authorizer.entity.GiftCard;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.dao.SVSDAO;
import com.aafes.stargate.util.ResponseType;
import com.aafes.stargate.util.StarGateConstants;
import com.svs.svsxml.beans.Amount;
import com.svs.svsxml.beans.IssueVirtualGiftCardRequest;
import com.svs.svsxml.beans.IssueVirtualGiftCardResponse;
import com.svs.svsxml.beans.Merchant;
import com.svs.svsxml.service.SVSXMLWay;
import com.svs.svsxml.service.SVSXMLWayService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.ejb.EJB;
import javax.xml.ws.BindingProvider;
import static org.apache.log4j.LogSF.log;
import org.slf4j.LoggerFactory;

/**
 *
 * @author singha
 */
public class SVSIssue {
     private static final org.slf4j.Logger log
            = LoggerFactory.getLogger(SVSGatewayProcessor.class.getSimpleName());
    
     public void issueGiftCard(Transaction t) {
        log.info("issueGiftCard.......");
        SVSXMLWayService sVSXMLWayService = new SVSXMLWayService();
        SVSXMLWay sVSXMLWay = sVSXMLWayService.getSVSXMLWay();
        Map<String, Object> requestContext
                = ((BindingProvider) sVSXMLWay).getRequestContext();
        requestContext.put(BindingProvider.USERNAME_PROPERTY, "extspeedfcuat");
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, "Rc464Fc14");
        IssueVirtualGiftCardRequest request = new IssueVirtualGiftCardRequest();
        request.setDate(t.getLocalDateTime());//("2017-05-14T00:09:04");
        request.setInvoiceNumber(t.getOrderNumber());//("9999");
        Merchant merchant = new Merchant();
        merchant.setDivision(StarGateConstants.MERCHANT_DIVISION_NUMBER);//("99999");
        merchant.setMerchantName(StarGateConstants.MERCHANT_NAME);//("IT-D VP OFFICE");
        merchant.setMerchantNumber(StarGateConstants.MERCHANT_NUMBER);//("061571");// transaction.merchantNumber is not available
        merchant.setStoreNumber(StarGateConstants.STORE_NUMBER);//("3858190100");//transaction.storenumber is not available
        request.setMerchant(merchant);
        request.setRoutingID(StarGateConstants.ROUTING_ID);//("6006491571000000000");  transaction.routingId is not available
        request.setStan(t.getSTAN());
        request.setTransactionID(t.getTransactionId());
        
        //   request.setTransactionID(t.getTransactionId());
        // how to check with duplicate value
        request.setCheckForDuplicate(StarGateConstants.CHECK_FOR_DUPLICATE);

        Amount amount = new Amount();
        amount.setAmount(t.getAmount());
        amount.setCurrency(t.getCurrencycode());
        request.setIssueAmount(amount);
        try {
            IssueVirtualGiftCardResponse response = sVSXMLWay.issueVirtualGiftCard(request);

            t.setReasonCode(response.getReturnCode().getReturnCode());
            t.setDescriptionField(response.getReturnCode().getReturnDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
