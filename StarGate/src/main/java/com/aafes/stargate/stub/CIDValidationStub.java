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
        if (t.getComment().equalsIgnoreCase(StarGateConstants.VALID_I_FOR_APPROVED)) 
            return true;
        else if(t.getComment().equalsIgnoreCase(StarGateConstants.INVALID_I_FOR_DECLINE))
             return false;
        else if(t.getComment().equalsIgnoreCase(StarGateConstants.INVALID_I_FOR_APPROVE))
            return true;
        else if(t.getComment().equalsIgnoreCase(StarGateConstants.VALID_I_FOR_DECLINE))
         return false;
        else 
            return true; 
    }
    
}
