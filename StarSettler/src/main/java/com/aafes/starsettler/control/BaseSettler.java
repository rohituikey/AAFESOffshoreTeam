/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.control;

import com.aafes.starsettler.entity.SettleEntity;
import com.aafes.starsettler.util.SettlerType;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author ganjis
 */
public abstract class BaseSettler {

    @EJB
    private SettleMessageRepository repository;
   
    public abstract void run(String identityUUID, String processDate);

    public List<SettleEntity> getSettleData(String identityUUID,String settlerType, String processDate, String settleStatus) {

        List<SettleEntity> settleData = null;

        switch (settlerType) {
            case SettlerType.FDMS:
                settleData = repository.getFDMSData(identityUUID,processDate,settleStatus);
                break;

            case SettlerType.MILSTAR:
                settleData = repository.getVisionData(identityUUID,processDate,settleStatus);
                break;
                
            case SettlerType.WEX:
                settleData = repository.getWexData(identityUUID,processDate,settleStatus);
                break;
            
            default:
                settleData = repository.getAll(identityUUID,processDate,settleStatus);
                break;
        }

        return settleData;
    }

    public void updateStatus(List<SettleEntity> settleData, String status) {
        repository.updateStatus(settleData, status);
    }

    public String getBatchId() {
        return repository.getBatchId();
    }

    public void updateFdmsData(List<SettleEntity> fdmsData, String In_Progress) {
        repository.updateFdmsData(fdmsData, In_Progress);
    }

    public void updateBatchRef(List<SettleEntity> fdmsData, String processDate) {
        repository.updateBatchRef(fdmsData,processDate);
    }
    
    
    
    //code for wex
    public void updateWexData(List<SettleEntity> fdmsData, String In_Progress) {
        repository.updateFdmsData(fdmsData, In_Progress);
    }

    public void updateWexBatchRef(List<SettleEntity> fdmsData, String processDate) {
        repository.updateBatchRef(fdmsData,processDate);
    }
    
    public List<String> getTIDList()
    {
       //  repository =  new SettleMessageRepository();
        return  repository.getTIDList();
    }
    
    public List<SettleEntity> getsettleTransaction(List<String> tidList, String identityUUID,String processDate,String SettleStatus) {
        //repository =  new SettleMessageRepository();
        return repository.getsettleTransaction(tidList, identityUUID, processDate, SettleStatus);
    }
    

    /**
     * @param repository the repository to set
     */
    public void setRepository(SettleMessageRepository repository) {
        this.repository = repository;
    }
}
