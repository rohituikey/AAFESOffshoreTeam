/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenizer;

import generated.TokenMessage;
import java.util.Random;
import javax.ejb.EJB;
import javax.ejb.Stateless;

 
@Stateless
public class IssueService {

    @EJB
    private VaultDao vaultDao;
    @EJB
    private TokenizerDao tokenizerDao;

    public String process(TokenMessage tm) {
        String token = "";

        Vault vault = vaultDao.find(tm.getRequest().getAccount());

        if (vault == null) {
            int attempts = 1;

            while (attempts < 4) {
                token = generateToken(tm.getRequest().getAccount(), tm.getRequest().getMedia());
                TokenBank tokenBank = tokenizerDao.find(token, tm.getRequest().getTokenBankName());
                if (tokenBank != null) {
                    // generate again
                    attempts ++;
                } else {

                    Vault newVault = new Vault();
                    newVault.setAccountnumber(tm.getRequest().getAccount());
                    newVault.setTokennumber(token);
                    vaultDao.save(newVault);

                    TokenBank newTokenBank = new TokenBank();
                    newTokenBank.setTokenbankname(tm.getRequest().getTokenBankName());
                    newTokenBank.setTokennumber(token);
                    newTokenBank.setStatus(TokenizerConstants.ACTIVE);
                    tokenizerDao.save(newTokenBank);
                    break;
                }
            }

        } else {
            token = vault.getTokennumber();
        }

        return token;

    }

    private String generateToken(String accountNumber, String cardType) {
        String token = "";
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 13; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        token = sb.toString();

        String last4 = accountNumber.substring(accountNumber.length() - 4);
        String mediaValue = findMediaValue(cardType);
        
        token = "9"+ mediaValue + token + last4;
        return token;
    }
    
    
    private String findMediaValue(String cardType)
    {
        switch(cardType)
        {
            case TokenizerConstants.MILSTAR:
                return "1";

            case TokenizerConstants.AMEX:
                return "2";

            case TokenizerConstants.DISCOVER:
                return "3";
            case TokenizerConstants.VISA:
                return "4";
            case TokenizerConstants.MASTER:
                return "5";
            case TokenizerConstants.GIFTCARD:
                return "6";
                
        }
        
        return "0";
    }
}