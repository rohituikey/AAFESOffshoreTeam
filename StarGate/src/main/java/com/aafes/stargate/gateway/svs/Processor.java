/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.svs;

import com.aafes.stargate.authorizer.entity.Transaction;
import javax.ejb.EJB;

/**
 *
 * @author ganjis
 */
public abstract class Processor {

    
    @EJB
    private ProcessorFactory processorFactory;

    public abstract void processRequest(Transaction t);

   
    public ProcessorFactory getProcessorFactory() {
        return processorFactory;
    }

    public void setProcessorFactory(ProcessorFactory processorFactory) {
        this.processorFactory = processorFactory;
    }
    
}
