/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.entity;

/**
 *
 * @author ganjis
 */
public class AuthorizationCodes {
    
    private String responseReasonCode = "" ;
    private String responseDate = "";
    private String authoriztionCode = "";
    private String avsResponseCode = "";
    private String csvResponseCode = "";

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
}
