/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.Gateway;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.ResponseType;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author singha
 */
@Stateless
public class WexGateway extends Gateway {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(WexGateway.class.getSimpleName());

    private Transaction t;

    @EJB
    private WEXProcessor wEXProcessor;

    @Override
    public Transaction processMessage(Transaction transaction) {

        t = transaction;
        String requestType = t.getRequestType();

        LOG.info("inside procesMessage() method of wexGateway class");

        try {
            if (null != wEXProcessor) {
                switch (requestType) {
                    case RequestType.PREAUTH:
                        t = wEXProcessor.preAuthProcess(t);
                        break;
                    case RequestType.FINAL_AUTH:
                        t = wEXProcessor.finalAuthProcess(t);
                        break;
                    case RequestType.SALE:
                        t = wEXProcessor.processSaleRequest(t);
                        break;
                    case RequestType.REFUND:
                        t = wEXProcessor.processRefundRequest(t);
                        break;    
                }
            } else {
                t.setResponseType(ResponseType.DECLINED);
                t.setDescriptionField("INTERNAL SERVER ERROR");
                return t;
            }

        } catch (Exception e) {
        }

        return t;
    }

    /**
     * @param wEXProcessor the wEXProcessor to set
     */
    public void setwEXProcessor(WEXProcessor wEXProcessor) {
        this.wEXProcessor = wEXProcessor;
    }
}