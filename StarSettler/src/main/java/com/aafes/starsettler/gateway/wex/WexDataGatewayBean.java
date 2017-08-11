/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.wex;

import com.aafes.starsettler.gateway.fdms.FirstDataException;
import com.aafes.starsettler.gateway.fdms.FirstDataGatewayBean;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
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
public class WexDataGatewayBean {

    private static final org.slf4j.Logger log
            = LoggerFactory.getLogger(FirstDataGatewayBean.class.
                    getSimpleName());

    @EJB
    private WexSettleXmlHandler settleXMLHandler;
    @Inject
    private String sourcePath;
    @Inject
    private String pid;
    private String finalBatchId;

    public void settle(Map map) throws
            ParserConfigurationException, SAXException, IOException,
            XPathExpressionException, TransformerException, FirstDataException {

        log.info("Entry in settle method of WexDataGatewayBean..");
//        HashMap wexSettlementMap = consolidateBatchRecords(wexData, batchId);
//        List<SettleEntity> settleList = new ArrayList<SettleEntity>(wexSettlementMap.values());

        String returnXML = settleXMLHandler.formatRequestXML(map);

        createXmlFile(returnXML);
        log.info("Exit from settle method of WexDataGatewayBean..");
    }

//    private HashMap consolidateBatchRecords(List<SettleEntity> wexData, String batchId) throws FirstDataException {
//
//        log.info("Entry in consolidateBatchRecords method of WexDataGatewayBean..");
//        HashMap<String, SettleEntity> wexSettlementMap = new HashMap<>();
//        finalBatchId = this.makeBatchId(batchId);
//        int sequnceNumber = 1;
//
//        for (SettleEntity settleEntity : wexData) {
//            String key = settleEntity.getOrderNumber() + settleEntity.getCardType() + settleEntity.getCardToken()
//                    + settleEntity.getSettleDate() + settleEntity.getTransactionType();
//
//            settleEntity.setBatchId(finalBatchId);
//
//            if (wexSettlementMap.containsKey(key)) {
//                SettleEntity settleEntityDup = wexSettlementMap.get(key);
//                if (settleEntityDup.getPaymentAmount() != null
//                        && settleEntity.getPaymentAmount() != null) {
//                    long lAmt = Long.parseLong(settleEntityDup.getPaymentAmount())
//                            + Long.parseLong(settleEntity.getPaymentAmount());
//                    settleEntityDup.setPaymentAmount(Long.toString(lAmt));
//                }
//                wexSettlementMap.put(key, settleEntityDup);
//            } else {
//                settleEntity.setSequenceId(format(sequnceNumber));
//                sequnceNumber++;
//                wexSettlementMap.put(key, settleEntity);
//
//            }
//        }
//
//        log.info("Exit from consolidateBatchRecords method of WexDataGatewayBean..");
//        return wexSettlementMap;
//    }

//    private String makeBatchId(String batchId) throws FirstDataException {
//
//        log.info("Entry in makeBatchId method of WexDataGatewayBean..");
//        try {
//            if (batchId == null || batchId.trim().isEmpty()) {
//                Calendar cal = Calendar.getInstance();
//                Date currnetDate = new Date();
//                cal.setTime(currnetDate);
//                GregorianCalendar gc = new GregorianCalendar();
//                gc.set(GregorianCalendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
//                gc.set(GregorianCalendar.MONTH, cal.get(Calendar.MONTH));
//                gc.set(GregorianCalendar.YEAR, cal.get(Calendar.YEAR));
//                DateFormat df = new SimpleDateFormat("yy");
//                String year = df.format(currnetDate);
//                int JULIAN_DAY = gc.get(GregorianCalendar.DAY_OF_YEAR);
//                String pacckedDay = ("000" + Integer.toString(JULIAN_DAY)).substring((Integer.toString(JULIAN_DAY)).length());
//                batchId = year + pacckedDay + "001";
//            } else {
//                long oldBatch = Long.parseLong(batchId);
//                oldBatch++;
//                batchId = Long.toString(oldBatch);
//            }
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            new FirstDataException("Unable to make batch Id");
//        }
//
//        log.info("Exit from makeBatchId method of WexDataGatewayBean..");
//        return batchId;
//    }

//    private void createFile(String returnXML) throws
//            UnsupportedEncodingException, IOException {
//
//        log.info("Entry in createFile method of WexDataGatewayBean..");
//        if (returnXML != null
//                && (!returnXML.equals(""))) {
//
//            Date date = new Date();
//            DateFormat dateFormat = new SimpleDateFormat("MMddyyyy_hh-mm-ss");
//            String createdDate = dateFormat.format(date);
//
//            String filename = sourcePath + finalBatchId + "_" + pid + "_" + createdDate + "_REQ.LB000391.zip";
//            log.info("FDMS Batch Settlement filename is : " + filename);
//            File file = new File(filename);
//            // if file doesn't exists, then create it
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            log.info("Writing values into " + filename + ".");
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile())));
//            bw.write(returnXML);
//            bw.close();
//            log.info("Exit from createFile method of WexDataGatewayBean..");
//        }
//    }
    private void createXmlFile(String settlexmlrecord) throws
            UnsupportedEncodingException, IOException {

        log.info("Entry in createXmlFile method of WexDataGatewayBean..");
        if (null != settlexmlrecord && !settlexmlrecord.isEmpty()) {

            File file = new File("TransactionFile");
            // if file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile())));
            bw.write(settlexmlrecord);
            bw.close();
            log.info("Exit from createFile method of WexDataGatewayBean..");
        }
    }

//    public String getPid() {
//        return pid;
//    }
//
//    private String format(int sequnceNumber) {
//        String seq = Integer.toString(sequnceNumber);
//
//        seq = ("000000" + seq).substring(seq.length());
//
//        return seq;
//    }

    /**
     * @param settleXMLHandler the settleXMLHandler to set
     */
//    public void setSettleXMLHandler(WexSettleXmlHandler wexSettleXMLHandler) {
//        this.settleXMLHandler = wexSettleXMLHandler;
//    }

}
