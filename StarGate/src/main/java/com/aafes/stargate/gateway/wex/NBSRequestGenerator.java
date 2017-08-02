/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.nbslogonrequestschema.NbsLogonRequest;
import com.aafes.nbsresponseacknowledgmentschema.ResponseAcknowlegment;
import com.aafes.stargate.util.ResponseType;
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

    public ResponseAcknowlegment unmarshalResponseAcknowledgment(String response) {

        try {
            isoMsg = new ISOMsg();
            GenericPackager genericPackager = new GenericPackager("src/main/resources/xml/ResponseAcknowledgment.xml");
            isoMsg.setPackager(genericPackager);
            isoMsg.unpack(response.getBytes());
            if(isoMsg.getString(10).trim().equalsIgnoreCase("c$"))
            responseAcknowlegment.setResponseType(ResponseType.APPROVED);
            else if(isoMsg.getString(10).trim().equalsIgnoreCase("c?"))
            responseAcknowlegment.setResponseType(ResponseType.DECLINED);
            responseAcknowlegment.setReasonCode(isoMsg.getString(11));
            return responseAcknowlegment;
        } catch (ISOException ex) {
            Logger.getLogger(NBSRequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex){
            Logger.getLogger(NBSRequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return responseAcknowlegment;
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
