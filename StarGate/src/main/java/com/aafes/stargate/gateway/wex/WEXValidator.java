/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.AuthorizerException;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.util.InputType;
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

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(WEXValidator.class.getSimpleName());
    private String sMethodName = "";
    private final String CLASS_NAME = WEXValidator.this.getClass().getSimpleName();
    
    public boolean validateForPreAuthAndFinalAuth(Transaction t)
    {
        LOG.info("Validating fields in WEXValidator for Pre_Auth & Final_Auth");
        if (!t.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
            this.buildErrorResponse(t, "INVALID_INPUT_TYPE", "INVALID_INPUT_TYPE");
            return false;
        } else if ((t.getProdDetailCount() == null || t.getProdDetailCount().trim().isEmpty())) {
            this.buildErrorResponse(t, "PRODUCT_DETAIL_COUNT_NOT_BE_NULL", "PRODUCT_DETAIL_COUNT_NOT_BE_NULL");
            return false;
        } else if ((t.getProdDetailCount() != null || t.getProdDetailCount().trim().isEmpty())
                && (Integer.parseInt(t.getProdDetailCount()) > 5)) {
            this.buildErrorResponse(t, "PRODUCT_DETAIL_COUNT_EXCEEDED", "MORE_THAN_FIVE_PRODUCTS");
            return false;
        } else if (t.getFuelProdCode() == null || t.getFuelProdCode().trim().isEmpty()) {
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

    public boolean validateRefundRequest(Transaction t) {
        sMethodName = "validateRefundRequest";
        LOG.info("Method " + sMethodName + " started." + "in  Class Name " + CLASS_NAME);
        if(t.getFuelProdCode() != null || 
                Long.valueOf(String.valueOf(t.getQtyPumped())) != null ||
                Long.valueOf(String.valueOf(t.getPricePerUnit())) != null ||
                Long.valueOf(String.valueOf(t.getFuelPrice())) != null || 
                Long.valueOf(String.valueOf(t.getFuelDollerAmount())) != null){
            //this.buildErrorResponse(t, "REFUND_REQUEST_CONTAINS_FUEL_CODES", "REFUND_REQUEST_CONTAINS_FUEL_CODES");
            throw new AuthorizerException("REFUND_REQUEST_CONTAINS_FUEL_CODES");
            //return false;
        }else{
            LOG.info("Method " + sMethodName + " ended." + "in  Class Name " + CLASS_NAME);
            return  true;
        }
    }
     
    private void buildErrorResponse(Transaction t, String reasonCode, String description) {
        t.setReasonCode(configurator.get(reasonCode));
        t.setResponseType(ResponseType.DECLINED);
        t.setDescriptionField(description);
        LOG.error("Exception/Error occured. reasonCode:" + reasonCode + " .description" + description);
    }
}