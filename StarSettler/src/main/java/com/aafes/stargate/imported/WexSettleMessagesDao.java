/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.imported;

import com.aafes.starsettler.control.CassandraSessionFactory;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

/**
 *
 * @author alugumetlas
 */
@Stateless
public class WexSettleMessagesDao {
    private Mapper mapper;
    private CassandraSessionFactory factory;
    Session session;
    @PostConstruct
    public void postConstruct() {
        if(session == null) session = factory.getSession();
        setMapper(new MappingManager(session).mapper(WexSettleEntity.class));
    }
   public void saveToWex(List<WexSettleEntity> wexSettleEntityList) {
        for (WexSettleEntity wexSettleEntity : wexSettleEntityList) {
            mapper.save(wexSettleEntity);
        }
    }

    public Mapper getMapper() {
        return mapper;
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    public CassandraSessionFactory getFactory() {
        return factory;
    }

    public void setFactory(CassandraSessionFactory factory) {
        this.factory = factory;
    }
  
}
