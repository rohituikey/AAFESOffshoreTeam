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
    int maxFuelProdCount, maxNonFuelProdCount, maxlProdCountSale, maxFuelProdCountSale;

    public boolean validateForPreAuthAndFinalAuth(Transaction t) {
        LOG.info("Validating fields in WEXValidator for Pre_Auth & Final_Auth");
        
        if(configurator.get("TOTAL_FUEL_PRODCODE_ALLWOED") != null)
            maxFuelProdCount = Integer.parseInt(configurator.get("TOTAL_FUEL_PRODCODE_ALLWOED"));
        else LOG.error("Please add TOTAL_FUEL_PRODCODE_ALLWOED in stargate.properties");
        if(configurator.get("TOTAL_NONFUEL_PRODCODE_ALLWOED") != null)
            maxNonFuelProdCount = Integer.parseInt(configurator.get("TOTAL_NONFUEL_PRODCODE_ALLWOED"));
        else LOG.error("Please add TOTAL_NONFUEL_PRODCODE_ALLWOED in stargate.properties");
        
        if (!t.getInputType().equalsIgnoreCase(InputType.SWIPED)) {
            this.buildErrorResponse(t, "INVALID_INPUT_TYPE", "INVALID_INPUT_TYPE");
            return false;
        } else if (t.getFuelProductGroup() == null || t.getFuelProductGroup().isEmpty()) {
            this.buildErrorResponse(t, "PRODUCT_DETAIL_COUNT_NOT_BE_NULL", "PRODUCT_DETAIL_COUNT_NOT_BE_NULL");
            return false;
        } else if ((t.getFuelProductGroup() != null) && (t.getFuelProductGroup().size()) > maxFuelProdCount) {
            this.buildErrorResponse(t, "FUEL_PRODUCT_DETAIL_COUNT_EXCEEDED", "FUEL_PRODUCT_DETAIL_COUNT_EXCEEDED");
            return false;
        } else if ((t.getNonFuelProductGroup() != null) && (t.getNonFuelProductGroup().size() > maxNonFuelProdCount)) {
            this.buildErrorResponse(t, "NONFUEL_PRODUCT_DETAIL_COUNT_EXCEEDED", "NONFUEL_PRODUCT_DETAIL_COUNT_EXCEEDED");
            return false;
        }
        return true;
    }

    public boolean validateSale(Transaction t) {
        if(configurator.get("TOTAL_FUEL_PRODCODE_ALLWOED_SALE") != null)
            maxlProdCountSale = Integer.parseInt(configurator.get("TOTAL_FUEL_PRODCODE_ALLWOED_SALE"));
        else LOG.error("Please add TOTAL_FUEL_PRODCODE_ALLWOED_SALE in stargate.properties");
        if(configurator.get("TOTAL_NONFUEL_PRODCODE_ALLWOED_SALE") != null)
            maxFuelProdCountSale = Integer.parseInt(configurator.get("TOTAL_NONFUEL_PRODCODE_ALLWOED_SALE"));
        else LOG.error("Please add TOTAL_NONFUEL_PRODCODE_ALLWOED_SALE in stargate.properties");
        
        if (Integer.parseInt(t.getProdDetailCount()) > maxlProdCountSale) {
            this.buildErrorResponse(t, "TOTAL_PROD_DETAILS_COUNT_EXCEEDED", "TOTAL_PROD_DETAILS_COUNT_EXCEEDED");
            return false;
        }else if (Integer.parseInt(t.getFuelProdCode()) > maxFuelProdCountSale) {
            this.buildErrorResponse(t, "FUEL_PRODUCT_DETAIL_COUNT_EXCEEDED", "FUEL_PRODUCT_DETAIL_COUNT_EXCEEDED");
            return false;
        }
        return true;
    }

    public boolean validateRefundRequest(Transaction t) {
        sMethodName = "validateRefundRequest";
        LOG.info("Method " + sMethodName + " started." + "in  Class Name " + CLASS_NAME);
        boolean localBoolVar = true;

        if (t.getFuelProdCode() != null && t.getFuelProdCode().trim().length() > 0) {
            LOG.error("FuelProdCode should not present in WEX refund request");
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