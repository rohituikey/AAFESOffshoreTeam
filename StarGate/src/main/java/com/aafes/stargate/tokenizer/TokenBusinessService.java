/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.tokenizer;

import com.aafes.stargate.authorizer.entity.Transaction;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.ProcessingException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pkalpesh
 */
@Stateless
public class TokenBusinessService {
    
    
    @EJB
    private TokenEndPointService tokenEndPointService;
    
    
    private static final org.slf4j.Logger log
            = LoggerFactory.getLogger(TokenBusinessService.class.getSimpleName());
    
    
    public boolean modifyTran(Transaction t) throws ProcessingException{
        
        log.info("TokenBusinessService#modifyTran#Start.......");
         
        if("Pan".equalsIgnoreCase(t.getPan())){ //Case 1 - Pan with AccountNumber
            String token = tokenEndPointService.issueToken(t.getAccount());
            log.info(token);
            t.setTokenId(token);
        }else{ //Case 2 - Token with TokenNumber
            String accountNumber = tokenEndPointService.lookupAccount(t.getTokenId());
            log.info("account number");
            t.setAccount(accountNumber);
        }
        
        return true;
        
    }

    public void setTokenEndPointService(TokenEndPointService service) {
        this.tokenEndPointService = service;
    }
    
}
