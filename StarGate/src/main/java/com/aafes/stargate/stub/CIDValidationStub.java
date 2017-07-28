/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.stub;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.util.StarGateConstants;

/**
 *
 * @author singha
 */
public class CIDValidationStub {
    
     public  static boolean validateStub(Transaction t) {
         
        if (t.getComment().toUpperCase().contains(StarGateConstants.INVALID)) 
            return false;
        else if(t.getComment().toUpperCase().contains(StarGateConstants.VALID))
            return true;
        else 
            return true; 
    }
    
}
