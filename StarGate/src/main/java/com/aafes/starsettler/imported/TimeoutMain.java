/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.imported;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.tokenvalidator.Message; import com.aafes.tokenvalidator.Message.Header;
import com.aafes.tokenvalidator.Message.Request;
import com.aafes.tokenvalidator.RequestTypeType;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder; import javax.ws.rs.client.Entity; import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author alugumetlas
 */
public class TimeoutMain {

    static Transaction t = new Transaction();

    public String lookupAccount(String uuid, String uname, String pwd) throws ProcessingException {
        try{

        String tokenEndpoint = "http://localhost:8080/stargate/1/token/";
        
        ClientBuilder abc = ClientBuilder.newBuilder();
        Client client = abc.build();
        WebTarget target = client.target(tokenEndpoint);

        Message tm = new Message();
        Header header = new Header();
        header.setIdentityUUID(uuid);
        header.setUerId(uname);
        header.setPassword(pwd);
        
        tm.setHeader(header);
        
        Request rq = new Request();
        rq.setRequestType(RequestTypeType.TOKEN);
        tm.setHeader(header);
        
        //target.property("com.sun.xml.internal.ws.request.timeout", 6000);
        //target.property("com.sun.xml.ws.request.timeout", 6000);
        Entity entity = Entity.entity(tm, MediaType.TEXT_PLAIN);

        Response response = null;
        Builder req = null;
 
            req = target.request(MediaType.TEXT_PLAIN);
            response = req.post(entity);
            tm = response.readEntity(Message.class);

            if (tm.getResponse() != null) {
                return tm.getResponse().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    public static void main(String[] args) {
        try{
//            t.setIdentityUuid("0ee1c509-2c70-4bcd-b261-f94f1fe6c43b");
//            t.setUserId("tmpUserName");
//            t.setP
            String res = new TimeoutMain().lookupAccount("0ee1c509-2c70-4bcd-b261-f94f1fe6c43b","tmpUserName","65656565");
            if (!res.equals("")) {
                System.out.println("time out error");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}