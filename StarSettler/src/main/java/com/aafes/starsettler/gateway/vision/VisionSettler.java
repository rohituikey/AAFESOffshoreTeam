/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.vision;

import com.aafes.starsettler.control.BaseSettler;
import com.aafes.starsettler.entity.SettleEntity;
import com.aafes.starsettler.util.SettleStatus;
import com.aafes.starsettler.util.SettlerType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author ganjis
 */
@Stateless
public class VisionSettler extends BaseSettler{
    

    
    @EJB
    private VisionService visionService;

    // Schedular will call this method 
    @Override
    public void run(String processDate) {
         // TODO
        // Get Milstar Data records from Base Settler
        // And process them
        List<SettleEntity> milstarData = super.getSettleData(SettlerType.MILSTAR,processDate,SettleStatus.Ready_to_settle);
         //super.getSettleData(SettlerType.MILSTAR);
        
       //  Format milstar data
        HashMap<String, SettleEntity> visionHashMap = consolidateVisionData(milstarData);
        
        //Create Vision File and SFTP to vision
        visionService.generateAndSendToVision(visionHashMap);
        
        super.updateStatus(milstarData, SettleStatus.In_Progress);
    }

   

    public void setVisionService(VisionService visionService) {
        this.visionService = visionService;
    }
    
    /**
     * EPG1-37
     * Transactions belonging to same Order number, Card number and Plan number should be consolidated within the day
     * @param settleEntityList
     * @return 
     */
    private HashMap<String, SettleEntity> consolidateVisionData(List<SettleEntity> settleEntityList) {

        HashMap<String,SettleEntity> visionMap = new HashMap<>();
        
        settleEntityList.forEach((settleEntity) -> {
            String key = settleEntity.getCardToken()+settleEntity.getOrderNumber()+settleEntity.getSettlePlan();
            if(visionMap.containsKey(key)){
                SettleEntity settleEntityDup = visionMap.get(key);
                if(settleEntityDup.getPaymentAmount()!=null &&
                        settleEntity.getPaymentAmount()!=null){
                     long lAmt = Long.parseLong(settleEntityDup.getPaymentAmount()) 
                             + Long.parseLong(settleEntity.getPaymentAmount());
                    settleEntityDup.setPaymentAmount(Long.toString(lAmt));
                }
                visionMap.put(key, settleEntityDup);
            }else{
                visionMap.put(key, settleEntity);
            }
        });
        visionMap.forEach((k, v) -> System.out.println("Key : " + k + " Value : " + v.getCardToken() + ", amount :" + v.getPaymentAmount()));
        return visionMap;
    }

   
   
    
}