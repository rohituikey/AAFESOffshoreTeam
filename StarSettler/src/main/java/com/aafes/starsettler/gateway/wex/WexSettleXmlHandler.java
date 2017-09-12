/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.wex;

import com.aafes.starsettler.entity.SettleEntity;
import com.aafes.starsettler.tokenizer.TokenEndPointService;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import jaxb.wextransaction.Transactionfile;
import jaxb.wextransaction.Transactionfile.Batch;
import jaxb.wextransaction.Transactionfile.Batch.Trans;
import jaxb.wextransaction.Transactionfile.Batch.Trans.Card;
import jaxb.wextransaction.Transactionfile.Batch.Trans.Product;
import jaxb.wextransaction.Transactionfile.Batch.Trans.Pump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author singha
 */
@Stateless
public class WexSettleXmlHandler {

    private static final Logger logger = LoggerFactory.getLogger(WexSettleXmlHandler.class.getName());
    private final DocumentBuilder documentBuilder;

    @EJB
    private TokenEndPointService tokenEndPointService;

    @Inject
    private String pid;
    @Inject
    private String sid;

    @Inject
    private String divisionNumber;

    private Transactionfile fileXmlMapper;

    public WexSettleXmlHandler() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        documentBuilderFactory.setNamespaceAware(true);
    }

    public String formatRequestXML(List<SettleEntity> transactionSettleData) throws SAXException, IOException,
            XPathExpressionException, TransformerConfigurationException, TransformerException, ParserConfigurationException {
        // fileXmlMapper = new Transactionfile();
//        fileXmlMapper.setDate(WexSettleXmlHandler.getformatedDate());
//        fileXmlMapper.setTime(WexSettleXmlHandler.getformatedTime());
//        fileXmlMapper.setSequence("001");
//        String xmlString;
//        fileXmlMapper = getTransaction(transactionSettleData);
//        StringWriter sw = new StringWriter();
//        JAXB.marshal(fileXmlMapper, sw);
//        xmlString = sw.toString();
        return "";
    }

    private static String getformatedDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String ts = dateFormat.format(date);
        return ts;
    }

    private static String getformatedTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        return sdf.format(cal.getTime());
    }

    private static Transactionfile getTransaction(List<SettleEntity> wexDataList) {

        String currentTid = null, oldTid = null;
        int batchid = 1;
        Transactionfile fileObj = new Transactionfile();

        for (SettleEntity settleEntity : wexDataList) {

            Trans trans = new Trans();
            Pump pump = new Pump();
            Card card = new Card();
            int i = 0, transactionnumber = 1;
            currentTid = settleEntity.getLineId();

            Batch batch = new Batch();
            if (oldTid == null) {
                oldTid = currentTid;
            }
            if (!currentTid.equalsIgnoreCase(oldTid)) {
                oldTid = currentTid;
            }
            batch.setTid(currentTid);
            batch.setBid(batchid++);
//            List<String> nonfuelprodlist = settleEntity.getNonfuelproductgroup();
//            for (String str : nonfuelprodlist) {
//                List<String> productDetail = Arrays.asList(str.split(","));
//                Product product = new Product();
//                product.setAmount(productDetail.get(0));
//                trans.getProduct().add(product);
//            }
//
//            List<String> fuelprodlist = settleEntity.getFuelproductgroup();
//            for (String str : fuelprodlist) {
//                Product product = new Product();
//                product.setAmount(str);
//                product.setAmount(settleEntity.getNonefuelamount());
//                product.setPrice(settleEntity.getUnitCost());
//                product.setQuantity(settleEntity.getQuantity());
//                trans.getProduct().add(product);
//            }
//modified as per modifications in settleEntity class.
            List<String> prodlist = settleEntity.getProductgroup();
            for (String str : prodlist) {
                Product product = new Product();
                product.setAmount(str);
                product.setAmount(settleEntity.getNonefuelamount());
                product.setPrice(settleEntity.getUnitCost());
                product.setQuantity(settleEntity.getQuantity());
                trans.getProduct().add(product);
            }

            pump.setCat(settleEntity.getCatflag());
            pump.setService(settleEntity.getService());
            pump.setNbr(i++);
            pump.setAmount(settleEntity.getNonefuelamount());
            card.setValue(settleEntity.getCardReferene());
            card.setTrack(settleEntity.getTrackdata2());
            trans.setNbr(String.format("%04d", transactionnumber));
            trans.setTime(settleEntity.getTime());
            trans.setCard(card);
            trans.setOdometer(settleEntity.getOdometer());
            trans.setAmount(settleEntity.getPaymentAmount());
            trans.setAuthref(settleEntity.getAuthreference());
            trans.setDriver(settleEntity.getDriverId());
            trans.setVehicle(settleEntity.getVehicleId());
            trans.setPump(pump);
            trans.setDate(settleEntity.getSettleDate());
            batch.getTrans().add(trans);
            batch.setApp("AuthReq");
            batch.setVersion("1");

            fileObj.getBatch().add(batch);
            fileObj.setDate(settleEntity.getSettleDate());
            fileObj.setTime(settleEntity.getTime());
            fileObj.setSequence("00001");

        }
        return fileObj;
    }
//    private static String getformatedDate() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//        Date date = new Date();
//        String ts = dateFormat.format(date);
//        return ts;
//    }
//
//    private static String getformatedTime() {
//        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
//        return sdf.format(cal.getTime());
//    }
//    private static Transactionfile getTransaction(List<SettleEntity> wexDataList) {
//
//        String currentTid = null, oldTid = null;
//        int batchid = 1;
//        Transactionfile fileObj = new Transactionfile();
//
//        for (SettleEntity settleEntity : wexDataList) {
//
//            Trans trans = new Trans();
//            Pump pump = new Pump();
//            Card card = new Card();
//            int i = 0, transactionnumber = 1;
//            currentTid = settleEntity.getLineId();
//
//            Batch batch = new Batch();
//            if (oldTid == null) {
//                oldTid = currentTid;
//            }
//            if (!currentTid.equalsIgnoreCase(oldTid)) {
//                oldTid = currentTid;
//            }
//            batch.setTid(currentTid);
//            batch.setBid(batchid++);
//            List<String> nonfuelprodlist = settleEntity.getNonfuelproductgroup();
//            for (String str : nonfuelprodlist) {
//                List<String> productDetail = Arrays.asList(str.split(","));
//                Product product = new Product();
//                product.setAmount(productDetail.get(0));
//                trans.getProduct().add(product);
//            }
//
//            List<String> fuelprodlist = settleEntity.getFuelproductgroup();
//            for (String str : fuelprodlist) {
//                Product product = new Product();
//                product.setAmount(str);
//                product.setAmount(settleEntity.getNonefuelamount());
//                product.setPrice(settleEntity.getUnitCost());
//                product.setQuantity(settleEntity.getQuantity());
//                trans.getProduct().add(product);
//            }
//
//            pump.setCat(settleEntity.getCatflag());
//            pump.setService(settleEntity.getService());
//            pump.setNbr(i++);
//            pump.setAmount(settleEntity.getNonefuelamount());
//            card.setValue(settleEntity.getCardReferene());
//            card.setTrack(settleEntity.getTrackdata2());
//            trans.setNbr(String.format("%04d", transactionnumber));
//            trans.setTime(settleEntity.getTime());
//            trans.setCard(card);
//            trans.setOdometer(settleEntity.getOdometer());
//            trans.setAmount(settleEntity.getPaymentAmount());
//            trans.setAuthref(settleEntity.getAuthreference());
//            trans.setDriver(settleEntity.getDriverId());
//            trans.setVehicle(settleEntity.getVehicleId());
//            trans.setPump(pump);
//            trans.setDate(settleEntity.getSettleDate());
//            batch.getTrans().add(trans);
//            batch.setApp("AuthReq");
//            batch.setVersion("1");
//
//            fileObj.getBatch().add(batch);
//            fileObj.setDate(settleEntity.getSettleDate());
//            fileObj.setTime(settleEntity.getTime());
//            fileObj.setSequence("00001");
//
//        }
//        return fileObj;
//    }
}
