/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.entity;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import java.util.List;

/**
 *
 * @author ganjis
 */
@Table(keyspace = "starsettler", name = "settlemessages")
public class SettleEntity {

    public String getReceiveddate() {
        return receiveddate;
    }

    public void setReceiveddate(String receiveddate) {
        this.receiveddate = receiveddate;
    }
    
    @PartitionKey(0)
    private String receiveddate ="";
    @ClusteringColumn(0)
    private String orderNumber = "";
    @ClusteringColumn(1)
    private String settleDate = "";
    @ClusteringColumn(2)
    private String cardType = "";
    @ClusteringColumn(3)
    private String transactionType = ""; 
    @ClusteringColumn(4)
    private String clientLineId = "";
    
    
    private String identityUUID = "";
    private String tokenBankName = "";
    
    // Line Item Fields
    private String lineId = "";
    
    private String shipId = "";
    private String crc = "";
    private String quantity = "";
    private String unitCost = "";
    private String unitDiscount = "";
    private String unit = "";
    private String unitTotal = "";
    private String couponCode = "";
    
    private String paymentAmount = "";
    private String transactionId = "";
    
    private String orderDate = "";
    private String shipDate = "";
    
    private String cardReferene = "";
    private String cardToken = "";
    private String expirationDate = "";
    private String authNum = "";
    private String requestPlan = "";
    private String responsePlan = "";
    private String qualifiedPlan = "";
    private String settlePlan = "";
    private String rrn = "";
    private String firstName = "";
    private String middleName = "";
    private String lastName = "";
    private String homePhone = "";
    private String email = "";
    private String addressLine1 = "";
    private String addressLine2 = "";
//    private String addressLine3 = "";
    private String city = "";
    private String provinceCode = "";
    private String postalCode = "";
    private String countryCode = "";
    private String shippingAmount = "";
    
    // appeasement fields
    private String appeasementCode = "";
    private String appeasementDate = "";
    private String appeasementDescription = "";
    private String appeasementReference = "";

    // response fields
    private String responseType = "";
    private String reasonCode = "";
    private String descriptionField = "";
    
    private String batchId ="";
    private String sequenceId = "";
    private String settleId = "";
    private String settlestatus = "";
    
    
    //From Authorization
        private String responseReasonCode = "" ;
    private String responseDate = "";
    private String authoriztionCode = "";
    private String avsResponseCode = "";
    private String csvResponseCode = "";
    
    
    //field added for wex 
    private String time = "";
    private String trackdata2 = "";
    private String odometer = "";
    private String driverId = "";
    private String authreference = "";
    private String vehicleId = "";
    private String catflag = "";
    private String service = "";
    private String nonefuelamount = "";
    private String productcode = "";
    private String filesequencenumber="";
    private List<String>  nonfuelproductgroup=null;
    private List<String>  fuelproductgroup=null;
        
    
    
    
    
    
    
    public String getIdentityUUID() {
        return identityUUID;
    }

    public void setIdentityUUID(String identityUUID) {
        this.identityUUID = identityUUID;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getClientLineId() {
        return clientLineId;
    }

    public void setClientLineId(String clientLineId) {
        this.clientLineId = clientLineId;
    }

    public String getShipId() {
        return shipId;
    }

    public void setShipId(String shipId) {
        this.shipId = shipId;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(String unitCost) {
        this.unitCost = unitCost;
    }

    public String getUnitDiscount() {
        return unitDiscount;
    }

    public void setUnitDiscount(String unitDiscount) {
        this.unitDiscount = unitDiscount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnitTotal() {
        return unitTotal;
    }

    public void setUnitTotal(String unitTotal) {
        this.unitTotal = unitTotal;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getShipDate() {
        return shipDate;
    }

    public void setShipDate(String shipDate) {
        this.shipDate = shipDate;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public String getCardReferene() {
        return cardReferene;
    }

    public void setCardReferene(String cardReferene) {
        this.cardReferene = cardReferene;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getAuthNum() {
        return authNum;
    }

    public void setAuthNum(String authNum) {
        this.authNum = authNum;
    }

    public String getRequestPlan() {
        return requestPlan;
    }

    public void setRequestPlan(String requestPlan) {
        this.requestPlan = requestPlan;
    }

    public String getResponsePlan() {
        return responsePlan;
    }

    public void setResponsePlan(String responsePlan) {
        this.responsePlan = responsePlan;
    }

    public String getQualifiedPlan() {
        return qualifiedPlan;
    }

    public void setQualifiedPlan(String qualifiedPlan) {
        this.qualifiedPlan = qualifiedPlan;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

//    public String getAddressLine3() {
//        return addressLine3;
//    }
//
//    public void setAddressLine3(String addressLine3) {
//        this.addressLine3 = addressLine3;
//    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAppeasementCode() {
        return appeasementCode;
    }

    public void setAppeasementCode(String appeasementCode) {
        this.appeasementCode = appeasementCode;
    }

    public String getAppeasementDate() {
        return appeasementDate;
    }

    public void setAppeasementDate(String appeasementDate) {
        this.appeasementDate = appeasementDate;
    }

    public String getAppeasementDescription() {
        return appeasementDescription;
    }

    public void setAppeasementDescription(String appeasementDescription) {
        this.appeasementDescription = appeasementDescription;
    }

    public String getAppeasementReference() {
        return appeasementReference;
    }

    public void setAppeasementReference(String appeasementReference) {
        this.appeasementReference = appeasementReference;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getDescriptionField() {
        return descriptionField;
    }

    public void setDescriptionField(String descriptionField) {
        this.descriptionField = descriptionField;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getShippingAmount() {
        return shippingAmount;
    }

    public void setShippingAmount(String shippingAmount) {
        this.shippingAmount = shippingAmount;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
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

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getResponseReasonCode() {
        return responseReasonCode;
    }

    public void setResponseReasonCode(String responseReasonCode) {
        this.responseReasonCode = responseReasonCode;
    }

    public String getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(String responseDate) {
        this.responseDate = responseDate;
    }

    public String getAuthoriztionCode() {
        return authoriztionCode;
    }

    public void setAuthoriztionCode(String authoriztionCode) {
        this.authoriztionCode = authoriztionCode;
    }

    public String getAvsResponseCode() {
        return avsResponseCode;
    }

    public void setAvsResponseCode(String avsResponseCode) {
        this.avsResponseCode = avsResponseCode;
    }

    public String getCsvResponseCode() {
        return csvResponseCode;
    }

    public void setCsvResponseCode(String csvResponseCode) {
        this.csvResponseCode = csvResponseCode;
    }

    public String getSettlePlan() {
        return settlePlan;
    }

    public void setSettlePlan(String settlePlan) {
        this.settlePlan = settlePlan;
    }

    public String getTokenBankName() {
        return tokenBankName;
    }

    public void setTokenBankName(String tokenBankName) {
        this.tokenBankName = tokenBankName;
    }
    
    
    //getter setter for wex fields

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTrackdata2() {
        return trackdata2;
    }

    public void setTrackdata2(String trackdata2) {
        this.trackdata2 = trackdata2;
    }

    public String getOdometer() {
        return odometer;
    }

    public void setOdometer(String odometer) {
        this.odometer = odometer;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getAuthreference() {
        return authreference;
    }

    public void setAuthreference(String authreference) {
        this.authreference = authreference;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
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

    public String getNonefuelamount() {
        return nonefuelamount;
    }

    public void setNonefuelamount(String nonefuelamount) {
        this.nonefuelamount = nonefuelamount;
    }

    public String getProductcode() {
        return productcode;
    }

    public void setProductcode(String productcode) {
        this.productcode = productcode;
    }

    public List<String> getNonfuelproductgroup() {
        return nonfuelproductgroup;
    }

    public void setNonfuelproductgroup(List<String> nonfuelproductgroup) {
        this.nonfuelproductgroup = nonfuelproductgroup;
    }

    public List<String> getFuelproductgroup() {
        return fuelproductgroup;
    }

    public void setFuelproductgroup(List<String> fuelproductgroup) {
        this.fuelproductgroup = fuelproductgroup;
    }

    public String getFilesequencenumber() {
        return filesequencenumber;
    }

    public void setFilesequencenumber(String filesequencenumber) {
        this.filesequencenumber = filesequencenumber;
    }
    
    
}
