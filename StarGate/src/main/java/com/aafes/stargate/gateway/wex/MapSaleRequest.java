/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.nbslogonrequestschema.NbsLogonRequest;
import com.aafes.nbslogonrequestschema.NbsLogonRequest.HeaderRecord;
import com.aafes.stargate.authorizer.entity.Transaction;
import javax.ejb.Stateless;

/**
 *
 * @author singha
 */
@Stateless
public class MapSaleRequest {

    public Transaction saleRequestMap(Transaction transaction, NbsLogonRequest logonRequest) {
        Transaction t = new Transaction();
        HeaderRecord headerRecord = new HeaderRecord();
        headerRecord = logonRequest.getHeaderRecord();
        if (null != headerRecord) {

//            if (null != headerRecord.getCATFlag()) {
//                transaction.setCatFlag(headerRecord.getCATFlag());
//            }
//            if (null != headerRecord.getCATFlag()) {
//                transaction.setCardSequenceNumber(headerRecord.getCardSpecificData());
//            }
//            if (null != headerRecord.getCATFlag()) {
//                transaction.setCardSeqNumber(headerRecord.getCardType());
//            }
//            if (null != headerRecord.getCATFlag()) {
//                transaction.setKsn(headerRecord.getKey());
//            }
//
//            if (null != headerRecord.getCATFlag()) {
//                transaction.setPumpNmbr(headerRecord.getPumpNo());
//            }
//            if (null != headerRecord.getCATFlag()) {
//                transaction.setTrack1(headerRecord.getTrack());
//            }
//            if (null != headerRecord.getCATFlag()) {
//                transaction.setTransactiontype(headerRecord.getTransType());
//            }

        }

        return t;
    }

}
