/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenizer;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
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

    @PostConstruct
    public void postConstruct() {
        Session session = factory.getSession();
        mapper = new MappingManager(session).mapper(TokenBank.class);
    }
    
    public void save(TokenBank tb)
    {
        mapper.save(tb);
    }
    
    public TokenBank find(String tokenNumber, String tokenBankName)
    {
        return (TokenBank) mapper.get(tokenNumber,tokenBankName);
        
    }
    
     @EJB
    public void setCassandraSessionFactory(CassandraSessionFactory factory) {
        this.factory = factory;
}
}