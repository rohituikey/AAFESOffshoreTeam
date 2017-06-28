/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.validatetoken;

import com.aafes.stargate.validatetoken.TokenGeneratorService;
import java.net.MalformedURLException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author burangir
 */
public class TokenGeneratorServiceTest {
    
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TokenGeneratorServiceTest.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = TokenGeneratorServiceTest.this.getClass().getSimpleName();
    TokenGeneratorService tokenGeneratorService = null;
    String requestXMLSuccess = "", requestXMLInvalidCredentials = "";
    String tokenId = "";
    
    @Before
    public void populateRequestData(){
        // BELOW REQUEST IS USED TO TEST SUCCESSFUL SCENARIO
        requestXMLSuccess = "<ns1:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:ns1='http://www.aafes.com/tokenvalidator' "
        + "xsi:schemaLocation='http://www.aafes.com/tokenvalidator file:D:/Users/burangir/Documents/git/New/StarGate_13June/"
        + "StarGate/StarGate/src/main/resources/jaxb/tokenvalidator/TokenValidator.xsd' MajorVersion=\"3\" MinorVersion=\"1\" "
        + "FixVersion=\"0\"><ns1:Header><ns1:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</ns1:IdentityUUID>"
        + "<ns1:UerId>tmpUserName</ns1:UerId><ns1:Password>65656565</ns1:Password></ns1:Header><ns1:Request>"
        + "<ns1:RequestType>Token</ns1:RequestType> </ns1:Request></ns1:Message>";
        
        // BELOW REQUEST IS USED TO TEST INCORRECT CREDENTIALS - PASSWORD IS INCORRECT
        requestXMLInvalidCredentials = "<ns1:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:ns1='http://www.aafes.com/tokenvalidator' "
        + "xsi:schemaLocation='http://www.aafes.com/tokenvalidator file:D:/Users/burangir/Documents/git/New/StarGate_13June/"
        + "StarGate/StarGate/src/main/resources/jaxb/tokenvalidator/TokenValidator.xsd' MajorVersion=\"3\" MinorVersion=\"1\" "
        + "FixVersion=\"0\"><ns1:Header><ns1:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</ns1:IdentityUUID>"
        + "<ns1:UerId>tmpUserName</ns1:UerId><ns1:Password>65656</ns1:Password></ns1:Header><ns1:Request>"
        + "<ns1:RequestType>Token</ns1:RequestType> </ns1:Request></ns1:Message>";
    }
    
    @Test
    public void testTokenGenrtationSuccess() throws MalformedURLException {
        sMethodName = "testTokenGenrtationSuccess";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        tokenGeneratorService = new TokenGeneratorService();
        tokenId = tokenGeneratorService.postXml(requestXMLSuccess);
        
        LOGGER.info("Token generated : " + tokenId);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
    }
    
     @Test
    public void testTokenAuthenticationFailure() throws MalformedURLException {
        sMethodName = "testTokenAuthenticationFailure";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        tokenGeneratorService = new TokenGeneratorService();
        tokenId = tokenGeneratorService.postXml(requestXMLInvalidCredentials);
        
        LOGGER.info("Message : " + tokenId);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
    }
}