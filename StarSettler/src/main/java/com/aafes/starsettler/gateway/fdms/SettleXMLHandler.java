/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.fdms;

import com.aafes.starsettler.entity.SettleEntity;
import com.aafes.starsettler.tokenizer.TokenEndPointService;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import com.aafes.starsettler.util.TransactionType;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 *
 * @author ghadiyamp
 */
@Stateless
public class SettleXMLHandler {

    private static final Logger logger = LoggerFactory.getLogger(SettleXMLHandler.class.getName());
    private final DocumentBuilder documentBuilder;

    @EJB
    private TokenEndPointService tokenEndPointService;

    @Inject
    private String pid;
    @Inject
    private String sid;
    
    @Inject
    private String divisionNumber;

    public SettleXMLHandler() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        documentBuilderFactory.setNamespaceAware(true);
    }

    public String formatRequestXML(List<SettleEntity> fdmsDataList) throws SAXException, IOException,
            XPathExpressionException, TransformerConfigurationException, TransformerException, ParserConfigurationException {
        
        if(fdmsDataList!=null
                && fdmsDataList.size() > 0){
            
            logger.info("formatRequestXML ## fdmsDataList is not empty");
        
            InputStream is = SettleXMLHandler.class.getClassLoader().getResourceAsStream("xml/CreditRequest.xml");
            Document doc = documentBuilder.parse(is);

            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            Object result = null;
            Node node = null;

            Long saleAmount = 0l;
            Long refundAmount = 0l;

            result = xpath.evaluate("//BatchTransRequest//PID", doc, XPathConstants.NODE);
            node = (Node) result;
            node.setTextContent(pid);

            result = xpath.evaluate("//BatchTransRequest//SID", doc, XPathConstants.NODE);
            node = (Node) result;
            node.setTextContent(sid);

            result = xpath.evaluate("//BatchTransRequest//BatchId", doc, XPathConstants.NODE);
            node = (Node) result;
            node.setTextContent(fdmsDataList.get(0).getBatchId());

            result = xpath.evaluate("//BatchTransRequest//BatchTotals", doc, XPathConstants.NODE);
            node = (Node) result;

            Element issoRecord = null;
            Element batchRecord = null;
            Element sRecord = null;
            Element ioiRecord = null;
            Element addressRecord = null;
            Element iRecord = null;
            Element billToAddress = null;
            Element fARecord = null;

            for (SettleEntity settleEntity : fdmsDataList) {

                batchRecord = doc.createElement("cmp:BatchRecord");
                sRecord = doc.createElement("cmpmsg:SRecord");
                iRecord = doc.createElement("cmpmsg:IRecord");
                ioiRecord = doc.createElement("cmpmsg:IOI");
                addressRecord = doc.createElement("cmpmsg:LA");
                billToAddress = doc.createElement("cmpmsg:BillToAddress");
                fARecord = doc.createElement("cmpmsg:FARecord");
                

                String orderNumber = "";
                orderNumber = settleEntity.getOrderNumber();
                orderNumber = String.format("%-22s", orderNumber);

                Element orderNumberTag = doc.createElement("cmpmsg:OrderNumber");
                Text orderValue = doc.createTextNode(orderNumber);
                orderNumberTag.appendChild(orderValue);
                sRecord.appendChild(orderNumberTag);

                Element mopTag = doc.createElement("cmpmsg:Mop");
                Text mopValue = doc.createTextNode(mapCardTypeToMop(settleEntity.getCardType()));
                mopTag.appendChild(mopValue);
                sRecord.appendChild(mopTag);

                Element accTag = doc.createElement("cmpmsg:AccountNumber");
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

                Text accValue = doc.createTextNode(accountNbr);
                accTag.appendChild(accValue);
                sRecord.appendChild(accTag);

                Element expTag = doc.createElement("cmpmsg:ExpirationDate");
                String expDate = settleEntity.getExpirationDate();
                int mid = expDate.length() / 2; //get the middle of the String
                String[] parts = {expDate.substring(0, mid), expDate.substring(mid)};
                String newDate = parts[1] + parts[0];
                Text expValue = doc.createTextNode(newDate);
                expTag.appendChild(expValue);
                sRecord.appendChild(expTag);

                Element divTag = doc.createElement("cmpmsg:DivisionNumber");
                Text divValue = doc.createTextNode(divisionNumber);
                divTag.appendChild(divValue);
                sRecord.appendChild(divTag);

                Element amountTag = doc.createElement("cmpmsg:Amount");
                Text amountValue = doc.createTextNode(settleEntity.getPaymentAmount().replace("-", ""));
                amountTag.appendChild(amountValue);
                sRecord.appendChild(amountTag);

                Element curCodeTag = doc.createElement("cmpmsg:CurrencyCode");
                Text curCodeValue = doc.createTextNode("840");
                curCodeTag.appendChild(curCodeValue);
                sRecord.appendChild(curCodeTag);

                Element tranTypeTag = doc.createElement("cmpmsg:TransactionType");
                Text tranTypeValue = doc.createTextNode("7");
                tranTypeTag.appendChild(tranTypeValue);
                sRecord.appendChild(tranTypeTag);

                Element actionCodeTag = doc.createElement("cmpmsg:ActionCode");
                Text actionCodeValue = doc.createTextNode(settleEntity.getTransactionType());
                actionCodeTag.appendChild(actionCodeValue);
                sRecord.appendChild(actionCodeTag);
                
                Element respReasonCodeTag = doc.createElement("cmpmsg:ResponseReasonCode");
                Text respReasonCodeValue = null;
                if (settleEntity.getTransactionType().equalsIgnoreCase(TransactionType.Deposit)) {
                    respReasonCodeValue = doc.createTextNode(settleEntity.getResponseReasonCode());
                } else if (settleEntity.getTransactionType().equalsIgnoreCase(TransactionType.Refund)) {
                    respReasonCodeValue = doc.createTextNode("   ");
                }
                respReasonCodeTag.appendChild(respReasonCodeValue);
                sRecord.appendChild(respReasonCodeTag);

                Element respDateTag = doc.createElement("cmpmsg:ResponseDate");
                Text respDateValue = doc.createTextNode(settleEntity.getResponseDate());
                respDateTag.appendChild(respDateValue);
                sRecord.appendChild(respDateTag);

                Element authVerifiCodeTag = doc.createElement("cmpmsg:AuthorizationVerificationCode");
                Text authVerifiCodeValue = doc.createTextNode(settleEntity.getAuthoriztionCode());
                authVerifiCodeTag.appendChild(authVerifiCodeValue);
                sRecord.appendChild(authVerifiCodeTag);

                Element avsaavTag = doc.createElement("cmpmsg:AVS-AAVResponseCode");
                Text avsaavValue = doc.createTextNode(settleEntity.getAvsResponseCode());
                avsaavTag.appendChild(avsaavValue);
                sRecord.appendChild(avsaavTag);

                Element tracingNumberTag = doc.createElement("cmpmsg:TrackingNumber");
                Text tracingNumberValue = doc.createTextNode(settleEntity.getBatchId() + settleEntity.getSequenceId());
                tracingNumberTag.appendChild(tracingNumberValue);
                ioiRecord.appendChild(tracingNumberTag);
                iRecord.appendChild(ioiRecord);

                Element nameTag = doc.createElement("cmpmsg:NameText");
                Text nameValue = null;
                if (settleEntity.getMiddleName() != null && !settleEntity.getMiddleName().trim().isEmpty()) {
                    nameValue = doc.createTextNode(settleEntity.getFirstName().toUpperCase() + " " + settleEntity.getMiddleName().toUpperCase() + "*" + settleEntity.getLastName().toUpperCase());
                } else {
                    nameValue = doc.createTextNode(settleEntity.getFirstName().toUpperCase() + "*" + settleEntity.getLastName().toUpperCase());
                }

                nameTag.appendChild(nameValue);
                addressRecord.appendChild(nameTag);

                String add1 = "";
                String add2 = "";
                if (settleEntity.getAddressLine1() != null) {
                    add1 = settleEntity.getAddressLine1();
                }

                if (settleEntity.getAddressLine2() != null) {
                    add2 = settleEntity.getAddressLine2();
                }

                if (add1.length() > 28) {
                    add1 = add1.substring(0, 28);
                }

                if (add2.length() > 28) {
                    add2 = add2.substring(0, 28);
                }

                add1 = String.format("%-28s", add1);
                

                Element address1Tag = doc.createElement("cmpmsg:Address1");
                Text address1Value = doc.createTextNode(add1.toUpperCase());
                address1Tag.appendChild(address1Value);
                addressRecord.appendChild(address1Tag);
                    
                if(add2 != null 
                        && !(add2.equalsIgnoreCase(""))){
                    add2 = String.format("%-28s", add2);
                    Element address2Tag = doc.createElement("cmpmsg:Address2");
                    Text address2Value = doc.createTextNode(add2.toUpperCase());
                    address2Tag.appendChild(address2Value);
                    addressRecord.appendChild(address2Tag);
                }
    //            Element address3Tag = doc.createElement("cmpmsg:Address3");
    //            Text address3Value = doc.createTextNode(settleEntity.getAddressLine3().toUpperCase());
    //            address3Tag.appendChild(address3Value);
    //            addressRecord.appendChild(address3Tag);
    
                Element countryTag = doc.createElement("cmpmsg:CountryCode");
                Text countryValue = null;
                if (settleEntity.getCountryCode().equalsIgnoreCase("US")
                        || settleEntity.getCountryCode().equalsIgnoreCase("UK")
                        || settleEntity.getCountryCode().equalsIgnoreCase("CA")
                        || settleEntity.getCountryCode().equalsIgnoreCase("GB")) {
                    countryValue = doc.createTextNode(settleEntity.getCountryCode());
                } else {
                    countryValue = doc.createTextNode("");
                }
                countryTag.appendChild(countryValue);
                addressRecord.appendChild(countryTag);
                
                String city = "";
                city = settleEntity.getCity();
                city = String.format("%-20s", city);
                Element cityTag = doc.createElement("cmpmsg:City");
                Text cityValue = doc.createTextNode(city.toUpperCase());
                cityTag.appendChild(cityValue);
                addressRecord.appendChild(cityTag);

                String state = "";
                state = settleEntity.getProvinceCode();
                state = String.format("%-2s", state);
                Element stateTag = doc.createElement("cmpmsg:State");
                Text stateValue = doc.createTextNode(state.toUpperCase());
                stateTag.appendChild(stateValue);
                addressRecord.appendChild(stateTag);

                Element postalCodeTag = doc.createElement("cmpmsg:PostalCode");
                Text postalCodeValue = doc.createTextNode(settleEntity.getPostalCode());
                postalCodeTag.appendChild(postalCodeValue);
                addressRecord.appendChild(postalCodeTag);

    //           Element countryTag = doc.createElement("cmpmsg:CountryCode");
    //            Text countryValue = doc.createTextNode(settleEntity.getCountryCode().toUpperCase());
    //            countryTag.appendChild(countryValue);
    //            addressRecord.appendChild(countryTag);
            
                billToAddress.appendChild(addressRecord);
                fARecord.appendChild(billToAddress);

                batchRecord.appendChild(sRecord);

                if (settleEntity.getUnitTotal() != null && !settleEntity.getUnitTotal().trim().isEmpty()
                        && Integer.parseInt(settleEntity.getUnitTotal()) > 1) {
                    issoRecord = doc.createElement("cmpmsg:ISS001");
                }

                if (settleEntity.getUnitTotal() != null && !settleEntity.getUnitTotal().trim().isEmpty()
                        && Integer.parseInt(settleEntity.getUnitTotal()) > 1) {

                    Element shipNbrTag = doc.createElement("cmpmsg:ShipmentNumber");
                    String unitValue = String.format("%-2d", Integer.parseInt(settleEntity.getUnit()));
                    Text shipNbrValue = doc.createTextNode(unitValue);
                    shipNbrTag.appendChild(shipNbrValue);
                    issoRecord.appendChild(shipNbrTag);

                    Element shipTotalTag = doc.createElement("cmpmsg:TotalNoOfShipments");
                    String unitTotalValue = String.format("%-2d", Integer.parseInt(settleEntity.getUnitTotal()));
                    Text shipTotalValue = doc.createTextNode(unitTotalValue);

                    shipTotalTag.appendChild(shipTotalValue);
                    issoRecord.appendChild(shipTotalTag);
                    
                    iRecord.appendChild(issoRecord);
                }

                batchRecord.appendChild(iRecord);
                batchRecord.appendChild(fARecord);

                doc.getFirstChild().insertBefore(batchRecord, node);

                if (settleEntity.getTransactionType().equalsIgnoreCase(TransactionType.Deposit)) {
                    saleAmount = saleAmount + Long.parseLong(settleEntity.getPaymentAmount());
                } else if (settleEntity.getTransactionType().equalsIgnoreCase(TransactionType.Refund)) {
                    refundAmount = refundAmount + Long.parseLong(settleEntity.getPaymentAmount());
                }

            }

            Long amountTotals = 0l;
            refundAmount = Long.parseLong(refundAmount.toString().replace("-", ""));
            result = xpath.evaluate("//BatchTransRequest//BatchTotals//AmountSales", doc, XPathConstants.NODE);
            node = (Node) result;
            node.setTextContent(saleAmount.toString());

            result = xpath.evaluate("//BatchTransRequest//BatchTotals//AmountRefunds", doc, XPathConstants.NODE);
            node = (Node) result;
            node.setTextContent(refundAmount.toString());

            result = xpath.evaluate("//BatchTransRequest//BatchTotals//AmountTotals", doc, XPathConstants.NODE);
            node = (Node) result;
            amountTotals = saleAmount + refundAmount;
            node.setTextContent(amountTotals.toString());

            result = xpath.evaluate("//BatchTransRequest//PID", doc, XPathConstants.NODE);
            node = (Node) result;
            node.setTextContent(pid);

            result = xpath.evaluate("//BatchTransRequest//SID", doc, XPathConstants.NODE);
            node = (Node) result;
            node.setTextContent(sid);

            StringWriter writer = new StringWriter();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");//This pretties it up\
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            doc.setXmlStandalone(true);
            Source src = new DOMSource(doc);
            Result res = new StreamResult(writer);
            transformer.transform(src, res);

            return writer.toString();
        }else{
            logger.info("formatRequestXML ## fdmsDataList is empty");
            return "";
        }
       
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
