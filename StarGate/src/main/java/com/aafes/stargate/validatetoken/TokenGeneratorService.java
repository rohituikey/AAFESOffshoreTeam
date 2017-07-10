/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.validatetoken;

import com.aafes.stargate.dao.TokenServiceDAO;
import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.util.CreditMessageTokenConstants;
import com.aafes.tokenvalidator.Message;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.XMLConstants;
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
 * THE SERVICE IS USED TO GENERATE A 6 DIGIT RANDOM SECURE NUMERIC TOKEN. TOKEN
 * HAS LIFE OF 1 HOUR AFTER WHICH THE TOKEN STATUS WILL BE HANGED TO 'EXPIRED'.
 * TABLE MAPPING IS STARGATE.CROSSSITEREQUESTTOKENTABLE. TOKEN CAN BE USED TO
 * VALIDATE THE TRANSACTIONS RECEIVED TO STARGATE, IF THE TOKEN IS VALID THEN
 * ONLY THE TRANSACTION IN VALID AND PROCESSED FURTHER.
 * <?xml version="1.0" encoding="UTF-8"?>
 * <ns1:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
 * xmlns:ns1='http://www.aafes.com/tokenvalidator'
 * xsi:schemaLocation='http://www.aafes.com/tokenvalidator
 * file:<TokenValidator.XSD PATH>' MajorVersion="3" MinorVersion="1"
 * FixVersion="0">
 * <ns1:Header>
 * <ns1:IdentityUUID>UUID value</ns1:IdentityUUID>
 * <ns1:UerId>user name value</ns1:UerId>
 * <ns1:Password>password value</ns1:Password>
 * </ns1:Header>
 * <ns1:Request>
 * <ns1:RequestType>Token</ns1:RequestType>
 * </ns1:Request>
 * </ns1:Message>
 *
 * @author burangir
 */
@Path("/token")
public class TokenGeneratorService {

    @EJB
    private TokenServiceDAO tokenServiceDAO;

    private CrosssiteRequestTokenTable tokenObj;
    private CrosssiteRequestUsertable tokenUserDetObj;

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(TokenGeneratorService.class.getSimpleName());
    private String sMethodName = "";
    private final String CLASS_NAME = TokenGeneratorService.this.getClass().getSimpleName();
    private String SCHEMA_PATH = "src/main/resources/jaxb/tokenvalidator/TokenValidator.xsd";
    /**
     * @param tokenServiceDAO the tokenServiceDAO to set
     */
    public void setTokenServiceDAO(TokenServiceDAO tokenServiceDAO) {
        this.tokenServiceDAO = tokenServiceDAO;
    }

    @POST
    @Consumes("application/xml")
    @Produces("text/plain")
    public String postXml(String requestXML) {
        sMethodName = "postXml";
        boolean validateUserFlg = false;
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);

        String responseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ErrorInformation><Error>Invalid XML</Error></ErrorInformation>";
        try {
            LOG.info("From Client TokenGeneratorService: " + requestXML);
            String ValidatedXML = FilterRequestXML(requestXML);
            if (requestXML.contains("DOCTYPE") || requestXML.contains("CDATA")) {
                LOG.error("Invalid Request type TokenGeneratorService");
            } else if (ValidatedXML != null) {
                Message requestMessage = unmarshalWithValidation(requestXML);
                 LOG.debug("uuid no is.." + requestMessage.getHeader().getIdentityUUID());
                //checkAndUpdateDuplicateToken(requestMessage.getHeader().getIdentityUUID());
                validateUserFlg = validateUserDetails(requestMessage);
              
                if (validateUserFlg) {
                    responseXML = generateToken(requestMessage);
                } else {
                    responseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ErrorInformation><Error>Invalid credentials"
                            + "</Error></ErrorInformation>";
                }
            } else {
                LOG.error("Invalid Request ");
            }

            LOG.info("To Client TokenGeneratorService: " + responseXML);
        } catch (JAXBException | SAXException e) {
            LOG.error(e.toString());
            throw new GatewayException("INTERNAL SYSTEM ERROR-->SAXException/JAXBException.. " + e);
        } catch (Exception ex) {
            Logger.getLogger(TokenGeneratorService.class.getName()).log(Level.SEVERE, null, ex);
            throw new GatewayException("INTERNAL SYSTEM ERROR" + ex);
        }
        LOG.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        return responseXML;
    }

    private Message unmarshalWithValidation(String xml) throws SAXException, JAXBException {
        sMethodName = "unmarshalWithValidation";
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        Message request = new Message();
        StringReader reader = new StringReader(xml);
        JAXBContext jaxbContext = JAXBContext.newInstance(Message.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try {
            schema = sf.newSchema(new File(SCHEMA_PATH));
        } catch (Exception e) {
            LOG.info("Schema file taking from server location");
            SCHEMA_PATH = System.getProperty("jboss.server.config.dir") + "/TokenValidator.xsd";
            schema = sf.newSchema(new File(SCHEMA_PATH));
        }

        jaxbUnmarshaller.setSchema(schema);
        request = (Message) jaxbUnmarshaller.unmarshal(reader);
        LOG.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        return request;
    }

    private static String FilterRequestXML(String xmlString) {
        LOG.info("Method FilterRequestXML started. Class Name TokenGeneratorService");

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
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        }
        LOG.info("Method FilterRequestXML ended. Class Name TokenGeneratorService");
        return retString;
    }

    public static String getStringFromDocument(Document doc) throws TransformerException {
        LOG.info("Method getStringFromDocument started. Class Name TokenGeneratorService");
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
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        }
        LOG.info("Method getStringFromDocument ended. Class Name TokenGeneratorService");
        if (null != writer) {
            return writer.toString();
        } else {
            return "";
        }
    }

    private String generateToken(Message requestMessage) {
        sMethodName = "generateToken";
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        boolean dataInsertedFlg = false;
        String tokenNumber = "";
        Date dateObj = new Date();
        DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");

        try {
            String creTime = dateFormat1.format(dateObj);
            tokenNumber = generateSecureRandomToken();
            if (tokenObj == null) {
                tokenObj = new CrosssiteRequestTokenTable();
            }
            tokenObj.setTokenid(tokenNumber);
            tokenObj.setTokenstatus(CreditMessageTokenConstants.STATUS_ACTIVE);
            tokenObj.setTokencredatetime(creTime);
            tokenObj.setIdentityuuid(requestMessage.getHeader().getIdentityUUID());

            dataInsertedFlg = tokenServiceDAO.insertTokenDetails(tokenObj);
            if (dataInsertedFlg) {
                LOG.info("Data Inserted in table stargate.crosssiterequesttokentable successfully!");
            } else {
                LOG.info("Data Insertion in table stargate.crosssiterequesttokentable FAILED!");
            }
        } catch (Exception ex) {
            LOG.error("Error while creating cross site request token " + ex.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        }
        LOG.info("Method " + sMethodName + " ENDED." + " Class Name " + CLASS_NAME);
        LOG.debug("uuid no is.." + requestMessage.getHeader().getIdentityUUID());
        return tokenNumber;
    }

    private String generateSecureRandomToken() {
        sMethodName = "generateSecureRandomToken";
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        SecureRandom randomNumber = null;
        int nextToken = 0;
        try {
            randomNumber = SecureRandom.getInstance("SHA1PRNG");
            nextToken = randomNumber.nextInt(900000) + 100000;
            LOG.info("Token Value " + nextToken);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CLASS_NAME).log(Level.SEVERE, null, ex);
            LOG.equals("NoSuchAlgorithmException"+ex.toString());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        }
        LOG.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        return String.valueOf(nextToken);
    }

    private boolean validateUserDetails(Message requestMessage) {
        sMethodName = "validateUserDetails";
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        boolean userValidated = false;
        try {
            if (tokenUserDetObj == null) {
                tokenUserDetObj = new CrosssiteRequestUsertable();
            }
            tokenUserDetObj.setIdentityuuid(requestMessage.getHeader().getIdentityUUID());
            tokenUserDetObj.setUserid(requestMessage.getHeader().getUerId());
            tokenUserDetObj.setPassword(requestMessage.getHeader().getPassword());

            userValidated = tokenServiceDAO.validateUserDetails(tokenUserDetObj);

            if (userValidated) {
                LOG.info("User Validated successfully!");
            } else {
                LOG.info("User Validation Failed!");
            }
        } catch (Exception ex) {
            Logger.getLogger(CLASS_NAME).log(Level.SEVERE, null, ex);
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        }
        LOG.debug("uuid no is.." + requestMessage.getHeader().getIdentityUUID());
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        return userValidated;
    }

//    private void checkAndUpdateDuplicateToken(String identityUuid) {
//        sMethodName = "checkAndUpdateDuplicateToken";
//        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
//        boolean dataUpdateFlg = false;
//        String tokenId, identityUuidLocal, tokenStatus = CreditMessageTokenConstants.STATUS_EXPIRED;
//        List<Row> resultSetRows = null;
//        try {
//            TokenServiceDAO tokenServiceDAO = null;
//            if (tokenServiceDAO == null) {
//                tokenServiceDAO = new TokenServiceDAO();
//            }
//            ResultSet resultSet = tokenServiceDAO.findActiveTokens(identityUuid, CreditMessageTokenConstants.STATUS_ACTIVE);
//
//            if (resultSet != null) {
//                resultSetRows = resultSet.all();
//                if(resultSetRows != null && resultSetRows.size() > 0)
//                    LOG.info("Active tokens found for identityUuid " + identityUuid);
//                
//                for(Row row : resultSetRows){
//                    tokenId = row.getString("tokenid");
//                    identityUuidLocal = row.getString("identityuuid");
//                    
//                    dataUpdateFlg = tokenServiceDAO.updateTokenStatus(tokenStatus, tokenId, identityUuidLocal);
//                    if(dataUpdateFlg)
//                        LOG.info("Duplicate Data Udpated. tokenid " + tokenId + ", identityuuid " + identityUuid + ", Status " + tokenStatus);
//                }
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(CLASS_NAME).log(Level.SEVERE, null, ex);
//        }
//        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
//    }
//    private void saveUserDetails(Message requestMessage) {
//        sMethodName = "saveUserDetails";
//        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
//        boolean dataInsertedFlg = false;
//        try {
//            if (tokenServiceDAO == null) tokenServiceDAO = new TokenServiceDAO();
//
//            CrosssiteRequestUsertable tokenUserDetObj = new CrosssiteRequestUsertable();
//            tokenUserDetObj.setIdentityuuid(requestMessage.getHeader().getIdentityUUID());
//            tokenUserDetObj.setUserid(requestMessage.getHeader().getUerId());
//            tokenUserDetObj.setPassword(requestMessage.getHeader().getPassword());
//            
//            dataInsertedFlg = tokenServiceDAO.insertUserDetails(tokenUserDetObj);
//
//            if (dataInsertedFlg) LOG.info("Data Inserted in table stargate.crosssiterequestusertable successfully!");
//            else LOG.info("Data Insertion in table stargate.crosssiterequestusertable FAILED!");
//
//        } catch (Exception ex) {
//            Logger.getLogger(CLASS_NAME).log(Level.SEVERE, null, ex);
//        }
//        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
//    }
}
