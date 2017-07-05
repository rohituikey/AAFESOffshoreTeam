/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.authorizer;

import com.aafes.stargate.util.StrategyType;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pkalpesh
 */
@Stateless
public class BaseStrategyFactory {

//    @EJB
//    private MPGStrategy mpgStrategy;
    @EJB
    private EcommStrategy ecommStrategy;
    @EJB
    private RetailStrategy retailStrategy;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(BaseStrategyFactory.class.getSimpleName());

    public BaseStrategy findStrategy(String strategy) {
        LOG.info("in BaseStrategy.findStrategy. The Strategy is : " + strategy);
        switch (strategy) {
            case StrategyType.ECOMM:
                return ecommStrategy;

            case StrategyType.MPG:
                return retailStrategy;

            case StrategyType.DECA:
                return retailStrategy;

            default:
                return retailStrategy;
        }
    }
}