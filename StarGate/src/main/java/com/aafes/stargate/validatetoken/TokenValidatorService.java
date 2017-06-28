/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.validatetoken;

import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.dao.TokenServiceDAO;
import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.util.CreditMessageTokenConstants;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author burangir
 */
@Stateless
public class TokenValidatorService {
    @EJB
    private TokenServiceDAO tokenServiceDAO;
    @EJB
    private Configurator configurator;
    
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TokenValidatorService.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = TokenValidatorService.this.getClass().getSimpleName();

    /**
     * @param tokenServiceDAO the tokenServiceDAO to set
     */
    public void setTokenServiceDAO(TokenServiceDAO tokenServiceDAO) {
        this.tokenServiceDAO = tokenServiceDAO;
    }

    /**
     * @param configurator the configurator to set
     */
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }

    public boolean validateToken(String tokenStr, String identityUuid) {
        sMethodName = "validateToken";
        boolean tokenValidateFlg = false, tokenUpdateFlg = false;
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        CrosssiteRequestTokenTable tokenObjLocal;
        try {
            if (null != tokenStr) {
                tokenObjLocal = tokenServiceDAO.validateToken(tokenStr, identityUuid, CreditMessageTokenConstants.STATUS_ACTIVE);
               
                if(tokenObjLocal != null && tokenObjLocal.getTokenstatus().equalsIgnoreCase(CreditMessageTokenConstants.STATUS_ACTIVE)){
                    tokenValidateFlg = true;
              
                    tokenValidateFlg = validateTokenExpiry(tokenObjLocal);
                    if(!tokenValidateFlg){ 
                        tokenUpdateFlg = udpateTokenStatus(CreditMessageTokenConstants.STATUS_EXPIRED, tokenStr, identityUuid);
                        if(tokenUpdateFlg)
                            LOGGER.info("Expired token status updated to " + CreditMessageTokenConstants.STATUS_EXPIRED + " successfully!");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occured in " + sMethodName + ". Exception  : " + e.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        }
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        return tokenValidateFlg;
    }
    
    public boolean udpateTokenStatus(String tokenStatus, String tokenId, String identityUuid) {
        sMethodName = "udpateTokenStatus";
        boolean tokenValidateFlg = false;
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        try {
            if (null != tokenId) {
                if (tokenServiceDAO == null)  setTokenServiceDAO(new TokenServiceDAO());
                tokenValidateFlg = tokenServiceDAO.updateTokenStatus(tokenStatus, tokenId, identityUuid);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occured in " + sMethodName + ". Exception  : " + e.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        }
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        return tokenValidateFlg;
    }
    
    private boolean validateTokenExpiry(CrosssiteRequestTokenTable tokenObjLocal){
        sMethodName = "validateTokenExpiry";
        boolean tokenValidFlg = true;
        String tokenGenerationDateTime, tokenId;
        DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
        Date tokenGenerationDateTimeDt;
        int tokenExpiryDuration = 0;
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        try {
                if(configurator.get("TOKEN_EXPIRY_DURATION") != null)
                    tokenExpiryDuration = Integer.parseInt(configurator.get("TOKEN_EXPIRY_DURATION"));
                else LOGGER.error("stargate.properties do not have value TOKEN_EXPIRY_DURATION. Please add it.");
                if(tokenExpiryDuration > 0){
                    Date dateObj = new Date();
                    tokenId = tokenObjLocal.getTokenid();
                    tokenGenerationDateTime = tokenObjLocal.getTokencredatetime();
                    tokenGenerationDateTimeDt = dateFormat1.parse(tokenGenerationDateTime);
                    dateObj = dateFormat1.parse(dateFormat1.format(dateObj));

                    long differenceInSeconds = TimeUnit.MILLISECONDS.toSeconds(dateObj.getTime() - tokenGenerationDateTimeDt.getTime());
                    if(differenceInSeconds >= tokenExpiryDuration){
                        LOGGER.info("Token " + tokenId + " is expired. Need to generate new token");
                        tokenValidFlg = false;
                    }
                }
        } catch (Exception e) {
            LOGGER.error("Exception occured in " + sMethodName + ". Exception  : " + e.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        }
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        return tokenValidFlg;
    }  
}