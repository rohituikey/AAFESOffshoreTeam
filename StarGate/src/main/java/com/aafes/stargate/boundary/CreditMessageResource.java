package com.aafes.stargate.boundary;

import com.aafes.credit.Message;
import com.aafes.stargate.control.Authorizer;
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

/**
 * RESTful Web Service for Vision Credit Messages. This is a plain ol' XML web
 * service endpoint for tendering MilStar.
 *
 * @author mercadoch
 */
@Path("/creditmessage")
public class CreditMessageResource {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(CreditMessageResource.class.
                    getSimpleName());

    @EJB
    private Authorizer authorizer;

    //TODO
    private String SCHEMA_PATH = "src/main/resources/jaxb/creditmessage/CreditMessage12S1.xsd";

    /**
     * Process a new credit authorization request.
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
            String ValidatedXML = FilterRequestXML(requestXML);
            if(requestXML.contains("DOCTYPE")
                    ||requestXML.contains("CDATA")){
                LOG.error("Invalid Request");
                responseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ErrorInformation><Error>Invalid XML</Error>"
                        + "</ErrorInformation>";
            }
            else if (ValidatedXML != null) {
                Message requestMessage = unmarshalWithValidation(requestXML);
                //Message requestMessage = unmarshalWithValidation(requestXML);
                Message responseMessage = authorizer.authorize(requestMessage);
                //putInfoOnHealthChecker(responseMessage);

                responseXML = marshal(responseMessage);
                LOG.info("To Client: " + responseXML);
            } else {
                LOG.error("Invalid Request");
                responseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ErrorInformation><Error>Invalid XML</Error>"
                        + "</ErrorInformation>";
            }

        } catch (JAXBException | SAXException e) {

            responseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ErrorInformation><Error>Invalid XML</Error>"
                    + "</ErrorInformation>";
            LOG.error(e.toString());
        } catch (Exception ex) {
            Logger.getLogger(CreditMessageResource.class.getName()).log(Level.SEVERE, null, ex);
        }

        return responseXML;

    }

//    private void putInfoOnHealthChecker(Message responseMessage) {
//        String identityUUID = responseMessage.getHeader().getIdentityUUID();
//        String responseType = responseMessage.getResponse().get(0).
//                getResponseType();
//        BigDecimal amountField = responseMessage.getRequest().get(0).
//                getAmountField();
//        String orderNumber = responseMessage.getHeader().getOrderNumber();
//        String info = identityUUID + " "
//                + orderNumber + " "
//                + String.valueOf(amountField) + " "
//                + responseType;
//        healthChecker.addInfo(info);
//    }
    /**
     * Process the Credit Message in JSON. Authorize the MilStar request
     * described in the inbound JSON document. The content of the JSON document
     * is the exact same content used in postXML.
     *
     * @param requestJSON
     * @return the request document with response data added, in JSON.
     */
//    private Message unmarshalCreditMessage(String content) throws SAXException, JAXBException {
//        Message request = new Message();
//        try {
//            StringReader reader = new StringReader(content);
//            JAXBContext jc = JAXBContext.newInstance(Message.class);
//            Unmarshaller unmarshaller = jc.createUnmarshaller();
//            JAXBContext jaxbContext = JAXBContext.newInstance(Message.class);
//            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//            request = (Message) jaxbUnmarshaller.unmarshal(reader);
//        } catch (JAXBException ex) {
//            LOG.error(ex.toString());
//        }
//        return request;
//    }
    private String marshal(Message request) {
        StringWriter sw = new StringWriter();
        JAXB.marshal(request, sw);
        String xmlString = sw.toString();
        return xmlString;
    }

    private Message unmarshalWithValidation(String xml) throws SAXException, JAXBException {
        Message request = new Message();

        StringReader reader = new StringReader(xml);
        JAXBContext jc = JAXBContext.newInstance(Message.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBContext jaxbContext = JAXBContext.newInstance(Message.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        SchemaFactory sf = SchemaFactory.newInstance(
                XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try {
            schema = sf.newSchema(new File(SCHEMA_PATH));
        } catch (Exception e) {
            SCHEMA_PATH = System.getProperty("jboss.server.config.dir") + "/CreditMessage12S1.xsd";
            schema = sf.newSchema(new File(SCHEMA_PATH));
        }

        jaxbUnmarshaller.setSchema(schema);
        request = (Message) jaxbUnmarshaller.unmarshal(reader);

        return request;
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
