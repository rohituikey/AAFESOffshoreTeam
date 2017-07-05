/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.authorizer;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.Gateway;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ganjis
 */
@Stateless
public class MPGStrategy extends BaseStrategy {

        private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(MPGStrategy.class.getSimpleName());

    @Override
    public Transaction processRequest(Transaction t) {
        LOG.info(" MPGStrategy class ,process requst method is started");

        boolean mpgFieldsValid = this.validateMPGFields(t);
        if (!mpgFieldsValid) {
            return t;
        }

        //Send transaction to Gateway
        Gateway gateway = super.pickGateway(t);
        if (gateway != null) {
            t = gateway.processMessage(t);
        }
        LOG.debug("rrn number in MPGStrategy.processRequst is : "+t.getRrn());
        LOG.info(" MPGStrategy class ,process requst method is ended");
        return t;
    }

    public boolean validateMPGFields(Transaction t) {

        // Validate only fields which are required for MPG 
        return true;
    }

}
