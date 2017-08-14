/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.wex;

import com.aafes.starsettler.entity.SettleEntity;
import com.aafes.starsettler.gateway.fdms.FirstDataException;
import com.aafes.starsettler.gateway.fdms.FirstDataGatewayBean;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author singha
 */
@Stateless
public class WexDataGatewayBean {

    private static final org.slf4j.Logger log
            = LoggerFactory.getLogger(FirstDataGatewayBean.class.
                    getSimpleName());

    @EJB
    private WexSettleXmlHandler settleXMLHandler;
    @Inject
    private String sourcePath;
    @Inject
    private String pid;
    private String finalBatchId;

    public void settle(List<SettleEntity> entity) throws
            ParserConfigurationException, SAXException, IOException,
            XPathExpressionException, TransformerException, FirstDataException {

        log.info("Entry in settle method of WexDataGatewayBean..");

        //settleXMLHandler= new WexSettleXmlHandler();
        String returnXML = settleXMLHandler.formatRequestXML(entity);

        createXmlFile(returnXML);
        log.info("Exit from settle method of WexDataGatewayBean..");
    }

    private void createXmlFile(String settlexmlrecord) throws
            UnsupportedEncodingException, IOException {

        log.info("Entry in createXmlFile method of WexDataGatewayBean..");
        if (null != settlexmlrecord && !settlexmlrecord.isEmpty()) {

            File file = new File("D:\\Users\\TransactionFile.txt");
            // if file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile())));
            bw.write(settlexmlrecord);
            bw.close();
            log.info("Exit from createFile method of WexDataGatewayBean..");
        }
    }

//    public String getPid() {
//        return pid;
//    }
//
//    private String format(int sequnceNumber) {
//        String seq = Integer.toString(sequnceNumber);
//
//        seq = ("000000" + seq).substring(seq.length());
//
//        return seq;
//    }
    /**
     * @param settleXMLHandler the settleXMLHandler to set
     */
//    public void setSettleXMLHandler(WexSettleXmlHandler wexSettleXMLHandler) {
//        this.settleXMLHandler = wexSettleXMLHandler;
//    }
}
