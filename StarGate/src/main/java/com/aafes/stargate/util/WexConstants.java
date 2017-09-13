/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author burangir
 *
 * THE CLASS HOLDS VALUES OF CONSTANTS WHICH ARE USED ACROSS THE APPLICATION.
 */
public class WexConstants {

    public static final String DAYLIGHTSAVINGSTIMEATSITEONE = "1";
    public static final String CAPTUREONLYREQUEST = "C";
    public static final String SESSIONTYPEAUTH = "A";
    public static final String TRANSTYPEPREAUTH = "08";
    public static final String TRANSTYPEFINALANDSALE = "10";
    public static final String TRANSTYPEREFUND = "30";
    public static final String CARDTYPEWEX = "WI";
    public static final String SERVICETYPE = "S";
    public static final String MTI = "200";
    public static final String TRACKNUMBERWEXTWO = "2";
    public static final String TRACKNUMBERWEXZERO = "0";
    public static final String VEHICLEID = "1";
    public static final String DRIVERID = "3";
    public static final String ODOMOETER = "4";
    public static final String PRODUCTDELIMITOR = ":";
    public static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String DELIMITERSTARTOFTEXT = "<SX>";
    public static final String DELIMITERENDOFTEXT = "<EX><LF>";
    public static final String DELIMITERFIELDSEPARATOR = "<FS>";

    public static String createDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
        Date date = new Date();
        String ts = dateFormat.format(date);
        //2017-08-08 08:39:30.967
        ts = ts.substring(11, 13) + ts.substring(14, 16) + DAYLIGHTSAVINGSTIMEATSITEONE;
        return ts;
    }
       public static String createDateAndTime() {
        //        YYMMDDhhmmss
        //2017-08-03 09:31:54.316
        DateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
        Date date = new Date();
        String dt = dateFormat.format(date);
        dt = dt.substring(2, 4) + dt.substring(5, 7) + dt.substring(8, 10) + dt.substring(11, 13) + dt.substring(14, 16) + dt.substring(17, 19);
        
        return dt;
    }
}
