/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.wex;

import com.aafes.starsettler.control.CassandraSessionFactory;
import com.aafes.starsettler.control.Configurator;
import com.aafes.starsettler.gateway.wex.WexDataSettler;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author singha
 */
public class WexTransactionFileTest {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WexTransactionFileTest.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = WexTransactionFileTest.this.getClass().getSimpleName();

    WexDataSettler wexDataSettler;
    Configurator configurator;
    CassandraSessionFactory factory;
    Session session;
    ResultSet resultSet;

    String uuid = "eacbc625-6fef-479e-8738-92adcfed7c65";

    @Before
    public void setDataForTesting() {
        configurator = new Configurator();
        configurator.postConstruct();
        configurator.load();
        wexDataSettler= new WexDataSettler();

        factory = new CassandraSessionFactory();
        factory.setSeedHost("localhost");
        factory.connect();

        session = factory.getSession();
    }

    //@Ignore
    @Test
    public void testSuccessFileGeneration() {
        sMethodName = "testSuccessFileGeneration";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        session = intiateSession();
        wexDataSettler.run(uuid, "2017-08-23");
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);

        // assertEquals("100", result.getResponse().get(0).getReasonCode());
    }

    @Ignore
    @Test
    public void testNoTransactionFound() {
        sMethodName = "testNoTransactionFound";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        intiateSession();
        wexDataSettler.run(uuid, "2017-08-23");
        session = intiateSession();
        session.execute("TRUNCATE STARGATE.TRANSACTIONS");
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
        // assertEquals("PRODUCT_DETAIL_COUNT_NOT_BE_NULL", result.getResponse().get(0).getDescriptionField());
    }

    private Session intiateSession() {
        if (factory == null) {
            factory = new CassandraSessionFactory();
            factory.setSeedHost("localhost");
            factory.connect();
        }
        if (session == null) {
            session = factory.getSession();
        }
        return session;
    }

}
