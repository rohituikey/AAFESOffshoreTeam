/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.wex;

import com.aafes.starsettler.entity.SettleEntity;
import com.aafes.starsettler.tokenizer.TokenEndPointService;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author singha
 */
@Stateless
public class WexFile {

    private static final Logger log = LoggerFactory.getLogger(WexFile.class.getName());

    @EJB
    private TokenEndPointService tokenEndPointService;

    public void createFile() {

    }

    public void createFile(String sourcePath ,String settlexmlrecord) throws UnsupportedEncodingException, IOException {

        log.info("Entry in createFile method of WexFile..");

        log.info("Entry in createXmlFile method of WexDataGatewayBean..");
        if (null != settlexmlrecord && !settlexmlrecord.isEmpty()) {

            File file = new File(sourcePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile())));
            bw.write(settlexmlrecord);
            bw.close();
            log.info("Exit from createFile method of WexDataGatewayBean..");
        }
        log.info("Exit from createFile method of WexFile..");
    }

}
