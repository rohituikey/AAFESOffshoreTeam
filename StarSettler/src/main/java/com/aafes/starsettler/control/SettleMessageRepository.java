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

    public List<SettleEntity> getFDMSData(String processDate, String settleStatus) {
       return settleMessageDAO.getFDMSData(processDate, settleStatus);
    }

    public List<SettleEntity> getVisionData(String processDate, String settleStatus) {
        return settleMessageDAO.getVisionData(processDate, settleStatus);
    }

    public List<SettleEntity> getAll(String processDate, String settleStatus) {
        return settleMessageDAO.getAll(processDate, settleStatus);
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

    
}
