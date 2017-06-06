package com.aafes.starsettler.gateway.retail;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.aafes.starsettler.retailer.RetailSFTP;
import java.io.IOException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 *
 * @author burangir
 */
public class RetailSFTPTest {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private RetailSFTP sftp = new RetailSFTP();
    
    @Before
    public void prepare() throws IOException, Exception {
        setupSSHServer();
    }

    private void setupSSHServer() throws IOException, Exception {
        sftp.setPassword("reddy");
        sftp.setSFTPHOST("hqjudgedredd01");
       // sftp.setSFTPPORT("");
        sftp.setSFTPUSER("purna");
        sftp.setSSL_RSA_filename("input.txt");
        sftp.setTargetAttributes("aaa");
        sftp.setTargetName("aaa");
        sftp.sendByPassword(USERNAME);
    }
     @Test
    public void testRetail() throws DatatypeConfigurationException, ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException, Exception {
        String filename = "purna.txt";
        RetailSFTP sftp = new RetailSFTP();
        sftp.sendByIdentity(filename);
        assertEquals("true", "true");
    }
}

