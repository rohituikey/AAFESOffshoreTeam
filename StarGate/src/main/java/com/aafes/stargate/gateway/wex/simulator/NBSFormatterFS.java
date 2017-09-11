/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex.simulator;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.util.ConstantsUtil;
import com.aafes.stargate.util.InputType;
import static com.ibm.disthub2.impl.formats.Multi.Constants.numbers_table_type.table_type.number;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.EJB;
/**
 *
 * @author alugumetlas
 */
public class NBSFormatterFS {

    String[] productDetails;
    @EJB
    private Configurator configurator;

    public String createPreAuthRequestForNBS(Transaction t) {

        StringBuilder str = new StringBuilder();
        //LOGON Request
        if (null != t.getTermId() || !t.getTermId().isEmpty()) {
            str.append(configurator.get("START_TAG")).append(t.getTermId());//termID
        }
        str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.APPLICATIONNAME);//APPLICATIONNAME
        str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.APPLICATIONVERSION);//APPLICATIONVERSION
        str.append(configurator.get("FIELD_SEPERATOR")).append(createDateFormat());
        //ACTUAL AUTH REQUEST STARTS
        str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.SESSIONTYPEAUTH);
        if (null != t.getTransactionId() || !t.getTransactionId().isEmpty()) {
            str.append(configurator.get("FIELD_SEPERATOR")).append(t.getTransactionId().substring(0, 4));//key
        }
        str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.TRANSTYPEPREAUTH);//transtype
        str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.CARDTYPEWEX);//cardType
        if (null != t.getCatFlag() || !t.getCatFlag().isEmpty()) {
            str.append(configurator.get("FIELD_SEPERATOR")).append(t.getCatFlag());//catFlag
        }
        if (null != t.getPumpNmbr() || !t.getPumpNmbr().isEmpty()) {
            str.append(configurator.get("FIELD_SEPERATOR")).append(t.getPumpNmbr());//pumpNUMBER
        }
        str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.SERVICETYPE);//SERVICETYPE
        if (t.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
            str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.TRACKNUMBERWEXTWO);//track
            if (null != t.getTrack2() || !t.getTrack2().isEmpty()) {
                str.append(configurator.get("FIELD_SEPERATOR")).append(t.getTrack2());//track2
            }
        }
        if(t.getAmount()>0){
          double d =  t.getAmount()/100;
        DecimalFormat format = new DecimalFormat("0.00");
        String formatted = format.format(d);
        str.append(configurator.get("FIELD_SEPERATOR")).append(formatted);//amount
        }
        str.append(configurator.get("FIELD_SEPERATOR")).append(t.getPromptDetailCount().toString());
        if (null != t.getVehicleId()&&t.getVehicleId().trim().length()>0 ) {
            str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.VEHICLEID);//prompttype
            str.append(configurator.get("FIELD_SEPERATOR")).append(t.getVehicleId());//promptvalue
        }
        if (null != t.getDriverId() && t.getDriverId().trim().length()>0 ) {
            str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.DRIVERID);//prompttype
            str.append(configurator.get("FIELD_SEPERATOR")).append(t.getDriverId());//promptvalue
        }
        if (null != t.getOdoMeter() || t.getOdoMeter().trim().length()>0 ) {
            str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.ODOMOETER);//prompttype
            str.append(configurator.get("FIELD_SEPERATOR")).append(t.getOdoMeter());//promptvalue
        }
        str.append(configurator.get("FIELD_SEPERATOR")).append(t.getProdDetailCount());//proDetailCount
        if (null != t.getProducts() && (t.getProducts().size()) > 0) {
            for (String nonFuelString : t.getProducts()) {
                if (nonFuelString.contains(ConstantsUtil.PRODUCTDELIMITOR)) {
                    productDetails = nonFuelString.split(ConstantsUtil.PRODUCTDELIMITOR);
                    str.append(configurator.get("FIELD_SEPERATOR")).append(productDetails[2]);//price
                    str.append(configurator.get("FIELD_SEPERATOR")).append(productDetails[1]);//quantity
                    str.append(configurator.get("FIELD_SEPERATOR")).append(productDetails[0]);//prodCode
                    str.append(configurator.get("FIELD_SEPERATOR")).append(productDetails[3]);//fdAmount
                }
            }
            str.append(configurator.get("END_TAG"));
        }
        return str.toString();
    }
    public String createFinalRequestForNbs(Transaction t)
    {
       StringBuilder str = new StringBuilder();
        //LOGON Request
        if (null != t.getTermId() || !t.getTermId().isEmpty()) {
            str.append(configurator.get("START_TAG")).append(t.getTermId());//termID
        }
        str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.APPLICATIONNAME);//APPLICATIONNAME
        str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.APPLICATIONVERSION);//APPLICATIONVERSION
        str.append(configurator.get("FIELD_SEPERATOR")).append(createDateFormat());
        //ACTUAL AUTH REQUEST STARTS
        str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.SESSIONTYPEAUTH);
        if (null != t.getTransactionId() || !t.getTransactionId().isEmpty()) {
            str.append(configurator.get("FIELD_SEPERATOR")).append(t.getTransactionId().substring(0, 4));//key
        }
        str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.TRANSTYPEPREAUTH);//transtype
        str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.CARDTYPEWEX);//cardType
        if (null != t.getCatFlag() || !t.getCatFlag().isEmpty()) {
            str.append(configurator.get("FIELD_SEPERATOR")).append(t.getCatFlag());//catFlag
        }
        if (null != t.getPumpNmbr() || !t.getPumpNmbr().isEmpty()) {
            str.append(configurator.get("FIELD_SEPERATOR")).append(t.getPumpNmbr());//pumpNUMBER
        }
        str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.SERVICETYPE);//SERVICETYPE
        if (t.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
            str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.TRACKNUMBERWEXTWO);//track
            if (null != t.getTrack2() || !t.getTrack2().isEmpty()) {
                str.append(configurator.get("FIELD_SEPERATOR")).append(t.getTrack2());//track2
            }
        }
        if(t.getAmount()>0){
          double d =  t.getAmount()/100;
        DecimalFormat format = new DecimalFormat("0.00");
        String formatted = format.format(d);
        str.append(configurator.get("FIELD_SEPERATOR")).append(formatted);//amount
        }
        str.append(configurator.get("FIELD_SEPERATOR")).append(t.getPromptDetailCount().toString());
        if (null != t.getVehicleId()&&t.getVehicleId().trim().length()>0 ) {
            str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.VEHICLEID);//prompttype
            str.append(configurator.get("FIELD_SEPERATOR")).append(t.getVehicleId());//promptvalue
        }
        if (null != t.getDriverId() && t.getDriverId().trim().length()>0 ) {
            str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.DRIVERID);//prompttype
            str.append(configurator.get("FIELD_SEPERATOR")).append(t.getDriverId());//promptvalue
        }
        if (null != t.getOdoMeter() || t.getOdoMeter().trim().length()>0 ) {
            str.append(configurator.get("FIELD_SEPERATOR") + ConstantsUtil.ODOMOETER);//prompttype
            str.append(configurator.get("FIELD_SEPERATOR")).append(t.getOdoMeter());//promptvalue
        }
        str.append(configurator.get("FIELD_SEPERATOR")).append(t.getProdDetailCount());//proDetailCount
        if (null != t.getProducts() && (t.getProducts().size()) > 0) {
            for (String nonFuelString : t.getProducts()) {
                if (nonFuelString.contains(ConstantsUtil.PRODUCTDELIMITOR)) {
                    productDetails = nonFuelString.split(ConstantsUtil.PRODUCTDELIMITOR);
                    str.append(configurator.get("FIELD_SEPERATOR")).append(productDetails[2]);//price
                    str.append(configurator.get("FIELD_SEPERATOR")).append(productDetails[1]);//quantity
                    str.append(configurator.get("FIELD_SEPERATOR")).append(productDetails[0]);//prodCode
                    str.append(configurator.get("FIELD_SEPERATOR")).append(productDetails[3]);//fdAmount
                }
            }
            str.append(configurator.get("END_TAG"));
        }
        return str.toString();
    }

    private String createDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date();
        String ts = dateFormat.format(date);
        //2017-08-08 08:39:30.967
        ts = ts.substring(11, 13) + ts.substring(14, 16) + ConstantsUtil.DAYLIGHTSAVINGSTIMEATSITEONE;
        return ts;
    }

    public Configurator getConfigurator() {
        return configurator;
    }

    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }
    
    
    
}
