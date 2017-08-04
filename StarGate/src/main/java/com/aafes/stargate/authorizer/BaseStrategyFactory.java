 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.authorizer;


import com.aafes.stargate.gateway.wex.WEXStrategy;
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
    @EJB
    private WEXStrategy wEXStrategy;
    
    public BaseStrategy findStrategy(String strategy) {

        switch (strategy) {
            case StrategyType.ECOMM:
                return ecommStrategy;

            case StrategyType.MPG:
                return retailStrategy;
                
            case StrategyType.DECA:
                return retailStrategy;

             case StrategyType.WEX:
                return wEXStrategy;
                
            // Add more strategies
            default:
                return retailStrategy;
        }
    }
    
    public void setRetailStrategy(RetailStrategy retailStrategy) {
        this.retailStrategy = retailStrategy;
        
    }

    /**
     * @param wEXStrategy the wEXStrategy to set
     */
    public void setwEXStrategy(WEXStrategy wEXStrategy) {
        this.wEXStrategy = wEXStrategy;
    }
}
