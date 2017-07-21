/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.control;

import com.aafes.stargate.dao.TransactionDAO;
import com.aafes.stargate.authorizer.entity.Transaction;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author ganjis
 */
@Stateless
public class TranRepository {

    @EJB
    private TransactionDAO transactionDAO;
    

    public Transaction find(String identityuuid, String rrn, String requesttype) {
        Transaction transaction = getTransactionDAO().find(identityuuid, rrn, requesttype);;
        return transaction;
    }

    public void save(Transaction t) {

        getTransactionDAO().save(t);
    }
    
     public void saveAndUpdate(Transaction t,
             Transaction authTran) {
         getTransactionDAO().save(t); 
         getTransactionDAO().save(authTran);
    }

    /**
     * @return the transactionDAO
     */
    public TransactionDAO getTransactionDAO() {
        return transactionDAO;
    }

    /**
     * @param transactionDAO the transactionDAO to set
     */
    public void setTransactionDAO(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }
}
