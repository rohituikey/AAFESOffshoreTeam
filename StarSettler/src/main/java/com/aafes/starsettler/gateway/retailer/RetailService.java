/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.retailer;

import com.aafes.starsettler.control.Configurator;
import com.aafes.starsettler.entity.SettleEntity;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author burangir
 * 
 */
@Stateless
public class RetailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetailService.class.getName());

    @EJB
    private RetailSFTP sftp;

    @EJB
    private RetailFile retailFile;
    
    @EJB
    private Configurator configurator;

    private final DateFormat dateFormat;
    
    @Inject
    private String host;
    
    @Inject
    private String user;
    
    @Inject
    private String destination;
    
    @Inject
    private String identity;
    
    @Inject
    private String sourcePath;
    
    @Inject
    private String password;
    
    @Inject
    private int SFTPPORT;
    
    String errorMessage = "";
    boolean fileSent = false;
    String fileSendingError = "";
   
    int facilityCount = 0;

    public RetailService() {
        this.dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    }

    public void generateAndSendToRetail(HashMap<String, SettleEntity> visionHashMap) {
        LOGGER.info("Triggered.");
        try {
            Date date = new Date();
            String createdDate = dateFormat.format(date);
            
            if(configurator == null ) configurator = new Configurator();
            
            if(null == sourcePath){
                if(System.getProperty("RETAIL_STRATEGY_REPORT_PATH") != null)
                    sourcePath = configurator.get("RETAIL_STRATEGY_REPORT_PATH");
                else {
                    configurator.postConstruct();
                    sourcePath = configurator.get("RETAIL_STRATEGY_REPORT_PATH");
                }
            }
            retailFile.createFile(createdDate, sourcePath, visionHashMap);

            sftp.setSFTPUSER(this.user);
            sftp.setSFTPHOST(this.host);
            sftp.setSFTPPORT(SFTPPORT);
            sftp.setTargetAttributes("/FTADV:RECFM=FB,LRECL=37,C=IBM-1047,D=IBM-1047,I=unix,J=mvs,F=record/");
            sftp.setTargetName(this.destination);
            try {
                String targetName = sourcePath + "visionSFTP_" + createdDate;
                LOGGER.info("Sending to " + targetName + ".");
                if(password == null) password = "none";
                if (this.password.equals("none")) {
                    LOGGER.info("Using identity.");
                    sftp.setSSL_RSA_filename(this.identity);
                    sftp.sendByIdentity(targetName);
                    fileSent = true;
                } else {
                    LOGGER.info("Using password.");
                    sftp.setPassword(this.password);
                    sftp.sendByPassword(targetName);
                    fileSent = true;
                }
                LOGGER.info("The file transfer was a success.");
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
                fileSendingError = ex.getMessage();
            }

        } catch (IOException | NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void setSFTPHOST(String SFTPHOST) {
        this.host = SFTPHOST;
    }

    public void setSFTPUSER(String SFTPUSER) {
        this.user = SFTPUSER;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public void setSourcePath(String path) {
        this.sourcePath = path;
    }

    public void setSourcePassword(String password) {
        this.password = password;
    }

    public void setSftp(RetailSFTP sftp) {
        this.sftp = sftp;
    }

    public void setRetailFile(RetailFile retailFile) {
        this.retailFile = retailFile;
    }
}