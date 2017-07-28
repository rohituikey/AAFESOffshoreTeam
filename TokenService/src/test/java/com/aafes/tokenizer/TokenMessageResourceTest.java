/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenizer;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import com.aafes.tokenservice.util.ResponseType;

/**
 *
 * @author shganji
 */
public class TokenMessageResourceTest {

    TokenMessageResource tmr = new TokenMessageResource();
    TokenMessageResource tmrExep = new TokenMessageResource();

    @Before
    public void setup() {
        Vault vault = new Vault();
        vault.setAccountnumber("6019440000000001234");
        vault.setTokennumber("91abcdef1234");
        vault.getTokennumber();
        VaultDao vaultDao = mock(VaultDao.class);
        when(vaultDao.findByAccount(any())).thenReturn(null);
        when(vaultDao.findByToken(any())).thenReturn(vault);
        doNothing().when(vaultDao).save(any());
        TokenBank tb = new TokenBank();
        tb.setTokennumber("91abcdef1234");
        tb.setExpirydate("20181212");
        tb.setStatus(TokenizerConstants.ACTIVE);
        tb.setTokenbankname("Ecomm1");
        tb.setDescription("Testing");
        tb.getDescription();
        tb.getStatus();
        tb.getTokennumber();
        tb.getTokenbankname();
        TokenizerDao tokenizerDao = mock(TokenizerDao.class);
        when(tokenizerDao.find(any(), any())).thenReturn(tb);
        doNothing().when(tokenizerDao).save(any());
        Configurator cfg = new Configurator();
        cfg.load();
        IssueService issueService = new IssueService();
        issueService.setVaultDao(vaultDao);
        issueService.setTokenizerDao(tokenizerDao);
        LookUpService lookUpService = new LookUpService();
        lookUpService.setTokenizerDao(tokenizerDao);
        lookUpService.setVaultDao(vaultDao);
        Tokenizer tokenizer = new Tokenizer();
        tokenizer.setConfigurator(cfg);
        tokenizer.setIssueService(issueService);
        tokenizer.setLookUpService(lookUpService);
        tmr.setTokenizer(tokenizer);
    }
    
    @Before
    public void setupExcep() {
        Vault vault = new Vault();
        vault.setAccountnumber("6019440000000001234");
        vault.setTokennumber("91abcdef1234");
        vault.getTokennumber();
        VaultDao vaultDao = mock(VaultDao.class);
        when(vaultDao.findByAccount(any())).thenReturn(null);
        when(vaultDao.findByToken(any())).thenReturn(null);
        doNothing().when(vaultDao).save(any());
        TokenBank tb = new TokenBank();
        tb.setTokennumber("91abcdef1234");
        tb.setExpirydate("20181212");
        tb.setStatus(TokenizerConstants.ACTIVE);
        tb.setTokenbankname("Ecomm1");
        tb.setDescription("Testing");
        tb.getDescription();
        tb.getStatus();
        tb.getTokennumber();
        tb.getTokenbankname();
        TokenizerDao tokenizerDao = mock(TokenizerDao.class);
        when(tokenizerDao.find(any(), any())).thenReturn(tb);
        doNothing().when(tokenizerDao).save(any());
        Configurator cfg = new Configurator();
        cfg.load();
        IssueService issueService = new IssueService();
        issueService.setVaultDao(vaultDao);
        issueService.setTokenizerDao(tokenizerDao);
        LookUpService lookUpService = new LookUpService();
        lookUpService.setTokenizerDao(tokenizerDao);
        lookUpService.setVaultDao(vaultDao);
        Tokenizer tokenizer = new Tokenizer();
        tokenizer.setConfigurator(cfg);
        tokenizer.setIssueService(issueService);
        tokenizer.setLookUpService(lookUpService);
        tmrExep.setTokenizer(tokenizer);
    }

    @Test
    public void testIssueXml() {
        try {
            String requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<TokenMessage xmlns='http://www.aafes.com/token' >\n"
                    + "	<Request>\n"
                    + "		<RequestType>Issue</RequestType>\n"
                    + "		<Media>Visa</Media>\n"
                    + "		<AccountType>Pan</AccountType>\n"
                    + "		<Account>6019440000000001234</Account>\n"
                    + "		<TokenBankName>ECOMM001</TokenBankName>\n"
                    + "	</Request>\n"
                    + "</TokenMessage>\n"
                    + "";
            String responseXml = tmr.postXml(requestXml);
            if (responseXml.contains(ResponseType.SUCCESS)) {
                assertEquals(true, true);
            } else {
                assertEquals(true, false);
            }
        } catch (Exception e) {
            assertEquals(true, false);
        }

    }

    @Test
    public void testLookupXml() {
        try {
            String requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<TokenMessage xmlns='http://www.aafes.com/token' >\n"
                    + "	<Request>\n"
                    + "		<RequestType>Lookup</RequestType>\n"
                    + "		<Media>Visa</Media>\n"
                    + "		<AccountType>Token</AccountType>\n"
                    + "		<Account>92xxxxxxxxxxxxx1234</Account>\n"
                    + "		<TokenBankName>ECOMM001</TokenBankName>\n"
                    + "	</Request>\n"
                    + "</TokenMessage>\n"
                    + "";
            String responseXml = tmr.postXml(requestXml);
            if (responseXml.contains(ResponseType.SUCCESS)) {
                assertEquals(true, true);
            } else {
                assertEquals(true, false);
            }
        } catch (Exception e) {
            assertEquals(true, false);
        }

    }

    @Test
    public void testException()
    {
        try {
            String requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<TokenMessage xmlns='http://www.aafes.com/token' >\n"
                    + "	<Request>\n"
                    + "		<RequestType>Lookup</RequestType>\n"
                    + "		<Media>Visa</Media>\n"
                    + "		<AccountType>Token</AccountType>\n"
                    + "		<Account>92xxxxxxxxxxxxx1234</Account>\n"
                    + "		<TokenBankName>ECOMM001</TokenBankName>\n"
                    + "	</Request>\n"
                    + "</TokenMessage>\n"
                    + "";
            String responseXml = tmrExep.postXml(requestXml);
            if (responseXml.contains(ResponseType.FAILED)) {
                assertEquals(true, true);
            } else {
                assertEquals(true, false);
            }
             requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<TokenMessage xmlns='http://www.aafes.com/token' >\n"
                    + "	<Request>\n"
                    + "		<RequestType>Issue</RequestType>\n"
                    + "		<Media>Visa</Media>\n"
                    + "		<AccountType>Pan</AccountType>\n"
                    + "		<Account>6019440000000001234</Account>\n"
                    + "		<TokenBankName>ECOMM001</TokenBankName>\n"
                    + "	</Request>\n"
                    + "</TokenMessage>\n"
                    + "";
             responseXml = tmrExep.postXml(requestXml);
            if (responseXml.contains(ResponseType.SUCCESS)) {
                assertEquals(true, true);
            } else {
                assertEquals(true, false);
            }
            
            Tokenizer t = new Tokenizer();
            t.setConfigurator(new Configurator());
        tmrExep.setTokenizer(t);
        responseXml = tmrExep.postXml(requestXml);
        if(responseXml.contains("INTERNAL_SERVER_ERROR"))
        {
            assertEquals(true, true);
            } else {
                assertEquals(true, false);
            }
                
                
        } catch (Exception e) {
            assertEquals(true, false);
        }
    }
    
    @Test
    public void testInvalidXml() {
        String requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<TokenMessage >\n"
                + "	<Request>\n"
                + "		<RequestType>Lookup</RequestType>\n"
                + "		<Media>Visa</Media>\n"
                + "		<AccountType>Token</AccountType>\n"
                + "		<Account>92xxxxxxxxxxxxx1234</Account>\n"
                + "		<TokenBankName>ECOMM001</TokenBankName>\n"
                + "	</Request>\n"
                + "</TokenMessage>\n"
                + "";
        
        String responseXml = tmr.postXml(requestXml);
        if(responseXml.contains("Invalid XML"))
            assertEquals(true, true);
        else assertEquals(true, false);
        
        
    }
}
