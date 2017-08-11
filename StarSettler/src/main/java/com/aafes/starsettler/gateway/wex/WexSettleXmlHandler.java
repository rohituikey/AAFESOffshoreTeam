/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.wex;

import com.aafes.starsettler.entity.SettleEntity;
import com.aafes.starsettler.tokenizer.TokenEndPointService;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import jaxb.wextransaction.Transactionfile;
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

    public String formatRequestXML(Map map) throws SAXException, IOException,
            XPathExpressionException, TransformerConfigurationException, TransformerException, ParserConfigurationException {
        fileXmlMapper = new Transactionfile();
        fileXmlMapper.setDate(Integer.parseInt(WexSettleXmlHandler.getformatedDate()));
        fileXmlMapper.setTime(Short.parseShort(WexSettleXmlHandler.getformatedTime()));
        fileXmlMapper.setSequence("001");
        Set key = map.keySet();
        Iterator it = key.iterator();
        int batchid = 001;
        String xmlString;

        while (it.hasNext()) {
            Transactionfile.Batch batch = new Transactionfile.Batch();
            batch.setBid(batchid++);
            String tid = (String) it.next();
            batch.setTid(tid);
           // batch.getTrans().add(getTrasaction(map.get(tid)));
            fileXmlMapper.getBatch().add(batch);
        }

// fileXmlMapper.setSequence(w);// need to add sequence number
        //sequenceId
        StringWriter sw = new StringWriter();
        JAXB.marshal(fileXmlMapper, sw);
        xmlString = sw.toString();

        return xmlString;
    }

//    private String mapCardTypeToMop(String cardType) {
//        switch (cardType) {
//            case "Visa":
//                return "VI";
//            case "Mastercard":
//                return "MC";
//            case "Amex":
//                return "AX";
//            case "Discover":
//                return "DI";
//        }
//        return "";
//    }
//
//    public void setPid(String pid) {
//        this.pid = pid;
//    }
//
//    public void setSid(String sid) {
//        this.sid = sid;
//    }
//
//    public void setDivisionNumber(String divisionNumber) {
//        this.divisionNumber = divisionNumber;
//    }
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

    private static List<SettleEntity> buildTransaction(List<SettleEntity> wexDataList) {
        List trnsList = new ArrayList();
        for (SettleEntity settleEntity : wexDataList) {
            Transactionfile.Batch.Trans trans = new Transactionfile.Batch.Trans();
            Transactionfile.Batch.Trans.Pump pump = new Transactionfile.Batch.Trans.Pump();
            Transactionfile.Batch.Trans.Card card = new Transactionfile.Batch.Trans.Card();
            List productList = new ArrayList();

            int i = 001, transactionnumber = 001;
            List<String> nonfuelprodlist = settleEntity.getNonfuelproductgroup();
            for (String str : nonfuelprodlist) {
                Transactionfile.Batch.Trans.Product product = new Transactionfile.Batch.Trans.Product();
                product.setAmount(str);
            }

            List<String> fuelprodlist = settleEntity.getFuelproductgroup();
            for (String str : fuelprodlist) {
                Transactionfile.Batch.Trans.Product product = new Transactionfile.Batch.Trans.Product();
                product.setAmount(str);
                product.setAmount(settleEntity.getNonefuelamount());
                product.setPrice(settleEntity.getUnitCost());
                product.setQuantity(settleEntity.getQuantity());
            }

            pump.setCat(settleEntity.getCatflag());
            pump.setService(settleEntity.getService());
            pump.setNbr(i++);
            pump.setAmount(settleEntity.getNonefuelamount());

            card.setValue(settleEntity.getCardReferene());
            card.setTrack(Byte.parseByte(settleEntity.getTrackdata2()));

            trans.setCard(card);
            trans.setVehicle(settleEntity.getVehicleId());
            trans.setAmount(settleEntity.getPaymentAmount());
            trans.setDate(Integer.parseInt(settleEntity.getTime()));
            trans.setDriver(settleEntity.getDriverId());
            trans.setAuthref(settleEntity.getAuthreference());
            trans.setOdometer(settleEntity.getOdometer());
            trans.setPump(pump);
            trans.setNbr(String.format("%04d", transactionnumber));
            trans.setTime(Integer.parseInt(settleEntity.getTime()));
            trnsList.add(trans);
        }
        return trnsList;
    }
}
