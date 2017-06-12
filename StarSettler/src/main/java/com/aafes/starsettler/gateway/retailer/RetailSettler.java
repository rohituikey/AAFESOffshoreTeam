/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.retailer;

import com.aafes.starsettler.control.BaseSettler;
import com.aafes.starsettler.control.SettleMessageRepository;
import com.aafes.starsettler.entity.SettleEntity;
import com.aafes.starsettler.util.SettleStatus;
import com.aafes.starsettler.util.StrategyType;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author burangir
 *
 */
@Stateless
public class RetailSettler extends BaseSettler {

    @EJB
    private RetailService retailService;
    @EJB
    private SettleMessageRepository repository;

    private static final Logger LOGGER = LoggerFactory.getLogger(RetailSettler.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = RetailSettler.this.getClass().getSimpleName();

    // Schedular will call this method 
    @Override
    public void run(String identityUuid, String processDate) {
        sMethodName = "run";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        List<SettleEntity> retailDataList;
        List<String> uuidList = new ArrayList<>();

        if (identityUuid != null && !identityUuid.trim().isEmpty()) {
            uuidList.add(identityUuid);
        } else {

            uuidList = repository.getUuidList(StrategyType.DECA);
        }

        LOGGER.info("Total UUID's with strategy = DECA " + uuidList.size());
        for (String uuid : uuidList) {
            retailDataList = repository.getRetailData(uuid, processDate, SettleStatus.Ready_to_settle);
            if (retailDataList != null && !retailDataList.isEmpty()) {
                retailService.generateAndSendToRetail(retailDataList);
                super.updateStatus(retailDataList, SettleStatus.In_Progress);
            }
        }

        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);

    }

    public void setRetailService(RetailService retailsServiceObj) {
        this.retailService = retailsServiceObj;
    }

    /**
     * EPG1-37 Transactions belonging to same Order number, Card number and Plan
     * number should be consolidated within the day
     *
     * @param settleEntityList
     * @return
     */
}
