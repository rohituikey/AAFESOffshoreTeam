/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.web;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.token.AccountTypeType;
import com.aafes.token.RequestTypeType;
import com.aafes.token.TokenMessage;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author alugumetlas
 */
public class TimeoutMain1 {

    static Transaction t = new Transaction();

    public String lookupAccount(Transaction t) throws ProcessingException {
        try{

        String tokenEndpoint = "http://localhost:8989/tokenizer/1/tokenmessage";
        
        ClientBuilder abc = ClientBuilder.newBuilder();
        Client client = abc.build();
        WebTarget target = client.target(tokenEndpoint);

        TokenMessage tm = new TokenMessage();
        TokenMessage.Request request = new TokenMessage.Request();
        request.setAccount(t.getAccount());
        request.setAccountType(AccountTypeType.TOKEN);
        request.setMedia(t.getMedia());
        request.setRequestType(RequestTypeType.LOOKUP);
        request.setTokenBankName(t.getTokenBankName());
        tm.setRequest(request);
        //target.property("com.sun.xml.internal.ws.request.timeout", 6000);
        //target.property("com.sun.xml.ws.request.timeout", 6000);
        Entity entity = Entity.entity(tm, MediaType.APPLICATION_XML);

        Response response = null;
        Builder req = null;
 
            req = target.request(MediaType.APPLICATION_XML);
            response = req.post(entity);
            tm = response.readEntity(TokenMessage.class);

            if (tm.getResponse() != null) {
                return tm.getResponse().getAccount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    public static void main(String[] args) {
        try{
            t.setAccount("Sale");
            t.setAccountTypeType("pan");
            t.setMedia("GiftCard");
            t.setTokenBankName("01");
            String res = new TimeoutMain1().lookupAccount(t);
            if (!res.equals("")) {
                System.out.println("time out error");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
