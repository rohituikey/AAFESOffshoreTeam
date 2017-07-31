/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.wex.simulator.NBSClient;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.util.ResponseType;
import com.aafes.stargate.util.SvsUtil;
import generated.Root;
import java.math.BigInteger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author alugumetlas
 */
@Stateless
public class WEXProcessor {

    @EJB
    private Configurator configurator;
    @EJB
    private Root root;

    public Transaction preAuthProcess(Transaction t) {
        if (Integer.parseInt(t.getProdDetailCount()) > 5) {
            //if(t.getNonFuelProdCode.size() > 2)
            this.buildErrorResponse(t, "PRODUCT_DETAIL_COUNT_EXCEEDED", "SELECTED PRODUCT COUNT EXCEEDED");
            return t;
        }
        //logon pocket fields setting
        //root.setAppName(value);
        //root.setAppVersion();
        //root.setHeaderRecord();
        root.setTermId(t.getTermId());
        //root.setTimeZone();
        
        String responseStr = "";
        NBSClient clientObj = new NBSClient();
        responseStr = clientObj.generateResponse("APPROVED");
        t.setResponseType(responseStr);

        return t;
    }

    public Transaction finalAuthProcess(Transaction t) {
        String responseStr = "";
        NBSClient clientObj = new NBSClient();
        responseStr = clientObj.generateResponse("APPROVED");
        t.setResponseType(responseStr);
        return t;
    }

    public Transaction processSaleRequest(Transaction t) {
        String responseStr = "";
        NBSClient clientObj = new NBSClient();
        responseStr = clientObj.generateResponse("APPROVED");
        t.setResponseType(responseStr);
        return t;
    }

    private void buildErrorResponse(Transaction t, String reasonCode, String description) {
        t.setReasonCode(configurator.get(reasonCode));
        t.setResponseType(ResponseType.DECLINED);
        t.setDescriptionField(description);
        //LOG.error("Exception/Error occured. reasonCode:" + reasonCode + " .description" + description);
    }

}
