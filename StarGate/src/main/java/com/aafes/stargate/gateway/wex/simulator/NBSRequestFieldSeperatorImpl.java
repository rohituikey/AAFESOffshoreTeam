/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex.simulator;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.EJB;


/**
 *
 * @author uikuyr
 */
public class NBSRequestFieldSeperatorImpl {
    private String applicationName;
    private String applicationVersion;
    private String daylightSavingsTimeAtSiteOne;
    private String captureOnlyRequest;
    private String sessionTypeAuth;
    private String transTypePreAuth;
    private String transTypeFinalAndSale;
    private String transTypeRefund;
    private String cardTypeWex;
    private String serviceType;
    private int index = 0;
    String[] productDetails;
    @EJB
    private Configurator configurator;
    
    public String createAsciiForNBS(Transaction t){
        if (configurator == null) {
            configurator = new Configurator();
            configurator.postConstruct();
        }
        
        applicationName = configurator.get("APPLICATION_NAME");
        applicationVersion = configurator.get("APPLICATION_VERSION");
        daylightSavingsTimeAtSiteOne = configurator.get("DAYLIGHT_SAVINGS_TIME_AT_SITE_ONE");
        captureOnlyRequest = configurator.get("CAPTURE_ONLY_REQUEST");
        sessionTypeAuth = configurator.get("SESSION_TYPE_AUTH");
        transTypePreAuth = configurator.get("TRANS_TYPE_PRE_AUTH");
        transTypeFinalAndSale = configurator.get("TRANS_TYPE_FINAL_AND_SALE");
        transTypeRefund = configurator.get("TRANS_TYPE_REFUND");
        cardTypeWex = configurator.get("CARD_TYPE_WEX");
        serviceType = configurator.get("SERVICE_TYPE");
        String request = "<SX>"+t.getTermId()+"<FS>"+applicationName+"<FS>"+applicationVersion+"<FS>"+createDateFormat()
                +"<FS>"+sessionTypeAuth+"<FS>"+t.getTransactionId().substring(0, 4)+"<FS>"+transTypePreAuth+"<FS>"+cardTypeWex+"<FS>"+
                t.getCatFlag()+"<FS>"+t.getPumpNmbr()+"<FS>"+serviceType+"<FS>"+"2"+"<FS>"+t.getTrack2()+"<FS>"
                +t.getAmount()+"<FS>"+t.getPromptDetailCount().toString()+"<FS>"+"1"+"<FS>"+t.getVehicleId()+"<FS>"+"3"+"<FS>"
                +t.getDriverId()+"<FS>"+"4"+"<FS>"+t.getOdoMeter()+"<FS>"+t.getProdDetailCount()+"<FS>"+t.getProducts().get(2)
                +"<FS>"+t.getProducts().get(1)+"<FS>"+t.getProducts().get(0)+"<FS>"+t.getProducts().get(3)+"<SX>";
    return request;
    }
    private String createDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date();
        String ts = dateFormat.format(date);
        //2017-08-08 08:39:30.967
        ts = ts.substring(11, 13) + ts.substring(14, 16) + daylightSavingsTimeAtSiteOne;
        return ts;
    }
    public Configurator getConfigurator() {
        return configurator;
    }

    /**
     * @param configurator the configurator to set
     */
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }
    
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
    
    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }
    
    public void setDaylightSavingsTimeAtSiteOne(String daylightSavingsTimeAtSiteOne) {
        this.daylightSavingsTimeAtSiteOne = daylightSavingsTimeAtSiteOne;
    }
    
    public void setCaptureOnlyRequest(String captureOnlyRequest) {
        this.captureOnlyRequest = captureOnlyRequest;
    }
    
    public void setSessionTypeAuth(String sessionTypeAuth) {
        this.sessionTypeAuth = sessionTypeAuth;
    }
    
    public void setTransTypePreAuth(String transTypePreAuth) {
        this.transTypePreAuth = transTypePreAuth;
    }
    
    public void setTransTypeFinalAndSale(String transTypeFinalAndSale) {
        this.transTypeFinalAndSale = transTypeFinalAndSale;
    }
    
    public void setTransTypeRefund(String transTypeRefund) {
        this.transTypeRefund = transTypeRefund;
    }
    
    public void setCardTypeWex(String cardTypeWex) {
        this.cardTypeWex = cardTypeWex;
    }
    
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
}

