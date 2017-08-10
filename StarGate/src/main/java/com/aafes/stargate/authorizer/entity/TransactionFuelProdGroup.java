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
public class TransactionFuelProdGroup {
    private BigDecimal fuelPricePerUnit;
    private BigDecimal fuelQuantity;
    private BigInteger fuelProductCode;
    private BigDecimal fuelDollarAmount;

    public BigDecimal getFuelPricePerUnit() {
        return fuelPricePerUnit;
    }

    public void setFuelPricePerUnit(BigDecimal fuelPricePerUnit) {
        this.fuelPricePerUnit = fuelPricePerUnit;
    }

    public BigDecimal getFuelQuantity() {
        return fuelQuantity;
    }

    public void setFuelQuantity(BigDecimal fuelQuantity) {
        this.fuelQuantity = fuelQuantity;
    }

    public BigInteger getFuelProductCode() {
        return fuelProductCode;
    }

    public void setFuelProductCode(BigInteger fuelProductCode) {
        this.fuelProductCode = fuelProductCode;
    }

    public BigDecimal getFuelDollarAmount() {
        return fuelDollarAmount;
    }

    public void setFuelDollarAmount(BigDecimal fuelDollarAmount) {
        this.fuelDollarAmount = fuelDollarAmount;
    }
}