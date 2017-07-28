package com.aafes.starsettler.entity;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import java.io.Serializable;

@Table(keyspace = "stargate", name = "facmapper")
final public class Facility implements Serializable {

    
    @PartitionKey
    private String uuid;
    private String facility;
    private String strategy;
    private String deviceType;
    private String tokenBankName;
    
    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
    
    @Override
    public String toString() {
        return "Facility{" + "facilityId=" + uuid + ", fac10=" + facility + ", strategy=" + strategy + '}';
    }

    public String getUuid() {
        return uuid;
}

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the deviceType
     */
    public String getDeviceType() {
        return deviceType;
}

    /**
     * @param deviceType the deviceType to set
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getTokenBankName() {
        return tokenBankName;
    }

    public void setTokenBankName(String tokenBankName) {
        this.tokenBankName = tokenBankName;
    }
}
