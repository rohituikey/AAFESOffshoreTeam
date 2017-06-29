/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.fdms;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.TranRepository;
import javax.ejb.EJB;

/**
 *
 * @author uikuyr
 */
public class InitiateReversal implements Runnable {

    @EJB
    private CompassGatewayProcessor cgp;
    @EJB
    private TranRepository transactioRepository;

    private Transaction t;

    public InitiateReversal(Transaction t) {
        this.t = t;
    }

    @Override
    public void run() {
        try {
            cgp.execute(t);
            transactioRepository.save(t);
        } catch (Exception e) {
        }
    }

    public void setCgp(CompassGatewayProcessor cgp) {
        this.cgp = cgp;
    }

    public void setTransactioRepository(TranRepository transactioRepository) {
        this.transactioRepository = transactioRepository;
    }

}
