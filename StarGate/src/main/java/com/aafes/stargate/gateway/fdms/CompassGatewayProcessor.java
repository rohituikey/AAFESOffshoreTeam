package com.aafes.stargate.gateway.fdms;

import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.util.AVSResponseReasonCode;
import com.aafes.stargate.util.Encryptor;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
import com.firstdata.cmpwsapi.schemas.cmpapi.CMPWSApi;
import com.firstdata.cmpwsapi.schemas.cmpapi.CMPWSApiService;
import com.firstdata.cmpwsapi.schemas.cmpapi.OnlineTransRequest;
import com.firstdata.cmpwsapi.schemas.cmpapi.OnlineTransResponse;
import com.firstdata.cmpwsapi.schemas.cmpmsg.ContactAddress;
import com.firstdata.cmpwsapi.schemas.cmpmsg.FR;
import com.firstdata.cmpwsapi.schemas.cmpmsg.OnlineAF;
import com.firstdata.cmpwsapi.schemas.cmpmsg.PA;
import com.firstdata.cmpwsapi.schemas.cmpmsg.Transaction;
//import com.sun.xml.internal.ws.client.BindingProviderProperties;

import java.net.URI;
import java.net.URL;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pkalpesh
 */
@Stateless
public class CompassGatewayProcessor {

    @Inject
    private String compassUser;

    @Inject
    private String compassPassword;

    @Inject
    private String wsdlLocation;

    @Inject
    private String fdmsTimeout;

    @EJB
    private Configurator configurator;

    private final static QName CMPWSAPISERVICE_QNAME = new QName("http://firstdata.com/cmpwsapi/schemas/cmpapi", "CMPWSApiService");

    OnlineTransResponse result = new OnlineTransResponse();
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CompassGatewayProcessor.class.getSimpleName());

    public CompassGatewayProcessor() {
    }

    @PostConstruct
    public void PostContruct() {
        try {
            log.info("Decrypting passwords");
            decryptValues();
        } catch (Exception ex) {
            log.error("Unable to Decrypt values" + ex.getMessage());
        }
    }

    @SuppressWarnings("UnusedAssignment")
    public com.aafes.stargate.authorizer.entity.Transaction execute(com.aafes.stargate.authorizer.entity.Transaction t) {
            log.info("CompassGatewayProcessor#execute.....started..");
        try {

            OnlineTransRequest otr = formOnlineTransRequest(t);
            log.info("CompassGatewayProcessor#execute#OnlineTransResponse......");
            /**
             * SOAP calling logic
             */

            URI wsdl = new URI(wsdlLocation);
            URL wsdlLocation = wsdl.toURL();
            CMPWSApiService service = new CMPWSApiService(wsdlLocation, CMPWSAPISERVICE_QNAME);
            log.info("Service created.......");

            CMPWSApi port = service.getCMPWSApiPort();
            log.info("Port created.......");

            Map<String, Object> requestContext
                    = ((BindingProvider) port).getRequestContext();
            requestContext.put(BindingProvider.USERNAME_PROPERTY, compassUser);
            requestContext.put(BindingProvider.PASSWORD_PROPERTY, compassPassword);

            requestContext.put("com.sun.xml.internal.ws.request.timeout", Integer.valueOf(fdmsTimeout));
            requestContext.put("com.sun.xml.internal.ws.connect.timeout", Integer.valueOf(fdmsTimeout));

            //result = port.onlineTrans(otr);
            log.info("got result.......");
            mapResponse(result, t);

        } catch (GatewayException e) {
            log.error("CompassGatewayProcessor#execute#Exception : " + e.toString());
            t.setDescriptionField(e.getMessage());
            t.setResponseType(ResponseType.DECLINED);
            t.setReasonCode(configurator.get(e.getMessage()));
//            mapResponse(result, t);
            return t;
        } //        } catch (com.sun.xml.internal.ws.client.ClientTransportException e) {
        //            log.error("CompassGatewayProcessor#execute#Exception : " + e.toString());
        //            t.setReasonCode(configurator.get("TIMEOUT_EXCEPTION"));
        //            t.setResponseType(ResponseType.TIMEOUT);
        //            t.setDescriptionField(e.getMessage());
        //            return t;
        //        } 
        catch (WebServiceException e) {
            log.error("CompassGatewayProcessor#execute#Exception : " + e.toString()+"   reason is TIMEOUT_EXCEPTION");
            t.setReasonCode(configurator.get("TIMEOUT_EXCEPTION"));
            t.setResponseType(ResponseType.TIMEOUT);
            t.setDescriptionField(e.getMessage());
            return t;
        } catch (Exception e) {
            log.error("CompassGatewayProcessor#execute#Exception : " + e.toString()+"  reason is INVALID_COMPASS_ENDPOINT");
            t.setDescriptionField("INVALID_COMPASS_ENDPOINT");
            t.setResponseType(ResponseType.DECLINED);
            t.setReasonCode(configurator.get("INVALID_COMPASS_ENDPOINT"));
//            mapResponse(result, t);
            return t;
        }
        log.info("CompassGatewayProcessor#execute.....ended..");
        log.debug("rrn number is " + t.getRrn());
        return t;
    }

    private OnlineTransRequest formOnlineTransRequest(com.aafes.stargate.authorizer.entity.Transaction t) {
        log.info("compassgateway processor's formOnlineTransRequest method started");
        OnlineTransRequest otr = new OnlineTransRequest();

        //<cmpmsg:Transaction>
        Transaction soapTran = new Transaction();
        String orderNumber = String.format("%-22s", t.getOrderNumber());
        soapTran.setOrderNumber(orderNumber);
        soapTran.setMop(mapRequestTypeToMop(t.getMedia()));

        String account = String.format("%-19s", t.getAccount());
        soapTran.setAccountNumber(account);

        if (t.getExpiration() != null) {
            String[] dates = {t.getExpiration().substring(0, 2), t.getExpiration().substring(2)};
            soapTran.setExpirationDate(dates[1] + dates[0]);
        }
        String divisionNumber = String.format("%010d", 805602);
        t.setDivisionnumber(divisionNumber);
        soapTran.setDivisionNumber(divisionNumber);

        long amount = t.getAmount();
        if (amount < 0) {
            amount = amount * -1;
        }
        String amountTr = String.format("%012d", amount);
        soapTran.setAmount(amountTr);

        String currencyCode = "840";
        t.setCurrencycode(currencyCode);
        soapTran.setCurrencyCode(currencyCode);

        String transactionType = "7";
        t.setTransactiontype(transactionType);
        soapTran.setTransactionType(transactionType);
        
        OnlineAF oaf = new OnlineAF();
        String actionCode = "";

        log.info("Reversal flag : " + t.getReversal());

        if (t.getReversal() != null
                && t.getReversal().equalsIgnoreCase(RequestType.REVERSAL)) {
            log.info("FDMS Reversal request.......");
            actionCode = "AR";
            t.setActioncode(actionCode);
            soapTran.setActionCode(actionCode);

            //<cmpmsg:PA> 
            PA pa = new PA();
            pa.setResponseDate(t.getResponseDate());
            pa.setAuthorizationCode(t.getAuthNumber());
            oaf.setPA(pa);
        } else {
            log.info("FDMS Auth request.......");
            actionCode = "AU";
            t.setActioncode(actionCode);
            soapTran.setActionCode(actionCode);

            //<cmpmsg:AB> 
            ContactAddress ab = new ContactAddress();
            String cardHolderName = t.getCardHolderName();
            if (cardHolderName != null) {
                String[] k = cardHolderName.split(",");

                try {
                    if (k.length > 1) {
                        cardHolderName = k[1].trim() + "*" + k[0].trim();
                    } else {
                        cardHolderName = "*" + k[0].trim();
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    log.info("FDMS Auth request Array Index Outr of bounds exception.......");
                }
                ab.setNameText(cardHolderName.toUpperCase());
            }
            if (t.getBillingPhone() != null
                    && !t.getBillingPhone().equals("")) {
                String phoneNumber = String.format("%-14s", t.getBillingPhone());
                ab.setTelephoneNumber(phoneNumber);
                String telePhoneType = "H";
                t.setTelephonetype(telePhoneType);
                ab.setTelephoneType(telePhoneType);
            }
            String address = t.getBillingAddress1();
            if (address == null) {
                address = "";
            }
            if (address != null
                    && address.length() > 30) {
                address = address.substring(0, 30);
            }
            address = String.format("%-30s", address);
            ab.setAddress1(address);

            String address2 = t.getBillingAddress2();
            if (address2 == null) {
                address2 = "";
            }
            if (address2 != null
                    && address2.length() > 30) {
                address2 = address2.substring(0, 30);
            }
            address2 = String.format("%-30s", address2);
            ab.setAddress2(address2);

            String zipCode = String.format("%-10s", t.getBillingZipCode());
            ab.setPostalCode(zipCode);
            ab.setCountryCode(t.getBillingCountryCode().toUpperCase());
            oaf.setAB(ab);

            //<cmpmsg:FR> 
            if (t.getCvv() != null
                    && (!t.getCvv().equals(""))) {
                FR fr = new FR();
                fr.setCardSecurityValue(t.getCvv());
                String cardSecurityPresence = getCardSecurityPresence(t.getMedia(), t.getCvv());
                t.setBillpaymentindicator(cardSecurityPresence);
                fr.setCardSecurityPresence(cardSecurityPresence);
                oaf.setFR(fr);
            }
        }
        otr.setAdditionalFormats(oaf);
        otr.setTransaction(soapTran);
        log.info("compassgateway processor's formOnlineTransRequest method started");
        return otr;
    }

    private String mapRequestTypeToMop(String mediaType) {
        log.info("CompassGatewayProcessor.mapRequestTypeToMap mediaType is : "+mediaType);
        switch (mediaType.toUpperCase()) {
            case "VISA":
                return "VI";
            case "MASTERCARD":
                return "MC";
            case "AMEX":
                return "AX";
            case "DISCOVER":
                return "DI";
        }
        return "";
    }

    private String getCardSecurityPresence(String mediaType,
            String cvv) {

        if (MediaType.AMEX.equalsIgnoreCase(mediaType)) {
            return " ";
        }

        if (MediaType.MASTER.equalsIgnoreCase(mediaType)
                && "999".equals(cvv)) {
            return " ";
        }

        return "1";
    }

    private void decryptValues() {
        String baseDir = System.getProperty("jboss.server.config.dir");
        String keysPath = baseDir + "/crypto/keys";
        String logPath = baseDir + "/crypto.log4j.properties";

        Encryptor encryptor = new Encryptor(keysPath, logPath);

        compassPassword = encryptor.decrypt(compassPassword);
    }

    public void setCompassUser(String compassUser) {
        this.compassUser = compassUser;
    }

    public void setCompassPassword(String compassPassword) {

        this.compassPassword = compassPassword;
    }

    private void mapResponse(OnlineTransResponse result,com.aafes.stargate.authorizer.entity.Transaction t) {
        log.info("CompassGatewayProcessor.mapResponce started");
        log.info("result.getResponseReasonCode() : " + result.getResponseReasonCode());

        t.setReasonCode(result.getResponseReasonCode());
        t.setResponseDate(result.getResponseDate());
        t.setAuthNumber(result.getAuthorizationCode());
        if (result.getAVSResponseCode().equalsIgnoreCase("i3")) {
            t.setAvsResponseCode(AVSResponseReasonCode.MATCHED);
        } else if (result.getAVSResponseCode().equalsIgnoreCase("i8")) {
            t.setAvsResponseCode(AVSResponseReasonCode.UNMATCHED);
        } else {
            t.setAvsResponseCode(AVSResponseReasonCode.NOTVERIFIED);
        }
        t.setCsvResponseCode(result.getCSVResponseCode());

        if (t.getReasonCode().equalsIgnoreCase("100")) {
            t.setResponseType(ResponseType.APPROVED);
            t.setDescriptionField(ResponseType.APPROVED);
        } else {
            t.setResponseType(ResponseType.DECLINED);
            t.setDescriptionField(ResponseType.DECLINED);
        }
        log.info("CompassGatewayProcessor.mapResponce ended");
    }

    /**
     * @param wsdlLocation the wsdlLocation to set
     */
    public void setWsdlLocation(String wsdlLocation) {
        this.wsdlLocation = wsdlLocation;
    }

    public void setFdmsTimeout(String fdmsTimeout) {
        this.fdmsTimeout = fdmsTimeout;
    }

}
