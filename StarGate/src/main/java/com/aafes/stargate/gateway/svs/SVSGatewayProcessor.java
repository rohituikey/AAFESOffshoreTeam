/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.svs;

import com.aafes.stargate.authorizer.entity.GiftCard;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.dao.SVSDAO;
import com.aafes.stargate.util.ResponseType;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pkalpesh
 */
@Stateless
public class SVSGatewayProcessor {

    private static final org.slf4j.Logger log
            = LoggerFactory.getLogger(SVSGatewayProcessor.class.getSimpleName());

    @EJB
    private ProcessorFactory processorFactory;
    @EJB
    private SVSDAO svsdao;

    public Transaction execute(Transaction t) {
        log.info("SVSGatewayProcessor.excute method started");
        try {
            log.info(" Gift Card Request Type : " + t.getRequestType());
            Processor processor = processorFactory.pickProcessor(t);
            processor.processRequest(t);

        } catch (Exception e) {
            log.error("SVSGatewayProcessor#execute#Exception : " + e.toString());
            t.setDescriptionField("SVS_GATEWAY_ERROR");
            t.setResponseType(ResponseType.DECLINED);
            t.setReasonCode("200");

        }
         log.info("SVSGatewayProcessor.excute method ended");
        return t;
    }

    private void processBalanceInquiry(Transaction t) {
        log.info("processing BalanceInquiry.......");
        GiftCard giftCard = svsdao.find(t.getAccount(), t.getGcpin());

        if (giftCard == null) {
            this.handleError(t);
            return;
        }

        long gcAmount = 0;

        if (giftCard.getBalanceAmount() != null) {
            gcAmount = Long.parseLong(giftCard.getBalanceAmount());
        }

        if (t.getAmount() <= gcAmount) {
            t.setBalanceAmount(gcAmount);
            t.setResponseType(ResponseType.APPROVED);
            t.setReasonCode("100");
        } else {
            log.info("invalid giftcard in processing BalanceInquiry"+t.getRrn());
            t.setResponseType(ResponseType.DECLINED);
            t.setDescriptionField("INVALID_GIFTCARD");
            t.setReasonCode("201");
        }
        log.info(" BalanceInquiry request processed");
    }

    private void preAuth(Transaction t) {
        log.info("processing preAuth......."+t.getRrn());

        GiftCard giftCard = svsdao.find(t.getAccount(), t.getGcpin());

        if (giftCard == null) {
            this.handleError(t);
            return;
        }

        long gcAmount = 0;

        if (giftCard.getBalanceAmount() != null) {
            gcAmount = Long.parseLong(giftCard.getBalanceAmount());
        }

        if (t.getAmount() <= gcAmount) {
            t.setResponseType(ResponseType.APPROVED);
            t.setReasonCode("100");
            t.setAmtPreAuthorized(t.getAmount());
            t.setBalanceAmount(gcAmount - t.getAmount());
        } else {
            log.info("INSUFFICIENT_FUNDS in processing BalanceInquiry");
            t.setResponseType(ResponseType.DECLINED);
            t.setDescriptionField("INSUFFICIENT_FUNDS");
            t.setReasonCode("202");
        }
    }

    private void preAuthComplete(Transaction t) {
        log.info("processing preAuthComplete......."+t.getRrn());

        GiftCard giftCard = svsdao.find(t.getAccount(), t.getGcpin());

        if (giftCard == null) {
            this.handleError(t);
            return;
        }
        long gcAmount = 0;

        if (giftCard.getBalanceAmount() != null) {
            gcAmount = Long.parseLong(giftCard.getBalanceAmount());
        }

        gcAmount = gcAmount - t.getAmount();

        giftCard.setBalanceAmount(String.valueOf(gcAmount));

        svsdao.save(giftCard);

        t.setResponseType(ResponseType.APPROVED);
        t.setReasonCode("100");
        t.setAmtPreAuthorized(t.getAmount());
        t.setBalanceAmount(gcAmount);
    }

    private void reverseTran(Transaction t) {
        log.info("reverseTran.......");
        t.setResponseType(ResponseType.APPROVED);
        t.setReasonCode("100");
    }

    private void issueGiftCard(Transaction t) {

        log.info("processing issueGiftCard.......");
        GiftCard giftCard = new GiftCard();
        Date d = new Date();
        SimpleDateFormat df = new SimpleDateFormat("YYmmDDHHMM");
        String cardNumber = "600649157" + df.format(d);
        String gcPin = d.getHours() + "" + d.getDay();
        String amount = Long.toString(t.getAmount());

        giftCard.setCardNumber(cardNumber);
        giftCard.setPin(gcPin);
        giftCard.setBalanceAmount(amount);

        svsdao.save(giftCard);

        t.setResponseType(ResponseType.APPROVED);
        t.setReasonCode("100");
    }

    public void setSvsdao(SVSDAO svsdao) {
        this.svsdao = svsdao;
    }

    private void handleError(Transaction t) {
        t.setResponseType(ResponseType.DECLINED);
        t.setDescriptionField("INVALID_CARD");
        t.setReasonCode("201");

    }

    /**
     * @param processorFactory the processorFactory to set
     */
    public void setProcessorFactory(ProcessorFactory processorFactory) {
        this.processorFactory = processorFactory;
    }

}
