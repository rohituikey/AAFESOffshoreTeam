package com.aafes.stargate.dao;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.CassandraSessionFactory;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import java.util.ArrayList;
import java.util.List;
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
    private Session session = null;
    private ResultSet resultSet = null;

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

    public List<Transaction> returnTransactions() {
        List<Transaction> result = new ArrayList<>();
        String query = "Select * from Stargate.transactions";
        resultSet = session.execute(query);

        Result<Transaction> resultTrans = mapper.map(resultSet);
        for(Transaction t : resultTrans){
            result.add(t);
        }
        return result;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
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
