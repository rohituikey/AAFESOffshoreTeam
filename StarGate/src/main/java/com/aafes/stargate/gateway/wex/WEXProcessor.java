/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.nbslogonrequest.NbsLogonRequest;
import com.aafes.nbsresponse.NBSResponse;
import com.aafes.nbsresponseacknowledgmentschema.ResponseAcknowlegment;
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
    private NBSRequestGenerator nbsRequestGeneratorObj;

    private NbsLogonRequest nbsLogOnRequest;

    public Transaction preAuthProcess(Transaction t) {
        LOG.info("WEXProcessor.preAuthProcess mothod started");

        try {
            nbsLogOnRequest = new NbsLogonRequest();

            String requestStr = nbsRequestGeneratorObj.generateLogOnPacketRequest(t);
            NBSClient clientObj = new NBSClient();
            String responseStr = clientObj.generateResponse(requestStr);
            String[] result = nbsRequestGeneratorObj.seperateResponse(responseStr);
            t = nbsRequestGeneratorObj.unmarshalAcknowledgment(result[0]);
            if (t.getResponseType().equals(ResponseType.APPROVED)) {
                LOG.info("LOGON successfull");
            } else {
                LOG.info("LOGON failed");
            }
            t = nbsRequestGeneratorObj.unmarshalNbsResponse(result[1]);
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
            if (authTran.getAuthNumber() != null) {
                t.setAuthNumber(authTran.getAuthNumber());
            }
            String requestStr = nbsRequestGeneratorObj.generateLogOnPacketRequest(t);
            NBSClient clientObj = new NBSClient();
            String responseStr = clientObj.generateResponse(requestStr);
            String[] result = nbsRequestGeneratorObj.seperateResponse(responseStr);
            t = nbsRequestGeneratorObj.unmarshalAcknowledgment(result[0]);
            if (t.getResponseType().equals(ResponseType.APPROVED)) {
                LOG.info("LOGON successfull");
            } else {
                LOG.info("LOGON failed");
            }
            t = nbsRequestGeneratorObj.unmarshalNbsResponse(result[1]);
            LOG.info("WEXProcessor.finalAuthProcess mothod ended");
            return t;
        } catch (Exception e) {
            throw e;
        }
    }

    public Transaction processSaleRequest(Transaction t) {

        LOG.info("WEXProcessor.ProcessSaleRequest mothod started");

        try {
            String responseStr = "";
            NBSClient clientObj = new NBSClient();
            responseStr = clientObj.generateResponse("APPROVED");
            t.setResponseType(responseStr.trim());
            if (t.getResponseType().equalsIgnoreCase(ResponseType.APPROVED)) {
                t.setReasonCode(configurator.get("SUCCESS"));
                t.setDescriptionField(ResponseType.APPROVED);
            } else {
                t.setDescriptionField(ResponseType.DECLINED);
            }
            t.setResponseType(responseStr);
            LOG.info("WEXProcessor.ProcessSaleRequest mothod ended");
            return t;
        } catch (Exception e) {
            throw e;
        }
    }

    public Transaction processRefundRequest(Transaction t) {
        sMethodName = "processRefundRequest";
        LOG.info("Method " + sMethodName + " started." + "in  Class Name " + CLASS_NAME);
        String requestStr = "", responseStr = "", logOffRequest = "";
        String[] seperatedResponseArr;
        ResponseAcknowlegment responseAcknowlegmentObj1;
        NBSResponse nBSResponse;

        nbsRequestGeneratorObj = new NBSRequestGenerator();
//        wexRequestResponseMappingObj = new WexRequestResponseMapping();

//        requestStr = nbsRequestGeneratorObj.generateLogOnPacketRequest(wexRequestResponseMappingObj.RequestMap(t));

        NBSClient clientObj = new NBSClient();
        responseStr = clientObj.generateResponse(requestStr);
        if (responseStr != null) {
            seperatedResponseArr = nbsRequestGeneratorObj.seperateResponse(responseStr);
//            if (seperatedResponseArr != null && seperatedResponseArr.length > 0) {
//                responseAcknowlegmentObj1 = nbsRequestGeneratorObj.unmarshalAcknowledgment(seperatedResponseArr[0]);
//                nBSResponse = nbsRequestGeneratorObj.unmarshalNbsResponse(seperatedResponseArr[1]);
//
//                if (nBSResponse != null) {
//                    t.setResponseType(nBSResponse.getAuthResponse().getMessage());
//                    t.setReasonCode(nBSResponse.getAuthCode().toString());
//                }
            //}

            logOffRequest = nbsRequestGeneratorObj.logOffRequest();

            //clientObj.generateResponse(seperatedResponseArr[0]+logOffRequest);
        }
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
