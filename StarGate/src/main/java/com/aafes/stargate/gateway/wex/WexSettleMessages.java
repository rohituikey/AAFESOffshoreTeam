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
public class WexSettleMessages {

    @ClusteringColumn(0)
    String transactionfiledate;
    @ClusteringColumn(1)
    String transactionfiletime;
    @ClusteringColumn(2)
    String transactionfilesequence;
    @ClusteringColumn(3)
    String batchtid;
    @ClusteringColumn(4)
    String batchid;
    String batchapp;
    String batchversion;
    String transcardCode;
    String transtype;
    String transnbr;
    String transdate;
    String transtime;
    String cardtrack;
    String pumpcat;
    String pumpservice;
    String pumpnbr;
    String pumpamount;
    String product;
    String odometer;
    String amount;
    String authref;
    String driverid;
    String vehicleid;

    public String getTransactionfiledate() {
        return transactionfiledate;
    }

    public void setTransactionfiledate(String transactionfiledate) {
        this.transactionfiledate = transactionfiledate;
    }

    public String getTransactionfiletime() {
        return transactionfiletime;
    }

    public void setTransactionfiletime(String transactionfiletime) {
        this.transactionfiletime = transactionfiletime;
    }

    public String getTransactionfilesequence() {
        return transactionfilesequence;
    }

    public void setTransactionfilesequence(String transactionfilesequence) {
        this.transactionfilesequence = transactionfilesequence;
    }

    public String getBatchtid() {
        return batchtid;
    }

    public void setBatchtid(String batchtid) {
        this.batchtid = batchtid;
    }

    public String getBatchid() {
        return batchid;
    }

    public void setBatchid(String batchid) {
        this.batchid = batchid;
    }

    public String getBatchapp() {
        return batchapp;
    }

    public void setBatchapp(String batchapp) {
        this.batchapp = batchapp;
    }

    public String getBatchversion() {
        return batchversion;
    }

    public void setBatchversion(String batchversion) {
        this.batchversion = batchversion;
    }

    public String getTranscardCode() {
        return transcardCode;
    }

    public void setTranscardCode(String transcardCode) {
        this.transcardCode = transcardCode;
    }

    public String getTranstype() {
        return transtype;
    }

    public void setTranstype(String transtype) {
        this.transtype = transtype;
    }

    public String getTransnbr() {
        return transnbr;
    }

    public void setTransnbr(String transnbr) {
        this.transnbr = transnbr;
    }

    public String getTransdate() {
        return transdate;
    }

    public void setTransdate(String transdate) {
        this.transdate = transdate;
    }

    public String getTranstime() {
        return transtime;
    }

    public void setTranstime(String transtime) {
        this.transtime = transtime;
    }

    public String getCardtrack() {
        return cardtrack;
    }

    public void setCardtrack(String cardtrack) {
        this.cardtrack = cardtrack;
    }

    public String getPumpcat() {
        return pumpcat;
    }

    public void setPumpcat(String pumpcat) {
        this.pumpcat = pumpcat;
    }

    public String getPumpservice() {
        return pumpservice;
    }

    public void setPumpservice(String pumpservice) {
        this.pumpservice = pumpservice;
    }

    public String getPumpnbr() {
        return pumpnbr;
    }

    public void setPumpnbr(String pumpnbr) {
        this.pumpnbr = pumpnbr;
    }

    public String getPumpamount() {
        return pumpamount;
    }

    public void setPumpamount(String pumpamount) {
        this.pumpamount = pumpamount;
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

    public String getAuthref() {
        return authref;
    }

    public void setAuthref(String authref) {
        this.authref = authref;
    }

    public String getDriverid() {
        return driverid;
    }

    public void setDriverid(String driverid) {
        this.driverid = driverid;
    }

    public String getVehicleid() {
        return vehicleid;
    }

    public void setVehicleid(String vehicleid) {
        this.vehicleid = vehicleid;
    }

}