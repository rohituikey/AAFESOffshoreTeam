/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenizer;

import generated.TokenMessage;
import javax.ejb.EJB;
import javax.ejb.Stateless;


@Stateless
public class LookUpService {
    
    @EJB
    private VaultDao vaultDao;
    
    public String process(TokenMessage tm)
    {
        String accountNumber = "";
        
        Vault vault = vaultDao.find(tm.getRequest().getAccount());
        
        if(vault != null)
        {
            accountNumber = vault.getAccountnumber();
        }
        
        return accountNumber;
                
        
    }
    
}