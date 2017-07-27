/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.stargate.authorizer.entity.Transaction;

/**
 *
 * @author alugumetlas
 */
public class WEXProcessor {
    Transaction t;
 
    public Transaction preAuthProcess(Transaction t)
    {
        return t;
    }
    public Transaction finalAuthProcess(Transaction t)
    {
        return t;
    }
}
