/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenizer;

import com.aafes.tokenservice.util.AccountType;
import com.aafes.token.AccountTypeType;
import com.aafes.token.TokenMessage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;


@Stateless
public class LookUpService {
    
    @EJB
    private VaultDao vaultDao;
    @EJB
    private TokenizerDao tokenizerDao;
    
    public String process(TokenMessage tm) throws ParseException
    {
        String accountNumber = "";
        Vault vault = null;
        
        if(tm.getRequest().getAccountType().value().equalsIgnoreCase(AccountType.TOKEN))
        {
           TokenBank tokenBank = tokenizerDao.find(tm.getRequest().getAccount(), tm.getRequest().getTokenBankName());
           if(tokenBank != null)
           {
               SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
               Date expDate = df.parse(tokenBank.getExpirydate());;
               String date = df.format(new Date());
               Date currentDate = df.parse(date);
               
               if(currentDate.after(expDate))
               {
                   tokenBank.setStatus(TokenizerConstants.EXPIRED);
                   tokenizerDao.save(tokenBank);
                   return accountNumber;
               }
               
           }
        } 
        
        if(tm.getRequest().getAccountType().value().equalsIgnoreCase(AccountType.PAN))
        {
            vault = vaultDao.findByAccount(tm.getRequest().getAccount());
        } else {
            vault = vaultDao.findByToken(tm.getRequest().getAccount());
        }
        
        
        if(vault != null)
        {
            accountNumber = vault.getAccountnumber();
        }
        
        return accountNumber;
                
        
    }
    
}