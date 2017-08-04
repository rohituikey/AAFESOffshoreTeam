/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.nbslogonrequestschema.NbsLogonRequest;
import com.aafes.nbsresponse.NBSResponse;
import com.aafes.nbsresponseacknowledgmentschema.ResponseAcknowlegment;
import com.aafes.stargate.util.ResponseType;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

/**
 *
 * @author uikuyr
 */
public class NBSRequestGenerator {

    private String iso8583Format;
    private int promptCountIndex;
    private ISOMsg isoMsg;
    private GenericPackager packager;
    private ResponseAcknowlegment responseAcknowlegment;
    private NBSResponse nBSResponse;

    public String generateLogOnPacketRequest(NbsLogonRequest nbsLogonRequest) {

        try {
            packager = new GenericPackager("src/main/resources/xml/NBSLogonPackager.xml");
            isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0231");
            isoMsg.set(10, nbsLogonRequest.getTermId());
            isoMsg.set(12, nbsLogonRequest.getAppName());
            isoMsg.set(13, nbsLogonRequest.getAppVersion().toString());
            isoMsg.set(14, nbsLogonRequest.getTimeZone().toString());
            isoMsg.set(15, nbsLogonRequest.getHeaderRecord().getA());
            isoMsg.set(16, nbsLogonRequest.getHeaderRecord().getKey().toString());
            isoMsg.set(17, nbsLogonRequest.getHeaderRecord().getTransType().toString());
            isoMsg.set(18, nbsLogonRequest.getHeaderRecord().getCardType());
            isoMsg.set(19, nbsLogonRequest.getHeaderRecord().getCATFlag().toString());
            isoMsg.set(110, nbsLogonRequest.getHeaderRecord().getPumpNo());
            isoMsg.set(111, nbsLogonRequest.getHeaderRecord().getServiceType().toString());
            isoMsg.set(112, nbsLogonRequest.getHeaderRecord().getTrack().toString());
            isoMsg.set(113, nbsLogonRequest.getHeaderRecord().getCardSpecificData().getAcctInfo());
            isoMsg.set(114, nbsLogonRequest.getHeaderRecord().getCardSpecificData().getAmount().toString());

            if (nbsLogonRequest.getHeaderRecord().getTransType().equals(10) || nbsLogonRequest.getHeaderRecord().getTransType().equals(30)) {
                isoMsg.set(115, (nbsLogonRequest.getHeaderRecord().getCardSpecificData().getRecieptNumber().toString()));
            }

//            isoMsg.set(15, nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexPromptDetails().getPromptDetailCount().toString());
//            for (promptCountIndex=16 ; promptCountIndex  <  (nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexPromptDetails().getPromptDetailCount().getPromptTypeOrPromptValue().size()); promptCountIndex++) {
//                 isoMsg.set(promptCountIndex, (nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexPromptDetails().getPromptDetailCount().getPromptTypeOrPromptValue().get(promptCountIndex)).toString());
//            }
//            isoMsg.set(promptCountIndex+1, nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexProductDetails().getProdDetailCount().toString());
//            for (promptCountIndex=promptCountIndex+2 ; promptCountIndex  <  (nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexProductDetails().getProdDetailCount().getPriceOrQuantityOrProductCode().size()); promptCountIndex++) {
//                 isoMsg.set(promptCountIndex, (nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexProductDetails().getProdDetailCount().getPriceOrQuantityOrProductCode().get(promptCountIndex)).toString());
//            }
            byte[] data = isoMsg.pack();
            iso8583Format = new String(data);
            System.out.println("output for NBS Iso 8583 format= " + iso8583Format);
            return iso8583Format;
        } catch (ISOException ex) {
            Logger.getLogger(NBSRequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return iso8583Format;
    }

    public String[] seperateResponse(String response) {
        String[] result = {"", ""};
        String mTI = response.substring(0, 4);
        result[0] = response.substring(0, response.substring(4).indexOf(mTI) + 4);
        result[1] = response.substring(result[0].length());
        return result;
    }

    public ResponseAcknowlegment unmarshalAcknowledgment(String response) {

        try {
            isoMsg = new ISOMsg();
            GenericPackager genericPackager;
            String SCHEMA_PATH = "src/main/resources/xml/ResponseAcknowledgment.xml";
            
            try {
                genericPackager = new GenericPackager(SCHEMA_PATH);
            } catch (Exception e) {
                SCHEMA_PATH = System.getProperty("jboss.server.config.dir") + "/ResponseAcknowledgment.xml";
                genericPackager = new GenericPackager(SCHEMA_PATH);
            }
            
            isoMsg.setPackager(genericPackager);
            isoMsg.unpack(response.getBytes());
            if (isoMsg.getString(10).trim().equalsIgnoreCase("c$")) {
                responseAcknowlegment.setResponseType(ResponseType.APPROVED);
            } else if (isoMsg.getString(10).trim().equalsIgnoreCase("c?")) {
                responseAcknowlegment.setResponseType(ResponseType.DECLINED);
            }
            responseAcknowlegment.setReasonCode(isoMsg.getString(11));
            return responseAcknowlegment;
        } catch (ISOException ex) {
            Logger.getLogger(NBSRequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(NBSRequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return responseAcknowlegment;
    }

    public NBSResponse unmarshalNbsResponse(String response) {
        try {
            isoMsg = new ISOMsg();
            nBSResponse = new NBSResponse();
            NBSResponse.AuthResponse authResponse = new NBSResponse.AuthResponse();
            NBSResponse.AuthResponse.PromptTypeDetails promptType = new NBSResponse.AuthResponse.PromptTypeDetails();
            NBSResponse.AuthResponse.ProductDetails productDetails = new NBSResponse.AuthResponse.ProductDetails();

            GenericPackager genericPackager = new GenericPackager("src/main/resources/xml/NBSResponse.xml");
            isoMsg.setPackager(genericPackager);
            isoMsg.unpack(response.getBytes());

            promptType.setPromptType(isoMsg.getString(23));
            promptType.setAuthRef(isoMsg.getString(24));
            promptType.setMaxAmount(new BigDecimal(isoMsg.getString(25)));
            promptType.setProductAuthCount(new BigInteger(isoMsg.getString(26)));

            productDetails.setMaxQuantity(new BigDecimal(isoMsg.getString(27)));
            productDetails.setProductCode(new BigInteger(isoMsg.getString(28)));
            productDetails.setMaxAmount(new BigDecimal(isoMsg.getString(29)));

            authResponse.setMessage(isoMsg.getString(15));
            authResponse.setCardType(isoMsg.getString(16));
            authResponse.setIdentity(isoMsg.getString(17));
            authResponse.setHostNumber(isoMsg.getString(18));
            authResponse.setCardNumber(isoMsg.getString(19));
            authResponse.setVehicleNumber(new BigInteger(isoMsg.getString(20)));
            authResponse.setServiceOption(new BigInteger(isoMsg.getString(21)));
            authResponse.setPromptCount(new BigInteger(isoMsg.getString(22)));
            authResponse.setProductDetails(productDetails);
            authResponse.setPromptTypeDetails(promptType);

            nBSResponse.setA(isoMsg.getString(10));
            nBSResponse.setKey(new BigInteger(isoMsg.getString(11)));
            nBSResponse.setApplicationUpdateNeeded(isoMsg.getString(12));
            nBSResponse.setAuthCode(new BigInteger(isoMsg.getString(13)));
            nBSResponse.setA(isoMsg.getString(14));
            nBSResponse.setAuthResponse(authResponse);

            return nBSResponse;
        } catch (ISOException ex) {
            Logger.getLogger(NBSRequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nBSResponse;
    }

    public String logOffRequest() {
        String result = "";
        try {
            isoMsg = new ISOMsg();
            GenericPackager packager = new GenericPackager("src/main/resources/xml/NBSLogOff.xml");
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0231");
            isoMsg.set(10, "O");
            byte[] data = isoMsg.pack();
            result = new String(data);
            return result;
        } catch (ISOException ex) {
            Logger.getLogger(NBSRequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public NBSResponse getnBSResponse() {
        return nBSResponse;
    }

    public void setnBSResponse(NBSResponse nBSResponse) {
        this.nBSResponse = nBSResponse;
    }

    public String getIso8583Format() {
        return iso8583Format;
    }

    public void setIso8583Format(String iso8583Format) {
        this.iso8583Format = iso8583Format;
    }

    public int getPromptCountIndex() {
        return promptCountIndex;
    }

    public void setPromptCountIndex(int promptCountIndex) {
        this.promptCountIndex = promptCountIndex;
    }

    public ISOMsg getIsoMsg() {
        return isoMsg;
    }

    public void setIsoMsg(ISOMsg isoMsg) {
        this.isoMsg = isoMsg;
    }

    public GenericPackager getPackager() {
        return packager;
    }

    public void setPackager(GenericPackager packager) {
        this.packager = packager;
    }

    public ResponseAcknowlegment getResponseAcknowlegment() {
        return responseAcknowlegment;
    }

    public void setResponseAcknowlegment(ResponseAcknowlegment responseAcknowlegment) {
        this.responseAcknowlegment = responseAcknowlegment;
    }

}
