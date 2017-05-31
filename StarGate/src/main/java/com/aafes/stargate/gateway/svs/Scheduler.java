/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.svs;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.util.ResponseType;
import java.util.TimerTask;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alugumetlas
 */
public class Scheduler extends TimerTask {

    private static final org.slf4j.Logger log
            = LoggerFactory.getLogger(Scheduler.class.getSimpleName());
    private NetworkMessageProcessor networkMessageProcessor = new NetworkMessageProcessor();
    private ProcessorFactory processorFactory = new ProcessorFactory();
    private SVSGatewayProcessor svsgp = new SVSGatewayProcessor();
    private Transaction transaction = new Transaction();
    private SVSGateway sVSGateway = new SVSGateway();

    public Scheduler() {
        processorFactory.setNetworkMessageProcessor(networkMessageProcessor);
        svsgp.setProcessorFactory(processorFactory);
        sVSGateway.setSvsgp(svsgp);

    }

    public void run() {
        log.info("its run scheduler    ..");

        networkMessageProcessor = new NetworkMessageProcessor();
        Transaction t = sVSGateway.processMessage(transaction);

        if (t.getResponseType() == (ResponseType.APPROVED)) {
            log.info("$$$$ Network request get approval  ");

        } else {
            log.info("**** Network request denied");
        }

    }

    
    
    
    
    
    
    
    
    
    
//    public static void main(String[] args) {
////        log.info("its main scheduler    ..");
//        System.out.println("its main scheduler    ..");
//
//        Timer time = new Timer();
//        Scheduler scheduler = new Scheduler();
//        time.schedule(scheduler, 0, 10000);
//        //Thread.sleep(10000);
//    }
}
