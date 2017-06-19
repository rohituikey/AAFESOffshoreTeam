/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.vision.simulator;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.Gateway;
import com.aafes.stargate.util.ResponseType;
import java.util.logging.Logger;
import javax.ejb.Stateless;

/**
 *
 * @author uikuyr
 */
@Stateless
public class VisionGatewayStub extends Gateway {
    
    @Override
    public Transaction processMessage(Transaction t) {
        if (t.getComment().equalsIgnoreCase(ResponseType.APPROVED)) {
            
            t.setResponseType(ResponseType.APPROVED);
            t.setReasonCode("100");
        } else {
            t.setResponseType(ResponseType.DECLINED);
            t.setReasonCode("200");
        }
        return t;
    }
    
}
