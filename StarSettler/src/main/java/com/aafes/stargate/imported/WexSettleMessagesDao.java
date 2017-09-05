/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.imported;

import com.aafes.starsettler.control.CassandraSessionFactory;
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
        if (session == null) {
            session = factory.getSession();
        }
        setMapper(new MappingManager(session).mapper(WexSettleEntity.class));
    }

    public void saveToWex(List<WexSettleEntity> wexSettleEntityList) {
        for (WexSettleEntity wexSettleEntity : wexSettleEntityList) {
            mapper.save(wexSettleEntity);
        }
    }

    public void updateWexSettleData(List<WexSettleEntity> Wexdata, String In_Progress) {
        factory = new CassandraSessionFactory();
        String status = "compleated";
        try {
            for (WexSettleEntity settleData : Wexdata) {
                String query = "update starsettler.settlemessages set "
                        + " filesequenceid = '" + settleData.getFileSequenceId()
                        + "', settlestatus = '" + status
                        + "' where receiveddate = '" + settleData.getReceivedDate()
                        + "' and settelmentdate ='" + settleData.getSettelmentDate()
                        + "' and transactiontype ='" + settleData.getTransactionType()
                        + "' and ordernumber = '" + settleData.getOrderNumber()
                        + "' and tid='" + settleData.getTid() + "';";
                factory.getSession().execute(query);
            }
        } catch (Exception e) {
            System.out.println("com.aafes.starsettler.dao.SettleMessageDAO.updateWexData()");
        }

    }

    public List<WexSettleEntity> getWexTransactions(String tid, String processDate, String status) {

        List<WexSettleEntity> wexSettleMessagesList = new ArrayList<>();
        String query = "";
        query = "SELECT * FROM stargate.wexsettlemessages "
                + "where receiveddate = '" + processDate + "', "
                + "settlestatus = '" + status + "', "
                + "tid = '" + tid + "' ALLOW FILTERING;";

        ResultSet result = factory.getSession().execute(query);
        for (Row row : result) {
            WexSettleEntity wexSettleMessages = new WexSettleEntity();
            wexSettleMessages.setReceivedDate(row.getString("receiveddate"));
            wexSettleMessages.setSettleStatus(row.getString("settlestatus"));
            wexSettleMessages.setTransactionType(row.getString("transactiontype"));
            wexSettleMessages.setOrderNumber(row.getString("ordernumber"));
            wexSettleMessages.setTid(row.getString("tid"));
            wexSettleMessages.setAmount(row.getString("amount"));
            wexSettleMessages.setAppName(row.getString("appname"));
            wexSettleMessages.setAppVersion(row.getString("appversion"));
            wexSettleMessages.setAuthRef(row.getString("authref"));
            wexSettleMessages.setCardTrack(row.getString("cardtrack"));
            wexSettleMessages.setCatFlag(row.getString("catflag"));
            wexSettleMessages.setDriverId(row.getString("driverid"));
            wexSettleMessages.setFileSequenceId(row.getString("filesequenceid"));
            wexSettleMessages.setOdometer(row.getString("odometer"));
            wexSettleMessages.setProduct(row.getList("product", String.class));
            wexSettleMessages.setPumpCat(row.getString("pumpcat"));
            wexSettleMessages.setPumpService(row.getString("pumpservice"));
            wexSettleMessages.setService(row.getString("service"));
            // wexSettleMessages.setSettelmentDate(row.getString("settelmentdate"));
            //wexSettleMessages.setSettelmentTime(row.getString("settelmenttime"));
            wexSettleMessages.setTransactionCode(row.getString("transactioncode"));
            wexSettleMessages.setTransactionId(row.getString("transactionid"));
            wexSettleMessages.setVehicleId(row.getString("vehicleid"));
            wexSettleMessages.setTransactionTime(row.getString("transactiontime"));
            wexSettleMessages.setPumpNumber(row.getString("pumpnumber"));

            // wexSettleMessages.set(row.getString("batchtid"));
//            
//
//            wexSettleMessages.setTransactionType(row.getString("transtype"));
//            wexSettleMessages.setOrderNumber(row.getString("transnbr"));
//
//            wexSettleMessages.setSettelmentTime(row.getString("transtime"));
//
//            wexSettleMessages.set(row.getString("pumpnbr"));
//            wexSettleMessages.setAmount(row.getString("pumpamount"));
//
//            wexSettleMessages.setAmount(row.getString("amount"));
//
//            wexSettleMessages.setVehicleId(row.getString("vehicleid"));
//            wexSettleMessages.setOrderDate(row.getString("orderDate"));
//            wexSettleMessages.setSequenceId(row.getString("sequenceId"));
//
//            //  wexSettleMessages.setTime(row.getString("time"));
//            wexSettleMessages.setService(row.getString("service"));
            wexSettleMessagesList.add(wexSettleMessages);
        }
        //LOG.info("WexSettleMessageDAO.find method is ended");
        return wexSettleMessagesList;
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
    
     @EJB
    public void setCassandraSessionFactory(CassandraSessionFactory factory) {
        this.factory = factory;
    }

}
