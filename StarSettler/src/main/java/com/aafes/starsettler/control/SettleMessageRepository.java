/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.control;

import com.aafes.starsettler.dao.FacilityDAO;
import com.aafes.starsettler.dao.SettleMessageDAO;
import com.aafes.starsettler.dao.TransactionDAO;
import com.aafes.starsettler.entity.AuthorizationCodes;
import com.aafes.starsettler.entity.Facility;
import com.aafes.starsettler.entity.SettleEntity;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author ganjis
 */
@Stateless
public class SettleMessageRepository {

    @EJB
    private SettleMessageDAO settleMessageDAO;
    @EJB
    private FacilityDAO facilityDAO;
    @EJB
    private TransactionDAO transactionDAO;   
    
     

    public void save(List<SettleEntity> settleEntityList) {
        settleMessageDAO.save(settleEntityList);
    }

    public Facility getFacility(String uuid) {
        return facilityDAO.get(uuid);
    }

    public AuthorizationCodes findAuthorizationCodes(SettleEntity settleEntity) {
        return transactionDAO.find(settleEntity);
    }

    public List<SettleEntity> getFDMSData(String identityUUID, String processDate, String settleStatus) {
       return settleMessageDAO.getFDMSData(identityUUID, processDate, settleStatus);
    }

    public List<SettleEntity> getVisionData(String identityUUID, String processDate, String settleStatus) {
        return settleMessageDAO.getVisionData(identityUUID, processDate, settleStatus);
    }
    
    public List<SettleEntity> getWexData(String identityUUID, String processDate, String settleStatus) {
       return settleMessageDAO.getWexData(identityUUID, processDate, settleStatus);
    }

    public List<SettleEntity> getRetailData(String processDate, String settleStatus, String uuid) {
        return settleMessageDAO.getRetailData(processDate, settleStatus, uuid);
    }
    
    public List<String> getUuidList(String strategyStr) {
        return settleMessageDAO.getIdentityUuidList(strategyStr);
    } 
    
    public List<SettleEntity> getAll(String identityUUID, String processDate, String settleStatus) {
        return settleMessageDAO.getAll(identityUUID, processDate, settleStatus);
    }

    public void updateStatus(List<SettleEntity> settleData, String status) {
        settleMessageDAO.updateStatus(settleData, status);
    }

    public String getBatchId() {
        return settleMessageDAO.getBatchId();
    }

    public void updateFdmsData(List<SettleEntity> fdmsData, String In_Progress) {
        settleMessageDAO.updateFdmsData(fdmsData, In_Progress);
    }
    
    public void updateWexData(List<SettleEntity> Wexdata, String In_Progress) {
        settleMessageDAO.updateWexData(Wexdata, In_Progress);
    }
    
     public void updateFileSeqxRef(List<String> tids, String seqNo) {
        settleMessageDAO.updateFileSeqxRef(tids, seqNo);
    }
    
    
     public  boolean validateDuplicateRecords(List<SettleEntity> fdmsData) {
        return  settleMessageDAO.validateDuplicateRecords(fdmsData);
    }

    public void updateBatchRef(List<SettleEntity> fdmsData, String processDate) {
        settleMessageDAO.updateBatchRef(fdmsData, processDate);
    }

    /**
     * @param settleMessageDAO the settleMessageDAO to set
     */
    public void setSettleMessageDAO(SettleMessageDAO settleMessageDAO) {
        this.settleMessageDAO = settleMessageDAO;
    }

    /**
     * @param facilityDAO the facilityDAO to set
     */
    public void setFacilityDAO(FacilityDAO facilityDAO) {
        this.facilityDAO = facilityDAO;
    }

    /**
     * @param transactionDAO the transactionDAO to set
     */
    public void setTransactionDAO(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }
    
    public List<String> getTIDList() {
        settleMessageDAO = new SettleMessageDAO();
        return settleMessageDAO.getTIDList();
    }

    public List<SettleEntity> getsettleTransaction(String tid, String processDate, String settleStatus) {
        settleMessageDAO = new SettleMessageDAO();
        return settleMessageDAO.getsettleTransaction(tid, processDate, settleStatus);
    }
     public String getFileSequenceId() {
          return settleMessageDAO.getFileSequenceId();
     }
}
