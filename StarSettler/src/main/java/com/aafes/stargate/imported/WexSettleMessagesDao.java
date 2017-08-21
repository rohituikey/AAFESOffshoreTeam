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
import javax.ejb.Stateless;

/**
 *
 * @author alugumetlas
 */
@Stateless
public class WexSettleMessagesDao {

    private Mapper mapper;
    private CassandraSessionFactory factory;

    @PostConstruct
    public void postConstruct() {
        Session session = factory.getSession();
        setMapper(new MappingManager(session).mapper(WexSettleEntity.class));
    }

    public void saveToWex(List<WexSettleEntity> wexSettleEntityList) {
        for (WexSettleEntity wexSettleEntity : wexSettleEntityList) {
            mapper.save(wexSettleEntity);
        }
    }

    public List<WexSettleEntity> getWexTransactions(String tid, String processDate, String status) {
        //LOG.info("WexSettleMessageDAO.find method is started");

        List<WexSettleEntity> wexSettleMessagesList = new ArrayList<>();
        String query = "";
        query = "SELECT * FROM stargate.wexsettlemessages "
                + "where transdate = '" + processDate + "', "
                + "settlestatus = '" + status + "', "
                + "batchtid = '" + tid + "' ALLOW FILTERING;";
        //LOG.info("Query : " + query);
        ResultSet result = factory.getSession().execute(query);
        for (Row row : result) {
            WexSettleEntity wexSettleMessages = new WexSettleEntity();
            wexSettleMessages.setTransactionFileDate(row.getString("transactionfiledate"));
            wexSettleMessages.setTransactionFileTime(row.getString("transactionfiletime"));
            wexSettleMessages.setTransactionFileSequence(row.getString("transactionfilesequence"));
            // wexSettleMessages.set(row.getString("batchtid"));
            wexSettleMessages.setTId(row.getString("batchid"));
            wexSettleMessages.setAppName(row.getString("batchapp"));
            wexSettleMessages.setAppVersion(row.getString("batchversion"));
            wexSettleMessages.setTranscardCode(row.getString("transcardCode"));
            wexSettleMessages.setTransactiontype(row.getString("transtype"));
            wexSettleMessages.setTransNbr(row.getString("transnbr"));
            wexSettleMessages.setTransDate(row.getString("transdate"));
            wexSettleMessages.setTransTime(row.getString("transtime"));
            wexSettleMessages.setCardTrack(row.getString("cardtrack"));
            wexSettleMessages.setPumpCat(row.getString("pumpcat"));
            wexSettleMessages.setPumpService(row.getString("pumpservice"));
            wexSettleMessages.setPumpNbr(row.getString("pumpnbr"));
            wexSettleMessages.setPumpAmount(row.getString("pumpamount"));
            wexSettleMessages.setProduct(row.getList("product", String.class));
            wexSettleMessages.setOdometer(row.getString("odometer"));
            wexSettleMessages.setAmount(row.getString("amount"));
            wexSettleMessages.setAuthRef(row.getString("authref"));
            wexSettleMessages.setDriverId(row.getString("driverid"));
            wexSettleMessages.setVehicleId(row.getString("vehicleid"));
            wexSettleMessages.setOrderDate(row.getString("orderDate"));
            wexSettleMessages.setSequenceId(row.getString("sequenceId"));
            wexSettleMessages.setSettleId(row.getString("settleId"));
            wexSettleMessages.setSettlestatus(row.getString("settlestatus"));
            wexSettleMessages.setTime(row.getString("time"));
            wexSettleMessages.setCatflag(row.getString("catflag"));
            wexSettleMessages.setService(row.getString("service"));
            wexSettleMessagesList.add(wexSettleMessages);
        }
        //LOG.info("WexSettleMessageDAO.find method is ended");
        return wexSettleMessagesList;
    }

    public void updateWexSettleData(List<WexSettleEntity> Wexdata, String In_Progress) {
        factory = new CassandraSessionFactory();
        String status = "compleated";
        try {
            for (WexSettleEntity settleData : Wexdata) {
                String query = "update starsettler.settlemessages set "
                        + " filesequenceid = '" + settleData.getSequenceId()
                        + "', settlestatus = '" + status
                        + "' where receiveddate = '" + settleData.getOrderDate()
                        + "' and ordernumber = '" + settleData.getOrdernumber()
                        + "' and settledate='" + settleData.getTransDate()
                        + "' and cardtype='" + settleData.getTransactiontype()
                        + "' and transactiontype = '" + settleData.getTransactiontype()
                        //   + "' and clientlineid='" + settleData.getClientLineId()  client id not present
                        + "' and transactionid='" + settleData.getTransactionId() + "';";
                factory.getSession().execute(query);
            }
        } catch (Exception e) {
            System.out.println("com.aafes.starsettler.dao.SettleMessageDAO.updateWexData()");
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
