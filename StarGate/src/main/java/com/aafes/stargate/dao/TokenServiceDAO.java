/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.dao;

import com.aafes.stargate.control.CassandraSessionFactory;
import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.validatetoken.CrosssiteRequestTokenTable;
import com.aafes.stargate.validatetoken.CrosssiteRequestUsertable;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import java.util.List;
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
    private CassandraSessionFactory factory;
    private Mapper mapper;
    private Session session = null;

    @PostConstruct
    public void postConstruct() {
//        if (factory == null) factory = new CassandraSessionFactory();
//        factory.setSeedHost("localhost");
        session = factory.getSession();
        mapper = new MappingManager(session).mapper(CrosssiteRequestTokenTable.class);
    }

    public boolean insertTokenDetails(CrosssiteRequestTokenTable tokenObj) {
        sMethodName = "insertTokenDetails";
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
//            if (factory == null) {
//                factory = new CassandraSessionFactory();
//            }
//            factory.setSeedHost("localhost");
//            postConstruct();
            mapper.save(tokenObj);
            LOG.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
            dataInsertedFlg = true;
        } catch (Exception ex) {
            LOG.error("Error while creating cross site request token " + ex.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        } finally {
//            if (session != null) session.close();
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
//            postConstruct();
            query = "SELECT * FROM stargate.crosssiterequesttokentable where tokenid = '" + tokenStr + "'"
                    + " and identityuuid = '" + identityUuid + "'" + " and tokenstatus = '" + tokenStatus + "'"
                    + " ALLOW FILTERING;";
            resultSet = session.execute(query);
        } catch (Exception ex) {
            LOG.error("Error while creating cross site request token " + ex.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        } finally {
            //if(session != null) session.close();
        }
        return resultSet;//(CrosssiteRequestTokenTable) mapper.get(tokenStr, identityUuid, CreditMessageTokenConstants.STATUS_ACTIVE);
    }

    public boolean updateTokenStatus(String tokenStatus, String tokenId, String identityUuid) {
        sMethodName = "updateTokenStatus";
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);

        String updateQuery = "";
        boolean dataInsertedFlg = false;
        //List<Row> rowList = null;
        ResultSet resultSet = null;
        try {
//            postConstruct();
            updateQuery = "UPDATE stargate.crosssiterequesttokentable SET "
                    + "tokenstatus = '" + tokenStatus + "' WHERE tokenid = '" + tokenId + "'"
                    + " AND identityuuid = '" + identityUuid + "';";

            resultSet = session.execute(updateQuery);

            if (resultSet != null) {
                //rowList = resultSet.all();
                //if(rowList != null && rowList.size() > 0){
                LOG.info("Data Udpated. tokenid " + tokenId + ", identityuuid " + identityUuid + ", Status " + tokenStatus);
                dataInsertedFlg = true;
                //}else  LOG.error("Data Udpatation failed ! tokenid " + tokenId + ", identityuuid " + identityUuid + ", Status " + tokenStatus +
                //", clientIPAddress " + clientIPAddress);
            } else {
                LOG.error("Data Udpatation failed ! tokenid " + tokenId + ", identityuuid " + identityUuid + ", Status " + tokenStatus);
            }
        } catch (Exception ex) {
            LOG.error("Error while creating cross site request token " + ex.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        } finally {
            //if(session != null) session.close();
        }
        return dataInsertedFlg;
    }

    public ResultSet findActiveTokens(String identityUuid, String tokenStatus) {
        sMethodName = "findActiveTokens";
        String query = "";
        ResultSet resultSet = null;
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        try {
//            postConstruct();
            query = "SELECT * FROM stargate.crosssiterequesttokentable where identityuuid = '" + identityUuid + "'"
                    + " and tokenstatus = '" + tokenStatus + "' ALLOW FILTERING;";
            resultSet = session.execute(query);
        } catch (Exception ex) {
            LOG.error("Error while creating cross site request token " + ex.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        } finally {
            //if (session != null)  session.close();
        }
        return resultSet;
    }
    
//    public boolean insertUserDetails(CrosssiteRequestUsertable tokenObj) {
//        sMethodName = "insertTokenDetails";
//        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
//        boolean dataInsertedFlg = false;
//        try {
//            postConstruct();
//            mapper.save(tokenObj);
//            LOG.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
//            dataInsertedFlg = true;
//        } catch (Exception ex) {
//            LOG.error("Error while creating cross site request token " + ex.getMessage());
//            throw new GatewayException("INTERNAL SYSTEM ERROR");
//        } finally {
//            //if (session != null)  session.close();
//        }
//        LOG.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
//        return dataInsertedFlg;
//    }
    
    public boolean validateUserDetails(CrosssiteRequestUsertable tokenObj) {
        sMethodName = "validateUserDetails";
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        String query = "";
        boolean userValidateFlg = false;
        ResultSet resultSet = null;
        List<Row> rowList = null;
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        try {
//            postConstruct();
            query = "SELECT * FROM stargate.crosssiterequestusertable where identityuuid = '" + tokenObj.getIdentityuuid() + "'"
                    + " and userid = '" + tokenObj.getUserid() + "' and password = '" + tokenObj.getPassword() + "' ALLOW FILTERING;";
            resultSet = session.execute(query);
            if(resultSet != null){
                rowList = resultSet.all();
                if(rowList != null && rowList.size() > 0) userValidateFlg = true;
            }
        } catch (Exception ex) {
            LOG.error("Error while creating cross site request token " + ex.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        } finally {
            //if (session != null)  session.close();
        }
        LOG.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        return userValidateFlg;
    }
}