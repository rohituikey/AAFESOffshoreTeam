/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.retailer;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author burangir
 * 
 */

@Stateless
public class RetailSFTP {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetailSFTP.class.getName());
    private Session session = null;
    private Channel channel = null;
    private ChannelSftp channelSftp = null;

    // private String SFTPHOST = "cpuc.aafes.com";
    private String SFTPHOST;
    private int SFTPPORT;
    private String SFTPUSER;
    private String targetName;
    private String targetAttributes;
    private String SSL_RSA_filename;
    private String password;
    private String SFTPErrorMsg;

    public RetailSFTP() {
        LOGGER.info("Starting SFTP session.");
    }

    public void sendByIdentity(String filename) throws Exception {
        LOGGER.info("Sending file: {}.", filename);
        try {
            JSch jsch = new JSch();
            //System.out.println(ssl_rsa);
            File file = new File(SSL_RSA_filename);
            URL keyFileURL = null;
            URI keyFileURI = null;
            if (file.exists()) {
                keyFileURL = file.toURL();
                if (keyFileURL == null) {
                    //System.out.println("what");
                    throw new RuntimeException("SSL_RSA Key file not found in classpath");
                }
            } else {
                LOGGER.error("SSL_RSA Key file not found in classpath" + file.getPath());
            }
            Path path = Paths.get(filename);
            if (Files.exists(path)) {

            }
            File file1 = new File(filename);
            if (!file1.exists()) {
                LOGGER.error("File could not found. {}", filename);
                return;
            }
            try {
                keyFileURI = keyFileURL.toURI();
            } catch (Exception URISyntaxException) {
                //System.out.println("Wrong URL");
                URISyntaxException.printStackTrace();
                throw new RuntimeException(file.getPath() + " not found in classpath");
            }

            jsch.addIdentity(new File(keyFileURI).getAbsolutePath());
            System.out.println(new File(keyFileURI).getAbsolutePath() + " LOL");

            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            //System.out.println("session" + session);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.connect();
            LOGGER.info("Host connected");
            channel = session.openChannel("sftp");
            channel.connect();
            LOGGER.info("Sftp channel opened and connected");
            channelSftp = (ChannelSftp) channel;
            FileInputStream fis = new FileInputStream(filename);
            String tmp = targetAttributes + targetName;
            LOGGER.info("SFTPData:" + tmp);
            LOGGER.info("Source:" + filename);
            channelSftp.put(fis, tmp);
            LOGGER.info("channelSftp" + channelSftp);
            LOGGER.info("File transferred successfully to Host");
            channelSftp.exit();
            LOGGER.info("Sftp channel exited");
            channel.disconnect();
            LOGGER.info("Sftp channel disconnected");
            session.disconnect();
            LOGGER.info("Sftp sesssion disconnected");
        } catch (MalformedURLException | RuntimeException | JSchException | FileNotFoundException | SftpException ex) {
            LOGGER.error(ex.getMessage());
            setSFTPErrorMsg(ex.getMessage());
        } finally {
        }
    }

    public void sendByPassword(String filename) throws Exception {
        LOGGER.info("Sending file: {}.", filename);
        try {
            JSch jsch = new JSch();
            Session session = null;
            Path path = Paths.get(filename);
            File file1 = new File(filename);
            if (!file1.exists()) {
                LOGGER.error("File could not found. {}", filename);
                return;
            }
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(password);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.connect();
            LOGGER.info("Host connected");
            channel = session.openChannel("sftp");
            channel.connect();
            LOGGER.info("Sftp channel opened and connected");
            channelSftp = (ChannelSftp) channel;
            FileInputStream fis = new FileInputStream(filename);
            String tmp = targetAttributes + targetName;
            LOGGER.info("SFTPData:" + tmp);
            LOGGER.info("Source:" + filename);
            channelSftp.put(fis, tmp);
            LOGGER.info("channelSftp" + channelSftp);
            LOGGER.info("File transferred successfully to Host");
            channelSftp.exit();
            LOGGER.info("Sftp channel exited");
            channel.disconnect();
            LOGGER.info("Sftp channel disconnected");
            session.disconnect();
            LOGGER.info("Sftp sesssion disconnected");
        } catch (JSchException | FileNotFoundException | SftpException ex) {
            LOGGER.error(ex.getMessage());
            setSFTPErrorMsg(ex.getMessage());
        } finally {
        }
    }

    public String getSFTPHOST() {
        return SFTPHOST;
    }

    public void setSFTPHOST(String SFTPHOST) {
        this.SFTPHOST = SFTPHOST;
    }

    public int getSFTPPORT() {
        return SFTPPORT;
    }

    public void setSFTPPORT(int SFTPPORT) {
        this.SFTPPORT = SFTPPORT;
    }

    public String getSFTPUSER() {
        return SFTPUSER;
    }

    public void setSFTPUSER(String SFTPUSER) {
        this.SFTPUSER = SFTPUSER;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetAttributes() {
        return targetAttributes;
    }

    public void setTargetAttributes(String targetAttributes) {
        this.targetAttributes = targetAttributes;
    }

    public String getSSL_RSA_filename() {
        return SSL_RSA_filename;
    }

    public void setSSL_RSA_filename(String SSL_RSA_filename) {
        this.SSL_RSA_filename = SSL_RSA_filename;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the SFTPErrorMsg
     */
    public String getSFTPErrorMsg() {
        return SFTPErrorMsg;
    }

    /**
     * @param SFTPErrorMsg the SFTPErrorMsg to set
     */
    public void setSFTPErrorMsg(String SFTPErrorMsg) {
        this.SFTPErrorMsg = SFTPErrorMsg;
    }
    
}