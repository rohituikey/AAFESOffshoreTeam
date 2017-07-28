/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.util.ResponseType;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author alugumetlas
 */
@Stateless
public class WEXProcessor {
      @EJB
    private Configurator configurator;
   
     public Transaction preAuthProcess(Transaction t)
    {
        if(Integer.parseInt(t.getProdDetailCount()) > 5)
        {
            //if(t.getNonFuelProdCode.size() > 2)
            this.buildErrorResponse(t, "PRODUCT_DETAIL_COUNT_EXCEEDED", "SELECTED PRODUCT COUNT EXCEEDED");
            return t;
        }
        
//     if(t.getTrack2() !=null || !t.getTrack2().isEmpty())
//     {
//         String Track2 = t.getTrack2();
//     }
//     String TransCode = "08";
        return t;
    }
    public Transaction finalAuthProcess(Transaction t)
    {
        return t;
    }
    
    public Transaction processSaleRequest(Transaction t)
    {
        return  t;
    }
     private void buildErrorResponse(Transaction t, String reasonCode, String description) {
        t.setReasonCode(configurator.get(reasonCode));
        t.setResponseType(ResponseType.DECLINED);
        t.setDescriptionField(description);
        //LOG.error("Exception/Error occured. reasonCode:" + reasonCode + " .description" + description);
    }
    
}
