///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.aafes.stargate.gateway.wex;
//
//import com.aafes.stargate.control.CassandraSessionFactory;
//import com.datastax.driver.core.ResultSet;
//import com.datastax.driver.core.Row;
//import com.datastax.driver.core.Session;
//import com.datastax.driver.mapping.Mapper;
//import com.datastax.driver.mapping.MappingManager;
//import com.datastax.driver.mapping.Result;
//import java.util.ArrayList;
//import java.util.List;
//import javax.annotation.PostConstruct;
//import javax.ejb.EJB;
//import javax.ejb.Stateless;
//import org.slf4j.LoggerFactory;
//
///**
// *
// * @author author burangir
// */
//@Stateless
//public class WexSettleEntityDAO {
//
//    /**
//     * @param mapper the mapper to set
//     */
//    public void setMapper(Mapper mapper) {
//        this.mapper = mapper;
//    }
//
//    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(WexSettleEntityDAO.class.getSimpleName());
//
//    private Mapper mapper;
//    private CassandraSessionFactory factory;
//    private ResultSet resultSet;
//    private Session session;
//
//    @PostConstruct
//    public void postConstruct() {
//        Session session = factory.getSession();
//        setMapper(new MappingManager(session).mapper(WexSettleEntiry.class));
//    }
//
//    public void save(List<WexSettleEntiry> wexSettleMessagesList) {
//        for (WexSettleEntiry wexSettleMessages : wexSettleMessagesList) {
//            mapper.save(wexSettleMessages);
//        }
//    }
//
//    /**
//     *
//     * @param wexSettleMessages
//     */
//    public void update(WexSettleEntiry wexSettleMessages) {
//        //Write a update query
//        // mapper.delete(settleEntity);
//        mapper.save(wexSettleMessages);
//    }
//
//    public WexSettleEntiry find(String transactionfiledate, String transactionfiletime, String transactionfilesequence, 
//            String batchtid, String batchid) {
//        LOG.info("WexSettleMessageDAO.find method is started");
//
//        WexSettleEntiry wexSettleMessages = null;
//        String query = "";
//        query = "SELECT * FROM stargate.wexsettlemessages "
//                + "where transactionfiledate = '" + transactionfiledate + "', "
//                + "transactionfiletime = '" + transactionfiletime + "', "
//                + "transactionfilesequence = '" + transactionfilesequence +"', "
//                + "batchtid = '" + batchtid + "', "
//                + "batchid = '" + batchid + "' ALLOW FILTERING;";
//        LOG.info("Query : " + query);
//        ResultSet result = factory.getSession().execute(query);
//        for (Row row : result) {
//            wexSettleMessages = new WexSettleEntiry();
//            wexSettleMessages.setTransactionFileTime(row.getString("transactionfiletime"));
//            wexSettleMessages.setBatchVersion(row.getString("batchversion"));
//            wexSettleMessages.setTranscardCode(row.getString("transcardCode"));
//            wexSettleMessages.setTransType(row.getString("transtype"));
//            wexSettleMessages.setTransNbr(row.getString("transnbr"));
//            wexSettleMessages.setTransDate(row.getString("transdate"));
//            wexSettleMessages.setTransTime(row.getString("transtime"));
//            wexSettleMessages.setCardTrack(row.getString("cardtrack"));
//            wexSettleMessages.setPumpCat(row.getString("pumpcat"));
//            wexSettleMessages.setPumpService(row.getString("pumpservice"));
//            wexSettleMessages.setPumpNbr(row.getString("pumpnbr"));
//            wexSettleMessages.setPumpAmount(row.getString("pumpamount"));
//            wexSettleMessages.setProduct(row.getString("product"));
//            wexSettleMessages.setOdometer(row.getString("odometer"));
//            wexSettleMessages.setAmount(row.getString("amount"));
//            wexSettleMessages.setAuthRef(row.getString("authref"));
//            wexSettleMessages.setDriverId(row.getString("driverid"));
//            wexSettleMessages.setVehicleId(row.getString("vehicleid"));
//            wexSettleMessages.setOrderDate(row.getString("orderDate"));
//            wexSettleMessages.setSequenceId(row.getString("sequenceId"));
//            wexSettleMessages.setSettleId(row.getString("settleId"));
//            wexSettleMessages.setSettlestatus(row.getString("settlestatus"));
//            wexSettleMessages.setTime(row.getString("time"));
//            wexSettleMessages.setCatflag(row.getString("catflag"));
//            wexSettleMessages.setService(row.getString("service"));
//        }
//        LOG.info("WexSettleMessageDAO.find method is ended");
//        return wexSettleMessages;
//    }
//    
//    public List<WexSettleEntiry> returnTransactions() {
//        List<WexSettleEntiry> result = new ArrayList<>();
//        String query = "Select * from Stargate.transactions";
//        resultSet = session.execute(query);
//
//        Result<WexSettleEntiry> resultTrans = mapper.map(resultSet);
//        for(WexSettleEntiry t : resultTrans){
//            result.add(t);
//        }
//        return result;
//    }
//
//    @EJB
//    public void setCassandraSessionFactory(CassandraSessionFactory factory) {
//        this.factory = factory;
//    }
//}