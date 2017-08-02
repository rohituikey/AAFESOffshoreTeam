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
import java.math.BigInteger;
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
    @EJB
    private TransactionDAO transactionDAO;

    private NbsLogonRequest nbsLogOnRequest;

    public Transaction preAuthProcess(Transaction t) {
        LOG.info("WEXProcessor.preAuthProcess mothod started");

        try {
            if (null == nbsLogOnRequest) {
                nbsLogOnRequest = new NbsLogonRequest();
            }
            //logon pocket fields setting
            //nbsLogOnRequest.setAppName(value);
            //nbsLogOnRequest.setAppVersion();
            //nbsLogOnRequest.setHeaderRecord();
            nbsLogOnRequest.setTermId(t.getTermId());
//        BigInteger dtf = createDateFormat(t.getLocalDateTime());
//        nbsLogOnRequest.setTimeZone(dtf);

            String responseStr = "";
            NBSClient clientObj = new NBSClient();
            responseStr = clientObj.generateResponse("APPROVED");
            t.setResponseType(responseStr);
            if (t.getResponseType().equalsIgnoreCase(ResponseType.APPROVED)) {
                t.setReasonCode("100");
                t.setDescriptionField(ResponseType.APPROVED);
            }
            else
            {
 
            t.setDescriptionField(ResponseType.DECLINED);
            }
            LOG.info("WEXProcessor.preAuthProcess mothod ended");
            return t;
        } catch (Exception e) {
            throw e;
        }
    }
    public Transaction finalAuthProcess(Transaction t) {
        LOG.info("WEXProcessor.finalAuthProcess mothod started");
        try {
            Transaction authTran = transactionDAO.find(t.getIdentityUuid(), t.getRrn(), RequestType.PREAUTH);
            if (authTran == null) {
                this.buildErrorResponse(t, "NO_PRIOR_TRANSACTION", "NO_PRIOR_TRANSACTION_FOUND_FOR_FINALAUTH");
                return t;
            }
            String responseStr = "";
            NBSClient clientObj = new NBSClient();
            responseStr = clientObj.generateResponse("APPROVED");
            t.setResponseType(responseStr);
            if (t.getResponseType().equalsIgnoreCase(ResponseType.APPROVED)) {
                t.setReasonCode("100");
                t.setDescriptionField(ResponseType.APPROVED);
            }
            LOG.info("WEXProcessor.finalAuthProcess mothod ended");
            return t;
        } catch (Exception e) {
            throw e;
        }
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

    private BigInteger createDateFormat(String df) {

        char[] dfc = df.toCharArray();
        df = "dfc[6]" + "dfc[7]" + "dfc[8]" + "dfc[9]";// need to add + daylight_savings_time_at_site.ONE;
        BigInteger rs = new BigInteger(df);
        return rs;
        //ex  17 05 31 13 31 33
    }

}
