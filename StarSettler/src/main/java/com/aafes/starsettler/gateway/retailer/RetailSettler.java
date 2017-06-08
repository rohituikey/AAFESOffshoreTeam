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
import java.util.HashMap;
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
public class RetailSettler extends BaseSettler{
    @EJB
    private RetailService retailService;
    @EJB
    private SettleMessageRepository repository;
    private static final Logger LOGGER = LoggerFactory.getLogger(RetailSettler.class.getSimpleName());
    String sMethodName = "";
    final String CLASS_NAME = RetailSettler.this.getClass().getSimpleName();

    // Schedular will call this method 
    @Override
    public void run(String processDate) {
        sMethodName = "run";
        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
        List<SettleEntity> retailDataList = new ArrayList<>();
        List<SettleEntity> retailDataListTmp = new ArrayList<>();
        List<String> uuidList = new ArrayList<>();
        HashMap<String,SettleEntity> retailDataMap = new HashMap<>();
        SettleMessageRepository repository = new SettleMessageRepository();// remove it

        uuidList = repository.getUuidList(StrategyType.DECA);
        LOGGER.info("Total UUID's with strategy = DECA " + uuidList.size());
        for(String localUuIdStr : uuidList){
                retailDataListTmp = repository.getRetailData(processDate, SettleStatus.Ready_to_settle, localUuIdStr);
                if(!retailDataListTmp.isEmpty()) retailDataList.addAll(retailDataListTmp);
        }
        //HashMap<String, SettleEntity> retailDataMap = consolidateRetailData(retailDataList);
        
        retailDataList.forEach((settleEntity) -> {
            String key = settleEntity.getCardToken() + settleEntity.getOrderNumber() + settleEntity.getSettlePlan();
            retailDataMap.put(key, settleEntity);
        });
        //retailService = new RetailService();
        retailService.generateAndSendToRetail(retailDataMap);
        //super.updateStatus(retailDataList, SettleStatus.In_Progress); // remove it
        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
    }

    public void setRetailService(RetailService retailsServiceObj) {
        this.retailService = retailsServiceObj;
    }
    
    /**
     * EPG1-37
     * Transactions belonging to same Order number, Card number and Plan number should be consolidated within the day
     * @param settleEntityList
     * @return 
     */
//    private HashMap<String, SettleEntity> consolidateRetailData(List<SettleEntity> settleEntityList) {
//        sMethodName = "consolidateRetailData";
//        LOGGER.info("Method " + sMethodName + " started." + " Class Name " + CLASS_NAME);
//        HashMap<String,SettleEntity> retailDataMapLocal = new HashMap<>();
//        
//        settleEntityList.forEach((settleEntity) -> {
//            String key = settleEntity.getCardToken()+settleEntity.getOrderNumber()+settleEntity.getSettlePlan();
//            if(retailDataMapLocal.containsKey(key)){
//                SettleEntity settleEntityDup = retailDataMapLocal.get(key);
//                if(settleEntityDup.getPaymentAmount()!=null &&
//                        settleEntity.getPaymentAmount()!=null){
//                     long lAmt = Long.parseLong(settleEntityDup.getPaymentAmount()) 
//                             + Long.parseLong(settleEntity.getPaymentAmount());
//                    settleEntityDup.setPaymentAmount(Long.toString(lAmt));
//                }
//                retailDataMapLocal.put(key, settleEntityDup);
//            }else{
//                retailDataMapLocal.put(key, settleEntity);
//            }
//        });
//        retailDataMapLocal.forEach((k, v) -> System.out.println("Key : " + k + " Value : " + v.getCardToken() + ", amount :" + v.getPaymentAmount()));
//        LOGGER.info("Method " + sMethodName + " ended." + " Class Name " + CLASS_NAME);
//        return retailDataMapLocal;
//    }
}