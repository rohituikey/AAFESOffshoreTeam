/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.nbslogonrequest.NbsLogonRequest;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.wex.simulator.NBSClient;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.dao.TransactionDAO;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
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
    private TransactionDAO transactionDAO;
    private NbsLogonRequest nbsLogOnRequest;

    public Transaction preAuthProcess(Transaction t) {
        //logon pocket fields 

//        root.setAppName("abcdef");
//        root.setAppVersion(BigInteger.valueOf(11));
////        root.setHeaderRecord();
//        root.setTermId(t.getTermId());
//        BigInteger dtf = createDateFormat(t.getLocalDateTime());
//        root.setTimeZone(dtf);

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
        Transaction authTran = transactionDAO.find(t.getIdentityUuid(),t.getRrn(),RequestType.PREAUTH);
        if(authTran == null)
        {
            this.buildErrorResponse(t, "NO_PRIOR_TRANSACTION", "NO_PRIOR_TRANSACTION_FOUND_FOR_FINALAUTH");
            return t;
        }
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
