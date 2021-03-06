/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.wex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author singha
 */
@Stateless
public class WexTransactionBean {

    private static final Logger log = LoggerFactory.getLogger(WexTransactionBean.class.getName());
    
    private final DateFormat dateFormat =new SimpleDateFormat("yyyyMMddHHmmss");
//    @EJB
//    private TokenEndPointService tokenEndPointService;

    public void createFile() {

    }

    public void createFile(String sourcePath ,String settlexmlrecord, String fileSeqNo) throws UnsupportedEncodingException, IOException {
          log.info("Entry in createFile method of WexFile..");
             Date date = new Date();
           // String createdDate = dateFormat.format(date);
        log.info("Entry in createFile method of WexTransactionBean..");
       
        if (null != settlexmlrecord && !settlexmlrecord.isEmpty()) {
             String filename = sourcePath +fileSeqNo; //"wexSFTP_" + createdDate;
           
             File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile())));
            bw.write(settlexmlrecord);
            bw.close();
            log.info("Exit from createFile method of WexTransactionBean..");
        }
        log.info("Exit from createFile method of WexTransactionBean..");
    }
    
    

}
