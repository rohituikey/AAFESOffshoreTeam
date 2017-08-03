/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenizer;

import com.aafes.tokenservice.util.Encryptor;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

@Stateless
public class VaultDao {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(TokenizerDao.class.getSimpleName());
    private Mapper mapper;
    private CassandraSessionFactory factory;
    private Encryptor encryptor;
    private String cryptoPath;
    private String logPath;

    @PostConstruct
    public void postConstruct() {
        LOG.info("Entry in postConstruct method of VaultDao..");
        Session session = factory.getSession();
        mapper = new MappingManager(session).mapper(Vault.class);
        cryptoPath = System.getProperty("jboss.server.config.dir") + "/crypto.keys";
        logPath = System.getProperty("jboss.server.config.dir") + "/crypto.log4j.properties";
        encryptor = new Encryptor(cryptoPath, logPath);
        LOG.info("Exit from postConstruct method of VaultDao..");
    }

    public void save(Vault tb) {

        LOG.info("Entry in save method of VaultDao..");
        if (encryptor != null) {
            String encryptedAccount = encryptor.encrypt(tb.getAccountnumber());
//            String encryptedToken = encryptor.encrypt(tb.getTokennumber());
            tb.setAccountnumber(encryptedAccount);
            tb.setTokennumber(tb.getTokennumber());
        }

        mapper.save(tb);
        LOG.info("Exit from save method of VaultDao..");
    }

    public Vault findByToken(String tokenNumber) {

        LOG.info("Entry in findByToken method of VaultDao..");
        Vault vault = null;
        if (encryptor != null) {
            //  String encryptedToken = encryptor.encrypt(tokenNumber);
            vault = (Vault) mapper.get(tokenNumber);
            if (vault != null) {
                String decryptedAccount = encryptor.decrypt(vault.getAccountnumber());
                // String decryptedToken = encryptor.decrypt(vault.getTokennumber());
                vault.setAccountnumber(decryptedAccount);
                //  vault.setTokennumber(decryptedToken);
            }

        }
        LOG.info("Exit from findByToken method of VaultDao..");
        return vault;

    }

    public Vault findByAccount(String accountNumber) {

        LOG.info("Entry in findByAccount method of VaultDao..");
        Vault vault = null;
        ResultSet resultSet = factory.getSession().execute("select * from tokenizer.vault where accountnumber = '"
                + accountNumber + "' allow filtering;");

        Result<Vault> vaultSet = mapper.map(resultSet);

        if (vaultSet != null) {
            for (Vault v : vaultSet) {
                vault = v;
                break;
            }

        }

        if (encryptor != null && vault != null) {
            String decryptedAccount = encryptor.decrypt(vault.getAccountnumber());
            //   String decryptedToken = encryptor.decrypt(vault.getTokennumber());
            vault.setAccountnumber(decryptedAccount);
            //  vault.setTokennumber(decryptedToken);
        }
        LOG.info("Exit from findByAccount method of VaultDao..");
        return vault;

    }

    @EJB
    public void setCassandraSessionFactory(CassandraSessionFactory factory) {
        this.factory = factory;
    }

    protected void setCryptoPath(String cryptoPath) {
        this.cryptoPath = cryptoPath;
    }

    protected void setLogPath(String logPath) {
        this.logPath = logPath;
    }
}