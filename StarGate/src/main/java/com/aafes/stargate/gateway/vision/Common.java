/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.vision;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author nguyentul
 */
final public class Common {

//    public static String getStackTrace(Throwable t) {
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new PrintWriter(sw);
//        t.printStackTrace(pw);
//        return sw.toString();
//    }
//
//    public static int convertDecimalToInt(String decimal) {
//        int longInt = Integer.parseInt(decimal.replace(".", ""));
//        return longInt;
//    }
//
//    public static String getLastFourChar(String msg) {
//        if (msg != null && msg.length() > 5) {
//            String returnStr = msg.substring(msg.length() - 4, msg.length());
//            return returnStr;
//        } else {
//            return msg;
//        }
//    }
//
//    public static String getDate(String format) {
//        Date dNow = new Date();
//        SimpleDateFormat ft
//                = new SimpleDateFormat(format);
//        System.out.println("Current Date: " + ft.format(dNow));
//        return ft.format(dNow);
//    }
//
//    public static String getTime() {
//        Date dNow = new Date();
//        SimpleDateFormat ft
//                = new SimpleDateFormat("HHmmssSS");
//
//        System.out.println("Current Date: " + ft.format(dNow));
//        return ft.format(dNow);
//    }

    public static String convertStackTraceToString(Exception e) {
        StringBuilder sb = new StringBuilder();
        if (e != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
                // ignore
            }
        }
        return sb.toString();
    }
}
