/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenizer;

import com.aafes.token.TokenMessage;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

 
@Path("/tokenmessage")
public class TokenMessageResource {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(TokenMessageResource.class.
                    getSimpleName());

    @EJB
    private Tokenizer tokenizer;

    //TODO
    private String SCHEMA_PATH = "src/main/resources/jaxb/tokenmessage/TokenMessage.xsd";

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public String postXml(String requestXml) {

        String responseXml = "";
        try {
            
             LOG.info("From Client: " + requestXml);
            String ValidatedXML = FilterRequestXML(requestXml);
            if(requestXml.contains("DOCTYPE")
                    ||requestXml.contains("CDATA")){
                LOG.error("Invalid Request");
                responseXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ErrorInformation><Error>Invalid XML</Error>"
                        + "</ErrorInformation>";
            }
            else if (ValidatedXML != null) {
            TokenMessage requestMessage = unmarshalWithValidation(requestXml);
            TokenMessage responseMessage = tokenizer.handle(requestMessage);
            
            responseXml = marshal(responseMessage);
            LOG.info("To Client: " + responseXml);
            } else {
                LOG.error("Invalid Request");
                responseXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ErrorInformation><Error>Invalid XML</Error>"
                        + "</ErrorInformation>";
            }
        } catch (SAXException ex) {
            Logger.getLogger(TokenMessageResource.class.getName()).log(Level.SEVERE, null, ex);
            responseXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ErrorInformation><Error>Invalid XML</Error>"
                        + "</ErrorInformation>";
        } catch (JAXBException ex) {
            Logger.getLogger(TokenMessageResource.class.getName()).log(Level.SEVERE, null, ex);
            responseXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ErrorInformation><Error>Invalid XML</Error>"
                        + "</ErrorInformation>";
        } catch (Exception e) {
            Logger.getLogger(TokenMessageResource.class.getName()).log(Level.SEVERE, null, e);
            responseXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ErrorInformation><Error>Invalid XML</Error>"
                        + "</ErrorInformation>";
        }

        return responseXml;
    }

    private String marshal(TokenMessage request) {
        StringWriter sw = new StringWriter();
        JAXB.marshal(request, sw);
        String xmlString = sw.toString();
        return xmlString;
    }

    private TokenMessage unmarshalWithValidation(String xml) throws SAXException, JAXBException {
        TokenMessage request = new TokenMessage();

        StringReader reader = new StringReader(xml);
        JAXBContext jc = JAXBContext.newInstance(TokenMessage.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBContext jaxbContext = JAXBContext.newInstance(TokenMessage.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        SchemaFactory sf = SchemaFactory.newInstance(
                XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try {
            schema = sf.newSchema(new File(SCHEMA_PATH));
        } catch (Exception e) {
            SCHEMA_PATH = System.getProperty("jboss.server.config.dir") + "/TokenMessage.xsd";
            schema = sf.newSchema(new File(SCHEMA_PATH));
        }

        jaxbUnmarshaller.setSchema(schema);
        request = (TokenMessage) jaxbUnmarshaller.unmarshal(reader);

        return request;
    }

    protected void setTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }
    
    private static String FilterRequestXML(String xmlString) {
        LOG.info("In filter request method.");

        String retString = null;
        try {
            StringReader reader = new StringReader(xmlString);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            Document document = dbf.newDocumentBuilder().parse(new InputSource(reader));
            retString = getStringFromDocument(document);
            LOG.info("Validated XML");
        } catch (Exception ex) {
            LOG.error(ex.toString());
            retString = null;
        }
        return retString;
    }

    public static String getStringFromDocument(Document doc) throws TransformerException {

        StringWriter writer = null;

        try {
            DOMSource domSource = new DOMSource(doc);
            writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

        } catch (Exception ex) {
            LOG.error(ex.toString());
        }

        return writer.toString();
    }

}