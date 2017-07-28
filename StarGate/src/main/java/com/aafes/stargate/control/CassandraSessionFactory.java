package com.aafes.stargate.control;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class CassandraSessionFactory {

    

    private Cluster cluster;
    private Session session;
    @Inject
    private String seedHost;

    public CassandraSessionFactory() {
        cluster = null;
        session = null;
        seedHost = null;
    }

    @PostConstruct
    public void connect() {

        if (null == this.cluster) {
            this.cluster = Cluster.builder()
                    .addContactPoint(this.seedHost)
                    .build();
        }
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s",
                metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            System.out.printf("Datatacenter: %s Host: %s Rack: %s",
                    host.getDatacenter(), host.getAddress(), host.getRack());
        }
        if (null == this.session) {
            session = cluster.connect();
        }
    }

    public Session getSession() {
        if (null == session) {
            connect();
        }
        if (session.isClosed()) {
            connect();
        }
        return this.session;
    }

    @PreDestroy
    public void close() {
        if (cluster != null) {
            cluster.close();
            session = null;
            cluster = null;
        }
    }
    
    public void setSeedHost(String seedHost) {
        this.seedHost = seedHost;
    }

}
