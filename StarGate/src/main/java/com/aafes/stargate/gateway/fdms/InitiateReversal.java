/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.fdms;

import com.aafes.stargate.authorizer.entity.Transaction;
import javax.ejb.EJB;

/**
 *
 * @author uikuyr
 */
public class InitiateReversal implements Runnable {

    @EJB
    private CompassGatewayProcessor cgp;
    Transaction t;

    public void setCgp(CompassGatewayProcessor cgp) {
        this.cgp = cgp;
    }

    public InitiateReversal(Transaction t) {
        this.t = t;
    }

    @Override
    public void run() {
        try {
            cgp.execute(t);
        } catch (Exception e) {
        }
    }

}
