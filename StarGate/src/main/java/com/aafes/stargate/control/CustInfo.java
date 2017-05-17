/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.control;

import com.aafes.credit.cs.jaxws.custinfo.ClsCustInfo;
import com.aafes.credit.cs.jaxws.custinfo.ClsCustInfoSoap;
import java.io.FileReader;
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
import javax.xml.ws.BindingProvider;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/*
 * @author joshid
 */
public class CustInfo {

    private static final org.slf4j.Logger logger
            = LoggerFactory.getLogger(Authorizer.class.getSimpleName());

    protected static final QName CLSCUSTINFO_QNAME = new QName("http://www.aafes.com", "clsCustInfo");
    protected static URL clsCustInfoURL = null;

    private final DocumentBuilder documentBuilder;

    @Resource(lookup = "java:jboss/param/CustInfo/urlWSDL")
    private String custInfoWSDL;
    @Resource(lookup = "java:jboss/param/CustInfo/connectTimeout")
    private String connectTimeout;
    @Resource(lookup = "java:jboss/param/CustInfo/receiveTimeout")
    private String receiveTimeout;

    //XPath Request
    private String xPathApplicationID;
    private String xPathApplicationSecurityPassword;
    private String xPathCID;

    //Request
    private String applicationID;
    private String applicationSecurityPassword;

    //Response
    private String xPathResponseReturnCode;
    private String xPathResponseErrorMessage;
    private String xPathResponseSSN;

    //A instance initializer containing the hardcoded stuff that should be in spring
    {
        setxPathApplicationID("//Header/WebSecurity/ApplicationSecurityID");
        setxPathApplicationSecurityPassword("//Header/WebSecurity/ApplicationSecurityPassword");
        setxPathCID("//CustInfo/PayLoad/CID");

        setApplicationID("17");
        setApplicationSecurityPassword("8493AEDC-7D7C-4D4E-A5CA-6E4313F0C20F");

        setxPathResponseReturnCode("//CustInfo/Header/ReturnCode");
        setxPathResponseErrorMessage("//CustInfo/Header/ErrorMessage");
        setxPathResponseSSN("//CustInfo/PayLoad/SSN");
    }

    public CustInfo() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        documentBuilderFactory.setNamespaceAware(true);
        logger.trace("CustInfo Initializing the parser ");
    }

    public String callCustomerLookup(String cid) {
        String returnVal = null;
        try {
            String custInfoRequestXML = formatLookupXML(cid);
            String custInfoResponseXML = null;
            if (custInfoRequestXML != null) {
                custInfoResponseXML = callWebService(custInfoRequestXML);
            } else {
                // We didn't get a repsonse so don't worry about it 
                return null;
            }
            if (custInfoResponseXML != null) {
                returnVal = parseLookupResponse(custInfoResponseXML);
             }
        } catch (SAXException | IOException | XPathExpressionException | TransformerException | NamingException ee) {
            throw new AuthorizerException("Unable to perform the customer lookup");
        }
        return returnVal;
    }

    protected String callWebService(String requestXML) throws MalformedURLException, NamingException {
        //Use the alternate constructor to see how to point between test and production 
        //clsCustInfoURL = new URL("http://toucan-hqws02-beta.aafes.com/wsECS/clsCustInfo.asmx?wsdl");

        //We are not using a bean so look things up this way
        InitialContext context = new InitialContext();

//        custInfoWSDL = (String) context.lookup("java:jboss/param/CustInfo/urlWSDL");
//        connectTimeout = (String) context.lookup("java:jboss/param/CustInfo/connectTimeout");
//        receiveTimeout = (String) context.lookup("java:jboss/param/CustInfo/receiveTimeout");
//        logger.debug("Using URL " + custInfoWSDL);
//        clsCustInfoURL = new URL(custInfoWSDL);
        ClsCustInfo custInfoWS = new ClsCustInfo();
        ClsCustInfoSoap clsCustInfoSoap = custInfoWS.getClsCustInfoSoap();
        String res = clsCustInfoSoap.custInfo(requestXML);
//        BindingProvider bindingProvider = (BindingProvider) custInfoWS.getPort(ClsCustInfoSoap.class);
//        logger.debug("Setting timeouts connectTimeout {}, receiveTimeout {} ", connectTimeout, receiveTimeout);
        //bindingProvider.getRequestContext().put("javax.xml.ws.client.connectionTimeout", "1000");
//        bindingProvider.getRequestContext().put("javax.xml.ws.client.connectionTimeout", connectTimeout);

        //Set timeout until the response is received
//        bindingProvider.getRequestContext().put("javax.xml.ws.client.receiveTimeout", receiveTimeout);

//        String response = custInfoWS.getClsCustInfoSoap().custInfo(requestXML);
//        logger.debug("Raw response " + response);
        return res;
    }

    protected String formatLookupXML(String cid) throws SAXException, IOException,
            XPathExpressionException, TransformerConfigurationException, TransformerException {
        InputStream is = CustInfo.class.getClassLoader().getResourceAsStream("custinfo-config/custinfo-request.xml");
        logger.debug("Input stream from the resource " + is);

        logger.debug("beginning parse.");
        Document doc = documentBuilder.parse(is);
        logger.debug("ending parse.");

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        Object result = null;
        Node node = null;

        logger.trace("Getting ready to run the query {}", getxPathApplicationID());
        result = xpath.evaluate(getxPathApplicationID(), doc, XPathConstants.NODE);
        node = (Node) result;
        node.setTextContent(getApplicationID());

        logger.trace("Getting ready to run the query {}", getxPathApplicationSecurityPassword());
        result = xpath.evaluate(getxPathApplicationSecurityPassword(), doc, XPathConstants.NODE);
        node = (Node) result;
        node.setTextContent(getApplicationSecurityPassword());

        logger.trace("Getting ready to run the query {}", getxPathCID());
        result = xpath.evaluate(getxPathCID(), doc, XPathConstants.NODE);
        node = (Node) result;
        node.setTextContent(cid);

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

        logger.debug("Request in CustInfo Format: " + writer.toString());
        return writer.toString();
    }

    protected String parseLookupResponse(String response) throws SAXException, IOException,
            XPathExpressionException, TransformerConfigurationException, TransformerException {

        String returnVal = null;

        logger.debug("beginning parse." + response);
        Document doc = documentBuilder.parse(new InputSource(new StringReader(response)));
        logger.debug("ending parse.");

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        Object result = null;
        Node node = null;

        char returnCode;
        String errorMessage = null;
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

        //Anything other than G we bomb w/ MAPPSException
        if (returnCode != 'G') {
            logger.warn("Did not get a good return code from CustInfo.");
        }

        logger.trace("Getting ready to run the query {}", getxPathResponseSSN());
        result = xpath.evaluate(getxPathResponseSSN(), doc, XPathConstants.NODE);
        node = (Node) result;
        logger.trace("node results " + node);
        if (node != null && !node.getTextContent().equals("000000000")) {
            returnVal = node.getTextContent();
        }

        return returnVal;
    }

    /**
     * @return the xPathApplicationID
     */
    public String getxPathApplicationID() {
        return xPathApplicationID;
    }

    /**
     * @param xPathApplicationID the xPathApplicationID to set
     */
    public void setxPathApplicationID(String xPathApplicationID) {
        this.xPathApplicationID = xPathApplicationID;
    }

    /**
     * @return the xPathApplicationSecurityPassword
     */
    public String getxPathApplicationSecurityPassword() {
        return xPathApplicationSecurityPassword;
    }

    /**
     * @param xPathApplicationSecurityPassword the
     * xPathApplicationSecurityPassword to set
     */
    public void setxPathApplicationSecurityPassword(String xPathApplicationSecurityPassword) {
        this.xPathApplicationSecurityPassword = xPathApplicationSecurityPassword;
    }

    public String getxPathCID() {
        return xPathCID;
    }

    public void setxPathCID(String xPathCID) {
        this.xPathCID = xPathCID;
    }

    /**
     * @return the applicationID
     */
    public String getApplicationID() {
        return applicationID;
    }

    /**
     * @param applicationID the applicationID to set
     */
    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    /**
     * @return the applicationSecurityPassword
     */
    public String getApplicationSecurityPassword() {
        return applicationSecurityPassword;
    }

    /**
     * @param applicationSecurityPassword the applicationSecurityPassword to set
     */
    public void setApplicationSecurityPassword(String applicationSecurityPassword) {
        this.applicationSecurityPassword = applicationSecurityPassword;
    }

    /**
     * @return the xPathResponseSSN
     */
    public String getxPathResponseSSN() {
        return xPathResponseSSN;
    }

    /**
     * @param xPathResponseSSN the xPathResponseSSN to set
     */
    public void setxPathResponseSSN(String xPathResponseSSN) {
        this.xPathResponseSSN = xPathResponseSSN;
    }

    public String getxPathResponseReturnCode() {
        return xPathResponseReturnCode;
    }

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

}
