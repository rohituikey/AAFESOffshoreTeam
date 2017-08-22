/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.imported;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Table;
import java.util.List;

/**
 *
 * @author alugumetlas
 */
@Table(keyspace = "starsettler", name = "wexsettlemessages")
public class WexSettleEntity {

    @ClusteringColumn(0)
    private String tid;
    @ClusteringColumn(1)
    private String receivedDate;
    @ClusteringColumn(2)
    private String settleStatus;
    @ClusteringColumn(3)
    private String transactionType;
    @ClusteringColumn(4)
    private String orderNumber;

    private String fileSequenceId;
    private String AppName;
    private String AppVersion;
    private String cardTrack;
    private String pumpCat;
    private String pumpService;
    private List<String> product;
    private String odometer;
    private String amount;
    private String authRef;
    private String driverId;
    private String vehicleId;
    private String catFlag;
    private String service;
    private String transactionId;
    private String transactionCode;
    private String settelmentDate;
    private String settelmentTime;

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getSettleStatus() {
        return settleStatus;
    }

    public void setSettleStatus(String settleStatus) {
        this.settleStatus = settleStatus;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getFileSequenceId() {
        return fileSequenceId;
    }

    public void setFileSequenceId(String fileSequenceId) {
        this.fileSequenceId = fileSequenceId;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String AppName) {
        this.AppName = AppName;
    }

    public String getAppVersion() {
        return AppVersion;
    }

    public void setAppVersion(String AppVersion) {
        this.AppVersion = AppVersion;
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

    public List<String> getProduct() {
        return product;
    }

    public void setProduct(List<String> product) {
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

    public String getCatFlag() {
        return catFlag;
    }

    public void setCatFlag(String catFlag) {
        this.catFlag = catFlag;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getSettelmentDate() {
        return settelmentDate;
    }

    public void setSettelmentDate(String settelmentDate) {
        this.settelmentDate = settelmentDate;
    }

    public String getSettelmentTime() {
        return settelmentTime;
    }

    public void setSettelmentTime(String settelmentTime) {
        this.settelmentTime = settelmentTime;
    }

}