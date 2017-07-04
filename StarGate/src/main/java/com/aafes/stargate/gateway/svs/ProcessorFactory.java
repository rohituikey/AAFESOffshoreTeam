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
    private MerchandiseReturnMessageProcessor merchandiseReturnMessageProcessor;
    @EJB
    private NetworkMessageProcessor networkMessageProcessor;
    @EJB
    private RedemptionProcessor redemptionProcessor;

    public Processor pickProcessor(Transaction t) {

        Processor processor = null;
        String requestType = t.getRequestType();

        if (requestType != null) {

            switch (requestType) {

                case RequestType.INQUIRY:
                    log.info("processing balanceInquiryProcessor");
                    processor = balanceInquiryProcessor;
                    break;
                case RequestType.PREAUTH:
                    log.info("processing preAuthorizationProcessor");
                    processor = preAuthorizationProcessor;
                    break;
                case RequestType.FINAL_AUTH:
                    log.info("processing sVSFinalAuthProcessor");
                    processor = sVSFinalAuthProcessor;
                    break;
                case RequestType.REVERSAL:
                    // Future
                    break;
                case RequestType.REFUND:
                    log.info("processing merchandiseReturnMessageProcessor");
                    processor = merchandiseReturnMessageProcessor;
                    break;
                case RequestType.ISSUE:
                    log.info("processing issueProcessor");
                    processor = issueProcessor;
                    break;
                //added
                case RequestType.NETWORK:
                    log.info("processing networkMessageProcessor");
                    processor = networkMessageProcessor;
                    break;
                case RequestType.REDEMPTION:
                    log.info("processing redemptionProcessor");
                    processor = redemptionProcessor;
                    break;

                default:
                    log.info("No Matching request type found.");

            }
        }log.debug("rrn number is"+t.getRrn());

        return processor;
    }

    /**
     * @param balanceInquiryProcessor the balanceInquiryProcessor to set
     */
    public void setBalanceInquiryProcessor(BalanceInquiryProcessor balanceInquiryProcessor) {
        this.balanceInquiryProcessor = balanceInquiryProcessor;
    }

    /**
     * @param preAuthorizationProcessor the preAuthorizationProcessor to set
     */
    public void setPreAuthorizationProcessor(PreAuthorizationProcessor preAuthorizationProcessor) {
        this.preAuthorizationProcessor = preAuthorizationProcessor;
    }

    /**
     * @param sVSFinalAuthProcessor the sVSFinalAuthProcessor to set
     */
    public void setsVSFinalAuthProcessor(SVSFinalAuthProcessor sVSFinalAuthProcessor) {
        this.sVSFinalAuthProcessor = sVSFinalAuthProcessor;
    }

    /**
     * @param issueProcessor the issueProcessor to set
     */
    public void setIssueProcessor(SVSIssueProcessor issueProcessor) {
        this.issueProcessor = issueProcessor;
    }

    public void setNetworkMessageProcessor(NetworkMessageProcessor networkMessageProcessor) {
        this.networkMessageProcessor = networkMessageProcessor;
    }

    public void setRedemptionProcessor(RedemptionProcessor redemptionProcessor) {
        this.redemptionProcessor = redemptionProcessor;
    }

//    public void setSplitShipmentProcessor(SplitShipmentProcessor splitShipmentProcessor) {
//        this.splitShipmentProcessor = splitShipmentProcessor;
//    }
}
