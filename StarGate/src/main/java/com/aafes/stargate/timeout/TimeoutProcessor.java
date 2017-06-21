/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.timeout;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.dao.TransactionDAO;
import com.aafes.stargate.util.MediaType;
import com.aafes.stargate.util.ResponseType;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author uikuyr
 */
@Stateless
public class TimeoutProcessor {

    @EJB
    private TransactionDAO transactionDAO;
    
    private Transaction t = new Transaction();

    public Transaction processResponse(Transaction t) {
        //visionGateway
        switch (t.getMedia()) {
            case MediaType.MIL_STAR:
                handleVisionGateway(t);
            case MediaType.VISA:
            case MediaType.MASTER:
            case MediaType.DISCOVER:
            case MediaType.AMEX:
                handleCompassGateway(t);
        }
        return t;
    }

    private void handleVisionGateway(Transaction t) {
        if ((null == t.getReasonCode() || t.getReasonCode().isEmpty() || t.getReasonCode().trim().equals("")) || (ResponseType.DECLINED.equalsIgnoreCase(t.getResponseType()) && (t.getReasonCode().equals("130") || t.getReasonCode().equals("99") || t.getReasonCode().equals("130")))) {
            t.setResponseType(ResponseType.TIMEOUT);
        }
    }

    private void handleCompassGateway(Transaction t) {
        if ((null == t.getReasonCode() || t.getReasonCode().isEmpty() || t.getReasonCode().trim().equals(""))) {
            t.setResponseType(ResponseType.TIMEOUT);
        }
        if ((ResponseType.DECLINED.equalsIgnoreCase(t.getResponseType())) && (t.getReasonCode().equals("000"))) {
            t.setResponseType(ResponseType.TIMEOUT);
            String counter = transactionDAO.getCountAttempt(t);
            int count = counter != null ? Integer.parseInt(counter) : 0;
            if (count == 0 || count < 1) {
                count++;
                t.setNumberOfAttempts(String.valueOf(count));
                transactionDAO.updateCountAttepmt(t);
            }
        }
    }

    public TransactionDAO getTransactionDAO() {
        return transactionDAO;
    }

    public void setTransactionDAO(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    public Transaction getT() {
        return t;
    }

    public void setT(Transaction t) {
        this.t = t;
    }

}
