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
    @EJB
    private RedemptionProcessor redemptionProcessor;

    
    @EJB
    private MerchandiseReturnMessageProcessor merchandiseReturnMessageProcessor;
    
    @EJB
    private SVSReversalProcessor sVSReversalProcessor;

    @EJB
    private SVSIssueGiftCardProcessor issueGiftCard;

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
                    processor = sVSReversalProcessor;
                    break;
                case RequestType.REFUND:
                    processor = merchandiseReturnMessageProcessor;
                    break;
                case RequestType.ISSUE:
                    processor = issueProcessor;
                    break;
                    //added below two cases
                case RequestType.NETWORK:
                    processor = networkMessageProcessor;
                    break;
                case RequestType.REDEMPTION:
                    processor = redemptionProcessor;
                    break;
                case RequestType.ISSUEGIFTCARD:
                    processor = issueGiftCard;
                    break;

                default:
                    log.info("No Matching request type found.");

            }
        }

        return processor;
    }

    public BalanceInquiryProcessor getBalanceInquiryProcessor() {
        return balanceInquiryProcessor;
    }

    public void setBalanceInquiryProcessor(BalanceInquiryProcessor balanceInquiryProcessor) {
        this.balanceInquiryProcessor = balanceInquiryProcessor;
    }

    public PreAuthorizationProcessor getPreAuthorizationProcessor() {
        return preAuthorizationProcessor;
    }

    public void setPreAuthorizationProcessor(PreAuthorizationProcessor preAuthorizationProcessor) {
        this.preAuthorizationProcessor = preAuthorizationProcessor;
    }

    public SVSFinalAuthProcessor getsVSFinalAuthProcessor() {
        return sVSFinalAuthProcessor;
    }

    public void setsVSFinalAuthProcessor(SVSFinalAuthProcessor sVSFinalAuthProcessor) {
        this.sVSFinalAuthProcessor = sVSFinalAuthProcessor;
    }

    public SVSIssueProcessor getIssueProcessor() {
        return issueProcessor;
    }

    public void setIssueProcessor(SVSIssueProcessor issueProcessor) {
        this.issueProcessor = issueProcessor;
    }

    public NetworkMessageProcessor getNetworkMessageProcessor() {
        return networkMessageProcessor;
    }

    public void setNetworkMessageProcessor(NetworkMessageProcessor networkMessageProcessor) {
        this.networkMessageProcessor = networkMessageProcessor;
    }

    public MerchandiseReturnMessageProcessor getMerchandiseReturnMessageProcessor() {
        return merchandiseReturnMessageProcessor;
    }

    public void setMerchandiseReturnMessageProcessor(MerchandiseReturnMessageProcessor merchandiseReturnMessageProcessor) {
        this.merchandiseReturnMessageProcessor = merchandiseReturnMessageProcessor;
    }

    public SVSReversalProcessor getsVSReversalProcessor() {
        return sVSReversalProcessor;
    }

    public void setsVSReversalProcessor(SVSReversalProcessor sVSReversalProcessor) {
        this.sVSReversalProcessor = sVSReversalProcessor;
    }
    
    public RedemptionProcessor getRedemptionProcessor() {
        return redemptionProcessor;
    }

    public void setRedemptionProcessor(RedemptionProcessor redemptionProcessor) {
        this.redemptionProcessor = redemptionProcessor;
    }

    public SVSIssueGiftCardProcessor getIssueGiftCard() {
        return issueGiftCard;
    }

    public void setIssueGiftCard(SVSIssueGiftCardProcessor issueGiftCard) {
        this.issueGiftCard = issueGiftCard;
    }
}
