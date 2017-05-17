/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.tokenizer;


import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pkalpesh
 */
@Stateless
public class TokenEndPointService {
    
    
    @Inject
    private String tokenEndpoint;
    
    
     private static final org.slf4j.Logger log
            = LoggerFactory.getLogger(TokenEndPointService.class.getSimpleName());
    
    
    public String lookupAccount(String token) throws ProcessingException{
       log.info("TokenServicer#lookupAccount#Start.......");
        
        //http://haqapsgate03:7070/tokenizer/lookup
        String lookupEndpoint = tokenEndpoint + "/lookup";
        log.info("TokenServicer#lookupAccount#lookupEndpoint : "+lookupEndpoint);
                
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(lookupEndpoint);
        log.info("TokenServicer#lookupAccount#Done WebTarget.........");
                
        TokenRequest tR = new TokenRequest();
        tR.setClient("gso");
        tR.setToken(token);
        Entity entity = Entity.entity(tR, MediaType.APPLICATION_JSON);
         
        Response response = target.request(MediaType.APPLICATION_JSON).post(entity);
         
        LookupResponse entityFromResponse = response.readEntity(LookupResponse.class);    
        log.info("TokenServicer#lookupAccount#Got response.........");
        return entityFromResponse.getAccount();
       
    }
    
    
    public String issueToken(String account) throws ProcessingException{
        log.info("TokenServicer#issueToken#Start.......");
        
        //http://haqapsgate03:7070/tokenizer/issue
        String issueEndpoint = tokenEndpoint + "/issue";
        log.info("TokenServicer#issueToken#lookupEndpoint : "+issueEndpoint);
                
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(issueEndpoint);
        log.info("TokenServicer#issueToken#Done WebTarget.........");
                
        TokenRequest tR = new TokenRequest();
        tR.setClient("gso");
        tR.setAccountNumber(account);
        Entity entity = Entity.entity(tR, MediaType.APPLICATION_JSON);
         
        Response response = target.request(MediaType.APPLICATION_JSON).post(entity);
         
        IssueResponse entityFromResponse = response.readEntity(IssueResponse.class);    
        log.info("TokenServicer#issueToken#Got response.........");
        return entityFromResponse.getToken();
       
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    
    
    
}
