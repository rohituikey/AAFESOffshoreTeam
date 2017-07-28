/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenizer;

import com.aafes.token.TokenMessage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.security.SecureRandom;
import java.util.List;
import org.slf4j.LoggerFactory;

@Stateless
public class IssueService {
    
    @EJB
    private VaultDao vaultDao;
    @EJB
    private TokenizerDao tokenizerDao;
    
    private int validity = 1;
    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(TokenizerDao.class.getSimpleName());
    
    public String process(TokenMessage tm) throws ParseException {
        LOG.info("Entry in  process method of IssueService......");
        String token = "";
        Vault vault = null;
        vault = getExistingToken(tm);
        tm.getRequest().getTokenBankName();
        
        if (vault == null) {
            
            token = this.generarte(tm);
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
        
        LOG.info("Exit from process method of IssueService......");
        return token;
        
    }
    
    private Vault getExistingToken(TokenMessage tm) {
        LOG.info("Entry in  getExistingToken method of IssueService......");
        Vault vault = null;
        List<String> tokensList = tokenizerDao.getAllTokensByName(tm.getRequest().getTokenBankName());
        if (tokensList == null) {
            return vault;
        }
        
        Vault existingVault;
        String accountNumber = "";
        for (String token : tokensList) {
            existingVault = vaultDao.findByToken(token);
            if (existingVault != null) {
                if (existingVault.getAccountnumber().contains(tm.getRequest().getTokenBankName())) {
                    accountNumber = existingVault.getAccountnumber();
                    accountNumber = accountNumber.replaceAll(tm.getRequest().getTokenBankName(), "");
                    
                }
                if (tm.getRequest().getAccount().equalsIgnoreCase(accountNumber)) {
                    existingVault.setAccountnumber(accountNumber);
                    vault = existingVault;
                }
            }
        }
        
        LOG.info("Exit from getExistingToken method of IssueService......");
        return vault;
    }
    
    private String generarte(TokenMessage tm) {
        
        LOG.info("Entry into generarte method of IssueService......");
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
        
        LOG.info("Exit from generarte method of IssueService......");
        return token;
    }
    
    private String generateToken(String accountNumber, String cardType) {
        
        LOG.info("Entry into generateToken method of IssueService......");
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
        LOG.info("Exit from generateToken method of IssueService......");
        return token;
    }
    
    private String findMediaValue(String cardType) {
        
        LOG.info("Entry into findMediaValue method of IssueService......");
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
        
        LOG.info("Entry into getExpirationDate method of IssueService......");
        
        String expDate = "";
        
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, validity);
        Date newDate = c.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        expDate = df.format(newDate);
        LOG.info("Exit from getExpirationDate method of IssueService......");
        return expDate;
    }
    
    protected void setVaultDao(VaultDao vaultDao) {
        this.vaultDao = vaultDao;
    }
    
    protected void setTokenizerDao(TokenizerDao tokenizerDao) {
        this.tokenizerDao = tokenizerDao;
    }
    
}
