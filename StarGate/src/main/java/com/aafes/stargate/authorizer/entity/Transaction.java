package com.aafes.stargate.authorizer.entity;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;
import java.math.BigDecimal;

@Table(keyspace = "stargate", name = "transactions")
final public class Transaction {

    public String getBillingAddress1() {
        return billingAddress1;
    }

    public void setBillingAddress1(String billingAddress1) {
        this.billingAddress1 = billingAddress1;
    }

    public String getBillingAddress2() {
        return billingAddress2;
    }

    public void setBillingAddress2(String billingAddress2) {
        this.billingAddress2 = billingAddress2;
    }

//    public String getCardPresence() {
//        return cardPresence;
//    }
//
//    public void setCardPresence(String cardPresence) {
//        this.cardPresence = cardPresence;
//    }
    public String getInputCode() {
        return inputCode;
    }

    public void setInputCode(String inputCode) {
        this.inputCode = inputCode;
    }

    //Header Fields
    private String authHour = "";
    @ClusteringColumn(0)
    private String identityUuid = "";
    private String localDateTime = "";
    private String settleIndicator = "";

    private String orderNumber = "";
    private String transactionId = "";
    private String termId = "";
    private String comment = "";
    private String customerId = "";

    //Request Fields
    @ClusteringColumn(1)
    private String rrn = "";
    private String media = "";
    @ClusteringColumn(2)
    private String requestType = "";
    private String accountTypeType = "";
    private String account = "";
    private String pan = "";
    private String tokenId = "";
    private String encryptedPayLoad = "";
    @Transient
    private String cvv = "";
    private String ksn = "";
    private String pinBlock = "";
    @Transient
    private String expiration = "";
    private long amount = 0L;
    @Transient
    private String gcpin = "";
    private String reversal = "";
    private String voidFlag = "";
    private String inputType = "";
    private String descriptionField = "";
    @Transient
    private String track1 = "";
    @Transient
    private String track2 = "";
    @Transient
    private String encryptTrack = "";
    private String planNumber = "";
    private long pumpPrice = 0L;
    private String productCode = "";
    private String odoMeter = "";
    private String rationAmt = "";
    private String unitMeas = "";
    private long essoLoadAmount = 0L;
    private String pumpNmbr = "";
    private String driverId = "";
    private String restrictCode = "";
    private long qtyPumped = 0L;
    private long fuelPrice = 0L;
    private String fuelProdCode = "";
    private String unitOfMeas = "";
    private String vehicleId = "";
    private String licenceNumber = "";
    private String deptNumber = "";
    private String jobValueNumber = "";
    private String dataNumber = "";
    private String userId = "";
    private String contact = "";
    private String cardHolderName = "";
    private String billingAddress1 = "";
    private String billingAddress2 = "";
    private String billingCountryCode = "";
    private String shippingCountryCode = "";
    private String shippingAddress = "";
    private String billingZipCode = "";
    private String shippingZipCode = "";
    private String billingPhone = "";
    private String shippingPhone = "";
    private String email = "";
    private String upc = "";
    private String encryptMgmt = "";
    private String encryptAlgo = "";
    private String settleRq = "";
    private String originalOrder = "";
    private String origTransId = "";
    private String origAuthCode = "";
     private String origRRN= null;
    private long amtPreAuthorized = 0L;
    private String prodDetailCount = "";
    private String serviceCode = "";
    private String paymentType = "";

    // Response Fields
    private String reasonCode = "";
    private String authNumber = "";
    private String responseType = "";
    private long partialAmount = 0L;
    private String milstarNumber = "";
    private long balanceAmount = 0L;
    private String fee = "";
    private String settleRs = "";
    private long settleAmt = 0L;
    private String origAcctType = "";
    private String modifiedAcctValue = "";
    private String originalRequestType = "";

    //General Fields
    private String requestXmlDateTime = "";
    private String responseXmlDateTime = "";
    private String requestAuthDateTime = "";
    private String responseAuthDateTime = "";
    private String facility = "";
    @Transient
    private String strategy = "";
    @Transient
    private String tokenBankName = "";

    // MilStar Gateway Fields
    @Transient
    private String deviceType = "";
    private String traceId = "";
    private String STAN = "";
    private String CardSequenceNumber = "";
    private String Facility10 = "";
    private String Facility7 = "";
    private String MerchantOrg = "";
    private String DownPayment = "";
    private String SKUNumber = "";
    private String CardReferenceID = "";
    private String AmountSign = "";
    private String ZipCode = "";
    private String inputCode = "";
    private String sequenceNumber = "";
    // private String ResponseCode = "";

    // FDMS Gateway Fields
    private String divisionnumber = "";
    private String currencycode = "";
    private String transactiontype = "";
    private String actioncode = "";
//    private String cardPresence = "";
    private String billpaymentindicator = "";
    private String telephonetype = "";
    private String responseDate = "";
    private String authoriztionCode = "";
    private String avsResponseCode = "";
    private String csvResponseCode = "";

    /* FIELDS ADDED FOR WEX USE CASE - start */
    private String nonFuelProdCode;
    private BigDecimal nonFuelAmount;
    private BigDecimal fuelDollerAmount;
    private BigDecimal pricePerUnit;
    private String catFlag;
    private String cardSeqNumber;
    private BigDecimal quantity;
    private BigDecimal nonFuelqty;
    /* FIELDS ADDED FOR WEX USE CASE - end */
    
    public Transaction() {

    }

    public String getAuthHour() {
        return authHour;
    }

    public void setAuthHour(String authHour) {
        this.authHour = authHour;
    }

    public String getIdentityUuid() {
        return identityUuid;
    }

    public void setIdentityUuid(String identityUuid) {
        this.identityUuid = identityUuid;
    }

    public String getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getSettleIndicator() {
        return settleIndicator;
    }

    public void setSettleIndicator(String settleIndicator) {
        this.settleIndicator = settleIndicator;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getAccountTypeType() {
        return accountTypeType;
    }

    public void setAccountTypeType(String accountTypeType) {
        this.accountTypeType = accountTypeType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getEncryptedPayLoad() {
        return encryptedPayLoad;
    }

    public void setEncryptedPayLoad(String encryptedPayLoad) {
        this.encryptedPayLoad = encryptedPayLoad;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getKsn() {
        return ksn;
    }

    public void setKsn(String ksn) {
        this.ksn = ksn;
    }

    public String getPinBlock() {
        return pinBlock;
    }

    public void setPinBlock(String pinBlock) {
        this.pinBlock = pinBlock;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getReversal() {
        return reversal;
    }

    public void setReversal(String reversal) {
        this.reversal = reversal;
    }

    public String getVoidFlag() {
        return voidFlag;
    }

    public void setVoidFlag(String voidFlag) {
        this.voidFlag = voidFlag;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getDescriptionField() {
        return descriptionField;
    }

    public void setDescriptionField(String descriptionField) {
        this.descriptionField = descriptionField;
    }

    public String getTrack1() {
        return track1;
    }

    public void setTrack1(String track1) {
        this.track1 = track1;
    }

    public String getTrack2() {
        return track2;
    }

    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    public String getEncryptTrack() {
        return encryptTrack;
    }

    public void setEncryptTrack(String encryptTrack) {
        this.encryptTrack = encryptTrack;
    }

    public String getPlanNumber() {
        return planNumber;
    }

    public void setPlanNumber(String planNumber) {
        this.planNumber = planNumber;
    }

    public long getPumpPrice() {
        return pumpPrice;
    }

    public void setPumpPrice(long pumpPrice) {
        this.pumpPrice = pumpPrice;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getOdoMeter() {
        return odoMeter;
    }

    public void setOdoMeter(String odoMeter) {
        this.odoMeter = odoMeter;
    }

    public String getRationAmt() {
        return rationAmt;
    }

    public void setRationAmt(String rationAmt) {
        this.rationAmt = rationAmt;
    }

    public String getUnitMeas() {
        return unitMeas;
    }

    public void setUnitMeas(String unitMeas) {
        this.unitMeas = unitMeas;
    }

    public long getEssoLoadAmount() {
        return essoLoadAmount;
    }

    public void setEssoLoadAmount(long essoLoadAmount) {
        this.essoLoadAmount = essoLoadAmount;
    }

    public String getPumpNmbr() {
        return pumpNmbr;
    }

    public void setPumpNmbr(String pumpNmbr) {
        this.pumpNmbr = pumpNmbr;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getRestrictCode() {
        return restrictCode;
    }

    public void setRestrictCode(String restrictCode) {
        this.restrictCode = restrictCode;
    }

    public long getQtyPumped() {
        return qtyPumped;
    }

    public void setQtyPumped(long qtyPumped) {
        this.qtyPumped = qtyPumped;
    }

    public long getFuelPrice() {
        return fuelPrice;
    }

    public void setFuelPrice(long fuelPrice) {
        this.fuelPrice = fuelPrice;
    }

    public String getFuelProdCode() {
        return fuelProdCode;
    }

    public void setFuelProdCode(String fuelProdCode) {
        this.fuelProdCode = fuelProdCode;
    }

    public String getUnitOfMeas() {
        return unitOfMeas;
    }

    public void setUnitOfMeas(String unitOfMeas) {
        this.unitOfMeas = unitOfMeas;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public void setLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }

    public String getDeptNumber() {
        return deptNumber;
    }

    public void setDeptNumber(String deptNumber) {
        this.deptNumber = deptNumber;
    }

    public String getJobValueNumber() {
        return jobValueNumber;
    }

    public void setJobValueNumber(String jobValueNumber) {
        this.jobValueNumber = jobValueNumber;
    }

    public String getDataNumber() {
        return dataNumber;
    }

    public void setDataNumber(String dataNumber) {
        this.dataNumber = dataNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getBillingCountryCode() {
        return billingCountryCode;
    }

    public void setBillingCountryCode(String billingCountryCode) {
        this.billingCountryCode = billingCountryCode;
    }

    public String getShippingCountryCode() {
        return shippingCountryCode;
    }

    public void setShippingCountryCode(String shippingCountryCode) {
        this.shippingCountryCode = shippingCountryCode;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getBillingZipCode() {
        return billingZipCode;
    }

    public void setBillingZipCode(String billingZipCode) {
        this.billingZipCode = billingZipCode;
    }

    public String getShippingZipCode() {
        return shippingZipCode;
    }

    public void setShippingZipCode(String shippingZipCode) {
        this.shippingZipCode = shippingZipCode;
    }

    public String getBillingPhone() {
        return billingPhone;
    }

    public void setBillingPhone(String billingPhone) {
        this.billingPhone = billingPhone;
    }

    public String getShippingPhone() {
        return shippingPhone;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getEncryptMgmt() {
        return encryptMgmt;
    }

    public void setEncryptMgmt(String encryptMgmt) {
        this.encryptMgmt = encryptMgmt;
    }

    public String getEncryptAlgo() {
        return encryptAlgo;
    }

    public void setEncryptAlgo(String encryptAlgo) {
        this.encryptAlgo = encryptAlgo;
    }

    public String getSettleRq() {
        return settleRq;
    }

    public void setSettleRq(String settleRq) {
        this.settleRq = settleRq;
    }

    public String getOriginalOrder() {
        return originalOrder;
    }

    public void setOriginalOrder(String originalOrder) {
        this.originalOrder = originalOrder;
    }

    public String getOrigTransId() {
        return origTransId;
    }

    public void setOrigTransId(String origTransId) {
        this.origTransId = origTransId;
    }

    public String getOrigAuthCode() {
        return origAuthCode;
    }

    public void setOrigAuthCode(String origAuthCode) {
        this.origAuthCode = origAuthCode;
    }

    public long getAmtPreAuthorized() {
        return amtPreAuthorized;
    }

    public void setAmtPreAuthorized(long amtPreAuthorized) {
        this.amtPreAuthorized = amtPreAuthorized;
    }

    public String getProdDetailCount() {
        return prodDetailCount;
    }

    public void setProdDetailCount(String prodDetailCount) {
        this.prodDetailCount = prodDetailCount;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getAuthNumber() {
        return authNumber;
    }

    public void setAuthNumber(String authNumber) {
        this.authNumber = authNumber;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public long getPartialAmount() {
        return partialAmount;
    }

    public void setPartialAmount(long partialAmount) {
        this.partialAmount = partialAmount;
    }

    public String getMilstarNumber() {
        return milstarNumber;
    }

    public void setMilstarNumber(String milstarNumber) {
        this.milstarNumber = milstarNumber;
    }

    public long getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(long balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getSettleRs() {
        return settleRs;
    }

    public void setSettleRs(String settleRs) {
        this.settleRs = settleRs;
    }

    public long getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(long settleAmt) {
        this.settleAmt = settleAmt;
    }

    public String getOrigAcctType() {
        return origAcctType;
    }

    public void setOrigAcctType(String origAcctType) {
        this.origAcctType = origAcctType;
    }

    public String getModifiedAcctValue() {
        return modifiedAcctValue;
    }

    public void setModifiedAcctValue(String modifiedAcctValue) {
        this.modifiedAcctValue = modifiedAcctValue;
    }

    public String getOriginalRequestType() {
        return originalRequestType;
    }

    public void setOriginalRequestType(String originalRequestType) {
        this.originalRequestType = originalRequestType;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

//    @Override
//    public String toString() {
//        return "Transaction{" + "authHour=" + authHour + ", identityUuid=" + identityUuid + ", localDateTime=" + localDateTime + ", settleIndicator=" + settleIndicator + ", orderNumber=" + orderNumber + ", comment=" + comment + ", customerId=" + customerId + ", media=" + media + ", requestType=" + requestType + ", reversal=" + reversal + ", voidFlag=" + voidFlag + ", inputType=" + inputType + ", upc=" + upc + ", primaryAccountNumber=" + primaryAccountNumber + ", expiration=" + expiration + ", cvv=" + cvv + ", track1=" + track1 + ", track2=" + track2 + ", amount=" + amount + ", essoLoadAmount=" + essoLoadAmount + ", planNumber=" + planNumber + ", originalOrder=" + originalOrder + ", zipCode=" + zipCode + ", requestDescription=" + requestDescription + ", cardholderName=" + cardholderName + ", billingAddress=" + billingAddress + ", billingCountry=" + billingCountry + ", billingZipCode=" + billingZipCode + ", email=" + email + ", billingPhone=" + billingPhone + ", shippingAddress=" + shippingAddress + ", shippingCountry=" + shippingCountry + ", shippingZipCode=" + shippingZipCode + ", shippingPhone=" + shippingPhone + ", responseType=" + responseType + ", authNumber=" + authNumber + ", reasonCode=" + reasonCode + ", milstarNumber=" + milstarNumber + ", balanceAmount=" + balanceAmount + ", responseDescription=" + responseDescription + ", avsResponse=" + avsResponse + ", panToken=" + panToken + ", systemDateTime=" + systemDateTime + ", facility=" + facility + ", settleTs=" + settleTs + ", originalRequestType=" + originalRequestType + '}';
//    }
    public String getSTAN() {
        return STAN;
    }

    public void setSTAN(String STAN) {
        this.STAN = STAN;
    }

    public String getSKUNumber() {
        return SKUNumber;
    }

    public void setSKUNumber(String SKUNumber) {
        this.SKUNumber = SKUNumber;
    }

    public String getCardReferenceID() {
        return CardReferenceID;
    }

    public void setCardReferenceID(String CardReferenceID) {
        this.CardReferenceID = CardReferenceID;
    }

//    public String getResponseCode() {
//        return ResponseCode;
//    }
//
//    public void setResponseCode(String ResponseCode) {
//        this.ResponseCode = ResponseCode;
//    }
    public String getZipCode() {
        return ZipCode;
    }

    public void setZipCode(String ZipCode) {
        this.ZipCode = ZipCode;
    }

    public String getAmountSign() {
        return AmountSign;
    }

    public void setAmountSign(String AmountSign) {
        this.AmountSign = AmountSign;
    }

    public String getDownPayment() {
        return DownPayment;
    }

    public void setDownPayment(String DownPayment) {
        this.DownPayment = DownPayment;
    }

    public String getMerchantOrg() {
        return MerchantOrg;
    }

    public void setMerchantOrg(String MerchantOrg) {
        this.MerchantOrg = MerchantOrg;
    }

    public String getFacility10() {
        return Facility10;
    }

    public void setFacility10(String Facility10) {
        this.Facility10 = Facility10;
    }

    public String getFacility7() {
        return Facility7;
    }

    public void setFacility7(String Facility7) {
        this.Facility7 = Facility7;
    }

    public String getCardSequenceNumber() {
        return CardSequenceNumber;
    }

    public void setCardSequenceNumber(String CardSequenceNumber) {
        this.CardSequenceNumber = CardSequenceNumber;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    /**
     * @return the strategy
     */
    public String getStrategy() {
        return strategy;
    }

    /**
     * @param strategy the strategy to set
     */
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getRequestXmlDateTime() {
        return requestXmlDateTime;
    }

    public void setRequestXmlDateTime(String requestXmlDateTime) {
        this.requestXmlDateTime = requestXmlDateTime;
    }

    public String getResponseXmlDateTime() {
        return responseXmlDateTime;
    }

    public void setResponseXmlDateTime(String responseXmlDateTime) {
        this.responseXmlDateTime = responseXmlDateTime;
    }

    public String getRequestAuthDateTime() {
        return requestAuthDateTime;
    }

    public void setRequestAuthDateTime(String requestAuthDateTime) {
        this.requestAuthDateTime = requestAuthDateTime;
    }

    public String getResponseAuthDateTime() {
        return responseAuthDateTime;
    }

    public void setResponseAuthDateTime(String responseAuthDateTime) {
        this.responseAuthDateTime = responseAuthDateTime;
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

    public String getDivisionnumber() {
        return divisionnumber;
    }

    public void setDivisionnumber(String divisionnumber) {
        this.divisionnumber = divisionnumber;
    }

    public String getCurrencycode() {
        return currencycode;
    }

    public void setCurrencycode(String currencycode) {
        this.currencycode = currencycode;
    }

    public String getTransactiontype() {
        return transactiontype;
    }

    public void setTransactiontype(String transactiontype) {
        this.transactiontype = transactiontype;
    }

    public String getActioncode() {
        return actioncode;
    }

    public void setActioncode(String actioncode) {
        this.actioncode = actioncode;
    }

    public String getBillpaymentindicator() {
        return billpaymentindicator;
    }

    public void setBillpaymentindicator(String billpaymentindicator) {
        this.billpaymentindicator = billpaymentindicator;
    }

    public String getTelephonetype() {
        return telephonetype;
    }

    public void setTelephonetype(String telephonetype) {
        this.telephonetype = telephonetype;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getGcpin() {
        return gcpin;
    }

    public void setGcpin(String gcpin) {
        this.gcpin = gcpin;
    }

    /**
     * @return the termId
     */
    public String getTermId() {
        return termId;
    }

    /**
     * @param termId the termId to set
     */
    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getTokenBankName() {
        return tokenBankName;
    }

    public void setTokenBankName(String tokenBankName) {
        this.tokenBankName = tokenBankName;
    }
    
    public String getOrigRRN() {
        return origRRN;
    }

    public void setOrigRRN(String origRRN) {
        this.origRRN = origRRN;
    }

    /* FIELDS ADDED FOR WEX USE CASE - start */
    
    /**
     * @return the nonFuelProdCode
     */
    public String getNonFuelProdCode() {
        return nonFuelProdCode;
    }

    /**
     * @param nonFuelProdCode the nonFuelProdCode to set
     */
    public void setNonFuelProdCode(String nonFuelProdCode) {
        this.nonFuelProdCode = nonFuelProdCode;
    }

    /**
     * @return the nonFuelAmount
     */
    public BigDecimal getNonFuelAmount() {
        return nonFuelAmount;
    }

    /**
     * @return the fuelDollerAmount
     */
    public BigDecimal getFuelDollerAmount() {
        return fuelDollerAmount;
    }

    /**
     * @param fuelDollerAmount the fuelDollerAmount to set
     */
    public void setFuelDollerAmount(BigDecimal fuelDollerAmount) {
        this.fuelDollerAmount = fuelDollerAmount;
    }

    /**
     * @param nonFuelAmount the nonFuelAmount to set
     */
    public void setNonFuelAmount(BigDecimal nonFuelAmount) {
        this.nonFuelAmount = nonFuelAmount;
    }

   

    /**
     * @return the pricePerUnit
     */
    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    /**
     * @param pricePerUnit the pricePerUnit to set
     */
    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

   
    /* FIELDS ADDED FOR WEX USE CASE - start */

    /**
     * @return the catFlag
     */
    public String getCatFlag() {
        return catFlag;
    }

    /**
     * @param catFlag the catFlag to set
     */
    public void setCatFlag(String catFlag) {
        this.catFlag = catFlag;
    }

    /**
     * @return the cardSeqNumber
     */
    public String getCardSeqNumber() {
        return cardSeqNumber;
    }

    /**
     * @param cardSeqNumber the cardSeqNumber to set
     */
    public void setCardSeqNumber(String cardSeqNumber) {
        this.cardSeqNumber = cardSeqNumber;
    }

    /**
     * @return the quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the nonFuelqty
     */
    public BigDecimal getNonFuelqty() {
        return nonFuelqty;
    }

    /**
     * @param nonFuelqty the nonFuelqty to set
     */
    public void setNonFuelqty(BigDecimal nonFuelqty) {
        this.nonFuelqty = nonFuelqty;
    }
  
}