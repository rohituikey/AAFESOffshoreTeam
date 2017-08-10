/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.authorizer.entity;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author burangir
 */
public class TransactionNonFuelProductGroup {
    private BigDecimal nonFuelPricePerUnit;
    private BigDecimal nonFuelQuantity;
    private BigInteger nonFuelProductCode;
    private BigDecimal nonFuelAmount;

    public BigDecimal getNonFuelPricePerUnit() {
        return nonFuelPricePerUnit;
    }

    public void setNonFuelPricePerUnit(BigDecimal nonFuelPricePerUnit) {
        this.nonFuelPricePerUnit = nonFuelPricePerUnit;
    }

    public BigDecimal getNonFuelQuantity() {
        return nonFuelQuantity;
    }

    public void setNonFuelQuantity(BigDecimal nonFuelQuantity) {
        this.nonFuelQuantity = nonFuelQuantity;
    }

    public BigInteger getNonFuelProductCode() {
        return nonFuelProductCode;
    }

    public void setNonFuelProductCode(BigInteger nonFuelProductCode) {
        this.nonFuelProductCode = nonFuelProductCode;
    }

    public BigDecimal getNonFuelAmount() {
        return nonFuelAmount;
    }

    public void setNonFuelAmount(BigDecimal nonFuelAmount) {
        this.nonFuelAmount = nonFuelAmount;
    }
}