/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.util;

import com.svs.svsxml.service.SVSXMLWay;
import com.svs.svsxml.service.SVSXMLWayService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;

/**
 *
 * @author burangir
 */
public class SvsUtil {
     public static String formatLocalDateTime() {
        String ts = "";
        try{ 
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String dateStr = sdf.format(new Date());
            XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateStr); 

            ts = xmlCal.toString();
        }catch(Exception e){
            e.printStackTrace();;
        }
        return ts;
    }
    
    public static String generateStan(){
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        String stanValue = sdf.format(new Date());
        return stanValue;
    } 
    
    public static SVSXMLWay setUserNamePassword(){
        SVSXMLWayService sVSXMLWayService = new SVSXMLWayService();

        SVSXMLWay sVSXMLWay = sVSXMLWayService.getSVSXMLWay();
        Map<String, Object> requestContext = ((BindingProvider) sVSXMLWay).getRequestContext();
        requestContext.put(BindingProvider.USERNAME_PROPERTY, "extspeedfcuat");
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, "Rc464Fc14");
        
        //FOLLOWING PROPERTIES ARE SET TO MAKE TIMEOUT OF READ RESPONSE OF 10 SECONDS I.E. 10000 MILLISECONDS
        requestContext.put("com.sun.xml.internal.ws.request.timeout", StarGateConstants.LONG_REQUEST_TIMEOUT);
        requestContext.put("com.sun.xml.internal.ws.connect.timeout", StarGateConstants.LONG_CONNECT_TIMEOUT);
        
        return sVSXMLWay;
    }
}