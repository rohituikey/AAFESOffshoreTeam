/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.wex;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author singha
 */
@Stateless
public class WexService {

    private static final Logger log = LoggerFactory.getLogger(WexService.class.getName());

    @EJB
    private WexSFTP sftp;

    @EJB
    private WexFile wexFile;

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

    public WexService() {

        this.dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    }

    public void generateAndSendToNBS(String settlexmlrecord, String fileSeqNo) {
        log.info("Triggered.");

        try {

            Date date = new Date();
            String createdDate = dateFormat.format(date);
            wexFile.createFile(sourcePath,settlexmlrecord, fileSeqNo);
            sftp.setSFTPUSER(this.user);
            sftp.setSFTPHOST(this.host);
            sftp.setSFTPPORT(SFTPPORT);
            sftp.setTargetAttributes("/FTADV:RECFM=FB,LRECL=37,C=IBM-1047,D=IBM-1047,I=unix,J=mvs,F=record/");
            sftp.setTargetName(this.destination);
            try {
                String targetName = sourcePath + "wexSFTP_" + createdDate;
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

    public void setSftp(WexSFTP sftp) {
        this.sftp = sftp;
    }

    public void setVisionFile(WexFile wexFile) {
        this.wexFile = wexFile;
    }

}
