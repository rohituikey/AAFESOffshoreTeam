/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.aafes.stargate.gateway.wex;

import com.aafes.stargate.authorizer.entity.Transaction;
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

    public boolean validateForPreAuthAndFinalAuth(Transaction t) {
        LOG.info("Validating fields in WEXValidator for Pre_Auth & Final_Auth");
        if (!t.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
            this.buildErrorResponse(t, "INVALID_INPUT_TYPE", "INVALID_INPUT_TYPE");
            return false;
        } else if (t.getFuelProductGroup() == null) {
            this.buildErrorResponse(t, "PRODUCT_DETAIL_COUNT_NOT_BE_NULL", "PRODUCT_DETAIL_COUNT_NOT_BE_NULL");
            return false;
        } else if ((t.getFuelProductGroup() != null) && (t.getFuelProductGroup().size()) > 1) {
            this.buildErrorResponse(t, "PRODUCT_DETAIL_COUNT_EXCEEDED", "MORE_THAN_FIVE_PRODUCTS");
            return false;
        } else if (null != t.getFuelProductGroup() && t.getFuelProductGroup().size() > 0) {
            for (String tmpObj : t.getFuelProductGroup()) {
                if (tmpObj == null) {
                    this.buildErrorResponse(t, "ONLY_NONFUEL_PRODUCTS_ARE_NOTALLOWED", "ONLY_NONFUEL_PRODUCTS_ARE_NOTALLOWED");
                    return false;
                }
            }
        }
        if ((t.getNonFuelProductGroup() != null) && (t.getNonFuelProductGroup().size() > 1)) {
                this.buildErrorResponse(t, "PRODUCT_DETAIL_COUNT_EXCEEDED", "MORE_THAN_FIVE_PRODUCTS");
                return false;
            }
        return true;
    }

    public boolean validateSale(Transaction t) {
        if (Integer.parseInt(t.getProdDetailCount()) > 5 && Integer.parseInt(t.getFuelProdCode()) > 2) {
            this.buildErrorResponse(t, "MORE_THAN_TWO_FUEL_PRODUCT", "MORE_THAN_TWO_FUEL_PRODUCT");
            return false;
        }
        return true;
    }

    public boolean validateRefundRequest(Transaction t) {
        sMethodName = "validateRefundRequest";
        LOG.info("Method " + sMethodName + " started." + "in  Class Name " + CLASS_NAME);
        boolean localBoolVar = true;

        if (t.getFuelProdCode() != null && t.getFuelProdCode().trim().length() > 0) {
            LOG.error("FuelProdCode( should not present in WEX refund request");
            localBoolVar = false;
        } else if (localBoolVar && Long.valueOf(String.valueOf(t.getQtyPumped())) != null && t.getQtyPumped() > 0) {
            LOG.error("QtyPumped should not present in WEX refund request");
            localBoolVar = false;
        } else if (localBoolVar && t.getPricePerUnit() != null && Long.valueOf(String.valueOf(t.getPricePerUnit())) != null) {
            LOG.error("PricePerUnit should not present in WEX refund request");
            localBoolVar = false;
        } else if (localBoolVar && Long.valueOf(String.valueOf(t.getFuelPrice())) != null && t.getFuelPrice() > 0) {
            LOG.error("FuelPrice should not present in WEX refund request");
            localBoolVar = false;
        } else if (localBoolVar && t.getFuelDollerAmount() != null && Long.valueOf(String.valueOf(t.getFuelDollerAmount())) != null) {
            LOG.error("FuelDollerAmount should not present in WEX refund request");
            localBoolVar = false;
        }

        if (!localBoolVar) {
            this.buildErrorResponse(t, "REFUND_REQUEST_CONTAINS_FUEL_CODES", "REFUND_REQUEST_CONTAINS_FUEL_CODES");
        } else {
            LOG.info("Method " + sMethodName + " ended." + "in  Class Name " + CLASS_NAME);
            localBoolVar = true;
        }
        return localBoolVar;
    }

    private void buildErrorResponse(Transaction t, String reasonCode, String description) {
        t.setReasonCode(configurator.get(reasonCode));
        t.setResponseType(ResponseType.DECLINED);
        t.setDescriptionField(description);
        LOG.error("Exception/Error occured. reasonCode:" + reasonCode + " .description" + description);
    }
}