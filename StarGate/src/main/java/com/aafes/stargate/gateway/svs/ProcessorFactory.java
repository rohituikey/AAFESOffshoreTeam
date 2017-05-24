/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.svs;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.util.RequestType;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ganjis
 */
@Stateless
public class ProcessorFactory {

    private static final org.slf4j.Logger log
            = LoggerFactory.getLogger(ProcessorFactory.class.getSimpleName());

    @EJB
    private BalanceInquiryProcessor balanceInquiryProcessor;
    @EJB
    private PreAuthorizationProcessor preAuthorizationProcessor;
    @EJB
    private SVSFinalAuthProcessor sVSFinalAuthProcessor;
    @EJB
    private SVSIssueProcessor issueProcessor;
    @EJB
    private NetworkMessageProcessor networkMessageProcessor;

    public Processor pickProcessor(Transaction t) {

        Processor processor = null;
        String requestType = t.getRequestType();

        if (requestType != null) {

            switch (requestType) {

                case RequestType.INQUIRY:
                    processor = balanceInquiryProcessor;
                    break;
                case RequestType.PREAUTH:
                    processor = preAuthorizationProcessor;
                    break;
                case RequestType.FINAL_AUTH:
                    processor = sVSFinalAuthProcessor;
                    break;
                case RequestType.REVERSAL:
                    // Future
                    break;
                case RequestType.ISSUE:
                    processor = issueProcessor;
                    break;
                case RequestType.NETWORK:
                    processor = networkMessageProcessor;
                    break;

                default:
                    log.info("No Matching request type found.");

            }
        }

        return processor;
    }

}
