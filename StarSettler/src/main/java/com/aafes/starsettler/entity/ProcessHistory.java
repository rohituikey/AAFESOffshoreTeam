/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.entity;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

/**
 *
 * @author ganjis
 */
@Table(keyspace = "starsettler", name = "processhistory")
public class ProcessHistory {
    
    @PartitionKey(0)
    private String processDate = "";
    @ClusteringColumn(0)
    private String batchId = "";
    @ClusteringColumn(1)
    private String settlerType = "";
    private String startTime = "";
    private String endTime = "";

    public String getProcessDate() {
        return processDate;
    }

    public void setProcessDate(String processDate) {
        this.processDate = processDate;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getSettlerType() {
        return settlerType;
    }

    public void setSettlerType(String settlerType) {
        this.settlerType = settlerType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
}
