package com.aafes.starsettler.dao;

import com.aafes.starsettler.control.CassandraSessionFactory;
import com.aafes.starsettler.entity.Facility;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

@Stateless
public class FacilityDAO {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(FacilityDAO.class.getSimpleName());
    private Mapper mapper;
    private CassandraSessionFactory factory;

    public FacilityDAO() {
    }
    
    @PostConstruct
    public void postConstruct() {
        Session session = factory.getSession();
        mapper = new MappingManager(session).mapper(Facility.class);
    }

    public void save(Facility facility) {
        mapper.save(facility);
    }

//    public void delete(Facility facility) {
//        mapper.delete(facility);
//    }

    public Facility get(String facilityId) {
        return (Facility) mapper.get(facilityId);
    }

    @EJB
    public void setCassandraSessionFactory(CassandraSessionFactory factory) {
        this.factory = factory;
    }
}
