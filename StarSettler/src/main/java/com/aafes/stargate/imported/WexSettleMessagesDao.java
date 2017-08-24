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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    public List<String> getWexTIDList() {
        List<String> tid = new ArrayList<String>();
        String query = "";

        query = "SELECT tid FROM starsettler.wexsettlemessages where transactiontype ='FinalAuth' ALLOW FILTERING;";
       // factory = new CassandraSessionFactory();
        ResultSet result = factory.getSession().execute(query);

        for (Row row : result) {
            tid.add(row.getString(0));
        }
        return tid;

    }

    public void updateWexSettleData(List<WexSettleEntity> Wexdata, String In_Progress) {
        //factory = new CassandraSessionFactory();
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
       // factory = new CassandraSessionFactory();
        String query = "";
        String processDate1 = "2017-08-23";
        query = "SELECT * FROM starsettler.wexsettlemessages "
                + "where receiveddate = '" + processDate1 + "'and "
                 + "settlestatus = '" + status + "'and "
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

    public String getfileWexSequenceId() {

        String fileSequenceId = "";
        String query = "select filesequenceid from starsettler.fileidref "
                + "where processdate = '" + this.getProcessDate() + "' ALLOW FILTERING ;";
        ResultSet result = factory.getSession().execute(query);

        for (Row rs : result) {
            fileSequenceId = rs.getString("filesequenceid");
            break;
        }
        return fileSequenceId;
    }

    private String getProcessDate() {

        // TODO
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String processDate = dateFormat.format(new Date());
        return processDate;
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
