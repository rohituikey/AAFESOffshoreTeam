/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.test;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.svs.SVSGateway;
import com.aafes.stargate.gateway.svs.SVSGatewayProcessor;
import javax.xml.datatype.DatatypeConfigurationException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 *
 * @author singha
 */
public class SVSIssueCardTest {

    public Transaction svsIssueinit() throws DatatypeConfigurationException {
        Transaction transaction = new Transaction();
        transaction.setAmount((long) 100.00);
        transaction.setCurrencycode("USD");
        transaction.setDivisionnumber("99999");
        transaction.setOrderNumber("00009999");
        transaction.setLocalDateTime("2017-05-14T00:09:04");
        transaction.setMerchantOrg("IT-D VP OFFICE");
        transaction.setSTAN("112233");
        transaction.setRequestType("Issue");
        transaction.setMedia("GiftCard");
        transaction.setTransactionId("326598985232 ");
        return transaction;
    }

    @Test
    public void issueCard_success() throws DatatypeConfigurationException {
        SVSGateway sGateway = new SVSGateway();
        SVSGatewayProcessor gatewayProcessor = new SVSGatewayProcessor();
        sGateway.setSvsgp(gatewayProcessor);
        Transaction tranReq = svsIssueinit();
        Transaction t = sGateway.processMessage(tranReq);
        System.out.println(t.getReasonCode());
        Assert.assertEquals("01", t.getReasonCode());
    }

    @Ignore
    @Test
    public void issueCard_invalidTransaction() throws DatatypeConfigurationException {
        SVSGateway sGateway = new SVSGateway();
        SVSGatewayProcessor gatewayProcessor = new SVSGatewayProcessor();
        sGateway.setSvsgp(gatewayProcessor);
        Transaction tranReq = svsIssueinit();
        tranReq.setSTAN("wrongStan");
        Transaction t = sGateway.processMessage(tranReq);
        System.out.println(t.getDescriptionField());
        Assert.assertEquals("07", t.getReasonCode());
    }

    @Ignore
    @Test
    public void issueCard_invalid() throws DatatypeConfigurationException {
        SVSGateway sGateway = new SVSGateway();
        SVSGatewayProcessor gatewayProcessor = new SVSGatewayProcessor();
        sGateway.setSvsgp(gatewayProcessor);
        Transaction tranReq = svsIssueinit();
        tranReq.setCurrencycode(null);
        Transaction t = sGateway.processMessage(tranReq);
        System.out.println(t.getDescriptionField());
        Assert.assertEquals("07", t.getReasonCode());
    }

}

    
