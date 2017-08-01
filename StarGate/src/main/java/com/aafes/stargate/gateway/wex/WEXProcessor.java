/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.nbslogonrequestschema.NbsLogonRequest;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.wex.simulator.NBSClient;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.util.ResponseType;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alugumetlas
 */
@Stateless
public class WEXProcessor {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(WEXProcessor.class.getSimpleName());
    private String sMethodName = "";
    private final String CLASS_NAME = WEXProcessor.this.getClass().getSimpleName();
    
    @EJB
    private Configurator configurator;
    private NbsLogonRequest nbsLogOnRequest;

    public Transaction preAuthProcess(Transaction t) {
        if (Integer.parseInt(t.getProdDetailCount()) > 5) {
            //if(t.getNonFuelProdCode.size() > 2)
            this.buildErrorResponse(t, "PRODUCT_DETAIL_COUNT_EXCEEDED", "SELECTED PRODUCT COUNT EXCEEDED");
            return t;
        }
        if(null == nbsLogOnRequest) nbsLogOnRequest = new NbsLogonRequest();
        //logon pocket fields setting
        //nbsLogOnRequest.setAppName(value);
        //nbsLogOnRequest.setAppVersion();
        //nbsLogOnRequest.setHeaderRecord();
        nbsLogOnRequest.setTermId(t.getTermId());
        //nbsLogOnRequest.setTimeZone();
        
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

    public Transaction processRefundRequest(Transaction t) {
        sMethodName = "processRefundRequest";
        LOG.info("Method " + sMethodName + " started." + "in  Class Name " + CLASS_NAME);
        String responseStr = "";
        NBSClient clientObj = new NBSClient();
        responseStr = clientObj.generateResponse("APPROVED");
        t.setResponseType(responseStr);
        LOG.info("Method " + sMethodName + " ended." + "in  Class Name " + CLASS_NAME);
        return t;
    }
    
    private void buildErrorResponse(Transaction t, String reasonCode, String description) {
        t.setReasonCode(configurator.get(reasonCode));
        t.setResponseType(ResponseType.DECLINED);
        t.setDescriptionField(description);
        //LOG.error("Exception/Error occured. reasonCode:" + reasonCode + " .description" + description);
    }

}
