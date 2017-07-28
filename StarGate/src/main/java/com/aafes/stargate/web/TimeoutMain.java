/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.web;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.token.AccountTypeType;
import com.aafes.token.TokenMessage;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 *
 * @author alugumetlas
 */
public class TimeoutMain {

    static Transaction t = new Transaction();

    public String lookupAccount(Transaction t) throws ProcessingException {
        try {

            String tokenEndpoint = "http://localhost:8080/stargate/1/token/";
            ClientBuilder abc = ClientBuilder.newBuilder();
            Client client = abc.build();
            WebTarget target = client.target(tokenEndpoint);

            TokenMessage tm = new TokenMessage();
            TokenMessage.Request request = new TokenMessage.Request();
            request.setAccount(t.getAccount());
            request.setAccountType(AccountTypeType.TOKEN);
            request.setMedia(t.getMedia());
            request.setRequestType(com.aafes.token.RequestTypeType.LOOKUP);
            request.setTokenBankName(t.getTokenBankName());
            tm.setRequest(request);

//            target.property("com.sun.xml.internal.ws.request.timeout", 6000);
//            target.property("com.sun.xml.ws.request.timeout", 6000);
            Entity entity = Entity.entity(tm, MediaType.APPLICATION_XML);
            try {
                Response response = target.request(MediaType.TEXT_PLAIN).post(entity);
                tm = response.readEntity(TokenMessage.class);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ProcessingException(e.getCause());
            }
            if (tm.getResponse() != null) {
                return tm.getResponse().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    public static void main(String[] args) {
        try {
//            t.setIdentityUuid("0ee1c509-2c70-4bcd-b261-f94f1fe6c43b");
//            t.setUserId("tmpUserName");
//            t.setP

            t.setAccount("787632787632");
            t.setMedia("Milstar");
            t.setTokenBankName("01");
            String res = new TimeoutMain().lookupAccount(t);
            if (!res.equals("")) {
                System.out.println("time out error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
//        Message tm = new Message();
//        Header header = new Header();
//        header.setIdentityUUID(uuid);
//        header.setUerId(uname);
//        header.setPassword(pwd);
//        tm.setHeader(header);
            // tm.setHeader(header);