/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.vision;

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
 *
 * @author pkalpesh
 */
@Stateless
public class VisionService {

    private static final Logger log = LoggerFactory.getLogger(VisionService.class.getName());

    @EJB
    private VisionSFTP sftp;

    @EJB
    private VisionFile visionFile;

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

    public VisionService() {

        this.dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    }

    public void generateAndSendToVision(HashMap<String, SettleEntity> visionHashMap) {
        log.info("Triggered.");

        try {

            Date date = new Date();
            String createdDate = dateFormat.format(date);
           

            visionFile.createFile(createdDate, sourcePath, visionHashMap);

            sftp.setSFTPUSER(this.user);
            sftp.setSFTPHOST(this.host);
            sftp.setSFTPPORT(SFTPPORT);
            sftp.setTargetAttributes("/FTADV:RECFM=FB,LRECL=37,C=IBM-1047,D=IBM-1047,I=unix,J=mvs,F=record/");
            sftp.setTargetName(this.destination);
            try {
                String targetName = sourcePath + "visionSFTP_" + createdDate;
                log.info("Sending to " + targetName + ".");
                if (this.password.equals("none")) {
                    log.info("Using identity.");
                    sftp.setSSL_RSA_filename(this.identity);
                    sftp.sendByIdentity(targetName);
                    fileSent = true;
                } else {
                    log.info("Using password.");
                    sftp.setPassword(this.password);
                    sftp.sendByPassword(targetName);
                    fileSent = true;
                }
                log.info("The file transfer was a success.");
            } catch (Exception ex) {
                log.error(ex.getMessage());
                fileSendingError = ex.getMessage();
            }

        } catch (IOException | NumberFormatException e) {
            log.error(e.getMessage());
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

    public void setSftp(VisionSFTP sftp) {
        this.sftp = sftp;
    }

    public void setVisionFile(VisionFile visionFile) {
        this.visionFile = visionFile;
    }

}