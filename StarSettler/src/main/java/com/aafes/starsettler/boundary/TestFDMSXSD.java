/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.boundary;

import com.aafes.starsettler.control.Settler;
import com.firstdata.cmpwsapi.schemas.cmpapi.BatchTransRequest;
import com.firstdata.cmpwsapi.schemas.cmpmsg.BatchRecord;
import com.firstdata.cmpwsapi.schemas.cmpmsg.BatchTotals;
import com.firstdata.cmpwsapi.schemas.cmpmsg.BillToAddress;
import com.firstdata.cmpwsapi.schemas.cmpmsg.ContactAddressWOTel;
import com.firstdata.cmpwsapi.schemas.cmpmsg.FormattedAddressRecords;
import com.firstdata.cmpwsapi.schemas.cmpmsg.IOI;
import com.firstdata.cmpwsapi.schemas.cmpmsg.ISS001;
import com.firstdata.cmpwsapi.schemas.cmpmsg.InformationRecords;
import com.firstdata.cmpwsapi.schemas.cmpmsg.S;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXB;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ganjis
 */
@Path("/testfdmsxsd")
public class TestFDMSXSD {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(SettleMessageResource.class.
                    getSimpleName());

    @EJB
    private Settler settler;

    //TODO add schema path here
    private String SCHEMA_PATH = "src/main/resources/jaxb/fdms/cmpapi/cmpapi.xsd";

    /**
     * Process a new settle request.
     *
     * @param requestXML
     * @return the request XML document with added response data
     */
    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public String postXml(String requestXML) {

        String responseXML = "";

        try {
            LOG.info("From Client: " + requestXML);
            Long saleAmount = 0l;
            Long refundAmount = 0l;

            BatchTransRequest request = new BatchTransRequest();
            
            request.setPID("123");
            request.setSID("321");
            request.setBatchId("batch1");

            ISS001 issoRecord = null;
            BatchRecord batchRecord = null;
            S sRecord = null;
            IOI ioiRecord = null;
            ContactAddressWOTel addressRecords = null;
            InformationRecords iRecord = null;
            BillToAddress billToAddress = null;
            FormattedAddressRecords faRecord = null;

            batchRecord = new BatchRecord();
            sRecord = new S();
            iRecord = new InformationRecords();
            ioiRecord = new IOI();
            addressRecords = new ContactAddressWOTel();
            billToAddress = new BillToAddress();
            faRecord = new FormattedAddressRecords();

            sRecord.setMop("VI");
            sRecord.setAccountNumber("1234567890");

            String expDate = "2003";
            int mid = expDate.length() / 2; //get the middle of the String
            String[] parts = {expDate.substring(0, mid), expDate.substring(mid)};
            String newDate = parts[1] + parts[0];
            sRecord.setExpirationDate(newDate);

            sRecord.setDivisionNumber("Div1234");
            sRecord.setAmount("50.12");
            sRecord.setCurrencyCode("840");
            sRecord.setTransactionType("7");
            sRecord.setActionCode("RF");
            sRecord.setResponseReasonCode("   ");
            String orderNumber = "";
            orderNumber = "order1234";
            orderNumber = String.format("%-22s", orderNumber);
            sRecord.setOrderNumber(orderNumber);
            sRecord.setResponseDate("2017-06-30");
            sRecord.setAuthorizationCode("Auth1234");
            sRecord.setAVSAAVResponseCode("I8");

            ioiRecord.setTrackingNumber("Batch001Seq001");
            iRecord.setIOI(ioiRecord);

            addressRecords.setNameText("Ganji*Shailendra");

            String add1 = "6445 Love Dr";
            add1 = String.format("%-28s", add1);
            addressRecords.setAddress1(add1.toUpperCase());

            String add2 = "Royal Ln";
            if (add2.length() > 28) {
                add2 = add2.substring(0, 28);
            }
            if (add2 != null
                    && !(add2.equalsIgnoreCase(""))) {
                add2 = String.format("%-28s", add2);
                addressRecords.setAddress2(add2.toUpperCase());
            }

            String countryValue = null;
            countryValue = "US";

            addressRecords.setCountryCode(countryValue);

            String city = "Irving";
            city = String.format("%-20s", city);
            addressRecords.setCity(city.toUpperCase());

            String state = "TX";
            state = String.format("%-2s", state);
            addressRecords.setState(state.toUpperCase());

            addressRecords.setPostalCode("75039");

            billToAddress.setLA(addressRecords);
            faRecord.setBillToAddress(billToAddress);

            batchRecord.setSRecord(sRecord);

            issoRecord = new ISS001();
            String unitValue = String.format("%-2d", Integer.parseInt("2"));
            issoRecord.setShipmentNumber(unitValue);

            String unitTotalValue = String.format("%-2d", Integer.parseInt("4"));
            issoRecord.setTotalNoOfShipments(unitTotalValue);

            iRecord.setISS001(issoRecord);

            batchRecord.setIRecord(iRecord);
            batchRecord.setFARecord(faRecord);
            request.getBatchRecord().add(batchRecord);
            BatchTotals bt = new BatchTotals();

            Long amountTotals = 0l;
            refundAmount = Long.parseLong(refundAmount.toString().replace("-", ""));

            bt.setAmountSales("20.43");
            bt.setAmountRefunds("30.12");

            amountTotals = saleAmount + refundAmount;
            bt.setAmountTotals("43.12");

            request.setBatchTotals(bt);
            responseXML = marshal(request);
            LOG.info("To Client: " + responseXML);
        } catch (Exception ex) {
            Logger.getLogger(SettleMessageResource.class.getName()).log(Level.SEVERE, null, ex);
        }

        return responseXML;

    }

    private String marshal(BatchTransRequest request) {
        StringWriter sw = new StringWriter();
        
        JAXB.marshal(request, sw);
        String xmlString = sw.toString();
        return xmlString;
    
    }
}
