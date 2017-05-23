/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenizer;

import generated.AccountTypeType;
import generated.TokenMessage;
import javax.ejb.EJB;
import javax.ejb.Stateless;


@Stateless
public class Tokenizer {

    @EJB
    private LookUpService lookUpService;
    @EJB
    private IssueService issueService;

    public TokenMessage handle(TokenMessage tm) {

        try {

            if (tm.getRequest() != null) {
                TokenMessage.Request request = tm.getRequest();
                TokenMessage.Response response = new TokenMessage.Response();
                if (request.getRequestType().value().equalsIgnoreCase(TokenizerConstants.LOOKUP)) {
                    String account = lookUpService.process(tm);
                    
                    if (account != null && !account.trim().isEmpty()) {
                        response.setAccountType(AccountTypeType.PAN);
                        response.setAccount(account);
                        response.setDescriptionField("Success");
                        response.setResponseType("SUCCESS");
                        response.setReasonCode("100");
                    } else {
                        
                        response.setDescriptionField("INVALID TOKEN");
                        response.setResponseType("FAILURE");
                        response.setReasonCode("900");
                    }

                } else if (request.getRequestType().value().equalsIgnoreCase(TokenizerConstants.ISSUE)) {
                    String token = issueService.process(tm);
                   
                    if (token != null && !token.trim().isEmpty()) {
                        response.setAccountType(AccountTypeType.TOKEN);
                        response.setAccount(token);
                        response.setDescriptionField("Success");
                        response.setResponseType("SUCCESS");
                        response.setReasonCode("100");
                    } else {
                        
                        response.setDescriptionField("INVALID CARD NUMBER");
                        response.setResponseType("FAILURE");
                        response.setReasonCode("901");
                    }
                }
                
                tm.setResponse(response);

            }

        } catch (Exception e) {

        }

        return tm;
    }
}