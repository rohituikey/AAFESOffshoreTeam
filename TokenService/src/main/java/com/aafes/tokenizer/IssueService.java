/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenizer;

import com.aafes.tokenservice.util.AccountType;
import com.aafes.token.TokenMessage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.security.SecureRandom;
import javax.inject.Inject;

@Stateless
public class IssueService {

    @EJB
    private VaultDao vaultDao;
    @EJB
    private TokenizerDao tokenizerDao;

    private int validity = 1;

    public String process(TokenMessage tm) throws ParseException {
        String token = "";
        Vault vault = null;
        vault = vaultDao.findByAccount(tm.getRequest().getAccount() + tm.getRequest().getTokenBankName());

        if (vault == null) {

           token =  this.generarte(tm);
        } else {
            token = vault.getTokennumber();
//            TokenBank tokenBank = tokenizerDao.find(token, tm.getRequest().getTokenBankName());
//           if(tokenBank != null)
//           {
//               SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
//               Date expDate = df.parse(tokenBank.getExpirydate());;
//               String date = df.format(new Date());
//               Date currentDate = df.parse(date);
               
//               if(currentDate.after(expDate))
//               {
//                   tokenBank.setStatus(TokenizerConstants.EXPIRED);
//                   tokenizerDao.save(tokenBank);
//                   token = this.generarte(tm);
//                   
//               }
//               
//           } else {
//             tokenBank.setStatus(TokenizerConstants.EXPIRED);
//                   tokenizerDao.save(tokenBank);
//                   token = this.generarte(tm);
//           }
        }

        return token;

    }

    private String generarte(TokenMessage tm)
    {
         String token = "";
            int attempts = 1;

            while (attempts < 4) {
                token = generateToken(tm.getRequest().getAccount(), tm.getRequest().getMedia());
                TokenBank tokenBank = tokenizerDao.find(token, tm.getRequest().getTokenBankName());
                if (tokenBank != null) {
                    // generate again
                    attempts++;
                } else {

                    Vault newVault = new Vault();
                    newVault.setAccountnumber(tm.getRequest().getAccount() + tm.getRequest().getTokenBankName());
                    newVault.setTokennumber(token);
                    vaultDao.save(newVault);

                    TokenBank newTokenBank = new TokenBank();
                    newTokenBank.setTokenbankname(tm.getRequest().getTokenBankName());
                    newTokenBank.setTokennumber(token);
                    newTokenBank.setExpirydate(getExpirationDate());
                    newTokenBank.setStatus(TokenizerConstants.ACTIVE);
                    tokenizerDao.save(newTokenBank);
                    break;
                }
            }

        return token;
    }
    private String generateToken(String accountNumber, String cardType) {
        String token = "";
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 13; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        token = sb.toString();

        String last4 = accountNumber.substring(accountNumber.length() - 4);
        String mediaValue = findMediaValue(cardType);
        
        token = "9" + mediaValue + token + last4;
        return token;
    }
    
    private String findMediaValue(String cardType) {
        switch (cardType) {
            case TokenizerConstants.MILSTAR:
                return "0";

            case TokenizerConstants.AMEX:
                return "3";

            case TokenizerConstants.DISCOVER:
                return "6";
            case TokenizerConstants.VISA:
                return "4";
            case TokenizerConstants.MASTER:
                return "5";
            case TokenizerConstants.GIFTCARD:
                return "8";
                
        }
        
        return "9";
    }

    private String getExpirationDate() {

        String expDate = "";

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, validity);
        Date newDate = c.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        expDate = df.format(newDate);
        return expDate;
}

    protected void setVaultDao(VaultDao vaultDao) {
        this.vaultDao = vaultDao;
    }

    protected void setTokenizerDao(TokenizerDao tokenizerDao) {
        this.tokenizerDao = tokenizerDao;
    }

}