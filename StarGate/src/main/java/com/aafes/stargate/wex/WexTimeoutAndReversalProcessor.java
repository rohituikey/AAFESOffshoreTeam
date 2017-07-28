/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.wex;

import com.aafes.stargate.control.Configurator;
import javax.ejb.EJB;
import org.slf4j.LoggerFactory;

/**
 *
 * @author burangir
 */
public class WexTimeoutAndReversalProcessor {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WexTimeoutAndReversalProcessor.class.getSimpleName());
    private String sMethodName = "";
    private final String CLASS_NAME = WexTimeoutAndReversalProcessor.this.getClass().getSimpleName();
    private int wexTimeoutValue = 0;
    private int wexRetryCount = 0;
    
    @EJB
    private Configurator configurator;
    
    private void readTimeoutReversalProperties(){
        sMethodName = "readTimeoutReversalProperties";
        LOGGER.info("Method " + sMethodName + " started." + "in  Class Name " + CLASS_NAME);
        
        if(configurator != null){
            if(configurator.get("WEX_REQUEST_TIMEOUT") != null)
                wexTimeoutValue = Integer.parseInt(configurator.get("WEX_REQUEST_TIMEOUT"));
            else LOGGER.error("stargate.properties do not have value WEX_REQUEST_TIMEOUT. Please add it.");

            if(configurator.get("WEX_RETRY_COUNT") != null)
                wexRetryCount = Integer.parseInt(configurator.get("WEX_RETRY_COUNT"));
            else LOGGER.error("stargate.properties do not have value WEX_RETRY_COUNT. Please add it.");
        LOGGER.error("configurator object is null. Can not continue with this class.");
        }
        LOGGER.info("Method " + sMethodName + " ended." + "in  Class Name " + CLASS_NAME);
    }
    
    /**
     * @param configurator the configurator to set
     */
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }
}