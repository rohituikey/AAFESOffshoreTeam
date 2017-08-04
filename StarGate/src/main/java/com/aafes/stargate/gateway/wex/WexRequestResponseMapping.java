/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.nbslogonrequestschema.NbsLogonRequest;
import com.aafes.nbslogonrequestschema.NbsLogonRequest.HeaderRecord;
import com.aafes.nbslogonrequestschema.NbsLogonRequest.HeaderRecord.CardSpecificData.WexProductDetails;
import com.aafes.nbslogonrequestschema.NbsLogonRequest.HeaderRecord.CardSpecificData.WexProductDetails.ProdDetailCount;
import com.aafes.nbslogonrequestschema.NbsLogonRequest.HeaderRecord.CardSpecificData.WexPromptDetails;
import com.aafes.nbslogonrequestschema.NbsLogonRequest.HeaderRecord.CardSpecificData.WexPromptDetails.PromptDetailCount;
import com.aafes.nbsresponse.NBSResponse;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.util.RequestType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author alugumetlas
 */
@Stateless
public class WexRequestResponseMapping {

    //@EJB
    Transaction t = new Transaction();
    @Inject
    String daylight_savings_time_at_site_one;

    HeaderRecord headerRecord;

    public NbsLogonRequest RequestMap(Transaction transaction) {

        NbsLogonRequest logonRequest = new NbsLogonRequest();
        if (null == headerRecord) {
            headerRecord = new HeaderRecord();
        }
        if (null != transaction.getCatFlag()) {
            headerRecord.setCATFlag(BigInteger.valueOf(Long.parseLong(transaction.getCatFlag())));
        }
        
        if (null != transaction.getMedia()) {
            headerRecord.setCardType("WI");
        }
        if (null != transaction.getTransactionId()) {
            String key = transaction.getTransactionId().substring(0, 4);
            headerRecord.setKey(BigInteger.valueOf(Long.parseLong(key)));
        }

        if (null != headerRecord.getCATFlag()) {
            transaction.setPumpNmbr(headerRecord.getPumpNo());
        }
        // this will map only for preeauth and final auth these fields are requstType specific
        if (t.getRequestType().equalsIgnoreCase(RequestType.PREAUTH)
                || t.getRequestType().equalsIgnoreCase(RequestType.PREAUTH)) {

            if (null != transaction.getTrack2()) {
                headerRecord.setTrack(BigInteger.valueOf(2));
            }
        }
        if (null != transaction.getRequestType()) {
            if (transaction.getRequestType().equalsIgnoreCase(RequestType.PREAUTH)) {
                headerRecord.setTransType(BigInteger.valueOf(Long.parseLong("08")));
            }
            if (transaction.getRequestType().equalsIgnoreCase(RequestType.FINAL_AUTH)
                    || transaction.getRequestType().equalsIgnoreCase(RequestType.SALE)) {
                headerRecord.setTransType(BigInteger.valueOf(Long.parseLong("10")));
            }
            if (transaction.getRequestType().equalsIgnoreCase(RequestType.REFUND)) {
                headerRecord.setTransType(BigInteger.valueOf(Long.parseLong("30")));
            }
        }
        HeaderRecord.CardSpecificData  cardSpecificData = new HeaderRecord.CardSpecificData();
        cardSpecificData.setAcctInfo(t.getTrack2());
        cardSpecificData.setAmount(BigDecimal.valueOf(t.getAmount()));
        
        WexPromptDetails wexPromptDetails = new WexPromptDetails();
        PromptDetailCount promptDetailCount = new PromptDetailCount();
//        promptDetailCount.setPromptTypeOrPromptValue();//(2 fields## set prompttype 3 and prompt value t.getDriverID)&(set prompttype 4 and prompt value t.getOdeometer)
        wexPromptDetails.setPromptDetailCount(promptDetailCount);
        
        WexProductDetails wexProductDetails = new WexProductDetails();
        ProdDetailCount prodDetailCount = new ProdDetailCount();
//        prodDetailCount.setPriceOrQuantityOrProductCode();//(4 fields## set quantity for nonfuel 0.00 & fuel 0.00000 and codes and price-->t.getPricePerunit amount--> t.getFuelDolleramount)
        wexProductDetails.setProdDetailCount(prodDetailCount);
        cardSpecificData.setWexProductDetails(wexProductDetails);
        if(RequestType.SALE.equalsIgnoreCase(t.getRequestType()) || RequestType.REFUND.equalsIgnoreCase(t.getRequestType())){
            cardSpecificData.setRecieptNumber(BigInteger.valueOf(Long.valueOf(t.getTransactionId()+t.getTermId())));
        }
        
        headerRecord.setCardSpecificData(cardSpecificData);
        headerRecord.setServiceType("s");
        headerRecord.setA("A");
        logonRequest.setAppName("AAFES");
        logonRequest.setAppVersion(BigInteger.valueOf(Long.parseLong("0001")));
        logonRequest.setTermId(transaction.getTermId());
        logonRequest.setTimeZone(BigInteger.valueOf(Long.parseLong(createDateFormat())));
        logonRequest.setHeaderRecord(headerRecord);
        return logonRequest;
    }

    public Transaction ResponseMap(NBSResponse nbsResponse) {
        nbsResponse.getKey();
        t.setReasonCode(nbsResponse.getAuthCode().toString());
        
        String rs = nbsResponse.getLocalDateTime().toString();
        t.setLocalDateTime(this.CreateDF_forTransaction(rs));
        
        t.setResponseType(nbsResponse.getAuthResponse().getMessage());
        //t.setCardSeqNumber(nbsResponse.getAuthResponse().getCardNumber());
        t.setProdDetailCount(nbsResponse.getAuthResponse().getPromptTypeDetails().getProductAuthCount().toString());
        t.setAuthNumber(nbsResponse.getAuthResponse().getPromptTypeDetails().getAuthRef());
       // nbsResponse.getAuthResponse().getPromptTypeDetails().getMaxAmount();
        nbsResponse.getAuthResponse().getPromptTypeDetails().getProductAuthCount();
//        t.setPnbsResponse.getAuthResponse().getPromptTypeDetails().getPromptType();
        nbsResponse.getAuthResponse().getProductDetails().getMaxAmount();
        //t.setProdDetailCount(nbsResponse.getAuthResponse().getProductDetails().getMaxQuantity().toString());
        t.setProductCode(nbsResponse.getAuthResponse().getProductDetails().getProductCode().toString());
        return t;
    }

    private String createDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date();
        String ts = dateFormat.format(date);
        //2017-08-03 09:31:54.316
        ts = ts.substring(11, 13) + ts.substring(14, 16) + "0";
        return ts;
    }
    private String CreateDF_forTransaction(String df)
    {
        //yyyy-MM-dd HH:mm:ss.SSS
        //2017-08-03 09:31:54.316
        
       // WYYMMDDhhmmss
       // 3170621071655
      df = "20"+df.substring(1, 3)+"-"+df.substring(3, 5)+"-"
              +df.substring(5, 7)+" "+df.substring(7, 9)+":"+df.substring(9, 11)+":"+df.substring(11, 13)+".000";
        return df;
    }

}
