/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.util;

/**
 *
 * @author burangir
 */
public class QueryConstants {
    
    public static final String FETCH_UUID = "SELECT * FROM STARGATE.FACMAPPER WHERE STRATEGY = ? ALLOW FILTERING";
    public static final String FETCH_DATA_FOR_RETAIL_REPORT =  " FROM STARSETTLER.SETTLEMESSAGES WHERE RECEIVEDDATE = ? AND SETTLESTATUS = ? "
    + "AND IDENTITYUUID = ? ALLOW FILTERING";
}