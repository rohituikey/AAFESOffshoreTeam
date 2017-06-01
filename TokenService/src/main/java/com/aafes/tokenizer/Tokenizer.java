/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenizer;

import com.aafes.tokenservice.util.AccountType;
import com.aafes.tokenservice.util.ResponseType;
import com.aafes.token.AccountTypeType;
import com.aafes.token.TokenMessage;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class Tokenizer {

    @EJB
    private LookUpService lookUpService;
    @EJB
    private IssueService issueService;
    @EJB
    private Configurator configurator;

    public TokenMessage handle(TokenMessage tm) {

        TokenMessage.Response response = new TokenMessage.Response();
        try {

            if (tm.getRequest() != null) {
                TokenMessage.Request request = tm.getRequest();

                if (request.getRequestType().value().equalsIgnoreCase(TokenizerConstants.LOOKUP)
                        && request.getAccountType().value().equalsIgnoreCase(AccountType.TOKEN)) {
                    String account = lookUpService.process(tm);

                    if (account != null && !account.trim().isEmpty()) {
                        response.setAccountType(AccountTypeType.PAN);
                        response.setAccount(account);
                        response.setDescriptionField("Success");
                        response.setResponseType(ResponseType.SUCCESS);
                        response.setReasonCode(configurator.get("SUCCESS"));
                    } else {

                        response.setDescriptionField("INVALID_TOKEN");
                        response.setResponseType(ResponseType.FAILED);
                        response.setReasonCode(configurator.get("INVALID_TOKEN"));
                    }

                } else if (request.getRequestType().value().equalsIgnoreCase(TokenizerConstants.ISSUE)
                        && request.getAccountType().value().equalsIgnoreCase(AccountType.PAN)) {
                    String token = issueService.process(tm);

                    if (token != null && !token.trim().isEmpty()) {
                        response.setAccountType(AccountTypeType.TOKEN);
                        response.setAccount(token);
                        response.setDescriptionField("Success");
                        response.setResponseType(ResponseType.SUCCESS);
                        response.setReasonCode(configurator.get("SUCCESS"));
                    } else {

                        response.setDescriptionField("INVALID_CARD_NUMBER");
                        response.setResponseType(ResponseType.FAILED);
                        response.setReasonCode(configurator.get("INVALID_CARD_NUMBER"));
                    }
                } else {
                    response.setDescriptionField("BAD_REQUEST");
                    response.setResponseType(ResponseType.FAILED);
                    response.setReasonCode(configurator.get("BAD_REQUEST"));
                }

            }

        } catch (Exception e) {
            response.setDescriptionField("INTERNAL_SERVER_ERROR");
            response.setResponseType(ResponseType.FAILED);
            response.setReasonCode(configurator.get("INTERNAL_SERVER_ERROR"));
        }

        tm.setResponse(response);
        return tm;
    }
}
