/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.starsettler.imported.SettleEntity;
import com.aafes.starsettler.imported.SettleMessageDAO;
import com.aafes.transaction.Transactionfile;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.xml.bind.JAXB;

/**
 *
 * @author uikuyr
 */
public class TransactionFileGenerator {
    
    private SettleMessageDAO SettleMessageDAO;
    List<SettleEntity> SettleEntityList;
    private Transactionfile fileXmlMapper;
//    private File.Batch batch;
//    private List<File.Batch.Trans> transactionXMLList;
//    private File.Batch.Trans transXML;
//    private File.Batch.Trans.Card cardXML;
//    private File.Batch.Trans.Product productXML;
//    private File.Batch.Trans.Pump pumpXML;

    @PostConstruct
    public void setup(){
        SettleMessageDAO = new SettleMessageDAO();
        SettleEntityList = new ArrayList<>();
//        fileXmlMapper = new File();
//        batch = new File.Batch();
//        transXML = new File.Batch.Trans();
//        transactionXMLList = new ArrayList<>();
//        cardXML = new File.Batch.Trans.Card();
//        productXML= new File.Batch.Trans.Product();
//        pumpXML = new File.Batch.Trans.Pump();
    }
    
    public void generateFile(){
        
        SettleEntityList = SettleMessageDAO.returnTransactions();
        for(SettleEntity settleEntity: SettleEntityList){
            
        }
        StringWriter sw = new StringWriter();
        JAXB.marshal(fileXmlMapper, sw);
        String xmlString = sw.toString();
    }
}
