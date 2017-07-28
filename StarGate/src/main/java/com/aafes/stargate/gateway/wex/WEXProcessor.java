/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.wex.simulator.NBSClient;
import javax.ejb.Stateless;

/**
 *
 * @author alugumetlas
 */
@Stateless
public class WEXProcessor {
    Transaction t;
 
    public Transaction preAuthProcess(Transaction t)
    {
        String responseStr = "";
        NBSClient clientObj = new NBSClient();
        responseStr = clientObj.generateResponse("APPROVED");
        t.setResponseType(responseStr);
        
        return t;
    }
    public Transaction finalAuthProcess(Transaction t)
    {
        String responseStr = "";
        NBSClient clientObj = new NBSClient();
        responseStr = clientObj.generateResponse("APPROVED");
        t.setResponseType(responseStr);
        return t;
    }
    
    public Transaction processSaleRequest(Transaction t)
    {
        String responseStr = "";
        NBSClient clientObj = new NBSClient();
        responseStr = clientObj.generateResponse("APPROVED");
        t.setResponseType(responseStr);
        return  t;
    }
    
}
