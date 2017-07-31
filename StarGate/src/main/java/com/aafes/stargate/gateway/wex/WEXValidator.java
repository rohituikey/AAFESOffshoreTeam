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
import org.slf4j.LoggerFactory;

/**
 *
 * @author singha
 */
@Stateless
public class WEXValidator {

    @EJB
    private Configurator configurator;

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(WEXStrategy.class.getSimpleName());

    boolean validateSale(Transaction t) {
        if (Integer.parseInt(t.getProdDetailCount()) > 5 && Integer.parseInt(t.getFuelProdCode()) > 2) {
            this.buildErrorResponse(t, "MORE_THAN_TWO_FUEL_PRODUCT", "MORE_THAN_TWO_FUEL_PRODUCT");
            return false;
        }
        return  true;
    }

    private void buildErrorResponse(Transaction t, String reasonCode, String description) {
        t.setReasonCode(configurator.get(reasonCode));
        t.setResponseType(ResponseType.DECLINED);
        t.setDescriptionField(description);
        LOG.error("Exception/Error occured. reasonCode:" + reasonCode + " .description" + description);
    }

}
