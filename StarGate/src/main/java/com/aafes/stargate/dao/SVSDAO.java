/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.dao;

import com.aafes.stargate.authorizer.entity.GiftCard;
import com.aafes.stargate.control.CassandraSessionFactory;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ganjis
 */
@Stateless
public class SVSDAO {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(TransactionDAO.class.getSimpleName());

    private Mapper mapper;
    private CassandraSessionFactory factory;

    @PostConstruct
    public void postConstruct() {
        Session session = factory.getSession();
        mapper = new MappingManager(session).mapper(GiftCard.class);
    }

    public void save(GiftCard giftCard) {
        mapper.save(giftCard);
    }

    public GiftCard find(String cardNumber, String pin) {
        LOG.info("Finding gift card");
        return (GiftCard) mapper.get(cardNumber, pin);
    }

    @EJB
    public void setCassandraSessionFactory(CassandraSessionFactory factory) {
        this.factory = factory;
    }
}
