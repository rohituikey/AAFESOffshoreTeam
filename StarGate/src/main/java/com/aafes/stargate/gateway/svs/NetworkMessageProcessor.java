/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.svs;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.gateway.svs.Processor;
import com.aafes.stargate.util.ResponseType;
import com.aafes.stargate.util.StarGateConstants;
import com.aafes.stargate.util.SvsUtil;
import com.svs.svsxml.beans.Merchant;
import com.svs.svsxml.beans.NetworkRequest;
import com.svs.svsxml.beans.NetworkResponse;
import com.svs.svsxml.service.SVSXMLWay;
import javax.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alugumetlas
 */
@Stateless
public class NetworkMessageProcessor extends Processor {
    
    private static final Logger LOGGER=  LoggerFactory.getLogger(NetworkMessageProcessor.class.getSimpleName());
    @Override
    public void processRequest(Transaction t) {
 

        SVSXMLWay sVSXMLWay = SvsUtil.setUserNamePassword();
        NetworkRequest networkRequest = new NetworkRequest();
        networkRequest.setDate(SvsUtil.formatLocalDateTime());
        
        
        Merchant merchant = new Merchant();
        merchant.setDivision(StarGateConstants.MERCHANT_DIVISION_NUMBER);
        merchant.setMerchantName(StarGateConstants.MERCHANT_NAME);
        merchant.setMerchantNumber(StarGateConstants.MERCHANT_NUMBER);
        merchant.setStoreNumber(StarGateConstants.STORE_NUMBER);
        networkRequest.setMerchant(merchant);
        
        networkRequest.setRoutingID(StarGateConstants.ROUTING_ID);
        networkRequest.setNetworkCode(StarGateConstants.NETWORK_CODE);
        
        
        LOGGER.info("REQUEST---->AuthorizationCode " + t.getAuthoriztionCode()+"return code :" +t.getReasonCode() );
        
        NetworkResponse networkResponse = sVSXMLWay.network(networkRequest);
        t.setAuthoriztionCode(networkResponse.getAuthorizationCode());
        LOGGER.info("return code : " + networkResponse.getReturnCode().getReturnCode());
        t.setReasonCode(networkResponse.getReturnCode().getReturnCode());
        t.setDescriptionField(networkResponse.getReturnCode().getReturnDescription());
        //t.setTransactionId(networkResponse.get);
        if(networkResponse.getReturnCode().getReturnCode().equals("01")){
            t.setResponseType(ResponseType.APPROVED);
        }
        else{
            t.setResponseType(ResponseType.DECLINED);
        }
    }
}
