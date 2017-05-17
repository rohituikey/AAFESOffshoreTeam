package com.aafes.stargate.dao;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.CassandraSessionFactory;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

@Stateless
public class TransactionDAO {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(TransactionDAO.class.getSimpleName());

    private Mapper mapper;
    private CassandraSessionFactory factory;

    @PostConstruct
    public void postConstruct() {
        Session session = factory.getSession();
        mapper = new MappingManager(session).mapper(Transaction.class);
    }


    public void save(Transaction transaction) {
        mapper.save(transaction);
    }

    public Transaction find(String identityuuid, String rrn, String requesttype) {
        return (Transaction) mapper.get(identityuuid, rrn, requesttype);
    }
//
//    public void delete(Transaction transaction) {
//        mapper.delete(transaction);
//    }
    @EJB
    public void setCassandraSessionFactory(CassandraSessionFactory factory) {
        this.factory = factory;
}
}
