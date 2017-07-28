/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenizer;

import com.aafes.tokenservice.util.Encryptor;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

@Stateless
public class TokenizerDao {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(TokenizerDao.class.getSimpleName());
    private Mapper mapper;
    private CassandraSessionFactory factory;
    private Encryptor encryptor;
    private String cryptoPath;
    private String logPath;

    @PostConstruct
    public void postConstruct() {
        LOG.info("Entry in  postConstruct method of TokenizerDao......");
        Session session = factory.getSession();
        mapper = new MappingManager(session).mapper(TokenBank.class);
        cryptoPath = System.getProperty("jboss.server.config.dir") + "/crypto.keys";
        logPath = System.getProperty("jboss.server.config.dir") + "/crypto.log4j.properties";
        encryptor = new Encryptor(cryptoPath, logPath);
        LOG.info("Exit from postConstruct method of TokenizerDao......");
    }

    public void save(TokenBank tb) {

        LOG.info("Entry in  save method of TokenizerDao......");
//         if(encryptor != null)
//        {
//            String encryptedToken = encryptor.encrypt(tb.getTokennumber());
//            tb.setTokennumber(encryptedToken);
//        }
        mapper.save(tb);
        LOG.info("Exit from save method of TokenizerDao......");
    }

    public TokenBank find(String tokenNumber, String tokenBankName) {

        LOG.info("Entry in  find method of TokenizerDao......");
        // String encryptedToken = "";
        if (encryptor != null) {
            // encryptedToken = encryptor.encrypt(tokenNumber);
        }

        return (TokenBank) mapper.get(tokenNumber, tokenBankName);

    }

    public List<String> getAllTokensByName(String tokenBankName) {

        LOG.info("Entry in  getAllTokensByName method of TokenizerDao......");
        List<String> tokensList = new ArrayList<>();
        String query = "select * from tokenizer.tokenbank where tokenbankname = '" + tokenBankName + "' ALLOW FILTERING";

        ResultSet result = factory.getSession().execute(query);

        for (Row row : result) {
            tokensList.add(row.getString("tokennumber"));
        }

        LOG.info("Entry in  getAllTokensByName method of TokenizerDao......");
        return tokensList;
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
