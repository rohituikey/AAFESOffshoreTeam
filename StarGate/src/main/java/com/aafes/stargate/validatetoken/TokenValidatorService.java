/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.validatetoken;

import com.aafes.stargate.dao.TokenServiceDAO;
import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.util.CreditMessageTokenConstants;
import com.datastax.driver.core.ResultSet;
import org.slf4j.LoggerFactory;

/**
 *
 * @author burangir
 */
public class TokenValidatorService {
    private TokenServiceDAO tokenServiceDAO;
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TokenValidatorService.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = TokenValidatorService.this.getClass().getSimpleName();

    public boolean validateToken(String tokenStr, String identityUuid) {
        sMethodName = "validateToken";
        boolean tokenValidateFlg = false;
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        try {
            if (null != tokenStr) {
                if (tokenServiceDAO == null) {
                    tokenServiceDAO = new TokenServiceDAO();
                }
                ResultSet tokenObj = null;
                tokenObj = tokenServiceDAO.validateToken(tokenStr, identityUuid, CreditMessageTokenConstants.STATUS_ACTIVE);

                if (tokenObj != null) tokenValidateFlg = true;
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
                if (tokenServiceDAO == null)  tokenServiceDAO = new TokenServiceDAO();
                tokenValidateFlg = tokenServiceDAO.updateTokenStatus(tokenStatus, tokenId, identityUuid);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occured in " + sMethodName + ". Exception  : " + e.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        }
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        return tokenValidateFlg;
    }
}