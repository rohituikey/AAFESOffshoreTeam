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

    @EJB
    private MPGStrategy mpgStrategy;
    @EJB
    private EcommStrategy ecommStrategy;
    @EJB
    private RetailStrategy retailStrategy;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(BaseStrategyFactory.class.getSimpleName());

    public BaseStrategy findStrategy(String strategy) {

        switch (strategy) {
            case StrategyType.ECOMM:
                LOG.info(" processing ecommStrategy ");
                return ecommStrategy;

            case StrategyType.MPG:
                LOG.info(" processing retailStrategy  MPG");
                return retailStrategy;

            case StrategyType.DECA:
                LOG.info(" processing retailStrategy  DECA");
                return retailStrategy;

            // Add more strategies
            default:
                LOG.info(" processing retailStrategy  ");
                return retailStrategy;
        }

    }

}
