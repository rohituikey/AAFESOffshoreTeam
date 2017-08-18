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

    public List<SettleEntity> getFDMSData(String identityuuid, String processDate, String settleStatus) {

        LOG.info("Entry in getFDMSData method of Settlemessagedao..");
        List<SettleEntity> fdmsData = new ArrayList<SettleEntity>();

        if (processDate == null || processDate.isEmpty()) {
            processDate = this.getProcessDate();
        }

        // Get settle data from Cassandra
        String query = "";
        if (identityuuid != null && !identityuuid.isEmpty()) {
            query = "SELECT " + buildCoulumns() + " FROM starsettler.settlemessages where receiveddate = '" + processDate
                    + "' and settlestatus = '" + settleStatus
                    + "' and identityUUID = '" + identityuuid + "' ALLOW FILTERING";
        } else {
            query = "SELECT " + buildCoulumns() + " FROM starsettler.settlemessages where receiveddate = '" + processDate
                    + "' and settlestatus = '" + settleStatus + "' ALLOW FILTERING";
        }

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
                settleEntity.setTokenBankName(row.getString("tokenbankname"));
                fdmsData.add(settleEntity);
            }

        }
        LOG.info("Exit from getFDMSData method of Settlemessagedao..");
        return fdmsData;
    }

    public List<SettleEntity> getWexData(String identityuuid, String processDate, String settleStatus) {

        LOG.info("Entry in getFDMSData method of Settlemessagedao..");
        List<SettleEntity> fdmsData = new ArrayList<SettleEntity>();

        if (processDate == null || processDate.isEmpty()) {
            processDate = this.getProcessDate();
        }

        // Get settle data from Cassandra
        String query = "";
        if (identityuuid != null && !identityuuid.isEmpty()) {
            query = "SELECT " + buildCoulumns() + " FROM starsettler.settlemessages where receiveddate = '" + processDate
                    + "' and settlestatus = '" + settleStatus
                    + "' and identityUUID = '" + identityuuid + "' ALLOW FILTERING";
        } else {
            query = "SELECT " + buildCoulumns() + " FROM starsettler.settlemessages where receiveddate = '" + processDate
                    + "' and settlestatus = '" + settleStatus + "' ALLOW FILTERING";
        }

        ResultSet result = factory.getSession().execute(query);

        String cardType = "";
        for (Row row : result) {
            cardType = row.getString("cardtype");
            if (cardType != null && !cardType.trim().isEmpty()
                    && (cardType.equalsIgnoreCase(CardType.AMEX)
                    || cardType.equalsIgnoreCase(CardType.DISCOVER)
                    || cardType.equalsIgnoreCase(CardType.MASTER)
                    || cardType.equalsIgnoreCase(CardType.VISA)
                    || cardType.equalsIgnoreCase(CardType.WEX))) {
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
                settleEntity.setTokenBankName(row.getString("tokenbankname"));
                fdmsData.add(settleEntity);
            }

        }
        LOG.info("Exit from getFDMSData method of Settlemessagedao..");
        return fdmsData;
    }

    public List<SettleEntity> getVisionData(String identityuuid, String processDate, String settleStatus) {

        LOG.info("Entry in getVisionData method of Settlemessagedao..");
        List<SettleEntity> visionData = new ArrayList<SettleEntity>();

        if (processDate == null || processDate.isEmpty()) {
            processDate = this.getProcessDate();
        }
        String query = "";
        if (identityuuid != null && !identityuuid.isEmpty()) {
            query = "SELECT " + buildCoulumns() + " FROM starsettler.settlemessages where receiveddate = '" + processDate
                    + "' and settlestatus = '" + settleStatus
                    + "' and identityUUID = '" + identityuuid + "' ALLOW FILTERING";
        } else {
            query = "SELECT " + buildCoulumns() + " FROM starsettler.settlemessages where receiveddate = '" + processDate
                    + "' and settlestatus = '" + settleStatus + "' ALLOW FILTERING";
        }
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
                settleEntity.setTokenBankName(row.getString("tokenbankname"));
                visionData.add(settleEntity);
            }

        }
        LOG.info("Exit from getVisionData method of Settlemessagedao..");
        return visionData;
    }

    public List<SettleEntity> getAll(String identityuuid, String processDate, String settleStatus) {

        LOG.info("Entry in getAll method of Settlemessagedao..");
        List<SettleEntity> settleRecords = new ArrayList<SettleEntity>();

        if (processDate == null || processDate.isEmpty()) {
            processDate = this.getProcessDate();
        }
        // Get settle data from Cassandra
        String query = "";
        if (identityuuid != null && !identityuuid.isEmpty()) {
            query = "SELECT " + buildCoulumns() + " FROM starsettler.settlemessages where receiveddate = '" + processDate
                    + "' and settlestatus = '" + settleStatus
                    + "' and identityUUID = '" + identityuuid + "' ALLOW FILTERING";
        } else {
            query = "SELECT " + buildCoulumns() + " FROM starsettler.settlemessages where receiveddate = '" + processDate
                    + "' and settlestatus = '" + settleStatus + "' ALLOW FILTERING";
        }
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
            settleEntity.setTokenBankName(row.getString("tokenbankname"));
            settleRecords.add(settleEntity);
        }

        LOG.info("Exit from getAll method of Settlemessagedao..");
        return settleRecords;
    }

    public void updateStatus(List<SettleEntity> settleDataList, String status) {

        LOG.info("Entry in updateStatus method of Settlemessagedao..");
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

        LOG.info("Exit from updateStatus method of Settlemessagedao..");
    }

    public String getBatchId() {

        LOG.info("Entry in getBatchId method of Settlemessagedao..");
        String batchId = "";
        String query = "select batchid from starsettler.batchidxref "
                + "where processdate = '" + this.getProcessDate() + "' ALLOW FILTERING ;";
        ResultSet result = factory.getSession().execute(query);

        for (Row rs : result) {
            batchId = rs.getString("batchid");
            break;
        }
        LOG.info("Exit from getBatchId method of Settlemessagedao..");
        return batchId;

    }

    public String getFileSequenceId() {

        LOG.info("Entry in getFileSequence method of Settlemessagedao..");
        String fileSequenceId = "";
        String query = "select filesequenceid from starsettler.fileidref "
                + "where processdate = '" + this.getProcessDate() + "' ALLOW FILTERING ;";
        ResultSet result = factory.getSession().execute(query);

        for (Row rs : result) {
            fileSequenceId = rs.getString("filesequenceid");
            break;
        }
        LOG.info("Exit from getBatchId method of Settlemessagedao..");
        return fileSequenceId;

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
                + "tokenbankname, "
                + "avsresponsecode";

        return columns;

    }

    private String getProcessDate() {

        LOG.info("Entry in getProcessDate method of Settlemessagedao..");
        // TODO
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String processDate = dateFormat.format(new Date());
        LOG.info("Exit from getProcessDate method of Settlemessagedao..");
        return processDate;
    }

    public List<SettleEntity> getRetailData(String uuid, String processDate, String settleStatus) {

        LOG.info("Entry in getRetailData method of Settlemessagedao..");
        List<SettleEntity> retailData = new ArrayList<>();

        if (processDate == null || processDate.isEmpty()) {
            processDate = this.getProcessDate();
        }

        // Get settle data from Cassandra
        String query = "";
        if (uuid != null && !uuid.isEmpty()) {
            query = "SELECT " + buildCoulumns() + " FROM starsettler.settlemessages where receiveddate = '" + processDate
                    + "' and settlestatus = '" + settleStatus
                    + "' and identityUUID = '" + uuid + "' ALLOW FILTERING";
        } else {
            query = "SELECT " + buildCoulumns() + " FROM starsettler.settlemessages where receiveddate = '" + processDate
                    + "' and settlestatus = '" + settleStatus + "' ALLOW FILTERING";
        }

        ResultSet result = factory.getSession().execute(query);

        String cardType = "";
        for (Row row : result) {
            cardType = row.getString("cardtype");
            if (cardType != null && (cardType.equalsIgnoreCase(CardType.MIL_STAR))) {
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
                settleEntity.setTokenBankName(row.getString("tokenbankname"));
                retailData.add(settleEntity);
            }

        }
        LOG.info("Exit from getRetailData method of Settlemessagedao..");
        return retailData;
    }

    public List<String> getIdentityUuidList(String strategyStr) {

        LOG.info("Entry in getIdentityUuidList method of Settlemessagedao..");
        List<String> decaUuIdList = new ArrayList<String>();

        String query = "";
        query = "SELECT *  FROM stargate.facmapper where strategy = '" + strategyStr
                + "' ALLOW FILTERING";

        ResultSet result = factory.getSession().execute(query);

        for (Row row : result) {

            decaUuIdList.add(row.getString("uuid"));
        }
        LOG.info("Exit from getIdentityUuidList method of Settlemessagedao..");
        return decaUuIdList;
    }

    @EJB
    public void setCassandraSessionFactory(CassandraSessionFactory factory) {
        this.factory = factory;
    }

    public void updateFdmsData(List<SettleEntity> fdmsData, String status) {

        LOG.info("Entry in updateFdmsData method of Settlemessagedao..");
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
        LOG.info("Exit from updateFdmsData method of Settlemessagedao..");
    }

    public void updateWexData(List<SettleEntity> wexdata, String filesequenceid) {

        LOG.info("Entry in updateFdmsData method of Settlemessagedao..");
        for (SettleEntity settleData : wexdata) {
            String query = "update starsettler.settlemessages set "
                    + " filesequenceid = '" + settleData.getSequenceId() + "',"
                    + " settlestatus ='completed' "
                    + "' where receiveddate = '" + settleData.getReceiveddate()
                    + "' and ordernumber = '" + settleData.getOrderNumber()
                    + "' and settledate='" + settleData.getSettleDate()
                    + "' and cardtype='" + settleData.getCardType()
                    + "' and transactiontype = '" + settleData.getTransactionType()
                    + "' and clientlineid='" + settleData.getClientLineId()
                    + "' and lineid='" + settleData.getLineId()
                    + "' and transactionid='" + settleData.getTransactionId() + "';";
            factory.getSession().execute(query);
        }
        LOG.info("Exit from updateFdmsData method of Settlemessagedao..");
    }

    public void updateFileSeqxRef(List<SettleEntity> wexData, String seqNo) {

        LOG.info("Entry in updateFileSeqxRef method of Settlemessagedao..");

        String processDate = this.getProcessDate();

        for (SettleEntity settleData : wexData) {
            String query = "insert into starsettler.fileidref(receiveddate, filesequenceid , ordernumber, processdate) "
                    + "VALUES ('" + settleData.getReceiveddate() + "', "
                    + "'" + settleData.getSequenceId() + "',"
                    + "'" + settleData.getOrderNumber() + "',"
                    + "'" + processDate + "');";
            factory.getSession().execute(query);
        }

        LOG.info("Exit from updateFileSeqxRef method of Settlemessagedao..");
    }

    public void updateBatchRef(List<SettleEntity> fdmsData, String processDate) {

        LOG.info("Entry in updateBatchRef method of Settlemessagedao..");

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

        LOG.info("Exit from updateBatchRef method of Settlemessagedao..");
    }

    public boolean validateDuplicateRecords(List<SettleEntity> settledData) {

        LOG.info("Entry in validateDuplicateRecords method of Settlemessagedao..");
        boolean recordDuplicate = false;

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
                recordDuplicate = true;
                break;
            }

        }
        LOG.info("Exit from  validateDuplicateRecords method of Settlemessagedao..");
        return recordDuplicate;
    }

    public List<String> getTIDList() {

        LOG.info("Entry in getTID method of Settlemessagedao..");
        // Get settle data from Cassandra
        String query = "";

        query = "SELECT lineId FROM starsettler.settlemessages where cardType ='WEX' ALLOW FILTERING;";
        //factory = new CassandraSessionFactory();
        ResultSet result = factory.getSession().execute(query);
        List<String> tid = new ArrayList<String>();
        for (Row row : result) {
            tid.add(row.getString(0));
        }

        LOG.info("Exit from getTID method of Settlemessagedao..");
        return tid;
    }

    public List<SettleEntity> getsettleTransaction(String tid, String processDate, String settleStatus) {

        LOG.info("Entry in getTID method of Settlemessagedao..");
        List<SettleEntity> settleTransactionList = new ArrayList<SettleEntity>();

        try {
            String query = "";

            //  try{
            query = "SELECT identityuuid, lineId, quantity, unitCost, cardType, paymentAmount, transactionType, transactionId, orderDate, "
                    + "batchId, trackdata2, odometer, driverId, authreference, vehicleId, catflag, service ,pumpnumber,time, cardtype,transactionType, "
                    + "filesequenceid, nonfuelproductgroup, fuelproductgroup FROM starsettler.settlemessages where receiveddate='" + processDate
                    + "' and settlestatus = '" + settleStatus
                    + "' and lineId = '" + tid
                    //"' and settlestatus = '" + settleStatus + "' " + "' and lineId = '" + tid + " '"
                    + "' ALLOW FILTERING;";

//        "SELECT " + buildCoulumns() + " FROM starsettler.settlemessages where receiveddate = '" + processDate
//                    + "' and settlestatus = '" + settleStatus + "' ALLOW FILTERING";
            //factory = new CassandraSessionFactory();
            ResultSet result = factory.getSession().execute(query);
//        }catch(Exception e)
//        {
            System.out.println("com.aafes.starsettler.dao.SettleMessageDAO.getsettleTransaction()");
            //set values one by one
            if (null != result) {
                for (Row row : result) {
                    SettleEntity settleEntity = new SettleEntity();
                    //settleEntity.setIdentityUUID(row.getString("identityuuid"));
                    settleEntity.setCardType(row.getString("cardType"));
                    settleEntity.setLineId(row.getString("lineId"));
                    settleEntity.setQuantity(row.getString("quantity"));
                    settleEntity.setUnitCost(row.getString("unitCost"));
                    settleEntity.setPaymentAmount(row.getString("paymentAmount"));
                    settleEntity.setTransactionType(row.getString("transactionType"));
                    settleEntity.setTransactionId(row.getString("transactionId"));
                    //settleEntity.setOrderNumber(row.getString("ordernumber"));
                    settleEntity.setOrderDate(row.getString("orderdate"));
                    settleEntity.setBatchId(row.getString("batchId"));
                    // settleEntity.setExpirationDate(row.getString("expirationdate"));
                    // settleEntity.setRrn(row.getString("rrn"));
                    settleEntity.setTrackdata2(row.getString("trackdata2"));
                    settleEntity.setPumpNumber(row.getString("pumpnumber"));
                    settleEntity.setOdometer(row.getString("odometer"));
                    settleEntity.setTime(row.getString("time"));
                    settleEntity.setCardType(row.getString("cardtype"));
                    settleEntity.setTransactionType(row.getString("transactionType"));
                    settleEntity.setDriverId(row.getString("driverId"));
                    settleEntity.setAuthreference(row.getString("authreference"));
                    settleEntity.setVehicleId(row.getString("vehicleId"));
                    settleEntity.setCatflag(row.getString("catflag"));
                    settleEntity.setService(row.getString("service"));
                    settleEntity.setFilesequencenumber(row.getString("filesequenceid"));
                    settleEntity.setDate(row.getString("date"));
                    settleEntity.setProductgroup(row.getList("productgroup", String.class));
                    settleTransactionList.add(settleEntity);
                }
            }
            //settleTransactionList.add((SettleEntity) result);
            LOG.info("Exit from getTID method of Settlemessagedao..");
        } catch (Exception e) {
            System.out.println("com.aafes.starsettler.dao.SettleMessageDAO.getsettleTransaction()");
        }
        return settleTransactionList;
    }

    private String buildWEXCoulumns() {
        String columns = "identityuuid, "
                + "lineId,"
                + "quantity, "
                + "unitCost, "
                + "cardType, "
                + "paymentAmount, "
                + "transactionType, "
                + "transactionId, "
                + "orderDate, "
                + "batchId, "
                + "trackdata2, "
                + "odometer, "
                + "driverId, "
                + "authreference, "
                + "vehicleId, "
                + "catflag, "
                + "service, "
                + "nonefuelamount, "
                + "productcode, "
                + "filesequenceid, "
                + "nonfuelproductgroup, "
                + "fuelproductgroup ";

        return columns;

    }

}
