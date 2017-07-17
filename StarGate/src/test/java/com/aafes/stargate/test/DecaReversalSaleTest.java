/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.test;

import com.aafes.stargate.boundary.CreditMessageResource;
import java.net.MalformedURLException;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author burangir
 */
public class DecaReversalSaleTest {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(DecaReversalSaleTest.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = DecaReversalSaleTest.this.getClass().getSimpleName();
    CreditMessageResource creditMessageResource = null;

    String responseXML = "";

    String requestXMLSale  = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><cm:Message TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\" xmlns:cm=\"http://www.aafes.com/credit\"><cm:Header><cm:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</cm:IdentityUUID><cm:LocalDateTime>2017-07-14T13:41:00</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>1234567</cm:OrderNumber><cm:transactionId>10000001</cm:transactionId><cm:termId>12</cm:termId><cm:Comment>Approved</cm:Comment></cm:Header><cm:Request RRN=\"200000000001\"><cm:Media>Milstar</cm:Media><cm:RequestType>Sale</cm:RequestType><cm:InputType>Keyed</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue><cm:TrackData1>%B6019450000289697^Milstar RET0001^2009000000000000100000000000000?</cm:TrackData1><cm:AmountField>2500</cm:AmountField><cm:PlanNumbers><cm:PlanNumber>10001</cm:PlanNumber></cm:PlanNumbers><cm:DescriptionField>SALE</cm:DescriptionField><cm:AddressVerificationService><cm:CardHolderName>John Doe</cm:CardHolderName><cm:BillingAddress1>1222</cm:BillingAddress1><cm:BillingCountryCode>US</cm:BillingCountryCode><cm:BillingZipCode>12345</cm:BillingZipCode><cm:Email>johndoe@kk.com</cm:Email><cm:BillingPhone>1122334455</cm:BillingPhone><cm:ShippingPhone>1122334455</cm:ShippingPhone></cm:AddressVerificationService></cm:Request></cm:Message>";
    String requestXMLReversalSale  = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><cm:Message TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\" xmlns:cm=\"http://www.aafes.com/credit\"><cm:Header><cm:IdentityUUID>0ee1c509-2c70-4bcd-b261-f94f1fe6c43b</cm:IdentityUUID><cm:LocalDateTime>2017-07-14T11:08:00</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>1234567</cm:OrderNumber><cm:transactionId>10000001</cm:transactionId><cm:termId>12</cm:termId><cm:Comment>Approved</cm:Comment></cm:Header><cm:Request RRN=\"200000000001\"><cm:Media>Milstar</cm:Media><cm:Reversal>Sale</cm:Reversal><cm:InputType>Keyed</cm:InputType><cm:Pan>Pan</cm:Pan><cm:Account>6006496628299904508</cm:Account><cm:Expiration>2103</cm:Expiration><cm:CardVerificationValue>837</cm:CardVerificationValue><cm:TrackData1>%B6019450000289697^Milstar RET0001^2009000000000000100000000000000?</cm:TrackData1><cm:AmountField>2500</cm:AmountField><cm:PlanNumbers><cm:PlanNumber>10001</cm:PlanNumber></cm:PlanNumbers><cm:DescriptionField>SALE</cm:DescriptionField><cm:AddressVerificationService><cm:CardHolderName>John Doe</cm:CardHolderName><cm:BillingAddress1>1222</cm:BillingAddress1><cm:BillingCountryCode>US</cm:BillingCountryCode><cm:BillingZipCode>12345</cm:BillingZipCode><cm:Email>johndoe@kk.com</cm:Email><cm:BillingPhone>1122334455</cm:BillingPhone><cm:ShippingPhone>1122334455</cm:ShippingPhone></cm:AddressVerificationService></cm:Request></cm:Message>";

    @Test
    public void testSaleRequesGeneration() throws MalformedURLException {
        sMethodName = "testTokenGenrtationSuccess";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        creditMessageResource = new CreditMessageResource();
        responseXML = creditMessageResource.postXml(requestXMLSale, "655937");
        LOGGER.info("Response XML = > " + responseXML);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
    }

    @Test
    public void testReverseSaleRequesGeneration() throws MalformedURLException {
        sMethodName = "testReverseSaleRequesGeneration";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        creditMessageResource = new CreditMessageResource();
        creditMessageResource.postXml(requestXMLReversalSale, "471579");
        LOGGER.info("Response XML = > " + responseXML);
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
    }
}
