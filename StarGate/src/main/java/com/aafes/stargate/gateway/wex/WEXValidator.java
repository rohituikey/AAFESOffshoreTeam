/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.util.InputType;
import com.aafes.stargate.util.RequestType;
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
    public boolean validateForPreAuthAndFinalAuth(Transaction t)
    {
          LOG.info("Validating fields in WEXValidator for Pre_Auth & Final_Auth");
            if (!t.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
                this.buildErrorResponse(t, "INVALID_INPUT_TYPE", "INVALID_INPUT_TYPE");
                return false;
            }
            if ((!t.getProdDetailCount().trim().isEmpty() || t.getRequestType() != null)
                    && (Integer.parseInt(t.getProdDetailCount()) > 5)) {
                this.buildErrorResponse(t, "PRODUCT_DETAIL_COUNT_EXCEEDED", "MORE_THAN_FIVE_PRODUCTS");
                return false;
            } else if (t.getFuelProdCode().trim().isEmpty() || t.getFuelProdCode() == null) {
                this.buildErrorResponse(t, "ONLY_NONFUEL_PRODUCTS_ARE_NOTALLOWED", "ONLY_NONFUEL_PRODUCTS_ARE_NOTALLOWED");
                return false;
            }
//        if (t.getSettleIndicator() == null || !t.getSettleIndicator().equalsIgnoreCase(SettleConstant.TRUE)) {
//            this.buildErrorResponse(t, "INVALID_SETTLE_INDICATOR", "INVALID_SETTLE_INDICATOR");
//            return false;
//        }
            return true;
    }

     public boolean validateSale(Transaction t) {
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
