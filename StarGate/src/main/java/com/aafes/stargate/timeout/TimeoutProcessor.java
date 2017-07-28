/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.timeout;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.ResponseType;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author uikuyr
 */
@Stateless
public class TimeoutProcessor {

    @EJB
    private Configurator configurator;
    
    private Transaction t = new Transaction();

    public Transaction processResponse(Transaction t) {

        //visionGateway
        switch (t.getMedia()) {
            case MediaType.MIL_STAR:
                handleVisionGateway(t);
            case MediaType.VISA:
            case MediaType.MASTER:
            case MediaType.DISCOVER:
            case MediaType.AMEX:
                handleCompassGateway(t);
        }
        return t;
    }

    private void handleVisionGateway(Transaction t) {

        if (t.getResponseType().equalsIgnoreCase(ResponseType.DECLINED)
                && "20001".equalsIgnoreCase(t.getPlanNumber())
                && ("364".equalsIgnoreCase(t.getReasonCode())
                || "190".equalsIgnoreCase(t.getReasonCode())
                || "023".equalsIgnoreCase(t.getReasonCode()))) {
            t.setResponseType(ResponseType.TIMEOUT);
            t.setReasonCode(configurator.get("TIMEOUT_EXCEPTION"));
        }
    }

    private void handleCompassGateway(Transaction t) {

        if ((ResponseType.DECLINED.equalsIgnoreCase(t.getResponseType())) && (t.getReasonCode().equals("000"))) {
            t.setResponseType(ResponseType.TIMEOUT);
            t.setReasonCode(configurator.get("TIMEOUT_EXCEPTION"));
        }
    }

    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }
    
}
