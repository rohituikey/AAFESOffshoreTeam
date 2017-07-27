/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.Gateway;
import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.gateway.vision.Validator;
import com.aafes.stargate.gateway.vision.VisionGateway;
import com.aafes.stargate.util.InputType;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.RequestType;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author singha
 */
@Stateless
public class WexGateway extends Gateway {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(WexGateway.class.getSimpleName());
    private Transaction t;

    @Override
    public Transaction processMessage(Transaction transaction) {
        t = transaction;
        
        LOG.info("inside procesMessage() method of wexGateway class");
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
