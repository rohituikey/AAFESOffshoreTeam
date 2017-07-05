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

    public boolean lookUpAccount(Transaction t) throws ProcessingException {

        log.info("TokenBusinessService#lookup#Start.......");

        String accountNumber = tokenEndPointService.lookupAccount(t);
        if (accountNumber != null && !accountNumber.trim().isEmpty()) {
            t.setAccount(accountNumber);
        } else {
            return false;
        }
        log.debug("rrn number is "+t.getRrn());
        return true;
    }

    public void issueToken(Transaction t) throws ProcessingException {

        log.info("TokenBusinessService#modifyTran#Start.......");
        String token = tokenEndPointService.issueToken(t);
        log.info(token);
        t.setTokenId(token);
        log.debug("rrn number is "+t.getRrn());
    }

    public void setTokenEndPointService(TokenEndPointService service) {
        this.tokenEndPointService = service;
    }

}
