/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.test;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.svs.SVSGateway;
import com.aafes.stargate.gateway.svs.SVSGatewayProcessor;
import com.aafes.stargate.util.InputType;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
import com.aafes.stargate.util.StarGateConstants;
import com.aafes.stargate.util.SvsUtil;
import java.net.MalformedURLException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author burangir
 */
public class TestPreAuthorization {

    Transaction transaction = new Transaction();
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TestPreAuthorization.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = TestPreAuthorization.this.getClass().getSimpleName();
     
    @Before
    public void getKeyedMILSTARSaleRequestatPOS() throws DatatypeConfigurationException {
        transaction.setMedia(MediaType.GIFT_CARD);
        transaction.setRequestType(RequestType.PREAUTH);
        transaction.setInputType(InputType.KEYED);
        transaction.setPan("12121221121212");
        transaction.setAccount("6006491572010002439");
        transaction.setGcpin("7020");
        transaction.setExpiration("2113");
        transaction.setTrack1("");
        transaction.setTrack2("");

        transaction.setAmount((long) 50.00);
        transaction.setCurrencycode(StarGateConstants.CURRENCY);
        transaction.setLocalDateTime(SvsUtil.formatLocalDateTime());
        transaction.setOrderNumber("55548741536");
        transaction.setSTAN(SvsUtil.generateStan());
        transaction.setTransactionId("326598985232");
        transaction.setCurrencycode(StarGateConstants.CURRENCY);
        transaction.setDivisionnumber(StarGateConstants.MERCHANT_DIVISION_NUMBER);
        transaction.setMerchantOrg(StarGateConstants.MERCHANT_NAME);
        // GET routingID IN PROCESSOR CLASS
        // GET checkForDuplicate IN PROCESSOR CLASS
    }
    
    //@Ignore
    @Test
    public void testPreAuthorizationSuccess() throws MalformedURLException {
        sMethodName = "testPreAuthorizationSuccess";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        SVSGateway gateway = new SVSGateway();
        SVSGatewayProcessor svsgp = new SVSGatewayProcessor();
        gateway.setSvsgp(svsgp);
        Transaction t = gateway.processMessage(transaction);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        LOGGER.info("-------------------------------------------------------------------------");
        Assert.assertEquals(t.getReasonCode(), "01");
    }
    
    //@Ignore
    @Test
    public void testPreAuthorizationFailInvalidPIN() throws MalformedURLException {
        sMethodName = "testPreAuthorizationFailInvalidPIN";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        SVSGateway gateway = new SVSGateway();
        SVSGatewayProcessor svsgp = new SVSGatewayProcessor();
        gateway.setSvsgp(svsgp);
        transaction.setGcpin("3130");
        Transaction t = gateway.processMessage(transaction);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        LOGGER.info("-------------------------------------------------------------------------");
        Assert.assertEquals(t.getReasonCode(), "20");
    }
    
    //@Ignore
    @Test
    public void testPreAuthorizationFailPINLocked() throws MalformedURLException {
        sMethodName = "testPreAuthorizationFailPINLocked";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        SVSGateway gateway = new SVSGateway();
        SVSGatewayProcessor svsgp = new SVSGatewayProcessor();
        gateway.setSvsgp(svsgp);
        //transaction.setGcpin("313");
        Transaction t = gateway.processMessage(transaction);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        LOGGER.info("-------------------------------------------------------------------------");
        Assert.assertEquals(t.getReasonCode(), "29");
    }

   // @Ignore
    @Test
    public void testNullAccount() throws MalformedURLException {
        sMethodName = "testNullAccount";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        SVSGateway gateway = new SVSGateway();
        SVSGatewayProcessor svsgp = new SVSGatewayProcessor();
        gateway.setSvsgp(svsgp);
        transaction.setAccount(null);
        Transaction t = gateway.processMessage(transaction);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        LOGGER.info("-------------------------------------------------------------------------");
        Assert.assertEquals(t.getResponseType(), ResponseType.DECLINED);
    }
    
   // @Ignore
    @Test
    public void testNullGCPin() throws MalformedURLException {
        sMethodName = "testNullGCPin";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        SVSGateway gateway = new SVSGateway();
        SVSGatewayProcessor svsgp = new SVSGatewayProcessor();
        gateway.setSvsgp(svsgp);
        transaction.setGcpin(null);
        Transaction t = gateway.processMessage(transaction);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        LOGGER.info("-------------------------------------------------------------------------");
        Assert.assertEquals(t.getResponseType(), ResponseType.DECLINED);
    }
    
    //@Ignore
    @Test
    public void testNullTransactionId() throws MalformedURLException {
        sMethodName = "testNullTransactionId";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        SVSGateway gateway = new SVSGateway();
        SVSGatewayProcessor svsgp = new SVSGatewayProcessor();
        gateway.setSvsgp(svsgp);
        transaction.setTransactionId(null);
        Transaction t = gateway.processMessage(transaction);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        LOGGER.info("-------------------------------------------------------------------------");
        Assert.assertEquals(t.getResponseType(), ResponseType.DECLINED);
    }
}