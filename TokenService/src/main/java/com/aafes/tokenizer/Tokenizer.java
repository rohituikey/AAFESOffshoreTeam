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
import javax.xml.bind.JAXBException;
import org.slf4j.LoggerFactory;

@Stateless
public class Tokenizer {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(Tokenizer.class.
                    getSimpleName());

    @EJB
    private LookUpService lookUpService;
    @EJB
    private IssueService issueService;
    @EJB
    private Configurator configurator;

    public TokenMessage handle(TokenMessage tm) throws JAXBException {

        LOG.info("Entry in  handle() method of Tokenzer class");
        this.validate(tm);
        TokenMessage.Response response = new TokenMessage.Response();
        try {
            if (tm.getRequest() != null) {
                TokenMessage.Request request = tm.getRequest();

                if (request.getRequestType().value().equalsIgnoreCase(TokenizerConstants.LOOKUP)
                        && request.getAccountType().value().equalsIgnoreCase(AccountType.TOKEN)) {

                    String account = lookUpService.process(tm);
                    LOG.debug("Account details are... " + account);

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
                    LOG.debug("Token Details...... " + token);

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
            LOG.error("Error occured inside handle() method of Tokenzer class" + e.getMessage());
            response.setDescriptionField("INTERNAL_SERVER_ERROR");
            response.setResponseType(ResponseType.FAILED);
            response.setReasonCode(configurator.get("INTERNAL_SERVER_ERROR"));
        }

        tm.setResponse(response);
        LOG.info("Exit from  handle() method of Tokenizer class");
        return tm;
    }

    private void validate(TokenMessage tm) throws JAXBException {
        LOG.info("Entry in  validate() method of Tokenzer class");
        if (tm.getRequest().getRequestType().value().equalsIgnoreCase(TokenizerConstants.ISSUE)) {
            if (tm.getRequest().getMedia() == null || tm.getRequest().getMedia().trim().isEmpty()) {
                LOG.error("Media required for Issue");
                throw new JAXBException("Media required for Issue");
            }
        }
        LOG.info("Exit from  validate() method of Tokenzer class");
    }

    protected void setLookUpService(LookUpService lookUpService) {
        this.lookUpService = lookUpService;
    }

    protected void setIssueService(IssueService issueService) {
        this.issueService = issueService;
    }

    protected void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }
}
