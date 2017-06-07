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
    private SettleMessageRepository repository = new SettleMessageRepository();
   
    public abstract void run(String processDate);

    public List<SettleEntity> getSettleData(String settlerType, String processDate, String settleStatus) {

        List<SettleEntity> settleData = null;

        switch (settlerType) {
            case SettlerType.FDMS:
                settleData = repository.getFDMSData(processDate,settleStatus);
                break;

            case SettlerType.MILSTAR:
                settleData = repository.getVisionData(processDate,settleStatus);
                break;
            
            case SettlerType.RETAIL:
                settleData = repository.getRetailData(processDate, settleStatus, "");
                break;    
                
            default:
                settleData = repository.getAll(processDate,settleStatus);
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

    /**
     * @param repository the repository to set
     */
    public void setRepository(SettleMessageRepository repository) {
        this.repository = repository;
    }
}
