/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.stargate.control.CassandraSessionFactory;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
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

/**
 *
 * @author ganjis
 */
@Stateless
public class WexSettleMessageDAO {

    /**
     * @param mapper the mapper to set
     */
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(WexSettleMessageDAO.class.getSimpleName());

    private Mapper mapper;
    private CassandraSessionFactory factory;
    private ResultSet resultSet;
    private Session session;

    @PostConstruct
    public void postConstruct() {
        Session session = factory.getSession();
        setMapper(new MappingManager(session).mapper(WexSettleMessages.class));
    }

    public void save(List<WexSettleMessages> wexSettleMessagesList) {
        for (WexSettleMessages wexSettleMessages : wexSettleMessagesList) {
            mapper.save(wexSettleMessages);
        }
    }

    /**
     *
     * @param wexSettleMessages
     */
    public void update(WexSettleMessages wexSettleMessages) {
        //Write a update query
        // mapper.delete(settleEntity);
        mapper.save(wexSettleMessages);
    }

    public WexSettleMessages find(String transactionfiledate, String transactionfiletime, String transactionfilesequence, 
            String batchtid, String batchid) {
        LOG.info("WexSettleMessageDAO.find method is started");

        WexSettleMessages wexSettleMessages = null;
        String query = "";
        query = "SELECT * FROM stargate.wexsettlemessages "
                + "where transactionfiledate = '" + transactionfiledate + "', "
                + "transactionfiletime = '" + transactionfiletime + "', "
                + "transactionfilesequence = '" + transactionfilesequence +"', "
                + "batchtid = '" + batchtid + "', "
                + "batchid = '" + batchid + "' ALLOW FILTERING;";
        LOG.info("Query : " + query);
        ResultSet result = factory.getSession().execute(query);
        for (Row row : result) {
            wexSettleMessages = new WexSettleMessages();
            wexSettleMessages.setTransactionfiledate(row.getString("transactionfiledate"));
            wexSettleMessages.setTransactionfiletime(row.getString("transactionfiletime"));
            wexSettleMessages.setTransactionfilesequence(row.getString("transactionfilesequence"));
            wexSettleMessages.setBatchtid(row.getString("batchtid"));
            wexSettleMessages.setBatchid(row.getString("batchid"));
            wexSettleMessages.setBatchapp(row.getString("batchapp"));
            wexSettleMessages.setBatchversion(row.getString("batchversion"));
            wexSettleMessages.setTranscardCode(row.getString("transcardCode"));
            wexSettleMessages.setTranstype(row.getString("transtype"));
            wexSettleMessages.setTransnbr(row.getString("transnbr"));
            wexSettleMessages.setTransdate(row.getString("transdate"));
            wexSettleMessages.setTranstime(row.getString("transtime"));
            wexSettleMessages.setCardtrack(row.getString("cardtrack"));
            wexSettleMessages.setPumpcat(row.getString("pumpcat"));
            wexSettleMessages.setPumpservice(row.getString("pumpservice"));
            wexSettleMessages.setPumpnbr(row.getString("pumpnbr"));
            wexSettleMessages.setPumpamount(row.getString("pumpamount"));
            wexSettleMessages.setProduct(row.getString("product"));
            wexSettleMessages.setOdometer(row.getString("odometer"));
            wexSettleMessages.setAmount(row.getString("amount"));
            wexSettleMessages.setAuthref(row.getString("authref"));
            wexSettleMessages.setDriverid(row.getString("driverid"));
            wexSettleMessages.setVehicleid(row.getString("vehicleid"));
            wexSettleMessages.setOrderDate(row.getString("orderDate"));
            wexSettleMessages.setSequenceId(row.getString("sequenceId"));
            wexSettleMessages.setSettleId(row.getString("settleId"));
            wexSettleMessages.setSettlestatus(row.getString("settlestatus"));
            wexSettleMessages.setTime(row.getString("time"));
            wexSettleMessages.setCatflag(row.getString("catflag"));
            wexSettleMessages.setService(row.getString("service"));
        }
        LOG.info("WexSettleMessageDAO.find method is ended");
        return wexSettleMessages;
    }
    
    public List<WexSettleMessages> returnTransactions() {
        List<WexSettleMessages> result = new ArrayList<>();
        String query = "Select * from Stargate.transactions";
        resultSet = session.execute(query);

        Result<WexSettleMessages> resultTrans = mapper.map(resultSet);
        for(WexSettleMessages t : resultTrans){
            result.add(t);
        }
        return result;
    }

    @EJB
    public void setCassandraSessionFactory(CassandraSessionFactory factory) {
        this.factory = factory;
    }
}