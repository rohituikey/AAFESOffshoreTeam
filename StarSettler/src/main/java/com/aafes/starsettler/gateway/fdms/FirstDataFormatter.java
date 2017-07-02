/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.fdms;

import com.aafes.starsettler.entity.SettleEntity;
import com.aafes.starsettler.tokenizer.TokenEndPointService;
import com.aafes.starsettler.util.TransactionType;
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
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXB;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author ganjis
 */
@Stateless
public class FirstDataFormatter {

    private static final Logger logger = LoggerFactory.getLogger(FirstDataFormatter.class.getName());
    @EJB
    private TokenEndPointService tokenEndPointService;
    @Inject
    private String pid;
    @Inject
    private String sid;
    @Inject
    private String divisionNumber;

    public String formatRequestXML(List<SettleEntity> fdmsDataList) throws SAXException, IOException,
            XPathExpressionException, TransformerConfigurationException, TransformerException, ParserConfigurationException {

        if (fdmsDataList != null
                && fdmsDataList.size() > 0) {

            logger.info("formatRequestXML ## fdmsDataList is not empty");

            Long saleAmount = 0l;
            Long refundAmount = 0l;

            BatchTransRequest request = new BatchTransRequest();
            request.setPID(pid);
            request.setSID(sid);
            request.setBatchId(fdmsDataList.get(0).getBatchId());
            
            ISS001 issoRecord = null;
            BatchRecord batchRecord = null;
            S sRecord = null;
            IOI ioiRecord = null;
            ContactAddressWOTel addressRecords = null;
            InformationRecords iRecord = null;
            BillToAddress billToAddress = null;
            FormattedAddressRecords faRecord = null;

            for (SettleEntity settleEntity : fdmsDataList) {

                batchRecord = new BatchRecord();
                sRecord = new S();
                iRecord = new InformationRecords();
                ioiRecord = new IOI();
                addressRecords = new ContactAddressWOTel();
                billToAddress = new BillToAddress();
                faRecord = new FormattedAddressRecords();

                String orderNumber = "";
                orderNumber = settleEntity.getOrderNumber();
                orderNumber = String.format("%-22s", orderNumber);
                sRecord.setOrderNumber(orderNumber);

                sRecord.setMop(mapCardTypeToMop(settleEntity.getCardType()));

                String token = settleEntity.getCardToken();
                String accountNbr = "NoMatchingAccount";
                try {
                    if (tokenEndPointService != null) { // ToDo Remove after junit is done
                        accountNbr = tokenEndPointService.lookupAccount(settleEntity);
                    }
                } catch (Exception e) {
                    logger.info("Error while calling tokenizer for token : " + token);
                    logger.error(e.toString());
                }
                sRecord.setAccountNumber(accountNbr);

                String expDate = settleEntity.getExpirationDate();
                int mid = expDate.length() / 2; //get the middle of the String
                String[] parts = {expDate.substring(0, mid), expDate.substring(mid)};
                String newDate = parts[1] + parts[0];
                sRecord.setExpirationDate(newDate);

                sRecord.setDivisionNumber(divisionNumber);
                sRecord.setAmount(settleEntity.getPaymentAmount().replace("-", ""));
                sRecord.setCurrencyCode("840");
                sRecord.setTransactionType("7");
                sRecord.setActionCode(settleEntity.getTransactionType());

                if (settleEntity.getTransactionType().equalsIgnoreCase(TransactionType.Deposit)) {
                    sRecord.setResponseReasonCode(settleEntity.getResponseReasonCode());
                } else if (settleEntity.getTransactionType().equalsIgnoreCase(TransactionType.Refund)) {
                    sRecord.setResponseReasonCode("   ");
                }

                sRecord.setResponseDate(settleEntity.getResponseDate());
                sRecord.setAuthorizationCode(settleEntity.getAuthoriztionCode());
                sRecord.setAVSAAVResponseCode(settleEntity.getAvsResponseCode());

                ioiRecord.setTrackingNumber(settleEntity.getBatchId() + settleEntity.getSequenceId());
                iRecord.setIOI(ioiRecord);

                String nameValue = null;
                if (settleEntity.getMiddleName() != null && !settleEntity.getMiddleName().trim().isEmpty()) {
                    nameValue = settleEntity.getFirstName().toUpperCase() + " " + settleEntity.getMiddleName().toUpperCase() + "*" + settleEntity.getLastName().toUpperCase();
                } else {
                    nameValue = settleEntity.getFirstName().toUpperCase() + "*" + settleEntity.getLastName().toUpperCase();
                }
                addressRecords.setNameText(nameValue);

                String add1 = "";
                if (settleEntity.getAddressLine1() != null) {
                    add1 = settleEntity.getAddressLine1();
                }
                if (add1.length() > 28) {
                    add1 = add1.substring(0, 28);
                }
                add1 = String.format("%-28s", add1);
                addressRecords.setAddress1(add1.toUpperCase());

                String add2 = "";
                if (settleEntity.getAddressLine2() != null) {
                    add2 = settleEntity.getAddressLine2();
                }
                if (add2.length() > 28) {
                    add2 = add2.substring(0, 28);
                }
                if (add2 != null
                        && !(add2.equalsIgnoreCase(""))) {
                    add2 = String.format("%-28s", add2);
                    addressRecords.setAddress2(add2.toUpperCase());
                }

                String countryValue = null;
                if (settleEntity.getCountryCode().equalsIgnoreCase("US")
                        || settleEntity.getCountryCode().equalsIgnoreCase("UK")
                        || settleEntity.getCountryCode().equalsIgnoreCase("CA")
                        || settleEntity.getCountryCode().equalsIgnoreCase("GB")) {
                    countryValue = settleEntity.getCountryCode();
                } else {
                    countryValue = "";
                }
                addressRecords.setCountryCode(countryValue);

                String city = "";
                city = settleEntity.getCity();
                city = String.format("%-20s", city);
                addressRecords.setCity(city.toUpperCase());

                String state = "";
                state = settleEntity.getProvinceCode();
                state = String.format("%-2s", state);
                addressRecords.setState(state.toUpperCase());

                addressRecords.setPostalCode(settleEntity.getPostalCode());

                billToAddress.setLA(addressRecords);
                faRecord.setBillToAddress(billToAddress);

                batchRecord.setSRecord(sRecord);

                if (settleEntity.getUnitTotal() != null && !settleEntity.getUnitTotal().trim().isEmpty()
                        && Integer.parseInt(settleEntity.getUnitTotal()) > 1) {
                    issoRecord = new ISS001();
                }

                if (settleEntity.getUnitTotal() != null
                        && !settleEntity.getUnitTotal().trim().isEmpty()
                        && Integer.parseInt(settleEntity.getUnitTotal()) > 1) {

                    String unitValue = String.format("%-2d", Integer.parseInt(settleEntity.getUnit()));
                    issoRecord.setShipmentNumber(unitValue);

                    String unitTotalValue = String.format("%-2d", Integer.parseInt(settleEntity.getUnitTotal()));
                    issoRecord.setTotalNoOfShipments(unitTotalValue);

                    iRecord.setISS001(issoRecord);
                }

                batchRecord.setIRecord(iRecord);
                batchRecord.setFARecord(faRecord);

                request.getBatchRecord().add(batchRecord);
                
                if (settleEntity.getTransactionType().equalsIgnoreCase(TransactionType.Deposit)) {
                    saleAmount = saleAmount + Long.parseLong(settleEntity.getPaymentAmount());
                } else if (settleEntity.getTransactionType().equalsIgnoreCase(TransactionType.Refund)) {
                    refundAmount = refundAmount + Long.parseLong(settleEntity.getPaymentAmount());
                }

            }

            BatchTotals bt = new BatchTotals();
            
            
            Long amountTotals = 0l;
            refundAmount = Long.parseLong(refundAmount.toString().replace("-", ""));
           
            bt.setAmountSales(saleAmount.toString());
            bt.setAmountRefunds(refundAmount.toString());

            amountTotals = saleAmount + refundAmount;
            bt.setAmountTotals(amountTotals.toString());

            request.setBatchTotals(bt);
            
            return marshal(request);
        } else {
            logger.info("formatRequestXML ## fdmsDataList is empty");
            return "";
        }

    }

    private String marshal(BatchTransRequest request) {
        StringWriter sw = new StringWriter();
        JAXB.marshal(request, sw);
        String xmlString = sw.toString();
        return xmlString;
    }
    
    private String mapCardTypeToMop(String cardType) {
        switch (cardType) {
            case "Visa":
                return "VI";
            case "Mastercard":
                return "MC";
            case "Amex":
                return "AX";
            case "Discover":
                return "DI";
        }
        return "";
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setDivisionNumber(String divisionNumber) {
        this.divisionNumber = divisionNumber;
    }

}
