/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.authorizer;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.Gateway;
import com.aafes.stargate.gateway.GatewayFactory;
import javax.ejb.EJB;

/**
 *
 * @author Ganji
 */
public abstract class BaseStrategy {

    @EJB
    private GatewayFactory gatewayFactory;

    public abstract Transaction processRequest(Transaction t);

    public Gateway pickGateway(Transaction t) {
        return gatewayFactory.pickGateway(t);
    }

    public void setGatewayFactory(GatewayFactory gatewayFactory) {
        this.gatewayFactory = gatewayFactory;
    }

}
