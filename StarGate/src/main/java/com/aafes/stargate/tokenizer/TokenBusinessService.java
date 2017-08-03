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
        log.info("rrn number in TokenBusinessService#lookup is : "+t.getRrn());
        log.info("TokenBusinessService#lookup#End.......");
        return true;
    }

    public void issueToken(Transaction t) throws ProcessingException {

        log.info("TokenBusinessService#modifyTran#Start.......");
        String token = tokenEndPointService.issueToken(t);
        t.setTokenId(token);
        log.info("rrn number in TokenBusinessService.issueToken is :"+token +"RRN Number"+t.getRrn());
        log.info("TokenBusinessService#modifyTran#end.......");
    }

    public void setTokenEndPointService(TokenEndPointService service) {
        this.tokenEndPointService = service;
    }

}