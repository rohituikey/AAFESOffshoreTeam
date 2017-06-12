package com.aafes.starsettler.boundary;

import generated.Settlement;
import com.aafes.starsettler.control.Settler;
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
 *
 * @author ganjis
 */
@Path("/settlemessage")
public class SettleMessageResource {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(SettleMessageResource.class.
                    getSimpleName());

    @EJB
    private Settler settler;

    //TODO add schema path here
    private String SCHEMA_PATH = "src/main/resources/jaxb/settlemessage/SettleMessage-StarGate-D033017.xsd";

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

            // Unmarshal against XSD
            // Send the settle message to Setler
             String ValidatedXML = FilterRequestXML(requestXML);
              if(requestXML.contains("DOCTYPE")
                    ||requestXML.contains("CDATA")){
                LOG.error("Invalid Request");
                responseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ErrorInformation><Error>Invalid XML</Error>"
                        + "</ErrorInformation>";
            }
            else if (ValidatedXML != null) {
            Settlement requestMessage = unmarshalWithValidation(requestXML);

            Settlement responseMessage = settler.saveForSettle(requestMessage);

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
            Logger.getLogger(SettleMessageResource.class.getName()).log(Level.SEVERE, null, ex);
        }

        return responseXML;

    }

    private String marshal(Settlement request) {
        StringWriter sw = new StringWriter();
        JAXB.marshal(request, sw);
        String xmlString = sw.toString();
        return xmlString;
    }

    private Settlement unmarshalWithValidation(String xml) throws SAXException, JAXBException {
        Settlement request = new Settlement();

        StringReader reader = new StringReader(xml);
        JAXBContext jc = JAXBContext.newInstance(Settlement.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBContext jaxbContext = JAXBContext.newInstance(Settlement.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        SchemaFactory sf = SchemaFactory.newInstance(
                XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try {
            schema = sf.newSchema(new File(SCHEMA_PATH));
        } catch (Exception e) {
            //TODO : XSD File Name
            SCHEMA_PATH = System.getProperty("jboss.server.config.dir") + "/SettleMessage-StarGate-D033017.xsd";
            schema = sf.newSchema(new File(SCHEMA_PATH));
        }

        jaxbUnmarshaller.setSchema(schema);
        request = (Settlement) jaxbUnmarshaller.unmarshal(reader);

        return request;
    }

    /**
     * @param settler the settler to set
     */
    public void setSettler(Settler settler) {
        this.settler = settler;
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
