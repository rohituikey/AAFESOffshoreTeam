/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.validatetoken;

import com.aafes.stargate.boundary.CreditMessageResource;
import java.net.MalformedURLException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author burangir
 */
@Ignore
public class TokenValidatorServiceTest {
    
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TokenValidatorServiceTest.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = TokenValidatorServiceTest.this.getClass().getSimpleName();
    CreditMessageResource creditMessageResource = null;
    String requestXMLSuccess = "", requestXMLInvalidCredentials = "";
    String tokenId = "", responseMsg = "";
    
     @Before
    public void populateRequestData(){
        // BELOW REQUEST IS USED TO TEST SUCCESSFUL SCENARIO
        requestXMLSuccess = "<?xml version='1.0' encoding='UTF-8'?><cm:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:cm='http://www.aafes.com/credit' xsi:schemaLocation='http://www.aafes.com/credit file:/home/cobbj/eComm/CreditMessage12jc.xsd' TypeCode='Request'  MajorVersion='3' MinorVersion='1' FixVersion='0'>"
                + " <cm:Header>"
                + " <cm:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</cm:IdentityUUID>"
                + " <cm:LocalDateTime>2017-05-04T15:20:01</cm:LocalDateTime>"
                + " <cm:SettleIndicator>true</cm:SettleIndicator>"
                + " <cm:OrderNumber>00009999</cm:OrderNumber>"
                + " <cm:transactionId>8521</cm:transactionId>"
                + " <cm:termId>01</cm:termId>"
                + " <cm:Comment>Approve</cm:Comment>"
                + " </cm:Header>"
                + " <cm:Request RRN='gw9dfwXrI+53'>"
                + " <cm:Media>Milstar</cm:Media>"
                + " <cm:RequestType>Refund</cm:RequestType>"
                + " <cm:InputType>Keyed</cm:InputType>"
                + " <cm:Pan>Pan</cm:Pan>"
                + " <cm:Account>6006496628299904508</cm:Account>"
                + " <cm:Expiration>2103</cm:Expiration>"
                + " <cm:AmountField>25.00</cm:AmountField>"
                + " <cm:GCpin>00002496</cm:GCpin>"
                + " <cm:PlanNumbers><cm:PlanNumber>10001</cm:PlanNumber></cm:PlanNumbers>"
                + " <cm:OriginalOrder>55548741536</cm:OriginalOrder>"
                + " <cm:DescriptionField>REFUND</cm:DescriptionField>"
                + "</cm:Request></cm:Message>";
    }
    
    @Test
    public void testTokenValidationSuccess() throws MalformedURLException {
        sMethodName = "testTokenValidationSuccess";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        creditMessageResource = new CreditMessageResource();
        tokenId = "400421";
        responseMsg = creditMessageResource.postXml(requestXMLSuccess, tokenId);
        
        LOGGER.info("Output : " + tokenId);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
    }
    
    @Test
    public void testTokenValidationNoToken() throws MalformedURLException {
        sMethodName = "testTokenValidationNoToken";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        creditMessageResource = new CreditMessageResource();
        tokenId = "";
        responseMsg = creditMessageResource.postXml(requestXMLSuccess, tokenId);
        
        LOGGER.info("Output : " + tokenId);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
    }
    
    @Test
    public void testTokenValidationExpiredToken() throws MalformedURLException {
        sMethodName = "testTokenValidationExpiredToken";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        creditMessageResource = new CreditMessageResource();
        tokenId = "504421";
        responseMsg = creditMessageResource.postXml(requestXMLSuccess, tokenId);
        
        LOGGER.info("Output : " + tokenId);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
    }
    
}
