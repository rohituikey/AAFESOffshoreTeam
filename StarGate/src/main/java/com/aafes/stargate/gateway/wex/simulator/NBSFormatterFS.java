/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex.simulator;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.util.WexConstants;
import com.aafes.stargate.util.InputType;
import java.text.DecimalFormat;
import javax.ejb.EJB;

/**
 *
 * @author alugumetlas
 */
public class NBSFormatterFS {

    String[] productDetails;
    @EJB
    String startOfText = WexConstants.DELIMITERSTARTOFTEXT;
    String endOfText = WexConstants.DELIMITERENDOFTEXT;
    String fieldSeparator = WexConstants.DELIMITERFIELDSEPARATOR;
    @EJB
    private Configurator configurator;

    public String createLogOnRequest(Transaction t) {
        StringBuilder sb = new StringBuilder();
        if (null != t.getTermId() || !t.getTermId().isEmpty()) {
            sb.append(startOfText).append(t.getTermId());//termID
        }
        sb.append(fieldSeparator);
        sb.append(configurator.get("APPLICATION_NAME"));//APPLICATIONNAME
        sb.append(fieldSeparator);
        sb.append(configurator.get("APPLICATION_VERSION"));//APPLICATIONVERSION
        sb.append(fieldSeparator);
        sb.append(WexConstants.createDateFormat());
        return sb.toString();
    }

    public String createPreAuthRequest(Transaction t) {

        StringBuilder sb = new StringBuilder();
        sb.append(fieldSeparator);
        sb.append(WexConstants.SESSIONTYPEAUTH);
        if (t.getTransactionId().length() >= 4) {
            sb.append(fieldSeparator);
            sb.append(t.getTransactionId().substring(t.getTransactionId().length() - 4, t.getTransactionId().length()));//key
        } else {
        }
        sb.append(fieldSeparator);
        sb.append(WexConstants.TRANSTYPEPREAUTH);//transtype
        sb.append(fieldSeparator);
        sb.append(WexConstants.CARDTYPEWEX);//cardType
        if (null != t.getCatFlag() || !t.getCatFlag().isEmpty()) {
            sb.append(fieldSeparator);
            sb.append(t.getCatFlag());//catFlag
        }
        if (null != t.getPumpNmbr() || !t.getPumpNmbr().isEmpty()) {
            sb.append(fieldSeparator);
            sb.append(t.getPumpNmbr());//pumpNUMBER
        }
        sb.append(fieldSeparator);
        sb.append(WexConstants.SERVICETYPE);//SERVICETYPE
        if (t.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
            sb.append(fieldSeparator);
            sb.append(WexConstants.TRACKNUMBERWEXTWO);//track
            if (null != t.getTrack2() || !t.getTrack2().isEmpty()) {
                sb.append(fieldSeparator);
                sb.append(t.getTrack2());//track2
            }
        }
        if (t.getAmount() > 0) {
            double d = t.getAmount() / 100;
            DecimalFormat format = new DecimalFormat("0.00");
            String formatted = format.format(d);
            sb.append(fieldSeparator);
            sb.append(formatted);//amount
        }
        sb.append(fieldSeparator);
        sb.append(t.getPromptDetailCount().toString());
        if (null != t.getVehicleId() && t.getVehicleId().trim().length() > 0) {
            sb.append(fieldSeparator);
            sb.append(WexConstants.VEHICLEID);//prompttype
            sb.append(fieldSeparator);
            sb.append(t.getVehicleId());//promptvalue
        }
        if (null != t.getDriverId() && t.getDriverId().trim().length() > 0) {
            sb.append(fieldSeparator);
            sb.append(WexConstants.DRIVERID);//prompttype
            sb.append(fieldSeparator);
            sb.append(t.getDriverId());//promptvalue
        }
        if (null != t.getOdoMeter() || t.getOdoMeter().trim().length() > 0) {
            sb.append(fieldSeparator);
            sb.append(WexConstants.ODOMOETER);//prompttype
            sb.append(fieldSeparator);
            sb.append(t.getOdoMeter());//promptvalue
        }
        sb.append(fieldSeparator);
        sb.append(t.getProdDetailCount());//proDetailCount
        if (null != t.getProducts() && (t.getProducts().size()) > 0) {
            for (String nonFuelString : t.getProducts()) {
                if (nonFuelString.contains(WexConstants.PRODUCTDELIMITOR)) {
                    productDetails = nonFuelString.split(WexConstants.PRODUCTDELIMITOR);
                    sb.append(fieldSeparator);
                    sb.append(productDetails[2]);//price
                    sb.append(fieldSeparator).append(productDetails[1]);//quantity
                    sb.append(fieldSeparator).append(productDetails[0]);//prodCode
                    sb.append(fieldSeparator).append(productDetails[3]);//fdAmount
                }
            }
            sb.append(endOfText);
        }
        return sb.toString();
    }

    public String createFinalAuthRequest(Transaction t) {
        StringBuilder sb = new StringBuilder();
        sb.append(fieldSeparator);
        sb.append(WexConstants.CAPTUREONLYREQUEST);
        if (t.getTransactionId().length() >= 4) {
            sb.append(fieldSeparator).append(t.getTransactionId().substring(t.getTransactionId().length() - 4, t.getTransactionId().length()));//key
        }
        sb.append(fieldSeparator).append(WexConstants.TRANSTYPEFINALANDSALE);//transtype
        sb.append(fieldSeparator).append(WexConstants.CARDTYPEWEX);//cardType
        if (null != t.getCatFlag() || !t.getCatFlag().isEmpty()) {
            sb.append(fieldSeparator).append(t.getCatFlag());//catFlag
        }
        if (null != t.getPumpNmbr() || !t.getPumpNmbr().isEmpty()) {
            sb.append(fieldSeparator).append(t.getPumpNmbr());//pumpNUMBER
        }
        sb.append(fieldSeparator).append(WexConstants.SERVICETYPE);//SERVICETYPE
        if (t.getAmount() > 0) {
            double d = t.getAmount() / 100;
            DecimalFormat format = new DecimalFormat("0.00");
            String formatted = format.format(d);
            sb.append(fieldSeparator);
            sb.append(formatted);//amount
        }
        if (t.getAmtPreAuthorized() > 0) {
            double d = t.getAmtPreAuthorized() / 100;
            DecimalFormat format = new DecimalFormat("0.00");
            String formatted = format.format(d);
            sb.append(fieldSeparator);
            sb.append(formatted);//amount
        }
        if (t.getTransactionId().length() >= 4) {
            sb.append(fieldSeparator);
            sb.append(t.getTransactionId().substring(t.getTransactionId().length() - 4, t.getTransactionId().length()));
        }
        //**for testing i commented and hardcoded value ///we have to unacomment  line no.156 and remove 157 line
        //sb.append(fieldSeparator).append(WexConstants.createDateAndTime());
        sb.append(fieldSeparator).append("170911123042");
        if (t.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
            sb.append(fieldSeparator).append(WexConstants.TRACKNUMBERWEXTWO);//track
            if (null != t.getTrack2() || !t.getTrack2().isEmpty()) {
                sb.append(fieldSeparator).append(t.getTrack2());//track2
            }
        }
        //sb.append(fieldSeparator).append(t.getTransactionId()).append(t.getTermId());
        sb.append(fieldSeparator);
        sb.append("0230072");
        sb.append(fieldSeparator);
        sb.append(t.getOrigAuthCode());
        sb.append(fieldSeparator);
        sb.append(t.getPromptDetailCount().toString());
        if (null != t.getVehicleId() && t.getVehicleId().trim().length() > 0) {
            sb.append(fieldSeparator);
            sb.append(WexConstants.VEHICLEID);//prompttype
            sb.append(fieldSeparator);
            sb.append(t.getVehicleId());//promptvalue
        }
        if (null != t.getDriverId() && t.getDriverId().trim().length() > 0) {
            sb.append(fieldSeparator);
            sb.append(WexConstants.DRIVERID);//prompttype
            sb.append(fieldSeparator);
            sb.append(t.getDriverId());//promptvalue
        }
        if (null != t.getOdoMeter() || t.getOdoMeter().trim().length() > 0) {
            sb.append(fieldSeparator);
            sb.append(WexConstants.ODOMOETER);//prompttype
            sb.append(fieldSeparator);
            sb.append(t.getOdoMeter());//promptvalue
        }
        sb.append(fieldSeparator);
        sb.append(t.getProdDetailCount());//proDetailCount
        if (null != t.getProducts() && (t.getProducts().size()) > 0) {
            for (String nonFuelString : t.getProducts()) {
                if (nonFuelString.contains(WexConstants.PRODUCTDELIMITOR)) {
                    productDetails = nonFuelString.split(WexConstants.PRODUCTDELIMITOR);
                    sb.append(fieldSeparator);
                    sb.append(productDetails[2]);//price
                    sb.append(fieldSeparator);
                    sb.append(productDetails[1]);//quantity
                    sb.append(fieldSeparator);
                    sb.append(productDetails[0]);//prodCode
                    sb.append(fieldSeparator);
                    sb.append(productDetails[3]);//fdAmount
                }
            }
            sb.append(endOfText);
        }
        return sb.toString();
    }

    public String createRefundRequest(Transaction t) {
         StringBuilder sb = new StringBuilder();
        sb.append(fieldSeparator);
        sb.append(WexConstants.SESSIONTYPEAUTH);
        if (t.getTransactionId().length() >= 4) {
            sb.append(fieldSeparator);
            sb.append(t.getTransactionId().substring(t.getTransactionId().length() - 4, t.getTransactionId().length()));//key
        } else {
        }
        sb.append(fieldSeparator);
        sb.append(WexConstants.TRANSTYPEREFUND);//transtype
        sb.append(fieldSeparator);
        sb.append(WexConstants.CARDTYPEWEX);//cardType
        if (null != t.getCatFlag() || !t.getCatFlag().isEmpty()) {
            sb.append(fieldSeparator);
            sb.append(t.getCatFlag());//catFlag
        }
        if (null != t.getPumpNmbr() || !t.getPumpNmbr().isEmpty()) {
            sb.append(fieldSeparator);
            sb.append(t.getPumpNmbr());//pumpNUMBER
        }
        sb.append(fieldSeparator);
        sb.append(WexConstants.SERVICETYPE);//SERVICETYPE
        if (t.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
            sb.append(fieldSeparator);
            sb.append(WexConstants.TRACKNUMBERWEXTWO);//track
            if (null != t.getTrack2() || !t.getTrack2().isEmpty()) {
                sb.append(fieldSeparator);
                sb.append(t.getTrack2());//track2
            }
        }
        if (t.getAmount() > 0) {
            double d = t.getAmount() / 100;
            DecimalFormat format = new DecimalFormat("0.00");
            String formatted = format.format(d);
            sb.append(fieldSeparator);
            sb.append(formatted);//amount
        }
        sb.append(fieldSeparator);
        sb.append(t.getPromptDetailCount().toString());
        if (null != t.getVehicleId() && t.getVehicleId().trim().length() > 0) {
            sb.append(fieldSeparator);
            sb.append(WexConstants.VEHICLEID);//prompttype
            sb.append(fieldSeparator);
            sb.append(t.getVehicleId());//promptvalue
        }
        if (null != t.getDriverId() && t.getDriverId().trim().length() > 0) {
            sb.append(fieldSeparator);
            sb.append(WexConstants.DRIVERID);//prompttype
            sb.append(fieldSeparator);
            sb.append(t.getDriverId());//promptvalue
        }
        if (null != t.getOdoMeter() || t.getOdoMeter().trim().length() > 0) {
            sb.append(fieldSeparator);
            sb.append(WexConstants.ODOMOETER);//prompttype
            sb.append(fieldSeparator);
            sb.append(t.getOdoMeter());//promptvalue
        }
        sb.append(fieldSeparator);
        sb.append(t.getProdDetailCount());//proDetailCount
        if (null != t.getProducts() && (t.getProducts().size()) > 0) {
            for (String nonFuelString : t.getProducts()) {
                if (nonFuelString.contains(WexConstants.PRODUCTDELIMITOR)) {
                    productDetails = nonFuelString.split(WexConstants.PRODUCTDELIMITOR);
                    sb.append(fieldSeparator);
                    sb.append(productDetails[2]);//price
                    sb.append(fieldSeparator).append(productDetails[1]);//quantity
                    sb.append(fieldSeparator).append(productDetails[0]);//prodCode
                    sb.append(fieldSeparator).append(productDetails[3]);//fdAmount
                }
            }
            sb.append(endOfText);
        }
        return sb.toString();
    }

    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }

}
