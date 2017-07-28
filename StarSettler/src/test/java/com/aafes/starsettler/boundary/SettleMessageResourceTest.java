///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.aafes.starsettler.boundary;
//
//import com.aafes.starsettler.control.Configurator;
//import com.aafes.starsettler.dao.FacilityDAO;
//import com.aafes.starsettler.control.SettleMessageRepository;
//import com.aafes.starsettler.control.Settler;
//import com.aafes.starsettler.entity.AuthorizationCodes;
//import com.aafes.starsettler.entity.Facility;
//import com.aafes.starsettler.entity.SettleEntity;
//import java.util.ArrayList;
//import java.util.List;
//import org.junit.Before;
//import org.junit.Test;
//import static org.junit.Assert.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import org.mockito.Mockito;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
///**
// *
// * @author Ganji
// */
//public class SettleMessageResourceTest {
//
//    SettleMessageResource settleMessageResource;
//    Settler settler;
//
//    @Before
//    public void setUp() {
//        settleMessageResource = new SettleMessageResource();
//        settler = new Settler();
//        Configurator configurator = new Configurator();
//        //configurator.postConstruct();
//        settler.setConfigurator(configurator);
//        settleMessageResource.setSettler(settler);
//
//    }
//
//    @Test
//    public void lineItemTest() {
//        String inputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
//                + "<Settlement>\n"
//                + "  <IdentityUUID>c47e4366-fe72-473b-b114-523e8de5641f</IdentityUUID>\n"
//                + "  <Shipment>\n"
//                + "    <Lines>\n"
//                + "      <Shipped line_id=\"15402344\" client_line_id=\"ci14009000055-1\" ship_id=\"5457732\">\n"
//                + "        <Crc>1360594</Crc>\n"
//                + "        <Quantity>1</Quantity>\n"
//                + "        <UnitCost>140.00</UnitCost>\n"
//                + "        <UnitDiscount>10.00</UnitDiscount>\n"
//                + "        <Unit>1</Unit>\n"
//                + "        <UnitTotal>10</UnitTotal>\n"
//                + "        <CouponCode>123</CouponCode>\n"
//                + "        <Payments>\n"
//                + "          <Payment>\n"
//                + "            <Type>Visa</Type>\n"
//                + "            <Amount>120.00</Amount>\n"
//                + "            <TransactionId>23073526</TransactionId>\n"
//                + "            <OrderNumber>1234</OrderNumber>\n"
//                + "            <OrderDate>20170403</OrderDate>\n"
//                + "            <ShipDate>20170403</ShipDate>\n"
//                + "            <SettleDate>20170403</SettleDate>\n"
//                + "            <CardReference>1765</CardReference>\n"
//                + "            <CardToken>1234-1234-1234-1234</CardToken>\n"
//                + "            <ExpirationDate>2009</ExpirationDate>\n"
//                + "            <AuthNum>070251</AuthNum>\n"
//                + "            <RequestPlan>10001</RequestPlan>\n"
//                + "            <ResponsePlan>10001</ResponsePlan>\n"
//                + "            <QualifiedPlan>30001</QualifiedPlan>\n"
//                + "            <RRN>774fea4dc630</RRN>\n"
//                + "            <Customer>\n"
//                + "              <Contact>\n"
//                + "                <FirstName>Fname</FirstName>\n"
//                + "                <LastName>Lname</LastName>\n"
//                + "                <HomePhone>2143126953</HomePhone>\n"
//                + "                <Email>mtest@aafes.com</Email>\n"
//                + "              </Contact>\n"
//                + "              <Address>\n"
//                + "                <Line sequence=\"1\">3911 S Walton Walker Blvd</Line>\n"
//                + "                <City>Dallas</City>\n"
//                + "                <ProvinceCode>TX</ProvinceCode>\n"
//                + "                <PostalCode>75236</PostalCode>\n"
//                + "                <CountryCode>US</CountryCode>\n"
//                + "              </Address>\n"
//                + "            </Customer>\n"
//                + "          </Payment>\n"
//                + "	 <Payment>\n"
//                + "            <Type>GiftCard</Type>\n"
//                + "            <Amount>10.00</Amount>\n"
//                + "            <TransactionId>23073527</TransactionId>\n"
//                + "            <OrderNumber>str1234</OrderNumber>\n"
//                + "            <OrderDate>20170403</OrderDate>\n"
//                + "            <ShipDate>20170403</ShipDate>\n"
//                + "            <SettleDate>20170403</SettleDate>\n"
//                + "            <CardReference>1765</CardReference>\n"
//                + "            <CardToken>1234-1234-1234-1234</CardToken>\n"
//                + "            <ExpirationDate>2009</ExpirationDate>\n"
//                + "            <AuthNum>070251</AuthNum>\n"
//                + "            <RRN>774fea4dc630</RRN>\n"
//                + "            <Customer>\n"
//                + "              <Contact>\n"
//                + "                <FirstName>Fname</FirstName>\n"
//                + "                <MiddleName>Mname</MiddleName>\n"
//                + "                <LastName>Lname</LastName>\n"
//                + "                <HomePhone>2143126953</HomePhone>\n"
//                + "                <Email>mtest@aafes.com</Email>\n"
//                + "              </Contact>\n"
//                + "              <Address>\n"
//                + "                <Line sequence=\"1\">3911 S Walton Walker Blvd</Line>\n"
//                + "                <City>Dallas</City>\n"
//                + "                <ProvinceCode>TX</ProvinceCode>\n"
//                + "                <PostalCode>75236</PostalCode>\n"
//                + "                <CountryCode>US</CountryCode>\n"
//                + "              </Address>\n"
//                + "            </Customer>\n"
//                + "          </Payment>\n"
//                + "        </Payments>\n"
//                + "      </Shipped>\n"
//                + "    </Lines>\n"
//                + "</Shipment>\n"
//                + "</Settlement>\n";
//
//        String identityUUID = "c47e4366-fe72-473b-b114-523e8de5641f";
//
//        Facility facility = new Facility();
//        facility.setFacility("1234567890");
//        facility.setUuid(identityUUID);
//
//        List<SettleEntity> fdmsData = getVisionEntityElements();
//        SettleMessageRepository repository = Mockito.mock(SettleMessageRepository.class);
//
//        when(repository.getVisionData(any(), any())).thenReturn(fdmsData);
//        when(repository.getFacility(any())).thenReturn(facility);
//        when(repository.findAuthorizationCodes(any())).thenReturn(new AuthorizationCodes());
//        settler.setSettleRepository(repository);
//        settleMessageResource.setSettler(settler);
//
//        String outputXml = settleMessageResource.postXml(inputXml);
//        assertEquals(true, outputXml.contains("SUCCESS"));
//    }
//
//    @Test
//    public void shippingTest() {
//        String inputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
//                + "<Settlement>\n"
//                + "  <IdentityUUID>c47e4366-fe72-473b-b114-523e8de5641f</IdentityUUID>\n"
//                + "  <Shipment>\n"
//                + "    <Shipping amount=\"9.99\" client_line_id=\"ci14009000055-1\" >\n"
//                + "      <Payments>\n"
//                + "          <Payment>\n"
//                + "            <Type>Milstar</Type>\n"
//                + "            <Amount>9.99</Amount>\n"
//                + "            <TransactionId>23073526</TransactionId>\n"
//                + "            <OrderNumber>1234</OrderNumber>\n"
//                + "            <OrderDate>20170403</OrderDate>\n"
//                + "            <ShipDate>20170403</ShipDate>\n"
//                + "            <SettleDate>20170403</SettleDate>\n"
//                + "            <CardReference>1765</CardReference>\n"
//                + "            <CardToken>6019447420006487</CardToken>\n"
//                + "            <ExpirationDate>2009</ExpirationDate>\n"
//                + "            <AuthNum>070251</AuthNum>\n"
//                + "            <RequestPlan>10001</RequestPlan>\n"
//                + "            <ResponsePlan>10001</ResponsePlan>\n"
//                + "            <QualifiedPlan>30001</QualifiedPlan>\n"
//                + "            <RRN>774fea4dc630</RRN>\n"
//                + "            <Customer>\n"
//                + "              <Contact>\n"
//                + "                <FirstName>Fname</FirstName>\n"
//                + "                <LastName>Lname</LastName>\n"
//                + "                <HomePhone>2143126953</HomePhone>\n"
//                + "                <Email>mtest@aafes.com</Email>\n"
//                + "              </Contact>\n"
//                + "              <Address>\n"
//                + "                <Line sequence=\"1\">3911 S Walton Walker Blvd</Line>\n"
//                + "                <City>Dallas</City>\n"
//                + "                <ProvinceCode>TX</ProvinceCode>\n"
//                + "                <PostalCode>75236</PostalCode>\n"
//                + "                <CountryCode>US</CountryCode>\n"
//                + "              </Address>\n"
//                + "            </Customer>\n"
//                + "          </Payment>\n"
//                + "      </Payments>\n"
//                + "    </Shipping>\n"
//                + "</Shipment>\n"
//                + "</Settlement>\n";
//
//        String identityUUID = "c47e4366-fe72-473b-b114-523e8de5641f";
//
//        Facility facility = new Facility();
//        facility.setFacility("1234567890");
//        facility.setUuid(identityUUID);
//
//        List<SettleEntity> fdmsData = getVisionEntityElements();
//        SettleMessageRepository repository = Mockito.mock(SettleMessageRepository.class);
//        when(repository.getVisionData(any(), any())).thenReturn(fdmsData);
//        when(repository.getFacility(any())).thenReturn(facility);
//        when(repository.findAuthorizationCodes(any())).thenReturn(new AuthorizationCodes());
//        settler.setSettleRepository(repository);
//        settleMessageResource.setSettler(settler);
//
//        String outputXml = settleMessageResource.postXml(inputXml);
//        assertEquals(true, outputXml.contains("SUCCESS"));
//    }
//
//    @Test
//    public void appeasementTest() {
//        String inputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
//                + "<Settlement>\n"
//                + "  <IdentityUUID>c47e4366-fe72-473b-b114-523e8de5641f</IdentityUUID>\n"
//                + "  <Appeasements>\n"
//                + "    <Appeasement code=\"C13\" date=\"20170225\" description=\"CR Price Challenge Adjustment\" \n"
//                + "                 reference=\"hello\" client_line_id=\"ci14009000055-1\">\n"
//                + "      <Payments>\n"
//                + "        <Payment>\n"
//                + "            <Type>Milstar</Type>\n"
//                + "            <Amount>100.00</Amount>\n"
//                + "            <TransactionId>23073526</TransactionId>\n"
//                + "            <OrderNumber>1234</OrderNumber>\n"
//                + "            <OrderDate>20170403</OrderDate>\n"
//                + "            <ShipDate>20170403</ShipDate>\n"
//                + "            <SettleDate>20170403</SettleDate>\n"
//                + "            <CardReference>1765</CardReference>\n"
//                + "            <CardToken>1234-1234-1234-1234</CardToken>\n"
//                + "            <ExpirationDate>2009</ExpirationDate>\n"
//                + "            <AuthNum>070251</AuthNum>\n"
//                + "            <RequestPlan>10001</RequestPlan>\n"
//                + "            <ResponsePlan>10001</ResponsePlan>\n"
//                + "            <QualifiedPlan>30001</QualifiedPlan>\n"
//                + "            <RRN>774fea4dc630</RRN>\n"
//                + "            <Customer>\n"
//                + "              <Contact>\n"
//                + "                <FirstName>Fname</FirstName>\n"
//                + "                <LastName>Lname</LastName>\n"
//                + "                <HomePhone>2143126953</HomePhone>\n"
//                + "                <Email>mtest@aafes.com</Email>\n"
//                + "              </Contact>\n"
//                + "              <Address>\n"
//                + "                <Line sequence=\"1\">3911 S Walton Walker Blvd</Line>\n"
//                + "                <City>Dallas</City>\n"
//                + "                <ProvinceCode>TX</ProvinceCode>\n"
//                + "                <PostalCode>75236</PostalCode>\n"
//                + "                <CountryCode>US</CountryCode>\n"
//                + "              </Address>\n"
//                + "            </Customer>\n"
//                + "          </Payment>\n"
//                + "      </Payments>\n"
//                + "    </Appeasement>\n"
//                + "  </Appeasements>\n"
//                + "</Settlement>\n";
//
//        String identityUUID = "c47e4366-fe72-473b-b114-523e8de5641f";
//        Facility facility = new Facility();
//        facility.setFacility("1234567890");
//        facility.setUuid(identityUUID);
//
//        List<SettleEntity> fdmsData = getVisionEntityElements();
//        SettleMessageRepository repository = Mockito.mock(SettleMessageRepository.class);
//        when(repository.getVisionData(any(), any())).thenReturn(fdmsData);
//        when(repository.getFacility(any())).thenReturn(facility);
//        when(repository.findAuthorizationCodes(any())).thenReturn(new AuthorizationCodes());
//        settler.setSettleRepository(repository);
//        settleMessageResource.setSettler(settler);
//
//        String outputXml = settleMessageResource.postXml(inputXml);
//        assertEquals(false, outputXml.contains("SUCCESS"));
//    }
//
//    @Test
//    public void multipleLineItemTest() {
//        String inputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
//                + "<Settlement>\n"
//                + "	<IdentityUUID>c47e4366-fe72-473b-b114-523e8de5641f</IdentityUUID>\n"
//                + "<shipment>\n"
//                + "	\n"
//                + "    <lines>\n"
//                + "        <shipped line_id=\"15402344\" client_line_id=\"ci14009000055-1\" ship_id=\"5457732\">\n"
//                + "            <crc>1360594</crc>\n"
//                + "            <quantity>1</quantity>\n"
//                + "            <unitcost>140.00</unitcost>\n"
//                + "            <unitdiscount>10.00</unitdiscount>\n"
//                + "            <unit>1</unit>\n"
//                + "            <unit_total>10</unit_total>\n"
//                + "            <coupon_code>123</coupon_code>\n"
//                + "            <payments>\n"
//                + "                <payment>\n"
//                + "                    <type>Milstar</type>\n"
//                + "                    <amount>120.00</amount>\n"
//                + "                    <transaction_id>23073526</transaction_id>\n"
//                + "                    <order_number>1234</order_number>\n"
//                + "                   <order_date>20170101</order_date>\n"
//                + "                    <ship_date>20170102</ship_date>\n"
//                + "                    <settle_date>20170102</settle_date>\n"
//                + "                    <card_reference>1765</card_reference>\n"
//                + "                    <card_token>1234-1234-1234-1234</card_token>\n"
//                + "                    <expiration_date>2009</expiration_date>\n"
//                + "                    <auth_num>070251</auth_num>\n"
//                + "                    <RequestPlan>10001</RequestPlan>\n"
//                + "                    <ResponsePlan>10001</ResponsePlan>\n"
//                + "                    <QualifiedPlan>30001</QualifiedPlan>\n"
//                + "                    <RRN>774fea4dc630</RRN>\n"
//                + "                    <customer>\n"
//                + "                        <contact>\n"
//                + "                            <first_name>Fnam1</first_name>\n"
//                + "                            <last_name>Lname2</last_name>\n"
//                + "                            <home_phone>2143126953</home_phone>\n"
//                + "                            <email>mtest@aafes.com</email>\n"
//                + "                        </contact>\n"
//                + "                        <address>\n"
//                + "                            <line sequence=\"1\">3911 S Walton Walker Blvd</line>\n"
//                + "                            <city>Dallas</city>\n"
//                + "                            <province_code>TX</province_code>\n"
//                + "                            <postal_code>75236</postal_code>\n"
//                + "                            <country_code>US</country_code>\n"
//                + "                        </address>\n"
//                + "                    </customer>\n"
//                + "                </payment>\n"
//                + "                <payment>\n"
//                + "                    <type>GiftCard</type>\n"
//                + "                    <amount>10.00</amount>\n"
//                + "                    <transaction_id>23073526</transaction_id>\n"
//                + "                    <order_number>1234</order_number>\n"
//                + "                   <order_date>20170101</order_date>\n"
//                + "                    <ship_date>20170102</ship_date>\n"
//                + "                    <settle_date>20170102</settle_date>\n"
//                + "                    <card_reference>1765</card_reference>\n"
//                + "                    <card_token>1234-1234-1234-1234</card_token>\n"
//                + "                    <expiration_date>2009</expiration_date>\n"
//                + "                    <auth_num>070251</auth_num>\n"
//                + "                    <RequestPlan>10001</RequestPlan>\n"
//                + "                    <ResponsePlan>10001</ResponsePlan>\n"
//                + "                    <QualifiedPlan>30001</QualifiedPlan>\n"
//                + "                    <RRN>774fea4dc630</RRN>\n"
//                + "                    <customer>\n"
//                + "                        <contact>\n"
//                + "                            <first_name>Fnam1</first_name>\n"
//                + "                            <last_name>Lname2</last_name>\n"
//                + "                            <home_phone>2143126953</home_phone>\n"
//                + "                            <email>mtest@aafes.com</email>\n"
//                + "                        </contact>\n"
//                + "                        <address>\n"
//                + "                            <line sequence=\"1\">3911 S Walton Walker Blvd</line>\n"
//                + "                            <line sequence=\"2\">3911 S Walton Walker Blvd</line>\n"
//                + "                            <line sequence=\"3\">3911 S Walton Walker Blvd</line>\n"
//                + "                            <city>Dallas</city>\n"
//                + "                            <province_code>TX</province_code>\n"
//                + "                            <postal_code>75236</postal_code>\n"
//                + "                            <country_code>US</country_code>\n"
//                + "                        </address>\n"
//                + "                    </customer>\n"
//                + "                </payment>\n"
//                + "            </payments>\n"
//                + "        </shipped>\n"
//                + "    </lines>\n"
//                + "   \n"
//                + "    <shipping amount=\"9.99\">\n"
//                + "        <payments>\n"
//                + "            <payment>\n"
//                + "                	<type>Milstar</type>\n"
//                + "                    <amount>9.99</amount>\n"
//                + "                    <transaction_id>23073526</transaction_id>\n"
//                + "                    <order_number>1234</order_number>\n"
//                + "                    <order_date>20170101</order_date>\n"
//                + "                    <ship_date>20170102</ship_date>\n"
//                + "                    <settle_date>20170102</settle_date>\n"
//                + "                    <card_reference>1765</card_reference>\n"
//                + "                    <card_token>1234-1234-1234-1234</card_token>\n"
//                + "                    <expiration_date>2009</expiration_date>\n"
//                + "                    <auth_num>070251</auth_num>\n"
//                + "                    <RequestPlan>10001</RequestPlan>\n"
//                + "                    <ResponsePlan>10001</ResponsePlan>\n"
//                + "                    <QualifiedPlan>30001</QualifiedPlan>\n"
//                + "                    <RRN>774fea4dc630</RRN>\n"
//                + "                    <customer>\n"
//                + "                        <contact>\n"
//                + "                            <first_name>Fnam1</first_name>\n"
//                + "                            <last_name>Lname2</last_name>\n"
//                + "                            <home_phone>2143126953</home_phone>\n"
//                + "                            <email>mtest@aafes.com</email>\n"
//                + "                        </contact>\n"
//                + "                        <address>\n"
//                + "                            <line sequence=\"1\">3911 S Walton Walker Blvd</line>\n"
//                + "                            <line sequence=\"2\">3911 S Walton Walker Blvd</line>\n"
//                + "                            <line sequence=\"3\">3911 S Walton Walker Blvd</line>\n"
//                + "                            <city>Dallas</city>\n"
//                + "                            <province_code>TX</province_code>\n"
//                + "                            <postal_code>75236</postal_code>\n"
//                + "                            <country_code>US</country_code>\n"
//                + "                        </address>\n"
//                + "                    </customer>\n"
//                + "                </payment>\n"
//                + "                \n"
//                + "        </payments>\n"
//                + "    </shipping>\n"
//                + "</shipment>\n"
//                + "</Settlement>\n"
//                + "\n"
//                + "";
//        String outputXml = settleMessageResource.postXml(inputXml);
//        assertEquals(true, outputXml.contains("Invalid XML"));
//    }
//
//    @Test
//    public void errorXmlTest() {
//        String inputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
//                + "<Settlement>\n"
//                + "<appeasements>\n"
//                + "    <appeasement code=\"C13\" date=\"20170225\" description=\"CR Price Challenge Adjustment\" reference=\"hello\">\n"
//                + "        <payments>\n"
//                + "        <payment>\n"
//                + "                    <type>Milstar</type>\n"
//                + "                    <amount>120.00</amount>\n"
//                + "                    <transaction_id>23073526</transaction_id>\n"
//                + "                    <order_number>1234</order_number>\n"
//                + "                    <order_date>20170101</order_date>\n"
//                + "                    <ship_date>20170102</ship_date>\n"
//                + "                    <settle_date>20170102</settle_date>\n"
//                + "                    <card_reference>1765</card_reference>\n"
//                + "                    <card_token>1234-1234-1234-1234</card_token>\n"
//                + "                    <expiration_date>2009</expiration_date>\n"
//                + "                    <auth_num>070251</auth_num>\n"
//                + "                    <RequestPlan>10001</RequestPlan>\n"
//                + "                    <ResponsePlan>10001</ResponsePlan>\n"
//                + "                    <QualifiedPlan>30001</QualifiedPlan>\n"
//                + "                    <RRN>774fea4dc630</RRN>\n"
//                + "                    <customer>\n"
//                + "                        <contact>\n"
//                + "                            <first_name>Fnam1</first_name>\n"
//                + "                            <last_name>Lname2</last_name>\n"
//                + "                            <home_phone>2143126953</home_phone>\n"
//                + "                            <email>mtest@aafes.com</email>\n"
//                + "                        </contact>\n"
//                + "                        <address>\n"
//                + "                            <line sequence=\"1\">3911 S Walton Walker Blvd</line>\n"
//                + "                            <line sequence=\"2\">3911 S Walton Walker Blvd</line>\n"
//                + "                            <line sequence=\"3\">3911 S Walton Walker Blvd</line>\n"
//                + "                            <city>Dallas</city>\n"
//                + "                            <province_code>TX</province_code>\n"
//                + "                            <postal_code>75236</postal_code>\n"
//                + "                            <country_code>US</country_code>\n"
//                + "                        </address>\n"
//                + "                    </customer>\n"
//                + "                </payment>\n"
//                + "    </payments>\n"
//                + "    </appeasement>\n"
//                + "</appeasements>\n"
//                + "</Settlement>\n"
//                + "\n"
//                + "";
//        String outputXml = settleMessageResource.postXml(inputXml);
//        assertEquals(true, outputXml.contains("Invalid XML"));
//    }
//
//    @Test
//    public void invalidCrediCount() {
//        String inputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
//                + "<Settlement>\n"
//                + "  <IdentityUUID>c47e4366-fe72-473b-b114-523e8de5641f</IdentityUUID>\n"
//                + "  <Shipment>\n"
//                + "    <Lines>\n"
//                + "      <Shipped line_id=\"15402344\" client_line_id=\"ci14009000055-1\" ship_id=\"5457732\">\n"
//                + "        <Crc>1360594</Crc>\n"
//                + "        <Quantity>1</Quantity>\n"
//                + "        <UnitCost>140.00</UnitCost>\n"
//                + "        <UnitDiscount>10.00</UnitDiscount>\n"
//                + "        <Unit>1</Unit>\n"
//                + "        <UnitTotal>10</UnitTotal>\n"
//                + "        <CouponCode>123</CouponCode>\n"
//                + "        <Payments>\n"
//                + "          <Payment>\n"
//                + "            <Type>Visa</Type>\n"
//                + "            <Amount>120.00</Amount>\n"
//                + "            <TransactionId>23073526</TransactionId>\n"
//                + "            <OrderNumber>1234</OrderNumber>\n"
//                + "            <OrderDate>20170403</OrderDate>\n"
//                + "            <ShipDate>20170403</ShipDate>\n"
//                + "            <SettleDate>20170403</SettleDate>\n"
//                + "            <CardReference>1765</CardReference>\n"
//                + "            <CardToken>1234-1234-1234-1234</CardToken>\n"
//                + "            <ExpirationDate>2009</ExpirationDate>\n"
//                + "            <AuthNum>070251</AuthNum>\n"
//                + "            <RequestPlan>10001</RequestPlan>\n"
//                + "            <ResponsePlan>10001</ResponsePlan>\n"
//                + "            <QualifiedPlan>30001</QualifiedPlan>\n"
//                + "            <RRN>774fea4dc630</RRN>\n"
//                + "            <Customer>\n"
//                + "              <Contact>\n"
//                + "                <FirstName>Fname</FirstName>\n"
//                + "                <LastName>Lname</LastName>\n"
//                + "                <HomePhone>2143126953</HomePhone>\n"
//                + "                <Email>mtest@aafes.com</Email>\n"
//                + "              </Contact>\n"
//                + "              <Address>\n"
//                + "                <Line sequence=\"1\">3911 S Walton Walker Blvd</Line>\n"
//                + "                <City>Dallas</City>\n"
//                + "                <ProvinceCode>TX</ProvinceCode>\n"
//                + "                <PostalCode>75236</PostalCode>\n"
//                + "                <CountryCode>US</CountryCode>\n"
//                + "              </Address>\n"
//                + "            </Customer>\n"
//                + "          </Payment>\n"
//                + "	 <Payment>\n"
//                + "            <Type>Visa</Type>\n"
//                + "            <Amount>10.00</Amount>\n"
//                + "            <TransactionId>23073527</TransactionId>\n"
//                + "            <OrderNumber>str1234</OrderNumber>\n"
//                + "            <OrderDate>20170403</OrderDate>\n"
//                + "            <ShipDate>20170403</ShipDate>\n"
//                + "            <SettleDate>20170403</SettleDate>\n"
//                + "            <CardReference>1765</CardReference>\n"
//                + "            <CardToken>1234-1234-1234-1234</CardToken>\n"
//                + "            <ExpirationDate>2009</ExpirationDate>\n"
//                + "            <AuthNum>070251</AuthNum>\n"
//                + "            <RRN>774fea4dc630</RRN>\n"
//                + "            <Customer>\n"
//                + "              <Contact>\n"
//                + "                <FirstName>Fname</FirstName>\n"
//                + "                <MiddleName>Mname</MiddleName>\n"
//                + "                <LastName>Lname</LastName>\n"
//                + "                <HomePhone>2143126953</HomePhone>\n"
//                + "                <Email>mtest@aafes.com</Email>\n"
//                + "              </Contact>\n"
//                + "              <Address>\n"
//                + "                <Line sequence=\"1\">3911 S Walton Walker Blvd</Line>\n"
//                + "                <City>Dallas</City>\n"
//                + "                <ProvinceCode>TX</ProvinceCode>\n"
//                + "                <PostalCode>75236</PostalCode>\n"
//                + "                <CountryCode>US</CountryCode>\n"
//                + "              </Address>\n"
//                + "            </Customer>\n"
//                + "          </Payment>\n"
//                + "        </Payments>\n"
//                + "      </Shipped>\n"
//                + "    </Lines>\n"
//                + "</Shipment>\n"
//                + "</Settlement>";
//        String outputXml = settleMessageResource.postXml(inputXml);
//        assertEquals(true, outputXml.contains("MULTIPLE_CREDIT_CARDS"));
//    }
//
//    private List<SettleEntity> getVisionEntityElements() {
//        List<SettleEntity> milstarData = new ArrayList<>();
//
//        SettleEntity settleEntity = new SettleEntity();
//        settleEntity.setOrderDate("2016-12-06T19:01:0");
//        settleEntity.setOrderNumber("3317153619");
//        settleEntity.setPaymentAmount("10");
//        settleEntity.setCardToken("6019440000000320");
//        settleEntity.setRequestPlan("20001");
//        settleEntity.setRrn("gW7BroSRcMT3");
//        settleEntity.setAuthNum("000033");
//        settleEntity.setSettleDate("20170411");
//        settleEntity.setProvinceCode("USA");
//        settleEntity.setIdentityUUID("c47e4366-fe72-473b-b114-523e8de5641f");
//
//        milstarData.add(settleEntity);
//
//        SettleEntity settleEntity1 = new SettleEntity();
//        settleEntity1.setOrderDate("2015-14-08T19:01:0");
//        settleEntity1.setOrderNumber("3317153628");
//        settleEntity1.setPaymentAmount("20");
//        settleEntity1.setCardToken("6019440000000321");
//        settleEntity1.setRequestPlan("10001");
//        settleEntity1.setRrn("gW7BroSRcMT4");
//        settleEntity1.setAuthNum("000034");
//        settleEntity1.setSettleDate("20170511");
//        settleEntity1.setProvinceCode("USA");
//        settleEntity.setIdentityUUID("c47e4366-fe72-473b-b114-523e8de5641f");
//
//        milstarData.add(settleEntity1);
//
//        SettleEntity settleEntity2 = new SettleEntity();
//        settleEntity2.setOrderDate("2015-14-08T19:01:0");
//        settleEntity2.setOrderNumber("3317156628");
//        settleEntity2.setPaymentAmount("30");
//        settleEntity2.setCardToken("6019440000000451");
//        settleEntity2.setRequestPlan("20001");
//        settleEntity2.setRrn("gW7BroSRcMT8");
//        settleEntity2.setAuthNum("000038");
//        settleEntity2.setSettleDate("20170611");
//        settleEntity2.setProvinceCode("USA");
//
//        milstarData.add(settleEntity2);
//
//        SettleEntity settleEntity3 = new SettleEntity();
//        settleEntity3.setOrderDate("2015-14-08T19:01:0");
//        settleEntity3.setOrderNumber("3317156628");
//        settleEntity3.setPaymentAmount("40");
//        settleEntity3.setCardToken("6019440000000451");
//        settleEntity3.setRequestPlan("20001");
//        settleEntity3.setRrn("gW7BroSRcMT9");
//        settleEntity3.setAuthNum("000038");
//        settleEntity3.setSettleDate("20170711");
//        settleEntity3.setProvinceCode("USA");
//
//        milstarData.add(settleEntity3);
//
//        SettleEntity settleEntity4 = new SettleEntity();
//        settleEntity4.setOrderDate("2015-14-08T19:01:0");
//        settleEntity4.setOrderNumber("3317158628");
//        settleEntity4.setPaymentAmount("50");
//        settleEntity4.setCardToken("6019440000000491");
//        settleEntity4.setRequestPlan("20001");
//        settleEntity4.setRrn("gW7BroSRiMT9");
//        settleEntity4.setAuthNum("000048");
//        settleEntity4.setSettleDate("20170811");
//        settleEntity4.setProvinceCode("USA");
//        settleEntity.setIdentityUUID("c47e4366-fe72-473b-b114-523e8de5641f");
//
//        milstarData.add(settleEntity4);
//
//        return milstarData;
//    }
//
//}
