/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.vision.simulator;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.Gateway;
import com.aafes.stargate.gateway.svs.NetworkMessageProcessor;
import com.aafes.stargate.util.ResponseType;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author uikuyr
 */
@Stateless
public class VisionGatewayStub extends Gateway {
     private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(VisionGatewayStub.class.getSimpleName());

    @Override
    public Transaction processMessage(Transaction t) {
        if (t.getComment().equalsIgnoreCase(ResponseType.APPROVED)) {
            LOGGER.info("responce code is approved and responce code is 100");
            t.setResponseType(ResponseType.APPROVED);
            t.setReasonCode("100");
        } else {
            LOGGER.info("responce code is declined and responce code is 200");
            t.setResponseType(ResponseType.DECLINED);
            t.setReasonCode("200");
        }
        LOGGER.debug("rrn number in VisionGatewayStub.processMessage is : "+t.getRrn());
        return t;
    }
    
}
