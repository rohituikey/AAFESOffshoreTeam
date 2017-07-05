/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.fdms;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.TranRepository;
import javax.ejb.EJB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author uikuyr
 */
public class InitiateReversal implements Runnable {

    @EJB
    private CompassGatewayProcessor cgp;
    @EJB
    private TranRepository transactionRepository;

    private Transaction t;

    private static final Logger LOG
            = LoggerFactory.getLogger(InitiateReversal.class.getSimpleName());

    public InitiateReversal(Transaction t) {
        this.t = t;
    }

    @Override
    public void run() {
        try {
            t = cgp.execute(t);
        } catch (Exception e) {
            LOG.error("Exception occured while initiaing reversal " + e.getMessage());
        }
        transactionRepository.save(t);
    }

    public void setCgp(CompassGatewayProcessor cgp) {
        this.cgp = cgp;
    }

    public void setTransactionRepository(TranRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

}
