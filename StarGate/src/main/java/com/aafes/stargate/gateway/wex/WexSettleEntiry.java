/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Table;

/**
 *
 * @author burangir
 */
@Table(keyspace = "stargate", name = "wexsettlemessages")
public class WexSettleEntiry {

    @ClusteringColumn(0)
    private String transactionFileDate;
    @ClusteringColumn(1)
    private String transactionFileTime;
    @ClusteringColumn(2)
    private String transactionFileSequence;
    @ClusteringColumn(3)
    private String batchTId;
    @ClusteringColumn(4)
    private String batchId;
    private String batchApp;
    private String batchVersion;
    private String transcardCode;
    private String transType;
    private String transNbr;
    private String transDate;
    private String transTime;
    private String cardTrack;
    private String pumpCat;
    private String pumpService;
    private String pumpNbr;
    private String pumpAmount;
    private String product;
    private String odometer;
    private String amount;
    private String authRef;
    private String driverId;
    private String vehicleId;
    private String orderDate = "";
    private String sequenceId = "";
    private String settleId = "";
    private String settlestatus = "";
    private String time = "";
    private String catflag = "";
    private String service = "";

    public String getTransactionFileDate() {
        return transactionFileDate;
    }

    public void setTransactionFileDate(String transactionFileDate) {
        this.transactionFileDate = transactionFileDate;
    }

    public String getTransactionFileTime() {
        return transactionFileTime;
    }

    public void setTransactionFileTime(String transactionFileTime) {
        this.transactionFileTime = transactionFileTime;
    }

    public String getTransactionFileSequence() {
        return transactionFileSequence;
    }

    public void setTransactionFileSequence(String transactionFileSequence) {
        this.transactionFileSequence = transactionFileSequence;
    }

    public String getBatchTId() {
        return batchTId;
    }

    public void setBatchTId(String batchTId) {
        this.batchTId = batchTId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getBatchApp() {
        return batchApp;
    }

    public void setBatchApp(String batchApp) {
        this.batchApp = batchApp;
    }

    public String getBatchVersion() {
        return batchVersion;
    }

    public void setBatchVersion(String batchVersion) {
        this.batchVersion = batchVersion;
    }

    public String getTranscardCode() {
        return transcardCode;
    }

    public void setTranscardCode(String transcardCode) {
        this.transcardCode = transcardCode;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getTransNbr() {
        return transNbr;
    }

    public void setTransNbr(String transNbr) {
        this.transNbr = transNbr;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getTransTime() {
        return transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public String getCardTrack() {
        return cardTrack;
    }

    public void setCardTrack(String cardTrack) {
        this.cardTrack = cardTrack;
    }

    public String getPumpCat() {
        return pumpCat;
    }

    public void setPumpCat(String pumpCat) {
        this.pumpCat = pumpCat;
    }

    public String getPumpService() {
        return pumpService;
    }

    public void setPumpService(String pumpService) {
        this.pumpService = pumpService;
    }

    public String getPumpNbr() {
        return pumpNbr;
    }

    public void setPumpNbr(String pumpNbr) {
        this.pumpNbr = pumpNbr;
    }

    public String getPumpAmount() {
        return pumpAmount;
    }

    public void setPumpAmount(String pumpAmount) {
        this.pumpAmount = pumpAmount;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getOdometer() {
        return odometer;
    }

    public void setOdometer(String odometer) {
        this.odometer = odometer;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAuthRef() {
        return authRef;
    }

    public void setAuthRef(String authRef) {
        this.authRef = authRef;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getSettleId() {
        return settleId;
    }

    public void setSettleId(String settleId) {
        this.settleId = settleId;
    }

    public String getSettlestatus() {
        return settlestatus;
    }

    public void setSettlestatus(String settlestatus) {
        this.settlestatus = settlestatus;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCatflag() {
        return catflag;
    }

    public void setCatflag(String catflag) {
        this.catflag = catflag;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}