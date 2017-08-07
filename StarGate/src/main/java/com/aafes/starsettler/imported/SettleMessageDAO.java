/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.imported;

import com.aafes.stargate.authorizer.entity.Transaction;
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
public class SettleMessageDAO {

    /**
     * @param mapper the mapper to set
     */
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SettleMessageDAO.class.getSimpleName());

    private Mapper mapper;
    private CassandraSessionFactory factory;
    private ResultSet resultSet;
    private Session session;
    

    @PostConstruct
    public void postConstruct() {
        Session session = factory.getSession();
        setMapper(new MappingManager(session).mapper(SettleEntity.class));
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
        mapper.save(settleEntity);

    }

    public SettleEntity find(String uuid, String ordernumber, String rrn, String transactionid) {
        LOG.info("SetteleMessageDAO.find method is started");

        SettleEntity settleEntity = null;
        String query = "";
        query = "SELECT *  FROM starsettler.settlemessages where identityuuid = '" + uuid
                + "' and ordernumber = '" + ordernumber + "' and "
                + " rrn = '" + rrn + "' and transactionid = '" + transactionid + "' ALLOW FILTERING";
         LOG.info("Query :"+query);
        ResultSet result = factory.getSession().execute(query);
        for (Row row : result) {
            settleEntity = new SettleEntity();
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
        }
        LOG.info("SetteleMessageDAO.find method is ended");
        return settleEntity;
    }
    
    public List<SettleEntity> returnTransactions() {
        List<SettleEntity> result = new ArrayList<>();
        String query = "Select * from Stargate.transactions";
        resultSet = session.execute(query);

        Result<SettleEntity> resultTrans = mapper.map(resultSet);
        for(SettleEntity t : resultTrans){
            result.add(t);
        }
        return result;
    }

    @EJB
    public void setCassandraSessionFactory(CassandraSessionFactory factory) {
        this.factory = factory;
    }
}
