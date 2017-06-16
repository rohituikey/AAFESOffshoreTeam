/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.fdms.CompassGateway;
import com.aafes.stargate.gateway.svs.SVSGateway;
import com.aafes.stargate.gateway.vision.VisionGateway;
import com.aafes.stargate.gateway.vision.simulator.VisionGatewayStub;
import com.aafes.stargate.util.GetMediaTypeByAccountNbr;
import com.aafes.stargate.util.MediaType;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author pkalpesh
 */
@Stateless
public class GatewayFactory {

    @EJB
    private VisionGateway visionGateway;

    @EJB
    private CompassGateway compassGateway;

    @EJB
    private SVSGateway sVSGateway;

    @Inject
    private String enableStub;

    @EJB
    private VisionGatewayStub visionGatewaySimulator;

    public Gateway pickGateway(Transaction t) {
        System.out.println("Inside Base");
        Gateway gateway = null;
        String mediaType = t.getMedia();

        if (mediaType == null || mediaType.isEmpty()) {
            mediaType = GetMediaTypeByAccountNbr.getCardType(t.getAccount());
        }

        System.out.println(mediaType);
        if (mediaType != null) {
            t.setMedia(mediaType);
            switch (mediaType) {
                case MediaType.MIL_STAR:
                    if (enableStub != null && enableStub.equalsIgnoreCase("true")) {
                        gateway = visionGatewaySimulator;
                    } else {
                        gateway = visionGateway;
                    }
                    return gateway;
                case MediaType.VISA:
                case MediaType.MASTER:
                case MediaType.DISCOVER:
                case MediaType.AMEX:
                    gateway = compassGateway;
                    return gateway;

                case MediaType.GIFT_CARD:
                    gateway = sVSGateway;
                    return gateway;
                // Add more gateways
            }
        }

        return null;
    }

    void setVisionGateway(VisionGateway visionGateway) {
        this.visionGateway = visionGateway;
    }

    public void setCompassGateway(CompassGateway compassGateway) {
        this.compassGateway = compassGateway;
    }

    public void setEnableStub(String enableStub) {
        this.enableStub = enableStub;
    }

}
