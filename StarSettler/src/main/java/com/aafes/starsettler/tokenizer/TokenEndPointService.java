/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.tokenizer;

import com.aafes.credit.AccountTypeType;
import com.aafes.credit.RequestTypeType;
import com.aafes.starsettler.entity.SettleEntity;
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

    public String lookupAccount(SettleEntity t) throws ProcessingException {

        log.info("Entry in lookupAccount method of TokenEndPointService..");

        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(tokenEndpoint);
        log.info("TokenServicer#lookupAccount#Done WebTarget.........");

        TokenMessage tm = new TokenMessage();
        TokenMessage.Request request = new TokenMessage.Request();
        request.setAccount(t.getCardToken());
        request.setAccountType(AccountTypeType.TOKEN);
        request.setMedia(t.getCardType());
        request.setRequestType(RequestTypeType.LOOKUP);
        request.setTokenBankName(t.getTokenBankName());
        tm.setRequest(request);
        //  request.set
        Entity entity = Entity.entity(tm, MediaType.APPLICATION_XML);

        Response response = target.request(MediaType.APPLICATION_XML).post(entity);

        tm = response.readEntity(TokenMessage.class);
        log.info("TokenServicer#lookupAccount#Got response.........");
        log.info("Exit from lookupAccount method of TokenEndPointService..");
        if (tm.getResponse() != null) {
            return tm.getResponse().getAccount();
        }
        return "";

    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

}
