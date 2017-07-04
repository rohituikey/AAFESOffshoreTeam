/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenizer;

import com.aafes.tokenservice.util.AccountType;
import com.aafes.token.TokenMessage;
import java.text.ParseException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

@Stateless
public class LookUpService {

    @EJB
    private VaultDao vaultDao;
    @EJB
    private TokenizerDao tokenizerDao;

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(LookUpService.class.
                    getSimpleName());

    public String process(TokenMessage tm) throws ParseException {
        LOG.info("Entry in process method of Lookupservice..");
        String accountNumber = "";
        Vault vault = null;

//        if(tm.getRequest().getAccountType().value().equalsIgnoreCase(AccountType.TOKEN))
//        {
//           TokenBank tokenBank = tokenizerDao.find(tm.getRequest().getAccount(), tm.getRequest().getTokenBankName());
//           if(tokenBank != null)
//           {
//               SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
//               Date expDate = df.parse(tokenBank.getExpirydate());;
//               String date = df.format(new Date());
//               Date currentDate = df.parse(date);
//        
//               if(currentDate.after(expDate))
//               {
//                   tokenBank.setStatus(TokenizerConstants.EXPIRED);
//                   tokenizerDao.save(tokenBank);
//                   return accountNumber;
//               }
//               
//           }
//        } 
        if (tm.getRequest().getAccountType().value().equalsIgnoreCase(AccountType.PAN)) {
            vault = vaultDao.findByAccount(tm.getRequest().getAccount());
            LOG.debug("Token details are:" + vault);
        } else {
            vault = vaultDao.findByToken(tm.getRequest().getAccount());
            LOG.debug("Token details are:" + vault);
        }

        if (vault != null && vault.getAccountnumber().contains(tm.getRequest().getTokenBankName())) {
            accountNumber = vault.getAccountnumber();
            accountNumber = accountNumber.replaceAll(tm.getRequest().getTokenBankName(), "");
        }

        LOG.info("Exitfrom process method of Lookupservice..");
        return accountNumber;

    }

    protected void setVaultDao(VaultDao vaultDao) {
        this.vaultDao = vaultDao;
    }

    protected void setTokenizerDao(TokenizerDao tokenizerDao) {
        this.tokenizerDao = tokenizerDao;
    }

}
