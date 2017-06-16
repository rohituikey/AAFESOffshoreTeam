/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.validatetoken;

import com.aafes.stargate.gateway.GatewayException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.LoggerFactory;

/**
 *
 * @author burangir
 */
public class TokenValidatorService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TokenValidatorService.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = TokenValidatorService.this.getClass().getSimpleName();
    DateFormat dateFormat = new SimpleDateFormat("HHmmss");
    long seconds = 0L, configuredTimeOut = 0L;
    
    public boolean validateToken(String tokenStr) {
        sMethodName = "preAuth";
        Date inputTime = null, currentTime = null;
        
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        try {
            if (null != tokenStr) {
                inputTime = dateFormat.parse(tokenStr);
                currentTime = dateFormat.parse(getLocalTime(new Date()));
                
                if(inputTime.after(currentTime))
                    seconds = (inputTime.getTime() - currentTime.getTime())/1000;
                else if(currentTime.after(inputTime))
                    seconds = (currentTime.getTime() - inputTime.getTime())/1000;
            }

        } catch (Exception e) {
            LOGGER.error("Exception occured in " + sMethodName + ". Exception  : " + e.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        }

        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        return false;
    }

    private String getLocalTime(Date dateObj) {
        try {
            SimpleDateFormat smpLocalDate = new SimpleDateFormat("HHmmss");
            return smpLocalDate.format(dateObj);
        } catch (Exception dateExc) {
            LOGGER.error(dateExc.getMessage());
        }
        return "";
    }
}
