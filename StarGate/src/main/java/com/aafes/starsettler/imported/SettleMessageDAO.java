/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.imported;

import com.aafes.stargate.control.CassandraSessionFactory;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ganjis
 */
@Stateless
public class SettleMessageDAO {
    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(SettleMessageDAO.class.getSimpleName());

    private Mapper mapper;
    private CassandraSessionFactory factory;

    @PostConstruct
    public void postConstruct() {
        Session session = factory.getSession();
        mapper = new MappingManager(session).mapper(SettleEntity.class);
    }

    public void save(List<SettleEntity> settleEntityList) {
        for (SettleEntity settleEntity : settleEntityList) {
            mapper.save(settleEntity);
        }
    }

    

    @EJB
    public void setCassandraSessionFactory(CassandraSessionFactory factory) {
        this.factory = factory;
    }
}
