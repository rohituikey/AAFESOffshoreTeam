/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.web;

import com.aafes.stargate.boundary.CreditMessageResource;
import java.util.Map;
import javax.ejb.Stateless;
import javax.xml.ws.BindingProvider;

/**
 *
 * @author alugumetlas
 */
@Stateless
public class TimeoutSoap {

    CreditMessageResource service = new CreditMessageResource();
    int requestTimeOutInMs = 2500;
    Map<String, Object> context = ((BindingProvider) service).getRequestContext();
    

    public void handleTimeOut() {
        context.put("com.sun.xml.internal.ws.request.timeout", requestTimeOutInMs);
        context.put("com.sun.xml.ws.request.timeout", requestTimeOutInMs);
       
    }
}
