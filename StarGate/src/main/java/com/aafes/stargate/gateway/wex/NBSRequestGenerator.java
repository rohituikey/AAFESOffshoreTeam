/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.nbslogonrequestschema.NbsLogonRequest;
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
    private ISOMsg request;
    private GenericPackager packager;

    public String generateLogOnPacketRequest(NbsLogonRequest nbsLogonRequest) {

        try {
            packager = new GenericPackager("src/main/resources/xml/NBSLogonPackager.xml");

            request = new ISOMsg();
            request.setPackager(packager);
            request.setMTI("0231");
            request.set(10, nbsLogonRequest.getTermId());
            request.set(12, nbsLogonRequest.getAppName());
            request.set(13, nbsLogonRequest.getAppVersion().toString());
            request.set(14, nbsLogonRequest.getTimeZone().toString());
            request.set(15, nbsLogonRequest.getHeaderRecord().getA());
            request.set(16, nbsLogonRequest.getHeaderRecord().getKey().toString());
            request.set(17, nbsLogonRequest.getHeaderRecord().getTransType().toString());
            request.set(18, nbsLogonRequest.getHeaderRecord().getCardType());
            request.set(19, nbsLogonRequest.getHeaderRecord().getCATFlag().toString());
            request.set(110, nbsLogonRequest.getHeaderRecord().getPumpNo());
            request.set(111, nbsLogonRequest.getHeaderRecord().getServiceType().toString());
            request.set(112, nbsLogonRequest.getHeaderRecord().getTrack().toString());
            request.set(113, nbsLogonRequest.getHeaderRecord().getCardSpecificData().getAcctInfo());
            request.set(114, nbsLogonRequest.getHeaderRecord().getCardSpecificData().getAmount().toString());
//            request.set(15, nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexPromptDetails().getPromptDetailCount().toString());
//            for (promptCountIndex=16 ; promptCountIndex  <  (nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexPromptDetails().getPromptDetailCount().getPromptTypeOrPromptValue().size()); promptCountIndex++) {
//                 request.set(promptCountIndex, (nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexPromptDetails().getPromptDetailCount().getPromptTypeOrPromptValue().get(promptCountIndex)).toString());
//            }
//            request.set(promptCountIndex+1, nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexProductDetails().getProdDetailCount().toString());
//            for (promptCountIndex=promptCountIndex+2 ; promptCountIndex  <  (nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexProductDetails().getProdDetailCount().getPriceOrQuantityOrProductCode().size()); promptCountIndex++) {
//                 request.set(promptCountIndex, (nbsLogonRequest.getHeaderRecord().getCardSpecificData().getWexProductDetails().getProdDetailCount().getPriceOrQuantityOrProductCode().get(promptCountIndex)).toString());
//            }
            byte[] data = request.pack();
            iso8583Format = new String(data);
            System.out.println("output for NBS Iso 8583 format= " + iso8583Format);
            return iso8583Format;
        } catch (ISOException ex) {
            Logger.getLogger(NBSRequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return iso8583Format;
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

    public ISOMsg getRequest() {
        return request;
    }

    public void setRequest(ISOMsg request) {
        this.request = request;
    }

    public GenericPackager getPackager() {
        return packager;
    }

    public void setPackager(GenericPackager packager) {
        this.packager = packager;
    }

}
