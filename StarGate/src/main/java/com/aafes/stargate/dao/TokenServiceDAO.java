/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.dao;

import com.aafes.stargate.control.CassandraSessionFactory;
import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.util.CreditMessageTokenConstants;
import com.aafes.stargate.validatetoken.CrosssiteRequestTokenTable;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import javax.annotation.PostConstruct;
import org.slf4j.LoggerFactory;

/**
 *
 * @author burangir
 */
public class TokenServiceDAO {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(TokenServiceDAO.class.getSimpleName());
    private final String CLASS_NAME = TokenServiceDAO.this.getClass().getSimpleName();
    private String sMethodName = "";
    private CassandraSessionFactory factory = new CassandraSessionFactory();
    private Mapper mapper;
    private Session session = null;

    @PostConstruct
    public void postConstruct() {
        sMethodName = "postConstruct";
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        factory.setSeedHost("localhost");
        session = factory.getSession();
        mapper = new MappingManager(session).mapper(CrosssiteRequestTokenTable.class);
        sMethodName = "postXml";
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
    }

    public boolean insertTokenDetails(CrosssiteRequestTokenTable tokenObj) {
        sMethodName = "postConstruct";
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        boolean dataInsertedFlg = false;
        try {
//            String query = "insert into stargate.crosssiterequesttokentable(tokenid, tokenstatus, tokencredatetime, "
//                    + "identityuuid) "
//                    + "VALUES ('" + tokenObj.getTokenid() + "', "
//                    + "'" + tokenObj.getTokenstatus() + "', "
//                    + "'" + tokenObj.getTokencredatetime() + "', "
//                    + "'" + tokenObj.getIdentityuuid() "');";
//            factory = new CassandraSessionFactory();
//            factory.setSeedHost("localhost");
//            Session session = factory.getSession();
//            ResultSet result = session.execute(query);
//
//            if (result != null) {
//                dataInsertedFlg = true;
//            }
            if (factory == null) {
                factory = new CassandraSessionFactory();
            }
            factory.setSeedHost("localhost");
            session = factory.getSession();
            if (mapper == null) {
                mapper = new MappingManager(session).mapper(CrosssiteRequestTokenTable.class);
            }
            mapper.save(tokenObj);
            LOG.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
            dataInsertedFlg = true;
        } catch (Exception ex) {
            LOG.error("Error while creating cross site request token " + ex.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        } finally{
            if(session != null) session.close();
        }
        return dataInsertedFlg;
    }

    public ResultSet validateToken(String tokenStr, String identityUuid, String tokenStatus) {
        sMethodName = "validateToken";
        String query = "";
        ResultSet resultSet = null;
        CrosssiteRequestTokenTable obj = null;
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        try {
            if (factory == null) factory = new CassandraSessionFactory();
            factory.setSeedHost("localhost");
            session = factory.getSession();
            if (mapper == null) mapper = new MappingManager(session).mapper(CrosssiteRequestTokenTable.class);
            query = "SELECT * FROM stargate.crosssiterequesttokentable where tokenid = '" + tokenStr + "'" +
                    " and identityuuid = '" +identityUuid + "'" + " and tokenstatus = '" + tokenStatus +"' ALLOW FILTERING;";
            resultSet = session.execute(query);
        } catch (Exception ex) {
            LOG.error("Error while creating cross site request token " + ex.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        } finally{
            //if(session != null) session.close();
        }
        
        return resultSet;//(CrosssiteRequestTokenTable) mapper.get(tokenStr, identityUuid, CreditMessageTokenConstants.STATUS_ACTIVE);
    }
    
    public boolean updateTokenStatus(String tokenStatus, String tokenId, String identityUuid){
        sMethodName = "updateTokenStatus";
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        
        String updateQuery = "";
        boolean dataInsertedFlg = false;
        try {
            if (factory == null) {
                factory = new CassandraSessionFactory();
            }
            factory.setSeedHost("localhost");
            session = factory.getSession();
            ResultSet resultSet = null;
            if (mapper == null) {
                mapper = new MappingManager(session).mapper(CrosssiteRequestTokenTable.class);
            }
            
            updateQuery = "UPDATE stargate.crosssiterequesttokentable SET "
                    + "tokenstatus = '" + tokenStatus +"' WHERE tokenid = '" + tokenId + "'" 
                    + " AND identityuuid = '" + identityUuid + "';" ;
            
            resultSet = session.execute(updateQuery);
            if(resultSet != null){
                LOG.info("Data Udpated. tokenid " + tokenId + ", identityuuid " + identityUuid + ", Status " + tokenStatus);
                dataInsertedFlg = true;
            }else{
                LOG.error("Data Udpatation failed ! tokenid " + tokenId + ", identityuuid " + identityUuid + ", Status " + tokenStatus);
            }
            
        } catch (Exception ex) {
            LOG.error("Error while creating cross site request token " + ex.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        } finally{
            if(session != null) session.close();
        }
        return dataInsertedFlg;
    }
}