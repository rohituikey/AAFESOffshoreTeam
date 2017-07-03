/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.fdms;

import com.aafes.starsettler.entity.SettleEntity;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ghadiyamp
 */
@Stateless
public class FirstDataGatewayBean {

    private static final org.slf4j.Logger log
            = LoggerFactory.getLogger(FirstDataGatewayBean.class.
                    getSimpleName());

    @EJB
    private SettleXMLHandler settleXMLHandler;
    @Inject
    private String sourcePath;
    @Inject
    private String pid;
    private String finalBatchId;
    
    public void settle(List<SettleEntity> fdmsData, String batchId) throws
            ParserConfigurationException, SAXException, IOException,
            XPathExpressionException, TransformerException, FirstDataException {

        HashMap fdmsSettlementMap = consolidateBatchRecords(fdmsData, batchId);
        List<SettleEntity> settleList = new ArrayList<SettleEntity>(fdmsSettlementMap.values());

        String returnXML = settleXMLHandler.formatRequestXML(settleList);

        createFile(returnXML);
    }

    private HashMap consolidateBatchRecords(List<SettleEntity> fdmsData, String batchId) throws FirstDataException {

        HashMap<String, SettleEntity> fdmsSettlementMap = new HashMap<>();
        finalBatchId = this.makeBatchId(batchId);
        int sequnceNumber = 1;

        for (SettleEntity settleEntity : fdmsData) {
            String key = settleEntity.getOrderNumber() + settleEntity.getCardType() + settleEntity.getCardToken()
                    + settleEntity.getSettleDate() + settleEntity.getTransactionType();

            settleEntity.setBatchId(finalBatchId);

            if (fdmsSettlementMap.containsKey(key)) {
                SettleEntity settleEntityDup = fdmsSettlementMap.get(key);
                if (settleEntityDup.getPaymentAmount() != null
                        && settleEntity.getPaymentAmount() != null) {
                    long lAmt = Long.parseLong(settleEntityDup.getPaymentAmount())
                            + Long.parseLong(settleEntity.getPaymentAmount());
                    settleEntityDup.setPaymentAmount(Long.toString(lAmt));
                }
                fdmsSettlementMap.put(key, settleEntityDup);
            } else {
                settleEntity.setSequenceId(format(sequnceNumber));
                sequnceNumber++;
                fdmsSettlementMap.put(key, settleEntity);

            }
        }

        return fdmsSettlementMap;
    }

    private String makeBatchId(String batchId) throws FirstDataException{

        try {
            if (batchId == null || batchId.trim().isEmpty()) {
                Calendar cal = Calendar.getInstance();
                Date currnetDate = new Date();
                cal.setTime(currnetDate);
                GregorianCalendar gc = new GregorianCalendar();
                gc.set(GregorianCalendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
                gc.set(GregorianCalendar.MONTH, cal.get(Calendar.MONTH));
                gc.set(GregorianCalendar.YEAR, cal.get(Calendar.YEAR));
                DateFormat df = new SimpleDateFormat("yy");
                String year = df.format(currnetDate);
                int JULIAN_DAY = gc.get(GregorianCalendar.DAY_OF_YEAR);
                String pacckedDay = ("000" + Integer.toString(JULIAN_DAY)).substring((Integer.toString(JULIAN_DAY)).length());
                batchId = year + pacckedDay + "001";
            } else {
                long oldBatch = Long.parseLong(batchId);
                oldBatch ++ ;
                batchId = Long.toString(oldBatch);
            }

        } catch (Exception e) {
            new FirstDataException("Unable to make batch Id");
        }

        return batchId;
    }

    private void createFile(String returnXML) throws
            UnsupportedEncodingException, IOException {
        
        if(returnXML != null
                && (!returnXML.equals("")) ){

            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("MMddyyyy_hh-mm-ss");
            String createdDate = dateFormat.format(date);

            String filename = sourcePath + finalBatchId + "_" + pid + "_" + createdDate + "_REQ.LB000391.zip";
            log.info("FDMS Batch Settlement filename is : " + filename);
            File file = new File(filename);
            // if file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            log.info("Writing values into " + filename + ".");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile())));
            bw.write(returnXML);
            bw.close();
        
        }
    }

    public String getPid() {
        return pid;
    }

    private String format(int sequnceNumber) {
        String seq = Integer.toString(sequnceNumber);
        
        seq = ("000000" + seq).substring(seq.length());
        
        return seq;
    }

    /**
     * @param settleXMLHandler the settleXMLHandler to set
     */
    public void setSettleXMLHandler(SettleXMLHandler settleXMLHandler) {
        this.settleXMLHandler = settleXMLHandler;
    }

}
