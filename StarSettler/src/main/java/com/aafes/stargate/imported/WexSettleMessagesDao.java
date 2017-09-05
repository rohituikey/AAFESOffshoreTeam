/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.imported;

import com.aafes.starsettler.control.CassandraSessionFactory;
import com.aafes.starsettler.dao.SettleMessageDAO;
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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alugumetlas
 */
@Stateless
public class WexSettleMessagesDao {

    private Mapper mapper;
    private CassandraSessionFactory factory;
    Session session;
    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(WexSettleMessagesDao.class.getSimpleName());

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

    public void updateWexFileSeqxRef(List<String> tids, String SeqNo) {

        LOG.info("Entry in updateFileSeqxRef method of Settlemessagedao..");
        factory = new CassandraSessionFactory();

        String processDate = this.getProcessDate();

        for (String tid : tids) {
            String query = "insert into starsettler.fileidref(filesequenceid , batchid, processdate) "
                    + "VALUES ('" + SeqNo + "', "
                    + "'" + tid + "',"
                    + "'" + processDate + "');";
            factory.getSession().execute(query);
        }

        LOG.info("Exit from updateFileSeqxRef method of Settlemessagedao..");
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

    public List<String> getWexTIDList() {

        LOG.info("Entry in getWexTIDList method of WexSettlemessagedao..");
        List<String> tidList = new ArrayList<String>();
        String query = "select tid from starsettler.wexsettlemessages "
                + "where processdate = '" + this.getProcessDate() + "' ALLOW FILTERING ;";
        ResultSet result = factory.getSession().execute(query);

        for (Row rs : result) {
            tidList.add(rs.getString("batchid"));
            break;
        }
        LOG.info("Exit from getWexTIDList method of WexSettlemessagedao..");
        return tidList;

    }

    public String getfileWexSequenceId() {
        
        LOG.info("Entry in getfileWexSequenceId method of WexSettlemessagedao..");
        String query = "select filesequenceid from starsettler.wexsettlemessages "
                + "where processdate = '" + this.getProcessDate() + "' ALLOW FILTERING ;";
        ResultSet result = factory.getSession().execute(query);

        String filesequenceid="";
        for (Row rs : result) {
             filesequenceid= rs.getString("filesequenceid");
            break;
        }
        LOG.info("Exit from getfileWexSequenceId method of WexSettlemessagedao..");
        return filesequenceid;
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

    private String getProcessDate() {

        LOG.info("Entry in getProcessDate method of WexSettlemessagedao..");
        // TODO
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String processDate = dateFormat.format(new Date());
        LOG.info("Exit from getProcessDate method of Settlemessagedao..");
        return processDate;
    }

}
