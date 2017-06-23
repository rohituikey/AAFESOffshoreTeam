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
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
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
 *
 * @author burangir
 */
@Path("/token")
public class TokenGeneratorService {

    private TokenServiceDAO tokenServiceDAO;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(TokenGeneratorService.class.getSimpleName());
    private String sMethodName = "";
    private final String CLASS_NAME = TokenGeneratorService.this.getClass().getSimpleName();
    private String SCHEMA_PATH = "src/main/resources/jaxb/tokenvalidator/TokenValidator.xsd";

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
                LOG.error("Invalid Request TokenGeneratorService");
            } else if (ValidatedXML != null) {
                Message requestMessage = unmarshalWithValidation(requestXML);
                //checkAndUpdateDuplicateToken(requestMessage.getHeader().getIdentityUUID());
                validateUserFlg = validateUserDetails(requestMessage);
                if(validateUserFlg){
                    responseXML = generateToken(requestMessage);
                }else responseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ErrorInformation><Error>Invalid credentials"
                        + "</Error></ErrorInformation>";
            } else {
                LOG.error("Invalid Request");
            }
            LOG.info("To Client TokenGeneratorService: " + responseXML);
        } catch (JAXBException | SAXException e) {
            LOG.error(e.toString());
             throw new GatewayException("INTERNAL SYSTEM ERROR");
        } catch (Exception ex) {
            Logger.getLogger(TokenGeneratorService.class.getName()).log(Level.SEVERE, null, ex);
             throw new GatewayException("INTERNAL SYSTEM ERROR");
        }
        LOG.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        return responseXML;
    }

    private Message unmarshalWithValidation(String xml) throws SAXException, JAXBException {
        sMethodName = "unmarshalWithValidation";
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        Message request = new Message();
        StringReader reader = new StringReader(xml);
        JAXBContext jc = JAXBContext.newInstance(Message.class);
        JAXBContext jaxbContext = JAXBContext.newInstance(Message.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try {
            schema = sf.newSchema(new File(SCHEMA_PATH));
        } catch (Exception e) {
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
        //Message responseMessage = null;
        boolean dataInsertedFlg = false;
        String tokenNumber = "";
        try {
            Date dateObj = new Date();
            DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
            String creTime = dateFormat1.format(dateObj);
            tokenNumber = generateSecureRandomToken();
            if (tokenServiceDAO == null) {
                tokenServiceDAO = new TokenServiceDAO();
            }
            CrosssiteRequestTokenTable tokenObj = new CrosssiteRequestTokenTable();
            tokenObj.setTokenid(tokenNumber);
            tokenObj.setTokenstatus(CreditMessageTokenConstants.STATUS_ACTIVE);
            tokenObj.setTokencredatetime(creTime);
            tokenObj.setIdentityuuid(requestMessage.getHeader().getIdentityUUID());
            //tokenObj.setTermid(requestMessage.getHeader().getTermId());
            //tokenObj.setCustomerid(requestMessage.getHeader().getCustomerID());
            //tokenObj.setMedia(requestMessage.getRequest().get(0).getMedia());
            //tokenObj.setAccount(requestMessage.getRequest().get(0).getAccount());

            dataInsertedFlg = tokenServiceDAO.insertTokenDetails(tokenObj);
            if (dataInsertedFlg) {
                LOG.info("Data Inserted in table stargate.crosssiterequesttokentable successfully!");
            } else {
                LOG.info("Data Insertion in table stargate.crosssiterequesttokentable FAILED!");
            }
            //responseMessage = mapResponse(requestMessage, tokenNumber, dataInsertedFlg);
        } catch (Exception ex) {
            LOG.error("Error while creating cross site request token " + ex.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        }
        LOG.info("Method " + sMethodName + " ENDED." + " Class Name " + CLASS_NAME);
        return tokenNumber;
    }

//    private Message mapResponse(Message cm, String tokenNumber, boolean dataInsertedFlg) {
//        sMethodName = "mapResponse";
//        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
//        Response response = new Response();
//        cm.getRequest().clear();
//        cm.getResponse().clear();
//        if(dataInsertedFlg){
//            //response.setReasonCode("01");
//            //response.setResponseType("Approved");
//            response.setTokenId(tokenNumber);
//            //response.setMedia(media);
//        }else{
//            //response.setReasonCode("03");
//            //response.setResponseType("Decline");
//            response.setTokenId("0");
//        }
//        
//        cm.getResponse().add(response);
//        LOG.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
//        return cm;
//    }
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
             throw new GatewayException("INTERNAL SYSTEM ERROR");
        }
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        return String.valueOf(nextToken);
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
    
    private boolean validateUserDetails(Message requestMessage) {
        sMethodName = "validateUserDetails";
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        boolean userValidated = false;
        try {
            if (tokenServiceDAO == null) tokenServiceDAO = new TokenServiceDAO();

            CrosssiteRequestUsertable tokenUserDetObj = new CrosssiteRequestUsertable();
            tokenUserDetObj.setIdentityuuid(requestMessage.getHeader().getIdentityUUID());
            tokenUserDetObj.setUserid(requestMessage.getHeader().getUerId());
            tokenUserDetObj.setPassword(requestMessage.getHeader().getPassword());
            
            userValidated = tokenServiceDAO.validateUserDetails(tokenUserDetObj);

            if (userValidated) LOG.info("User Validated successfully!");
            else LOG.info("User Validation Failed!");
        } catch (Exception ex) {
            Logger.getLogger(CLASS_NAME).log(Level.SEVERE, null, ex);
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        }
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        return userValidated;
    }
}