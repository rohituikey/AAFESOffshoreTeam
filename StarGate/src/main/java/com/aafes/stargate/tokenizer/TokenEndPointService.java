/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.tokenizer;


import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.token.AccountTypeType;
import com.aafes.token.RequestTypeType;
import com.aafes.token.TokenMessage;
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
    
    
    public String lookupAccount(Transaction t) throws ProcessingException{
       log.info("TokenServicer#lookupAccount#Start.......");
        // REMOVE
        //if(tokenEndpoint == null) tokenEndpoint = "http://localhost:8080/tokenizer/1/tokenmessage";
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(tokenEndpoint);
        log.info("TokenServicer#lookupAccount#Done WebTarget.........");
                
        TokenMessage tm = new TokenMessage();
        TokenMessage.Request request = new TokenMessage.Request();
        request.setAccount("006491570000000301'");
        request.setAccountType(AccountTypeType.TOKEN);
        request.setMedia(t.getMedia());
        request.setRequestType(RequestTypeType.LOOKUP);
        request.setTokenBankName(t.getTokenBankName());
        tm.setRequest(request);
      //  request.set
        Entity entity = Entity.entity(tm, MediaType.APPLICATION_XML);
         
        Response response = target.request(MediaType.APPLICATION_XML).post(entity);
         
        tm = response.readEntity(TokenMessage.class);    
        log.info("TokenServicer#lookupAccount#Got response.........");
        if(tm.getResponse() != null) {
            return tm.getResponse().getAccount();
        }
        log.debug("rrn number is "+t.getRrn());
               log.info("TokenServicer#lookupAccount#ENDED.........");
        return "";
       
    }
    
    
    public String issueToken(Transaction t) throws ProcessingException{
        log.info("TokenServicer#issueToken#Start.......");
        
        // REMOVE
        if(tokenEndpoint == null) tokenEndpoint = "http://localhost:8080/tokenizer/1/tokenmessage";
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(tokenEndpoint);
        log.info("TokenServicer#issueToken#Done WebTarget.........");
                
        TokenMessage tm = new TokenMessage();
        TokenMessage.Request request = new TokenMessage.Request();
        request.setAccount(t.getAccount());
        request.setAccountType(AccountTypeType.PAN);
        request.setMedia(t.getMedia());
        request.setRequestType(RequestTypeType.ISSUE);
        request.setTokenBankName(t.getTokenBankName());
        tm.setRequest(request);
        Entity entity = Entity.entity(tm, MediaType.APPLICATION_XML);
         
        Response response = target.request(MediaType.APPLICATION_XML).post(entity);
         
        tm = response.readEntity(TokenMessage.class);       
        if(tm.getResponse() != null) {
            return tm.getResponse().getAccount();
        }
        log.debug("rrn number is "+t.getRrn());  
        log.info("TokenServicer#issueToken#END.......");
        return "";
       
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    
    
    
}
