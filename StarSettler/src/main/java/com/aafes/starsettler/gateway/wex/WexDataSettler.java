/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.wex;

import com.aafes.stargate.imported.WexSettleEntity;
import com.aafes.starsettler.control.BaseSettler;
import com.aafes.starsettler.entity.SettleEntity;
import com.aafes.starsettler.util.FomatedDateTime;
import com.aafes.starsettler.util.SettleStatus;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXB;
import jaxb.wextransaction.Transactionfile;
import org.slf4j.LoggerFactory;

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
    private WexTransactionFileBuilder wexGatewayBean;

    @EJB
    private WexService wexService;

    List<WexSettleEntity> wexSettlelist = new ArrayList();
    List<SettleEntity> settlelist = new ArrayList();

    // Schedular will call this method 
    @Override
    public void run(String identityUUID, String processDate) {

        wexService = new WexService();
        wexGatewayBean = new WexTransactionFileBuilder();
        log.info(" Wex Settlement process started ");
        try {
            String xmlString = null;
            List<String> terminalIdList = new ArrayList<>();
            terminalIdList = super.getWexTIDList();
            if (terminalIdList.size() > 0) {
                Transactionfile file = getFileContent(terminalIdList, processDate);
                file.setDate(FomatedDateTime.getformatedDate());
                file.setTime(FomatedDateTime.getformatedTime());
                String fileSeqNo = null;
                fileSeqNo = super.fileWexSequenceId();
                fileSeqNo = wexGatewayBean.makeFileSequenceId(fileSeqNo);
                file.setSequence(fileSeqNo);

                if (!file.getBatch().isEmpty()) {
                    StringWriter sw = new StringWriter();
                    JAXB.marshal(file, sw);
                    xmlString = sw.toString();
                    wexService.generateAndSendToNBS(xmlString, fileSeqNo);
                    super.updateWexsettleData(wexSettlelist, fileSeqNo);
                    super.updateWexFileidxref(terminalIdList, fileSeqNo);
                }
            } else {
                log.info("Transaction not exist for settlement");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error while formatting the FDMS Data " + e.getMessage());
            log.info("System is exiting.");
            return;
        }
    }

    /**
     * @param settlegatewayBean the settlegatewayBean to set
     */
    public void setSettlegatewayBean(WexTransactionFileBuilder settlegatewayBean) {
        this.wexGatewayBean = settlegatewayBean;
    }

    /**
     *
     * @param tids
     * @param file
     */
    private Transactionfile getFileContent(List<String> tids, String processDate) {
        Transactionfile file = new Transactionfile();
        Map map = new HashMap();

        for (String tid : tids) {
            List<WexSettleEntity> transactionSettleData = super.getWexsettleTransaction(tid, processDate, SettleStatus.Ready_to_settle);
            if (transactionSettleData.size() > 0) {
                Transactionfile.Batch batch = wexGatewayBean.buildBatchRecord(tid, transactionSettleData);
                wexSettlelist.addAll(transactionSettleData);
                file.getBatch().add(batch);
            }
        }
        return file;
    }

    public static void main(String args[]) {
        WexDataSettler wexDataSettler = new WexDataSettler();
        wexDataSettler.run(args[0], args[1]);
    }
}
