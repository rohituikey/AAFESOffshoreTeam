package com.aafes.starsettler.dao;

import com.aafes.starsettler.control.CassandraSessionFactory;
import com.aafes.starsettler.entity.AuthorizationCodes;
import com.aafes.starsettler.entity.SettleEntity;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

@Stateless
public class TransactionDAO {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(TransactionDAO.class.getSimpleName());

  
    private CassandraSessionFactory factory;

    @PostConstruct
    public void postConstruct() {
        Session session = factory.getSession();
       
    }


    public AuthorizationCodes find(SettleEntity settleEntity) {
        
        AuthorizationCodes ac = new AuthorizationCodes();
        
        String query = "select reasoncode,responsedate,authoriztioncode,avsresponsecode,csvresponsecode"
                + " from stargate.transactions "
                + "where ordernumber = '" + settleEntity.getOrderNumber() + "' ALLOW FILTERING";
        ResultSet result = factory.getSession().execute(query);

        for (Row rs : result) {
            ac.setResponseReasonCode(rs.getString("reasoncode"));
            ac.setResponseDate(rs.getString("responsedate"));
            ac.setAuthoriztionCode(rs.getString("authoriztioncode"));
            ac.setAvsResponseCode(rs.getString("avsresponsecode"));
            ac.setCsvResponseCode(rs.getString("csvresponsecode"));
        }
        
        return ac;
    }

    @EJB
    public void setCassandraSessionFactory(CassandraSessionFactory factory) {
        this.factory = factory;
}
}
