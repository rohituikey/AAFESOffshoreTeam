/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.wex;

import com.aafes.starsettler.control.BaseSettler;
import com.aafes.starsettler.entity.SettleEntity;
import com.aafes.starsettler.gateway.fdms.FirstDataException;
import com.aafes.starsettler.util.SettleStatus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author singha
 */
@Stateless
public class WexDataSettler extends BaseSettler {

    private static final org.slf4j.Logger log
            = LoggerFactory.getLogger(WexDataSettler.class.
                    getSimpleName());


    @EJB
    private WexDataGatewayBean wexGatewayBean;

    // Schedular will call this method 
    @Override
    public void run(String identityUUID, String processDate) {

        // Get First Data records from Base Settler
        // And process them
        log.info(" Wex Settlement process started ");
       // List<SettleEntity> wexData = getSettleData(identityUUID, SettlerType.WEX, processDate, SettleStatus.Ready_to_settle);

        // Format fdms data and send it to FDMS to settle
//        if (wexData == null || wexData.isEmpty()) {
//            log.info("FDMS Data is empty. System is exiting");
//            return;
//        }
//
//        log.info("Query yielded " + wexData.size() + " transactions.");

        try {
           // String batchId = super.getBatchId();
            List<String> terminalIdList =  new ArrayList<String>();
             terminalIdList= super.getTIDList();
             Map map=new HashMap();
            for(String tid:terminalIdList){
                   List<SettleEntity> transactionSettleData=super.getsettleTransaction(identityUUID,processDate,SettleStatus.Ready_to_settle);
                   map.put(tid,transactionSettleData);
                   
            }
            wexGatewayBean.settle(map);
           // super.updateWexData(transactionSettleData, SettleStatus.In_Progress);
            //super.updateWexBatchRef(transactionSettleData, processDate);

        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException | TransformerException ex) {
            log.info("Error while formatting the FDMS Data " + ex.getMessage());
            log.info("System is exiting.");
            return;

        } catch (FirstDataException e) {
            log.info("Error while formatting the FDMS Data " + e.getMessage());
            log.info("System is exiting.");
            return;
        }

    }

    /**
     * @param settlegatewayBean the settlegatewayBean to set
     */
    public void setSettlegatewayBean(WexDataGatewayBean settlegatewayBean) {
        this.wexGatewayBean = settlegatewayBean;
    }

}
