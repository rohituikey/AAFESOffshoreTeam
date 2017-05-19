/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.test;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.svs.SVSGateway;
import com.aafes.stargate.gateway.svs.SVSGatewayProcessor;
import com.aafes.stargate.util.ResponseType;
import javax.xml.datatype.DatatypeConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;


/**
 *
 * @author singha
 */
public class SVSIssueCardTest {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TestPreAuthorization.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = SVSIssueCardTest.this.getClass().getSimpleName();
    public Transaction svsIssueinit() throws DatatypeConfigurationException {
        Transaction transaction = new Transaction();
        transaction.setAmount((long) 0.00);
        transaction.setOrderNumber("00009999");
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

    @Test
    public void issueCardNullTransactionId() throws DatatypeConfigurationException {
        sMethodName="issueCardNullTransactionId";
        SVSGateway sGateway = new SVSGateway();
        SVSGatewayProcessor gatewayProcessor = new SVSGatewayProcessor();
        sGateway.setSvsgp(gatewayProcessor);
        Transaction tranReq = svsIssueinit();
        tranReq.setTransactionId(null);
        Transaction t = sGateway.processMessage(tranReq);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        LOGGER.info("-------------------------------------------------------------------------");
        Assert.assertEquals(t.getResponseType(), ResponseType.DECLINED);
    }


}

    
