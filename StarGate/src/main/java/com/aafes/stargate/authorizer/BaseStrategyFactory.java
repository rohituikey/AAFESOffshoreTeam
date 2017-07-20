/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.authorizer;


import com.aafes.stargate.util.StrategyType;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author pkalpesh
 */
@Stateless
public class BaseStrategyFactory {
    
    @EJB
    private MPGStrategy mpgStrategy;
    @EJB
    private EcommStrategy ecommStrategy;
    @EJB
    private RetailStrategy retailStrategy;
    
    public BaseStrategy findStrategy(String strategy) {

        switch (strategy) {
            case StrategyType.ECOMM:
                return ecommStrategy;

            case StrategyType.MPG:
                return retailStrategy;
                
            case StrategyType.DECA:
                return retailStrategy;

                
            // Add more strategies
            default:
                return retailStrategy;
        }
    }
    
    public void setRetailStrategy(RetailStrategy retailStrategy) {
        this.retailStrategy = retailStrategy;
        
    }

    public BaseStrategy findStrategy(RetailStrategy retailStrategy) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
