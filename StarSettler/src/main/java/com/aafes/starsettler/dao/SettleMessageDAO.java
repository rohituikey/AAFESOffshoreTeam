/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.dao;

import com.aafes.starsettler.control.CassandraSessionFactory;
import com.aafes.starsettler.control.Configurator;
import com.aafes.starsettler.entity.SettleEntity;
import com.aafes.starsettler.util.CardType;
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
 * @author ganjis
 */
@Stateless
public class SettleMessageDAO {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(SettleMessageDAO.class.getSimpleName());

    private Mapper mapper;
    private CassandraSessionFactory factory;
    @EJB
    private Configurator configurator;

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

    public SettleEntity find() {
        return (SettleEntity) mapper.get();
    }

    public List<SettleEntity> getFDMSData(String processDate, String settleStatus) {
        List<SettleEntity> fdmsData = new ArrayList<SettleEntity>();

        if (processDate == null || processDate.isEmpty()) {
            processDate = this.getProcessDate();
        }

        // Get settle data from Cassandra
        String query = "SELECT " + buildCoulumns() + " FROM starsettler.settlemessages where receiveddate = '" + processDate + "' and "
                + "settlestatus = '" + settleStatus + "' ALLOW FILTERING";

        ResultSet result = factory.getSession().execute(query);

        String cardType = "";
        for (Row row : result) {
            cardType = row.getString("cardtype");
            if (cardType != null && !cardType.trim().isEmpty()
                    && (cardType.equalsIgnoreCase(CardType.AMEX)
                    || cardType.equalsIgnoreCase(CardType.DISCOVER)
                    || cardType.equalsIgnoreCase(CardType.MASTER)
                    || cardType.equalsIgnoreCase(CardType.VISA))) {
                SettleEntity settleEntity = new SettleEntity();
                settleEntity.setIdentityUUID(row.getString("identityuuid"));
                settleEntity.setLineId(row.getString("lineId"));
                settleEntity.setClientLineId(row.getString("clientLineId"));
                settleEntity.setShipId(row.getString("shipId"));
                settleEntity.setCrc(row.getString("crc"));
                settleEntity.setQuantity(row.getString("quantity"));
                settleEntity.setUnitCost(row.getString("unitCost"));
                settleEntity.setUnitDiscount(row.getString("unitDiscount"));
                settleEntity.setUnit(row.getString("unit"));
                settleEntity.setUnitTotal(row.getString("unitTotal"));
                settleEntity.setCouponCode(row.getString("couponCode"));
                settleEntity.setCardType(row.getString("cardType"));
                settleEntity.setPaymentAmount(row.getString("paymentAmount"));
                settleEntity.setTransactionType(row.getString("transactionType"));
                settleEntity.setTransactionId(row.getString("transactionId"));
                settleEntity.setOrderNumber(row.getString("orderNumber"));
                settleEntity.setOrderDate(row.getString("orderDate"));
                settleEntity.setShipDate(row.getString("shipDate"));
                settleEntity.setSettleDate(row.getString("settleDate"));
                settleEntity.setCardReferene(row.getString("cardReferene"));
                settleEntity.setCardToken(row.getString("cardToken"));
                settleEntity.setExpirationDate(row.getString("expirationDate"));
                settleEntity.setAuthNum(row.getString("authNum"));
                settleEntity.setRequestPlan(row.getString("requestPlan"));
                settleEntity.setResponsePlan(row.getString("responsePlan"));
                settleEntity.setQualifiedPlan(row.getString("qualifiedPlan"));
                settleEntity.setRrn(row.getString("rrn"));
                settleEntity.setFirstName(row.getString("firstName"));
                settleEntity.setLastName(row.getString("lastName"));
                settleEntity.setHomePhone(row.getString("homePhone"));
                settleEntity.setEmail(row.getString("email"));
                settleEntity.setAddressLine1(row.getString("addressLine1"));
                settleEntity.setAddressLine2(row.getString("addressLine2"));
//                settleEntity.setAddressLine3(row.getString("addressLine3"));
                settleEntity.setCity(row.getString("city"));
                settleEntity.setProvinceCode(row.getString("provinceCode"));
                settleEntity.setPostalCode(row.getString("postalCode"));
                settleEntity.setCountryCode(row.getString("countryCode"));
                settleEntity.setShippingAmount(row.getString("shippingAmount"));
                settleEntity.setAppeasementCode(row.getString("appeasementCode"));
                settleEntity.setAppeasementDate(row.getString("appeasementDate"));
                settleEntity.setAppeasementDescription(row.getString("appeasementDescription"));
                settleEntity.setAppeasementReference(row.getString("appeasementReference"));
                settleEntity.setResponseType(row.getString("responseType"));
                settleEntity.setReasonCode(row.getString("reasonCode"));
                settleEntity.setDescriptionField(row.getString("descriptionField"));
                settleEntity.setBatchId(row.getString("batchId"));
                settleEntity.setSettleId(row.getString("settleId"));
                settleEntity.setReceiveddate(row.getString("receivedDate"));
                settleEntity.setSettlestatus(row.getString("settlestatus"));
                settleEntity.setResponseReasonCode(row.getString("responsereasoncode"));
                settleEntity.setResponseDate(row.getString("responsedate"));
                settleEntity.setAuthoriztionCode(row.getString("authoriztioncode"));
                settleEntity.setAvsResponseCode(row.getString("avsresponsecode"));
                fdmsData.add(settleEntity);
            }

        }
        return fdmsData;
    }

    public List<SettleEntity> getVisionData(String processDate, String settleStatus) {
        List<SettleEntity> visionData = new ArrayList<SettleEntity>();

        if (processDate == null || processDate.isEmpty()) {
            processDate = this.getProcessDate();
        }
        // Get vision data from Cassandra
        String query = "SELECT " + buildCoulumns() + " FROM starsettler.settlemessages where receiveddate = '" + processDate + "' and "
                + "settlestatus = '" + settleStatus + "' ALLOW FILTERING";

        ResultSet result = factory.getSession().execute(query);

        String cardType = "";
        for (Row row : result) {
            cardType = row.getString("cardtype");
            if (cardType != null && !cardType.trim().isEmpty()
                    && (cardType.equalsIgnoreCase(CardType.MIL_STAR))) {
                SettleEntity settleEntity = new SettleEntity();
                settleEntity.setIdentityUUID(row.getString("identityuuid"));
                settleEntity.setLineId(row.getString("lineId"));
                settleEntity.setClientLineId(row.getString("clientLineId"));
                settleEntity.setShipId(row.getString("shipId"));
                settleEntity.setCrc(row.getString("crc"));
                settleEntity.setQuantity(row.getString("quantity"));
                settleEntity.setUnitCost(row.getString("unitCost"));
                settleEntity.setUnitDiscount(row.getString("unitDiscount"));
                settleEntity.setUnit(row.getString("unit"));
                settleEntity.setUnitTotal(row.getString("unitTotal"));
                settleEntity.setCouponCode(row.getString("couponCode"));
                settleEntity.setCardType(row.getString("cardType"));
                settleEntity.setPaymentAmount(row.getString("paymentAmount"));
                settleEntity.setTransactionType(row.getString("transactionType"));
                settleEntity.setTransactionId(row.getString("transactionId"));
                settleEntity.setOrderNumber(row.getString("orderNumber"));
                settleEntity.setOrderDate(row.getString("orderDate"));
                settleEntity.setShipDate(row.getString("shipDate"));
                settleEntity.setSettleDate(row.getString("settleDate"));
                settleEntity.setCardReferene(row.getString("cardReferene"));
                settleEntity.setCardToken(row.getString("cardToken"));
                settleEntity.setExpirationDate(row.getString("expirationDate"));
                settleEntity.setAuthNum(row.getString("authNum"));
                settleEntity.setRequestPlan(row.getString("requestPlan"));
                settleEntity.setResponsePlan(row.getString("responsePlan"));
                settleEntity.setQualifiedPlan(row.getString("qualifiedPlan"));
                settleEntity.setSettlePlan(row.getString("settleplan"));
                settleEntity.setRrn(row.getString("rrn"));
                settleEntity.setFirstName(row.getString("firstName"));
                settleEntity.setLastName(row.getString("lastName"));
                settleEntity.setHomePhone(row.getString("homePhone"));
                settleEntity.setEmail(row.getString("email"));
                settleEntity.setAddressLine1(row.getString("addressLine1"));
                settleEntity.setAddressLine2(row.getString("addressLine2"));
//                settleEntity.setAddressLine3(row.getString("addressLine3"));
                settleEntity.setCity(row.getString("city"));
                settleEntity.setProvinceCode(row.getString("provinceCode"));
                settleEntity.setPostalCode(row.getString("postalCode"));
                settleEntity.setCountryCode(row.getString("countryCode"));
                settleEntity.setShippingAmount(row.getString("shippingAmount"));
                settleEntity.setAppeasementCode(row.getString("appeasementCode"));
                settleEntity.setAppeasementDate(row.getString("appeasementDate"));
                settleEntity.setAppeasementDescription(row.getString("appeasementDescription"));
                settleEntity.setAppeasementReference(row.getString("appeasementReference"));
                settleEntity.setResponseType(row.getString("responseType"));
                settleEntity.setReasonCode(row.getString("reasonCode"));
                settleEntity.setDescriptionField(row.getString("descriptionField"));
                settleEntity.setBatchId(row.getString("batchId"));
                settleEntity.setSettleId(row.getString("settleId"));
                settleEntity.setReceiveddate(row.getString("receivedDate"));
                settleEntity.setSettlestatus(row.getString("settlestatus"));
                visionData.add(settleEntity);
            }

        }

        return visionData;
    }

    public List<SettleEntity> getAll(String processDate, String settleStatus) {
        List<SettleEntity> settleRecords = new ArrayList<SettleEntity>();

        if (processDate == null || processDate.isEmpty()) {
            processDate = this.getProcessDate();
        }
        // Get settle data from Cassandra
        String query = "SELECT " + buildCoulumns() + " FROM starsettler.settlemessages where receiveddate = '" + processDate + "' and "
                + "settlestatus = '" + settleStatus + "' ALLOW FILTERING";

        ResultSet result = factory.getSession().execute(query);

        for (Row row : result) {
            SettleEntity settleEntity = new SettleEntity();
            settleEntity.setIdentityUUID(row.getString("identityuuid"));
            settleEntity.setLineId(row.getString("lineId"));
            settleEntity.setClientLineId(row.getString("clientLineId"));
            settleEntity.setShipId(row.getString("shipId"));
            settleEntity.setCrc(row.getString("crc"));
            settleEntity.setQuantity(row.getString("quantity"));
            settleEntity.setUnitCost(row.getString("unitCost"));
            settleEntity.setUnitDiscount(row.getString("unitDiscount"));
            settleEntity.setUnit(row.getString("unit"));
            settleEntity.setUnitTotal(row.getString("unitTotal"));
            settleEntity.setCouponCode(row.getString("couponCode"));
            settleEntity.setCardType(row.getString("cardType"));
            settleEntity.setPaymentAmount(row.getString("paymentAmount"));
            settleEntity.setTransactionType(row.getString("transactionType"));
            settleEntity.setTransactionId(row.getString("transactionId"));
            settleEntity.setOrderNumber(row.getString("orderNumber"));
            settleEntity.setOrderDate(row.getString("orderDate"));
            settleEntity.setShipDate(row.getString("shipDate"));
            settleEntity.setSettleDate(row.getString("settleDate"));
            settleEntity.setCardReferene(row.getString("cardReferene"));
            settleEntity.setCardToken(row.getString("cardToken"));
            settleEntity.setExpirationDate(row.getString("expirationDate"));
            settleEntity.setAuthNum(row.getString("authNum"));
            settleEntity.setRequestPlan(row.getString("requestPlan"));
            settleEntity.setResponsePlan(row.getString("responsePlan"));
            settleEntity.setQualifiedPlan(row.getString("qualifiedPlan"));
            settleEntity.setSettlePlan(row.getString("settleplan"));
            settleEntity.setRrn(row.getString("rrn"));
            settleEntity.setFirstName(row.getString("firstName"));
            settleEntity.setLastName(row.getString("lastName"));
            settleEntity.setHomePhone(row.getString("homePhone"));
            settleEntity.setEmail(row.getString("email"));
            settleEntity.setAddressLine1(row.getString("addressLine1"));
            settleEntity.setAddressLine2(row.getString("addressLine2"));
//            settleEntity.setAddressLine3(row.getString("addressLine3"));
            settleEntity.setCity(row.getString("city"));
            settleEntity.setProvinceCode(row.getString("provinceCode"));
            settleEntity.setPostalCode(row.getString("postalCode"));
            settleEntity.setCountryCode(row.getString("countryCode"));
            settleEntity.setShippingAmount(row.getString("shippingAmount"));
            settleEntity.setAppeasementCode(row.getString("appeasementCode"));
            settleEntity.setAppeasementDate(row.getString("appeasementDate"));
            settleEntity.setAppeasementDescription(row.getString("appeasementDescription"));
            settleEntity.setAppeasementReference(row.getString("appeasementReference"));
            settleEntity.setResponseType(row.getString("responseType"));
            settleEntity.setReasonCode(row.getString("reasonCode"));
            settleEntity.setDescriptionField(row.getString("descriptionField"));
            settleEntity.setBatchId(row.getString("batchId"));
            settleEntity.setSettleId(row.getString("settleId"));
            settleEntity.setReceiveddate(row.getString("receivedDate"));
            settleEntity.setSettlestatus(row.getString("settlestatus"));
            settleEntity.setResponseReasonCode(row.getString("responsereasoncode"));
            settleEntity.setResponseDate(row.getString("responsedate"));
            settleEntity.setAuthoriztionCode(row.getString("authoriztioncode"));
            settleEntity.setAvsResponseCode(row.getString("avsresponsecode"));
            settleRecords.add(settleEntity);
        }

        return settleRecords;
    }

    public void updateStatus(List<SettleEntity> settleDataList, String status) {

        settleDataList.forEach((settleData) -> {

            String query = "update starsettler.settlemessages set settlestatus ='" + status
                    + "' where receiveddate = '" + settleData.getReceiveddate()
                    + "' and ordernumber = '" + settleData.getOrderNumber()
                    + "' and settledate='" + settleData.getSettleDate()
                    + "' and cardtype='" + settleData.getCardType()
                    + "' and transactiontype = '" + settleData.getTransactionType()
                    + "' and clientlineid='" + settleData.getClientLineId()
                    + "' and transactionid='" + settleData.getTransactionId() + "';";
            factory.getSession().execute(query);
        }
        );

    }

    public String getBatchId() {

        String batchId = "";
        String query = "select batchid from starsettler.batchidxref "
                + "where processdate = '" + this.getProcessDate() + "' ALLOW FILTERING ;";
        ResultSet result = factory.getSession().execute(query);

        for (Row rs : result) {
            batchId = rs.getString("batchid");
            break;
        }
        return batchId;

    }

    private String buildCoulumns() {
        String columns = "identityuuid, "
                + "lineId,"
                + "clientLineId,"
                + "shipId,"
                + "crc, "
                + "quantity, "
                + "unitCost, "
                + "unitDiscount, "
                + "unit, "
                + "unitTotal, "
                + "couponCode, "
                + "cardType, "
                + "paymentAmount, "
                + "transactionType, "
                + "transactionId, "
                + "orderNumber, "
                + "orderDate, "
                + "shipDate, "
                + "settleDate, "
                + "cardReferene, "
                + "cardToken, "
                + "expirationDate, "
                + "authNum, "
                + "requestPlan, "
                + "responsePlan, "
                + "qualifiedPlan, "
                + "settlePlan, "
                + "rrn, "
                + "firstName, "
                + "lastName, "
                + "homePhone, "
                + "email, "
                + "addressLine1, "
                + "addressLine2, "
                + "addressLine3, "
                + "city, "
                + "provinceCode, "
                + "postalCode, "
                + "countryCode, "
                + "shippingAmount, "
                + "appeasementCode, "
                + "appeasementDate, "
                + "appeasementDescription, "
                + "appeasementReference, "
                + "responseType, "
                + "reasonCode, "
                + "descriptionField, "
                + "batchId, "
                + "settleId, "
                + "receivedDate, "
                + "settlestatus, "
                + "responsereasoncode, "
                + "responsedate, "
                + "authoriztioncode, "
                + "avsresponsecode";

        return columns;

    }

    private String getProcessDate() {
        // TODO
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String processDate = dateFormat.format(new Date());
        return processDate;
    }

    @EJB
    public void setCassandraSessionFactory(CassandraSessionFactory factory) {
        this.factory = factory;
    }

    public void updateFdmsData(List<SettleEntity> fdmsData, String status) {

        for (SettleEntity settleData : fdmsData) {
            String query = "update starsettler.settlemessages set "
                    + " batchid = '" + settleData.getBatchId() + "',"
                    + " sequenceid = '" + settleData.getSequenceId() + "',"
                    + "settlestatus ='" + status
                    + "' where receiveddate = '" + settleData.getReceiveddate()
                    + "' and ordernumber = '" + settleData.getOrderNumber()
                    + "' and settledate='" + settleData.getSettleDate()
                    + "' and cardtype='" + settleData.getCardType()
                    + "' and transactiontype = '" + settleData.getTransactionType()
                    + "' and clientlineid='" + settleData.getClientLineId()
                    + "' and transactionid='" + settleData.getTransactionId() + "';";
            factory.getSession().execute(query);
        }

    }

    public void updateBatchRef(List<SettleEntity> fdmsData, String processDate) {

        if (processDate == null || processDate.isEmpty()) {
            processDate = this.getProcessDate();
        }

        for (SettleEntity settleData : fdmsData) {
            String query = "insert into starsettler.batchidxref(receiveddate, batchid, sequenceid , ordernumber,processdate) "
                    + "VALUES ('" + settleData.getReceiveddate() + "', "
                    + "'" + settleData.getBatchId() + "', "
                    + "'" + settleData.getSequenceId() + "',"
                    + "'" + settleData.getOrderNumber() + "',"
                    + "'" + processDate + "');";
            factory.getSession().execute(query);
        }

    }

    public boolean validateDuplicateRecords(List<SettleEntity> settledData) {
         
        boolean recordDuplicate=false;
        
        for (SettleEntity settleData : settledData) {

             String query = "select ordernumber,settledate,cardtype,transactiontype,clientlineid,transactionid"
                    + " from starsettler.settlemessages "
                    + " where ordernumber = '" + settleData.getOrderNumber()
                    + "' and settledate = '" + settleData.getSettleDate()
                    + "' and cardtype = '" + settleData.getCardType()
                    + "' and transactiontype = '" + settleData.getTransactionType()
                    + "' and clientlineid = '" + settleData.getClientLineId()
                    + "' and transactionid = '" + settleData.getTransactionId() + "' ALLOW FILTERING";

            ResultSet result = factory.getSession().execute(query);

            if (result.one() != null) {
                recordDuplicate= true;
                break;
            }
          
        }
         return recordDuplicate;
    }
}