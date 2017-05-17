/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.authorizer;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.Gateway;
import javax.ejb.Stateless;

/**
 *
 * @author ganjis
 */
@Stateless
public class MPGStrategy extends BaseStrategy {

    @Override
    public Transaction processRequest(Transaction t) {

        boolean mpgFieldsValid = this.validateMPGFields(t);
        if (!mpgFieldsValid) {
            return t;
        }

        //Send transaction to Gateway
       
        Gateway gateway = super.pickGateway(t);
        if (gateway != null) {
            t = gateway.processMessage(t);
           
        }

        return t;
    }

    public boolean validateMPGFields(Transaction t) {

        // Validate only fields which are required for MPG 
        return true;
    }

}
