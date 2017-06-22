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
import com.datastax.driver.core.Row;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
        boolean tokenValidateFlg = false, tokenUpdateFlg = false;
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        try {
            if (null != tokenStr) {
                if (tokenServiceDAO == null) {
                    tokenServiceDAO = new TokenServiceDAO();
                }
                ResultSet tokenObj = null;
                tokenObj = tokenServiceDAO.validateToken(tokenStr, identityUuid, CreditMessageTokenConstants.STATUS_ACTIVE);
                List<Row> rowList = tokenObj.all();
                if(rowList != null && rowList.size() > 0){
                    tokenValidateFlg = validateTokenExpiry(rowList);
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
    
    private boolean validateTokenExpiry(List<Row> rowList){
        sMethodName = "validateTokenExpiry";
        boolean tokenValidFlg = true;
        String tokenGenerationDateTime, tokenId;
        DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
        Date tokenGenerationDateTimeDt;
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        try {
                Date dateObj = new Date();
                for(Row row : rowList){
                    tokenId = row.getString("tokenid");
                    tokenGenerationDateTime = row.getString("tokencredatetime");
                    tokenGenerationDateTimeDt = dateFormat1.parse(tokenGenerationDateTime);
                    dateObj = dateFormat1.parse(dateFormat1.format(dateObj));

                    long differenceInSeconds = TimeUnit.MILLISECONDS.toSeconds(dateObj.getTime() - tokenGenerationDateTimeDt.getTime());
                    if(differenceInSeconds >= 300){
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