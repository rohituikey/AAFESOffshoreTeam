/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenizer;

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

    @PostConstruct
    public void postConstruct() {
        Session session = factory.getSession();
        mapper = new MappingManager(session).mapper(Vault.class);
    }
    
    public void save(Vault tb)
    {
        mapper.save(tb);
    }
    
    public Vault findByToken(String tokenNumber)
    {
        return (Vault) mapper.get(tokenNumber);
        
    }
    
    public Vault findByAccount(String accountNumber)
    {
        Vault vault = null;
        ResultSet resultSet = factory.getSession().execute("select * from tokenizer.vault where accountnumber = '"
                +accountNumber +"' allow filtering;");
        
        Result<Vault> vaultSet = mapper.map(resultSet);
        
        if(vaultSet != null)
        {
            for(Vault v : vaultSet)
            {
                 vault = v;
                 break;
            }
           
        }
        
        return vault;
        
    }
    
     @EJB
    public void setCassandraSessionFactory(CassandraSessionFactory factory) {
        this.factory = factory;
}
}