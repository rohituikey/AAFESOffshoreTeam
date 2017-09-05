/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.wex.simulator.NBSConnector;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.util.ResponseType;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.ws.WebServiceException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alugumetlas
 */
@Stateless
public class WEXProcessor {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WEXProcessor.class.getSimpleName());
    private String sMethodName = "";
    private final String CLASS_NAME = WEXProcessor.this.getClass().getSimpleName();

    @EJB
    private Configurator configurator;
    private NBSRequestGenerator nbsRequestGenerator;
    // ADDED TO HANDLE TIMEOUT SCENARIO
    String retryReason = "";
    int dupCheckCounter = 0;
    NBSConnector clientObj;
    byte[] iSOMsg;
    String[] responseArr, iSOMsgResponse;
    boolean isTimeoutRetry = false;
    boolean logEnabled;

    public Transaction processWexRequests(Transaction t) throws Exception {
        LOGGER.info("WEXProcessor.processWexRequests mothod started");
        try {
            GenerateLogWexDetails generateLogWexDetails = new GenerateLogWexDetails();
            dupCheckCounter = 0;
            isTimeoutRetry = false;
            if (nbsRequestGenerator == null) {
                nbsRequestGenerator = new NBSRequestGenerator();
            }
            iSOMsg = nbsRequestGenerator.generateLogOnPacketRequest(t, isTimeoutRetry);
            if (clientObj == null) {
                clientObj = new NBSConnector();
            }
            iSOMsgResponse = clientObj.sendRequest(iSOMsg);
            if (null != iSOMsgResponse || iSOMsgResponse.length < 2) {
                //  responseArr = nbsRequestGenerator.seperateResponse(iSOMsgResponse.getBytes());
                //if(responseArr != null || responseArr.length < 2){
                t = nbsRequestGenerator.unmarshalAcknowledgment(iSOMsgResponse[0]);
                if ((ResponseType.ACCEPTED).equalsIgnoreCase(t.getResponseType())) {
                    LOGGER.info("Acknoledgement recieved from :" + ResponseType.ACCEPTED);
                    //t.setResponseType(ResponseType.APPROVED);
                } else if ((ResponseType.REJECTED).equalsIgnoreCase(t.getResponseType())) {
                    LOGGER.info("Acknoledgement recieved from :" + ResponseType.REJECTED);
                    handleRequestTimeOutScenaio(t);
                } else if ((ResponseType.CANCELED).equalsIgnoreCase(t.getResponseType())) {
                    LOGGER.info("Acknoledgement recieved from :" + ResponseType.CANCELED);
                    buildErrorResponse(t, configurator.get("NBS_AUTH_UNAVAILABLE"), "NBS_AUTH_UNAVAILABLE");
                }
                t = nbsRequestGenerator.unmarshalNbsResponse(iSOMsgResponse[1]);
//                }else{
//                    LOGGER.info("Invalid response from NBS.");
//                    buildErrorResponse(t, configurator.get("INVALID_RESPONSE"), "INVALID_RESPONSE");
//                }
            } else {
                LOGGER.info("Invalid response from NBS.");
                buildErrorResponse(t, configurator.get("INVALID_RESPONSE"), "INVALID_RESPONSE");
            }
            if (logEnabled) {
                generateLogWexDetails.generateDetails(t.getRequestType(), iSOMsgResponse.toString(), iSOMsg.toString());
            }
            return t;
        } catch (SocketTimeoutException e) {
            retryReason = "Exception : " + e.getMessage();
            handleRequestTimeOutScenaio(t);
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {// || e instanceof ClientTransportException){
                retryReason = "Exception : " + e.getMessage();
                handleRequestTimeOutScenaio(t);
            } else {
                throw e;
            }
        }
        return t;
    }

    private void handleRequestTimeOutScenaio(Transaction t) {
        sMethodName = "handleRequestTimeOutScenaio";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        int wexRetryCount = 0, wexRetryWaitTime = 0;
        try {
            if (configurator.get("WEX_RETRY_COUNT") != null) {
                wexRetryCount = Integer.parseInt(configurator.get("WEX_RETRY_COUNT"));
            } else {
                LOGGER.error("Please add WEX_RETRY_COUNT in stargate.properties");
            }
            if (configurator.get("WEX_RETRY_WAIT_TIME") != null) {
                wexRetryWaitTime = Integer.parseInt(configurator.get("WEX_RETRY_WAIT_TIME"));
            } else {
                LOGGER.error("Please add WEX_RETRY_WAIT_TIME in stargate.properties");
            }
            ++dupCheckCounter;
            if (dupCheckCounter <= wexRetryCount) {
                TimeUnit.SECONDS.sleep(wexRetryWaitTime);
                LOGGER.info("Retrying to send request. Retry Reason " + retryReason + ". Retry Number : " + dupCheckCounter
                        + ". Method " + sMethodName + ". Class Name " + CLASS_NAME);
                isTimeoutRetry = true;
                if (nbsRequestGenerator == null) {
                    nbsRequestGenerator = new NBSRequestGenerator();
                }
                iSOMsg = nbsRequestGenerator.generateLogOnPacketRequest(t, isTimeoutRetry);
                if (clientObj == null) {
                    clientObj = new NBSConnector();
                }
                iSOMsgResponse = clientObj.sendRequest(iSOMsg);
                if (null != iSOMsgResponse || iSOMsgResponse.length < 2) {
                    //        responseArr = nbsRequestGenerator.seperateResponse(iSOMsgResponse.getBytes());
                    t = nbsRequestGenerator.unmarshalAcknowledgment(iSOMsgResponse[0]);
                    if ((ResponseType.ACCEPTED).equalsIgnoreCase(t.getResponseType())) {
                        LOGGER.info("Acknoledgement recieved from :"+ResponseType.ACCEPTED);
                        //t.setResponseType(ResponseType.APPROVED);
                    } else if ((ResponseType.REJECTED).equalsIgnoreCase(t.getResponseType())) {
                        LOGGER.info("Acknoledgement recieved from :"+ResponseType.REJECTED);
                        handleRequestTimeOutScenaio(t);
                    } else if ((ResponseType.CANCELED).equalsIgnoreCase(t.getResponseType())) {
                        LOGGER.info("Acknoledgement recieved from :"+ResponseType.CANCELED);
                        buildErrorResponse(t, configurator.get("NBS_AUTH_UNAVAILABLE"), "NBS_AUTH_UNAVAILABLE");
                    }
                    t = nbsRequestGenerator.unmarshalNbsResponse(iSOMsgResponse[1]);
                }
            } else if (dupCheckCounter > wexRetryCount) {
                LOGGER.info("Retry count exausted. Please continue with manual follow-up!! " + "Method " + sMethodName
                        + ". Class Name " + CLASS_NAME);
                dupCheckCounter = 0;
                buildErrorResponse(t, configurator.get("WEX_REQUEST_TIMEOUT"), "WEX_REQUEST_TIMEOUT");
                isTimeoutRetry = false;
            }
        } catch (Exception e) {
            LOGGER.error("Exception occured in " + sMethodName + ". Exception  : " + e.getMessage());
            if (e instanceof SocketTimeoutException || e instanceof WebServiceException) {// || e instanceof ClientTransportException){ || e instanceof WebServiceException){
                retryReason = "Exception : " + e.getMessage();
                handleRequestTimeOutScenaio(t);
                //dupCheckCounter++;
            } else {
                try {
                    throw e;
                } catch (Exception ex) {
                    Logger.getLogger(WEXProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
    }

//    public Transaction preAuthProcess(Transaction t) {
//        LOG.info("WEXProcessor.preAuthProcess mothod started");
//
//        try {
//            nBSFormatter = new NBSRequestGenerator();
//            String requestStr = nBSFormatter.generateLogOnPacketRequest(t);
//            NBSConnector clientObj = new NBSConnector();
//            String responseStr = clientObj.generateResponse(requestStr);
//            String[] result = nBSFormatter.seperateResponse(responseStr);
//            t = nBSFormatter.unmarshalAcknowledgment(result[0]);
//            if (t.getResponseType().equals(ResponseType.APPROVED)) {
//                LOG.info("LOGON successfull");
//            } else {
//                LOG.info("LOGON failed");
//            }
//            t = nBSFormatter.unmarshalNbsResponse(result[1]);
//            LOG.info("WEXProcessor.preAuthProcess mothod ended");
//            return t;
//        } catch (Exception e) {
//            throw e;
//        }
//    }
//    public Transaction finalAuthProcess(Transaction t) {
//        LOG.info("WEXProcessor.finalAuthProcess mothod started");
//        try {
//            Transaction authTran = transactionDAO.find(t.getIdentityUuid(), t.getRrn(), RequestType.PREAUTH);
//            if (authTran == null) {
//                this.buildErrorResponse(t, "NO_PRIOR_TRANSACTION", "NO_PRIOR_TRANSACTION_FOUND_FOR_FINALAUTH");
//                return t;
//            }
//            if (authTran.getAuthNumber() != null) {
//                t.setAuthNumber(authTran.getAuthNumber());
//            }
//            String requestStr = nBSFormatter.generateLogOnPacketRequest(t);
//            NBSConnector clientObj = new NBSConnector();
//            String responseStr = clientObj.generateResponse(requestStr);
//            String[] result = nBSFormatter.seperateResponse(responseStr);
//            t = nBSFormatter.unmarshalAcknowledgment(result[0]);
//            if (t.getResponseType().equals(ResponseType.APPROVED)) {
//                LOG.info("LOGON successfull");
//            } else {
//                LOG.info("LOGON failed");
//            }
//            t = nBSFormatter.unmarshalNbsResponse(result[1]);
//            LOG.info("WEXProcessor.finalAuthProcess mothod ended");
//            return t;
//        } catch (Exception e) {
//            throw e;
//        }
//    }
//    public Transaction processSaleRequest(Transaction t) {
//
//        LOG.info("WEXProcessor.ProcessSaleRequest mothod started");
//
//        try {
////            String responseStr = "";
////            NBSConnector clientObj = new NBSConnector();
////            responseStr = clientObj.generateResponse("APPROVED");
////            t.setResponseType(responseStr.trim());
////            if (t.getResponseType().equalsIgnoreCase(ResponseType.APPROVED)) {
////                t.setReasonCode(configurator.get("SUCCESS"));
////                t.setDescriptionField(ResponseType.APPROVED);
////            } else {
////                t.setDescriptionField(ResponseType.DECLINED);
////            }
////            t.setResponseType(responseStr);
////            LOG.info("WEXProcessor.ProcessSaleRequest mothod ended");
//            return t;
//        } catch (Exception e) {
//            throw e;
//        }
//    }
//    public Transaction processRefundRequest(Transaction t) {
//        sMethodName = "processRefundRequest";
//        LOG.info("Method " + sMethodName + " started." + "in  Class Name " + CLASS_NAME);
//        String requestStr = "", responseStr = "", logOffRequest = "";
//        String[] seperatedResponseArr;
//        ResponseAcknowlegment responseAcknowlegmentObj1;
//        NBSResponse nBSResponse;
//
//        nBSFormatter = new NBSRequestGenerator();
////        wexRequestResponseMappingObj = new WexRequestResponseMapping();
//
////        requestStr = nBSFormatter.generateLogOnPacketRequest(wexRequestResponseMappingObj.RequestMap(t));
//
//        NBSConnector clientObj = new NBSConnector();
//        responseStr = clientObj.generateResponse(requestStr);
//        if (responseStr != null) {
//            seperatedResponseArr = nBSFormatter.seperateResponse(responseStr);
////            if (seperatedResponseArr != null && seperatedResponseArr.length > 0) {
////                responseAcknowlegmentObj1 = nBSFormatter.unmarshalAcknowledgment(seperatedResponseArr[0]);
////                nBSResponse = nBSFormatter.unmarshalNbsResponse(seperatedResponseArr[1]);
////
////                if (nBSResponse != null) {
////                    t.setResponseType(nBSResponse.getAuthResponse().getMessage());
////                    t.setReasonCode(nBSResponse.getAuthCode().toString());
////                }
//            //}
//
//            logOffRequest = nBSFormatter.logOffRequest();
//
//            //clientObj.generateResponse(seperatedResponseArr[0]+logOffRequest);
//        }
//        LOG.info("Method " + sMethodName + " ended." + "in  Class Name " + CLASS_NAME);
//        return t;
//    }
//    private void buildErrorResponse(Transaction t, String reasonCode, String description) {
//        t.setReasonCode(configurator.get(reasonCode));
//        t.setResponseType(ResponseType.DECLINED);
//        t.setDescriptionField(description);
//        //LOG.error("Exception/Error occured. reasonCode:" + reasonCode + " .description" + description);
//    }
//
//    private BigInteger createDateFormat(String df) {
//
//        char[] dfc = df.toCharArray();
//        df = "dfc[6]" + "dfc[7]" + "dfc[8]" + "dfc[9]";// need to add + daylight_savings_time_at_site.ONE;
//        BigInteger rs = new BigInteger(df);
//        return rs;
//        //ex  17 05 31 13 31 33
//    }
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }

    public void setNbsRequestGenerator(NBSRequestGenerator nbsRequestGenerator) {
        this.nbsRequestGenerator = nbsRequestGenerator;
    }

    private void buildErrorResponse(Transaction t, String reasonCode, String description) {
        t.setReasonCode(reasonCode);
        t.setResponseType(ResponseType.DECLINED);
        t.setDescriptionField(description);
    }

    public void setClientObj(NBSConnector clientObj) {
        this.clientObj = clientObj;
    }
}
