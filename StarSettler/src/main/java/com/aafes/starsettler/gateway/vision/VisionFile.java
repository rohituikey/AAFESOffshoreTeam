/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.vision;

import com.aafes.starsettler.entity.SettleEntity;
import com.aafes.starsettler.tokenizer.TokenEndPointService;
import com.aafes.starsettler.util.TransactionType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pkalpesh
 */
@Stateless
public class VisionFile {

    private static final Logger log = LoggerFactory.getLogger(VisionFile.class.getName());
    
    @EJB
    private TokenEndPointService tokenEndPointService;

    public void createFile() {

    }

    public void createFile(String createdDate, String sourcePath, HashMap<String, SettleEntity> visionHashMap) throws UnsupportedEncodingException, IOException {
        
        if(visionHashMap !=null
                && visionHashMap.entrySet().size() > 0)
        {
    
        String filename = sourcePath + "visionSFTP_" + createdDate;
        File file = new File(filename);
        // if file doesn't exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }
        log.info("Writing values into " + filename + ".");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile())));
        // BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile())));

        long debitAmount = 0;
        long creditAmount = 0;
        int iSequence = 0;
        
        List<SettleEntity> errorList = new ArrayList<SettleEntity>();
        
        for (Map.Entry<String, SettleEntity> entry : visionHashMap.entrySet()) {

            iSequence++;

            String recordType = "D";
            String batchSource = "0005";
            
            String token = entry.getValue().getCardToken();
            String accountNbr = "NoMatchingAccount";
            try{        
                 accountNbr = tokenEndPointService.lookupAccount(entry.getValue());
            }catch(Exception e){
                log.info("Error while calling tokenizer for token : "+token);
                log.error(e.toString());
            }
            
            try {
                accountNbr = String.format("%019d", Long.parseLong(accountNbr));
            } catch (NumberFormatException n) {
                accountNbr = String.format("%19s", accountNbr);
            }
            
            long tranAmount = Long.parseLong(entry.getValue().getPaymentAmount());

            String tranCode = "0000";
            if (TransactionType.Deposit.equalsIgnoreCase(entry.getValue().getTransactionType())) {
                tranCode = "0150"; 
                debitAmount = debitAmount + tranAmount;
            } else if(TransactionType.Refund.equalsIgnoreCase(entry.getValue().getTransactionType())) {
                
                if(tranAmount < 0) {
                    tranAmount = -1*tranAmount;
                }
                tranCode = "0400";
                creditAmount = creditAmount + tranAmount;
            }


            String tranAmountStr = String.format("%015d",tranAmount);

            int effectiveDate = getDayOfTheYear(entry.getValue().getSettleDate());
            String creditPlan = entry.getValue().getSettlePlan();
//            if(entry.getValue().getQualifiedPlan()!=null
//                    && (!entry.getValue().getQualifiedPlan().equals("")) ){
//                creditPlan = entry.getValue().getQualifiedPlan();
//            }else if(entry.getValue().getResponsePlan()!=null
//                    && (!entry.getValue().getResponsePlan().equals("")) ){
//                creditPlan = entry.getValue().getResponsePlan();
//            }else{
//                creditPlan = entry.getValue().getRequestPlan();
//            }
                    
            String authCode = entry.getValue().getAuthNum();
            String rRn = entry.getValue().getRrn();
            String seqNumber = String.format("%03d", iSequence);
            String ticketNumber = rRn + seqNumber;
            String salesClerk = "0005";
            int storeNumber = 385800000;
            String orderNbr = entry.getValue().getOrderNumber();
            String orderDate = entry.getValue().getOrderDate();
            try {
                orderDate = orderDate.substring(6, 8) + orderDate.substring(4, 6) + orderDate.substring(0, 4);
            } catch (IndexOutOfBoundsException e) {
                orderDate = "error";
            }

            String sVision = recordType
                    + batchSource
                    + accountNbr
                    + tranAmountStr
                    + tranCode
                    + String.format("%07d", effectiveDate)
                    + String.format("%-5s", creditPlan)
                    + String.format("%-6s", authCode)
                    + String.format("%-15s", ticketNumber)
                    + String.format("%09d", storeNumber)
                    + String.format("%-12s", salesClerk)
                    + String.format("%-10s", orderNbr)
                    + String.format("%-8s", orderDate)
                    + String.format("%-35s", "")+"\n";

            bw.write(sVision);

            log.info("file length " + sVision.length());

        }
        
       
            String sVisionTrailer = "T"
                    + "0005"
                    + String.format("%07d", getDayOfTheCurrentYear())
                    + String.format("%015d", debitAmount)
                    + String.format("%015d", creditAmount)
                    + String.format("%-108s", "");

            bw.write(sVisionTrailer);
            bw.close();
        
       }    

    }

    private int getDayOfTheYear(String settleDate) {

        try {
            int day = Integer.parseInt(settleDate.substring(6, 8));
            int month = Integer.parseInt(settleDate.substring(4, 6));
            int year = Integer.parseInt(settleDate.substring(0, 4));
            Calendar cal = new GregorianCalendar();
            cal.set(year, month-1, day);
            String dayofYear = year + "" + ("000" + cal.get(Calendar.DAY_OF_YEAR)).substring(Integer.toString(cal.get(Calendar.DAY_OF_YEAR)).length());
            System.out.println(dayofYear);
            return Integer.parseInt(dayofYear);
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println("getDayOfTheYear exception" + e);
        }
        return -1;
    }

    private int getDayOfTheCurrentYear() {
        Calendar cal = new GregorianCalendar();
        Date dCurrent = new Date();
        cal.setTime(dCurrent);
        String currentDayOfyear = new SimpleDateFormat("YYYY").format(dCurrent) + "" + cal.get(Calendar.DAY_OF_YEAR);
        return Integer.parseInt(currentDayOfyear);
    }

}
