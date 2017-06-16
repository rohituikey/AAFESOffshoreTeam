/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.dao;

import com.aafes.stargate.control.CassandraSessionFactory;
import com.aafes.stargate.gateway.GatewayException;
import com.aafes.stargate.validatetoken.CrosssiteRequestTokenTable;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author burangir
 */
@Stateless
public class TokenServiceDAO {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(TokenServiceDAO.class.getSimpleName());
    private final String CLASS_NAME = TokenServiceDAO.this.getClass().getSimpleName();
    private String sMethodName = "";
    private CassandraSessionFactory factory = new CassandraSessionFactory();
    private Mapper mapper;

    @PostConstruct
    public void postConstruct() {
        sMethodName = "postConstruct";
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        factory.setSeedHost("localhost");
        Session session = factory.getSession();
        mapper = new MappingManager(session).mapper(CrosssiteRequestTokenTable.class);
        sMethodName = "postXml";
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
    }

    public boolean insertTokenDetails(CrosssiteRequestTokenTable tokenObj) {
        sMethodName = "postConstruct";
        LOG.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        boolean dataInsertedFlg = false;
        try {
            String query = "insert into stargate.crosssiterequesttokentable(tokenid, tokenstatus, tokencredatetime, "
                    + "identityuuid, termid, customerid, media, account) "
                    + "VALUES ('" + tokenObj.getTokenid() + "', "
                    + "'" + tokenObj.getTokenstatus() + "', "
                    + "'" + tokenObj.getTokencredatetime() + "', "
                    + "'" + tokenObj.getIdentityuuid() + "', "
                    + "'" + tokenObj.getTermid() + "', "
                    + "'" + tokenObj.getCustomerid() + "', "
                    + "'" + tokenObj.getMedia() + "', "
                    + "'" + tokenObj.getAccount() + "');";
            factory = new CassandraSessionFactory();
            factory.setSeedHost("localhost");
            Session session = factory.getSession();
            ResultSet result = session.execute(query);

            if (result != null) {
                dataInsertedFlg = true;
            }
            LOG.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        } catch (Exception ex) {
            LOG.error("Error while creating cross site request token " + ex.getMessage());
            throw new GatewayException("INTERNAL SYSTEM ERROR");
        }
        return dataInsertedFlg;
    }
}