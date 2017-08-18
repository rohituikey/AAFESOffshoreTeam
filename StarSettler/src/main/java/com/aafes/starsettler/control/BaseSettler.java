/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.control;

import com.aafes.starsettler.dao.SettleMessageDAO;
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

    public List<SettleEntity> getSettleData(String identityUUID, String settlerType, String processDate, String settleStatus) {

        List<SettleEntity> settleData = null;

        switch (settlerType) {
            case SettlerType.FDMS:
                settleData = repository.getFDMSData(identityUUID, processDate, settleStatus);
                break;

            case SettlerType.MILSTAR:
                settleData = repository.getVisionData(identityUUID, processDate, settleStatus);
                break;

            default:
                settleData = repository.getAll(identityUUID, processDate, settleStatus);
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

    public String fileSequenceId() {
        return repository.getFileSequenceId();
    }

    public void updateFdmsData(List<SettleEntity> fdmsData, String In_Progress) {
        repository.updateFdmsData(fdmsData, In_Progress);
    }

    public void updateBatchRef(List<SettleEntity> fdmsData, String processDate) {
        repository.updateBatchRef(fdmsData, processDate);
    }

    //code for wex
    public void updateWexData(List<SettleEntity> WexData, String seqNo) {
        repository.updateWexData(WexData, seqNo);
    }

    public void updateFileidxref(List<SettleEntity> wexData, String sequenceNumber) {
        repository = new SettleMessageRepository();
        repository.updateFileSeqxRef(wexData, sequenceNumber);
    }

    public List<String> getTIDList() {
        repository = new SettleMessageRepository();
        return repository.getTIDList();
    }

    public List<SettleEntity> getsettleTransaction(String tid, String processDate, String SettleStatus) {
        repository = new SettleMessageRepository();
        return repository.getsettleTransaction(tid, processDate, SettleStatus);
    }

    /**
     * @param repository the repository to set
     */
    public void setRepository(SettleMessageRepository repository) {
        this.repository = repository;
    }
}
