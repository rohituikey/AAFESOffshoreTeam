/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.retailer;

import com.aafes.starsettler.control.BaseSettler;
import com.aafes.starsettler.control.SettleMessageRepository;
import com.aafes.starsettler.entity.SettleEntity;
import com.aafes.starsettler.util.SettleStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * @author burangir
 * 
 */

@Stateless
public class RetailSettler extends BaseSettler{
    

    
    @EJB
    private RetailsService retailServiceObj;

    // Schedular will call this method 
    @Override
    public void run(String processDate) {
        List<SettleEntity> retailDataList = new ArrayList<>();
        List<SettleEntity> retailDataListTmp = new ArrayList<>();
        List<String> decaUuidList = new ArrayList<>();
        SettleMessageRepository repository = new SettleMessageRepository();

        String tmpStr = null;
        decaUuidList = repository.getDecaIdentityUuid(processDate, tmpStr, tmpStr);
        System.out.println("Total UUID's with strategy = DECA " + decaUuidList.size());
        for(int i = 0; i < decaUuidList.size(); i++){
            if(null != decaUuidList.get(i)){
                tmpStr = decaUuidList.get(i);
                retailDataListTmp = repository.getRetailData(processDate, SettleStatus.Ready_to_settle, tmpStr);
            }
            retailDataList.addAll(retailDataListTmp);
        }
        
       //  Format milstar data
        HashMap<String, SettleEntity> retailDataMap = consolidateRetailData(retailDataList);
        
        //Create Vision File and SFTP to vision
        if(retailServiceObj == null) retailServiceObj = new RetailsService();
        retailServiceObj.generateAndSendToRetail(retailDataMap);
        
        super.updateStatus(retailDataList, SettleStatus.In_Progress);
    }

   

    public void setVisionService(RetailsService retailsServiceObj) {
        this.retailServiceObj = retailsServiceObj;
    }
    
    /**
     * EPG1-37
     * Transactions belonging to same Order number, Card number and Plan number should be consolidated within the day
     * @param settleEntityList
     * @return 
     */
    private HashMap<String, SettleEntity> consolidateRetailData(List<SettleEntity> settleEntityList) {

        HashMap<String,SettleEntity> retailDataMapLocal = new HashMap<>();
        
        settleEntityList.forEach((settleEntity) -> {
            String key = settleEntity.getCardToken()+settleEntity.getOrderNumber()+settleEntity.getSettlePlan();
            if(retailDataMapLocal.containsKey(key)){
                SettleEntity settleEntityDup = retailDataMapLocal.get(key);
                if(settleEntityDup.getPaymentAmount()!=null &&
                        settleEntity.getPaymentAmount()!=null){
                     long lAmt = Long.parseLong(settleEntityDup.getPaymentAmount()) 
                             + Long.parseLong(settleEntity.getPaymentAmount());
                    settleEntityDup.setPaymentAmount(Long.toString(lAmt));
                }
                retailDataMapLocal.put(key, settleEntityDup);
            }else{
                retailDataMapLocal.put(key, settleEntity);
            }
        });
        retailDataMapLocal.forEach((k, v) -> System.out.println("Key : " + k + " Value : " + v.getCardToken() + ", amount :" + v.getPaymentAmount()));
        return retailDataMapLocal;
    }
}