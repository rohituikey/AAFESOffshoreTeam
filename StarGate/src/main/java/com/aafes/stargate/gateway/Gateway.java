/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway;

import com.aafes.stargate.authorizer.entity.Transaction;

/**
 *
 * @author Ganji
 */

public abstract class Gateway {
    
    public abstract Transaction processMessage(Transaction t);
    
    
}
