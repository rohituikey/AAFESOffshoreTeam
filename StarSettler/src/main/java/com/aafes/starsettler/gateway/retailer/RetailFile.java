/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.retailer;

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
 * @author burangir
 * 
 */

@Stateless
public class RetailFile {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetailFile.class.getName());
    
    @EJB
    private TokenEndPointService tokenEndPointService;

    public void createFile() {

    }

    public void createFile(String createdDate, String sourcePath, List<SettleEntity> retailDataMap) throws UnsupportedEncodingException, IOException {
        
        if(retailDataMap !=null
                && retailDataMap.size() > 0)
        {
    
        String filename = sourcePath + "retailSFTP_" + createdDate;
        File file = new File(filename);
        // if file doesn't exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }
        LOGGER.info("Writing values into " + filename + ".");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile())));
        // BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile())));

        long debitAmount = 0, creditAmount = 0, tranAmount;
        int iSequence = 0, effectiveDate, storeNumber = 385800000;
        String transactionId, terminalId, accountNbr, token,  recordType = "D", batchSource = "0318", tranCode = "0000", tranAmountStr,
        creditPlan, authCode, rRn, seqNumber, ticketNumber, salesClerk = "0005", orderNbr, orderDate, sRetailDetails, sRetailTrailer,
        localDate, localTime;
        Date dateObj = new Date();
        
        //List<SettleEntity> errorList = new ArrayList<SettleEntity>();
        
        for (SettleEntity entry : retailDataMap) {
            iSequence++;
            token = entry.getCardToken();
            accountNbr = "NoMatchingAccount";
            try{        
                 accountNbr = tokenEndPointService.lookupAccount(entry);
            }catch(Exception e){
                LOGGER.info("Error while calling tokenizer for token : "+token);
                LOGGER.error(e.toString());
            }
            
            try {
                accountNbr = String.format("%019d", Long.parseLong(accountNbr));
            } catch (NumberFormatException n) {
                accountNbr = String.format("%19s", accountNbr);
            }
            
            tranAmount = Long.parseLong(entry.getPaymentAmount());

            if (TransactionType.Deposit.equalsIgnoreCase(entry.getTransactionType())) {
                tranCode = "0150"; 
                debitAmount = debitAmount + tranAmount;
            } else if(TransactionType.Refund.equalsIgnoreCase(entry.getTransactionType())) {
                if(tranAmount < 0)  tranAmount = -1 * tranAmount;
                tranCode = "0400";
                creditAmount = creditAmount + tranAmount;
            }

            tranAmountStr = String.format("%017d",tranAmount);
            effectiveDate = getDayOfTheYear(entry.getSettleDate());
            creditPlan = entry.getSettlePlan();
            authCode = entry.getAuthNum();
            rRn = entry.getRrn();
            seqNumber = String.format("%03d", iSequence);
            ticketNumber = rRn + seqNumber;
            salesClerk = "0005";
            storeNumber = 385800000;
            orderNbr = entry.getOrderNumber();
//            orderDate = entry.getOrderDate();
//            try {
//                orderDate = orderDate.substring(6, 8) + orderDate.substring(4, 6) + orderDate.substring(0, 4);
//            } catch (IndexOutOfBoundsException e) {
//                orderDate = "error";
//            }
            
            transactionId = entry.getTransactionId();

            localDate = getLocalDate(dateObj);
            localTime = getLocalTime(dateObj);
            
            sRetailDetails = recordType + batchSource + accountNbr + tranAmountStr + tranCode 
                    + String.format("%07d", effectiveDate)
                    + String.format("%-5s", creditPlan) + String.format("%-6s", authCode) 
                    + String.format("%-15s", ticketNumber)
                    + String.format("%-23s", "")
                    + String.format("%09d", storeNumber) + String.format("%-12s", salesClerk)
                    + String.format("%-16s", orderNbr)
                    + String.format("%-8s", "0") + String.format("%07d", effectiveDate)
                    + String.format("%-4s", "") + String.format("%-40s", "")
                    + String.format("%-19s", "") + String.format("%-1s", "")
                    + String.format("%-1s", "") + String.format("%-9s", "0")
                    + String.format("%-17s", "") + String.format("%-3s", "")
                    + String.format("%-1s", "") + String.format("%-15s", "0")
                    + String.format("%-17s", "0") + String.format("%-17s", "0")
                  //  + String.format("%-8s", transactionId) + String.format("%-8s", localDate) 
                    + String.format("%-15s", localTime) + String.format("%-38s", "")+"\n";

            bw.write(sRetailDetails);

            LOGGER.info("file length " + sRetailDetails.length());
        }
        sRetailTrailer = "T" + "0005" + String.format("%07d", getDayOfTheCurrentYear())
                + String.format("%-10s", "") + String.format("%-10s", "") 
                + String.format("%015d", debitAmount)
                + String.format("%015d", creditAmount) + String.format("%-288s", "");
        bw.write(sRetailTrailer);
        bw.close();
       }    
    }

    private int getDayOfTheYear(String settleDate) {
        try {
            settleDate = settleDate.replaceAll("-", "");
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
    
    private String getLocalDate(Date dateObj){
        try{
            SimpleDateFormat smpLocalDate = new SimpleDateFormat("yyyymmdd");
            return smpLocalDate.format(dateObj);
        }catch(Exception dateExc){
            LOGGER.error(dateExc.getMessage());
        }
        return "";
    }
    
    private String getLocalTime(Date dateObj){
         try{
            SimpleDateFormat smpLocalDate = new SimpleDateFormat("HHmmSS");
            return smpLocalDate.format(dateObj);
         }catch(Exception dateExc){
            LOGGER.error(dateExc.getMessage());
        }
        return "";
    }
}