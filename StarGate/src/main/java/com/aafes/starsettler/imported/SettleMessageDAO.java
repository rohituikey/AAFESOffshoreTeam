/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.imported;

import com.aafes.stargate.control.CassandraSessionFactory;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
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
    
    /**
     * 
     * @param settleEntity 
     */
    public void update(SettleEntity settleEntity) {
        //Write a update query
        // mapper.delete(settleEntity);
        try {
            PreparedStatement preparedStatementObj = null;
            String[] bindVaribleArray = new String[7];
            BoundStatement boundStatement = null;
            bindVaribleArray[0] = settleEntity.getSettlestatus();
            bindVaribleArray[1] = settleEntity.getReceiveddate();
            bindVaribleArray[2] = settleEntity.getOrderNumber();
            bindVaribleArray[3] = settleEntity.getSettleDate();
            bindVaribleArray[4] = settleEntity.getCardType();
            bindVaribleArray[5] = settleEntity.getTransactionType();
            bindVaribleArray[6] = settleEntity.getClientLineId();
            bindVaribleArray[6] = settleEntity.getTransactionId();

            preparedStatementObj = factory.getSession().prepare("UPDATE STARSETTLER.SETTLEENTITY SET SETTLESTATUS=? WHERE  receiveddate = ? AND ordernumber = ? AND settledate = ? AND cardtype = ? AND transactiontype = ? AND clientlineid = ? AND transactionid=?");
            boundStatement = new BoundStatement(preparedStatementObj);
            factory.getSession().execute(boundStatement.bind((Object[]) bindVaribleArray));
        } catch (Exception e) {
            LOG.error("Exception " + e.getMessage());
        }
       
    }

    
    public SettleEntity find(String uuid, String ordernumber, String rrn, String transactionid)
    {
        return (SettleEntity) mapper.get(uuid,ordernumber,rrn,transactionid);
    }

    @EJB
    public void setCassandraSessionFactory(CassandraSessionFactory factory) {
        this.factory = factory;
    }
}
