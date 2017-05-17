/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.svs;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.Gateway;
import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.util.ResponseType;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ganjis
 */
@Stateless
public class SVSGateway extends Gateway {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(SVSGateway.class.getSimpleName());

    @EJB
    private SVSGatewayProcessor svsgp;

    @Override
    public Transaction processMessage(Transaction t) {

        try {
            this.validateTransaction(t);
            if (svsgp != null) {
                t = svsgp.execute(t);
            } else {
                t.setResponseType(ResponseType.DECLINED);
                t.setDescriptionField("INTERNAL SERVER ERROR");
                return t;
            }
        } catch (GatewayException e) {
            t.setResponseType(ResponseType.DECLINED);
            t.setDescriptionField(e.getMessage());
        }

        return t;
    }

    private void validateTransaction(Transaction t) throws GatewayException {

    }

    public void setSvsgp(SVSGatewayProcessor svsgp) {
        this.svsgp = svsgp;
    }

}
