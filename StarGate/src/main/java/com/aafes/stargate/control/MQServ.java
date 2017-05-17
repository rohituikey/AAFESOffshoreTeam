/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.control;

import com.aafes.credit.cs.jaxws.mqserv.ClsMQServ;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.xml.namespace.QName;
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
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author joshid
 */
public class MQServ {

    private static final org.slf4j.Logger logger
            = LoggerFactory.getLogger(Authorizer.class.getSimpleName());
    private DocumentBuilder documentBuilder;
    private final static QName CLSMQSERV_QNAME = new QName("http://mq.aafes.com/wsMQServ", "clsMQServ");

    @Resource(lookup = "java:jboss/param/MQServ/urlWSDL")
    private String mqServWSDL;
    @Resource(lookup = "java:jboss/param/MQServ/connectTimeout")
    private String connectTimeout;
    @Resource(lookup = "java:jboss/param/MQServ/receiveTimeout")
    private String receiveTimeout;

    URL clsMQServURL;

    //MQServ Lookup Request
    private String xPathLookupTranType;
    private String xPathLookupTranCPU;
    private String xPathLookupTransaction;
    private String xPathLookupTranID;
    private String xPathLookupTimeLimit;
    private String xPathLookupUserId;
    private String xPathLookupPassword;
    private String xPathLookupPayload;

    //Lookup values
    private String lookupTranType;
    @Resource(lookup = "java:jboss/param/MQServ/lookupTranCPU")
    private String lookupTranCPU;
    private String lookupTransaction;
    private String lookupTranID;
    @Resource(lookup = "java:jboss/param/MQServ/lookupTimeLimit")
    private String lookupTimeLimit;
    private String lookupUserId;
    private String lookupPassword;

    //MQServ Response
    private String xPathResponseReturnCode;
    private String xPathResponseErrorMessage;
    private String xPathResponsePayload;

    //A instance initializer containing the hardcoded stuff that should be in spring
    {
        setxPathLookupTranType("//InMakeCall/Header/TranType");
        setxPathLookupTranCPU("//InMakeCall/Header/TranCPU");
        setxPathLookupTransaction("//InMakeCall/Header/Transaction");
        setxPathLookupTranID("//InMakeCall/Header/TranID");
        setxPathLookupTimeLimit("//InMakeCall/Header/TimeLimit");
        setxPathLookupUserId("//InMakeCall/Header/UserId");
        setxPathLookupPassword("//InMakeCall/Header/Password");
        setxPathLookupPayload("//InMakeCall/PayLoad");

        setLookupTranType("CICS");
        setLookupTranCPU("T");
        setLookupTransaction("ECOININT");
        setLookupTranID("ICMQ");
//        setLookupTimeLimit("10");
        //private String lookupUserId; NOT Used 
        //private String lookupPassword; Not Used

        //Response 
        setxPathResponseReturnCode("//OutMakeCall/Header/ReturnCode");
        setxPathResponseErrorMessage("//OutMakeCall/Header/ErrorMessage");
        setxPathResponsePayload("//OutMakeCall/PayLoad");

    }

    public MQServ() throws ParserConfigurationException, NamingException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        documentBuilderFactory.setNamespaceAware(true);
        logger.trace("Initializing the parser ");

        InitialContext context = new InitialContext();

//        mqServWSDL = (String) context.lookup("java:jboss/param/MQServ/urlWSDL");
//        connectTimeout = (String) context.lookup("java:jboss/param/MQServ/connectTimeout");
//        receiveTimeout = (String) context.lookup("java:jboss/param/MQServ/receiveTimeout");
//        lookupTranCPU = (String) context.lookup("java:jboss/param/MQServ/TranCPU");
//        lookupTimeLimit = (String) context.lookup("java:jboss/param/MQServ/lookupTimeLimit");
    }

    protected String formatLookup(String ssn, String milstarNumber) {
        //Format is as follows: 
        //RQT-CMD-TYPE = 2          (New request type to validate SSN and card number)
        //RQT-CARD-NBR = 16 digit card number
        //RQT-USER-PIN = last 6 digits of the SSN 

        String lookupPayload = String.format("2%16.16s%6.6s",
                milstarNumber, ssn.substring(3));
        return lookupPayload;
    }

    public boolean callMatch(String ssn, String milstarNumber) {

        boolean responseVal = false;
        try {
            String payload = formatLookup(ssn, milstarNumber);

            String xml = formatLookupXML(payload);
            logger.debug("mqServ Lookup Request: " + xml);
            String xmlResponse = callWebService(xml);
            logger.debug("mqServ Lookup Response: " + xmlResponse);
            String response = unformatResponseXML(xmlResponse, lookupTransaction);
            logger.debug("mqServ Lookup unformatted Response: " + response);
            responseVal = parseValidateResponse(response);

        } catch (SAXException | IOException | XPathExpressionException | TransformerException | NamingException ee) {
            throw new AuthorizerException("Unable to perform the account number validation");
        }
        return responseVal;

    }

    protected String formatLookupXML(String payload) throws SAXException, IOException,
            XPathExpressionException, TransformerConfigurationException, TransformerException {

        InputStream is = MQServ.class.getClassLoader().getResourceAsStream("mqserv-config/mqserv-lookup-request.xml");
        logger.debug("Input stream from the resource " + is);

        logger.debug("beginning parse.");
        Document doc = documentBuilder.parse(is);
        logger.debug("ending parse.");

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        Object result = null;
        Node node = null;

        logger.trace("Getting ready to run the query {}", xPathLookupTranType);
        result = xpath.evaluate(xPathLookupTranType, doc, XPathConstants.NODE);
        node = (Node) result;
        logger.debug("lookup " + node + "lookupTranType " + lookupTranType);
        node.setTextContent(lookupTranType);

        logger.trace("Getting ready to run the query {}", xPathLookupTranCPU);
        result = xpath.evaluate(xPathLookupTranCPU, doc, XPathConstants.NODE);
        node = (Node) result;
        node.setTextContent(lookupTranCPU);

        logger.trace("Getting ready to run the query {}", xPathLookupTransaction);
        result = xpath.evaluate(xPathLookupTransaction, doc, XPathConstants.NODE);
        node = (Node) result;
        node.setTextContent(lookupTransaction);

        logger.trace("Getting ready to run the query {}", xPathLookupTranID);
        result = xpath.evaluate(xPathLookupTranID, doc, XPathConstants.NODE);
        node = (Node) result;
        node.setTextContent(lookupTranID);

        logger.trace("Getting ready to run the query {}", xPathLookupTimeLimit);
        result = xpath.evaluate(xPathLookupTimeLimit, doc, XPathConstants.NODE);
        node = (Node) result;
        node.setTextContent(lookupTimeLimit);

        logger.trace("Getting ready to run the query {}", xPathLookupUserId);
        result = xpath.evaluate(xPathLookupUserId, doc, XPathConstants.NODE);
        node = (Node) result;
        node.setTextContent(lookupUserId);

        logger.trace("Getting ready to run the query {}", xPathLookupPassword);
        result = xpath.evaluate(xPathLookupPassword, doc, XPathConstants.NODE);
        node = (Node) result;
        node.setTextContent(lookupPassword);

        logger.trace("Getting ready to run the query {}", xPathLookupPayload);
        result = xpath.evaluate(xPathLookupPayload, doc, XPathConstants.NODE);
        node = (Node) result;
        node.setTextContent(payload);

        //Format the output either way
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
    }

    protected String callWebService(String requestXML) throws MalformedURLException, NamingException {
        String responseVal = "";
        logger.debug("Initializing the MQServ service");

//        clsMQServURL = new URL(mqServWSDL);
//        ClsMQServ clsMQServ = new ClsMQServ(clsMQServURL, CLSMQSERV_QNAME);
        ClsMQServ clsMQServ = new ClsMQServ();
//        ClsMQServSoap cls = clsMQServ.getClsMQServSoap();
//        cls
//        BindingProvider bindingProvider = (BindingProvider) clsMQServ.getPort(ClsMQServSoap.class);
//        logger.debug("Setting timeouts connectTimeout {}, receiveTimeout {} ", connectTimeout, receiveTimeout);
//        //bindingProvider.getRequestContext().put("javax.xml.ws.client.connectionTimeout", "1000");
//        bindingProvider.getRequestContext().put("javax.xml.ws.client.connectionTimeout", connectTimeout);
//
//        //Set timeout until the response is received
//        bindingProvider.getRequestContext().put("javax.xml.ws.client.receiveTimeout", receiveTimeout);

        responseVal = clsMQServ.getClsMQServSoap().makeCall(requestXML);
        return responseVal;
    }

    protected String unformatResponseXML(String responsePayload, String programName) throws SAXException, IOException,
            XPathExpressionException {
        Document doc = documentBuilder.parse(new InputSource(new StringReader(responsePayload)));
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        Object result = null;
        Node node = null;

        char returnCode;
        String errorMessage = "";
        String payload = "";

        logger.trace("Getting ready to run the query {}", getxPathResponseReturnCode());
        result = xpath.evaluate(getxPathResponseReturnCode(), doc, XPathConstants.NODE);
        node = (Node) result;
        returnCode = node.getTextContent().charAt(0);

        logger.trace("Getting ready to run the query {}", getxPathResponseErrorMessage());
        result = xpath.evaluate(getxPathResponseErrorMessage(), doc, XPathConstants.NODE);
        node = (Node) result;
        if (node != null) {
            errorMessage = node.getTextContent();
        }

        logger.trace("Getting ready to run the query {}", getxPathResponsePayload());
        result = xpath.evaluate(getxPathResponsePayload(), doc, XPathConstants.NODE);
        node = (Node) result;
        if (result != null) {
            payload = node.getTextContent();
        }

        //Anything other than G we bomb w/ MAPPSException
        if (returnCode != 'G') {
            logger.error("Error from MQ Serv " + programName + " Details " + errorMessage);
            throw new AuthorizerException("Error from MQServ Program Name " + programName + " Details "
                    + errorMessage + ": ");
        }
        return payload;
    }

    protected boolean parseValidateResponse(String response) {
        boolean match = false;
       
        // No need to replace empty chars
//        String responseT = response.replaceAll(" ","");
         
        if (response.charAt(50) == '0') {
            logger.debug("Account number matches ");
            match = true;
        } else if (response.charAt(50) == 'S') {
            logger.debug("Account number does not match");
            match = false;
        } else {
            String errorResponse = response.substring(50, 61);
            throw new AuthorizerException(errorResponse);
        }
        return match;
    }

    public String getxPathLookupTranType() {
        return xPathLookupTranType;
    }

    public void setxPathLookupTranType(String xPathLookupTranType) {
        this.xPathLookupTranType = xPathLookupTranType;
    }

    public String getxPathLookupTranCPU() {
        return xPathLookupTranCPU;
    }

    public void setxPathLookupTranCPU(String xPathLookupTranCPU) {
        this.xPathLookupTranCPU = xPathLookupTranCPU;
    }

    public String getxPathLookupTransaction() {
        return xPathLookupTransaction;
    }

    public void setxPathLookupTransaction(String xPathLookupTransaction) {
        this.xPathLookupTransaction = xPathLookupTransaction;
    }

    public String getxPathLookupTranID() {
        return xPathLookupTranID;
    }

    public void setxPathLookupTranID(String xPathLookupTranID) {
        this.xPathLookupTranID = xPathLookupTranID;
    }

    public String getxPathLookupTimeLimit() {
        return xPathLookupTimeLimit;
    }

    public void setxPathLookupTimeLimit(String xPathLookupTimeLimit) {
        this.xPathLookupTimeLimit = xPathLookupTimeLimit;
    }

    public String getxPathLookupUserId() {
        return xPathLookupUserId;
    }

    public void setxPathLookupUserId(String xPathLookupUserId) {
        this.xPathLookupUserId = xPathLookupUserId;
    }

    public String getxPathLookupPassword() {
        return xPathLookupPassword;
    }

    public void setxPathLookupPassword(String xPathLookupPassword) {
        this.xPathLookupPassword = xPathLookupPassword;
    }

    public String getxPathLookupPayload() {
        return xPathLookupPayload;
    }

    public void setxPathLookupPayload(String xPathLookupPayload) {
        this.xPathLookupPayload = xPathLookupPayload;
    }

    /**
     * @return the lookupTranType
     */
    public String getLookupTranType() {
        return lookupTranType;
    }

    /**
     * @param lookupTranType the lookupTranType to set
     */
    public void setLookupTranType(String lookupTranType) {
        this.lookupTranType = lookupTranType;
    }

    /**
     * @return the lookupTranCPU
     */
    public String getLookupTranCPU() {
        return lookupTranCPU;
    }

    /**
     * @param lookupTranCPU the lookupTranCPU to set
     */
    public void setLookupTranCPU(String lookupTranCPU) {
        this.lookupTranCPU = lookupTranCPU;
    }

    /**
     * @return the lookupTransaction
     */
    public String getLookupTransaction() {
        return lookupTransaction;
    }

    /**
     * @param lookupTransaction the lookupTransaction to set
     */
    public void setLookupTransaction(String lookupTransaction) {
        this.lookupTransaction = lookupTransaction;
    }

    /**
     * @return the lookupTranID
     */
    public String getLookupTranID() {
        return lookupTranID;
    }

    /**
     * @param lookupTranID the lookupTranID to set
     */
    public void setLookupTranID(String lookupTranID) {
        this.lookupTranID = lookupTranID;
    }

    /**
     * @return the lookupTimeLimit
     */
    public String getLookupTimeLimit() {
        return lookupTimeLimit;
    }

    /**
     * @param lookupTimeLimit the lookupTimeLimit to set
     */
    public void setLookupTimeLimit(String lookupTimeLimit) {
        this.lookupTimeLimit = lookupTimeLimit;
    }

    /**
     * @return the lookupUserId
     */
    public String getLookupUserId() {
        return lookupUserId;
    }

    /**
     * @param lookupUserId the lookupUserId to set
     */
    public void setLookupUserId(String lookupUserId) {
        this.lookupUserId = lookupUserId;
    }

    /**
     * @return the lookupPassword
     */
    public String getLookupPassword() {
        return lookupPassword;
    }

    /**
     * @param lookupPassword the lookupPassword to set
     */
    public void setLookupPassword(String lookupPassword) {
        this.lookupPassword = lookupPassword;
    }

    /**
     * @return the xPathResponseReturnCode
     */
    public String getxPathResponseReturnCode() {
        return xPathResponseReturnCode;
    }

    /**
     * @param xPathResponseReturnCode the xPathResponseReturnCode to set
     */
    public void setxPathResponseReturnCode(String xPathResponseReturnCode) {
        this.xPathResponseReturnCode = xPathResponseReturnCode;
    }

    /**
     * @return the xPathResponseErrorMessage
     */
    public String getxPathResponseErrorMessage() {
        return xPathResponseErrorMessage;
    }

    /**
     * @param xPathResponseErrorMessage the xPathResponseErrorMessage to set
     */
    public void setxPathResponseErrorMessage(String xPathResponseErrorMessage) {
        this.xPathResponseErrorMessage = xPathResponseErrorMessage;
    }

    /**
     * @return the xPathResponsePayload
     */
    public String getxPathResponsePayload() {
        return xPathResponsePayload;
    }

    /**
     * @param xPathResponsePayload the xPathResponsePayload to set
     */
    public void setxPathResponsePayload(String xPathResponsePayload) {
        this.xPathResponsePayload = xPathResponsePayload;
    }

}
