/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.fdms;

import com.aafes.starsettler.control.BaseSettler;
import com.aafes.starsettler.entity.SettleEntity;
import com.aafes.starsettler.util.SettleStatus;
import com.aafes.starsettler.util.SettlerType;
import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author ganjis
 */
@Stateless
public class FirstDataSettler extends BaseSettler{

    
    private static final org.slf4j.Logger log
            = LoggerFactory.getLogger(FirstDataSettler.class.
                    getSimpleName());
    
    @EJB
    private FirstDataGatewayBean settlegatewayBean ;
    
    // Schedular will call this method 
    @Override
    public void run(String identityUUID, String processDate) {

        // Get First Data records from Base Settler
        // And process them
        log.info(" FDMS Settlement process started ");
        List<SettleEntity> fdmsData = getSettleData(identityUUID, SettlerType.FDMS,processDate,SettleStatus.Ready_to_settle);

        // Format fdms data and send it to FDMS to settle
        if (fdmsData == null || fdmsData.isEmpty()) {
            log.info("FDMS Data is empty. System is exiting");
            return;
        }
        
        log.info("Query yielded " + fdmsData.size() + " transactions.");

        try {
            String batchId = super.getBatchId();
            settlegatewayBean.settle(fdmsData, batchId);
            super.updateFdmsData(fdmsData, SettleStatus.In_Progress);
            super.updateBatchRef(fdmsData,processDate);

        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException | TransformerException ex) {
            log.info("Error while formatting the FDMS Data " + ex.getMessage());
            log.info("System is exiting.");
            return;
            
        } catch (FirstDataException e ) {
            log.info("Error while formatting the FDMS Data " + e.getMessage());
            log.info("System is exiting.");
            return;
        }
         
        
    }

    /**
     * @param settlegatewayBean the settlegatewayBean to set
     */
    public void setSettlegatewayBean(FirstDataGatewayBean settlegatewayBean) {
        this.settlegatewayBean = settlegatewayBean;
    }
}
