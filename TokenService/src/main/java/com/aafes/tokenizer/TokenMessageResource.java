/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenizer;

import generated.TokenMessage;
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
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.slf4j.LoggerFactory;
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

            TokenMessage requestMessage = unmarshalWithValidation(requestXml);
            TokenMessage responseMessage = tokenizer.handle(requestMessage);
            
            responseXml = marshal(responseMessage);
        } catch (SAXException ex) {
            Logger.getLogger(TokenMessageResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(TokenMessageResource.class.getName()).log(Level.SEVERE, null, ex);
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

}