/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.gateway.wex;

import com.aafes.stargate.imported.WexSettleEntity;
import com.aafes.starsettler.entity.SettleEntity;
import com.aafes.starsettler.gateway.fdms.FirstDataGatewayBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import jaxb.wextransaction.Transactionfile;
import org.slf4j.LoggerFactory;

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

//    public void settle(List<SettleEntity> entity) throws
//            ParserConfigurationException, SAXException, IOException,
//            XPathExpressionException, TransformerException, FirstDataException {
//
//        log.info("Entry in settle method of WexDataGatewayBean..");
//
//        log.info("Exit from settle method of WexDataGatewayBean..");
//    }
    public Transactionfile.Batch buildWexBachTag(String tid, List transactionSettleData) {
        Transactionfile.Batch batch = new Transactionfile.Batch();
        String bid = makeBatchId(tid); //getBid(); //get BID Date+time
        batch.setTid(tid);
        batch.setApp("AuthREq");
        batch.setVersion("2");
        batch.setBid(Integer.parseInt(bid));
        batch.getTrans().addAll(buildWexTransactionTag(transactionSettleData));
        return batch;
    }

    private List buildWexTransactionTag(List<WexSettleEntity> wexDataList) {

        List<Transactionfile.Batch.Trans> entities = new ArrayList<Transactionfile.Batch.Trans>();
        int i = 1;

        for (WexSettleEntity settleEntity : wexDataList) {
            Transactionfile.Batch.Trans trans = new Transactionfile.Batch.Trans();
            Transactionfile.Batch.Trans.Pump pump = new Transactionfile.Batch.Trans.Pump();
            Transactionfile.Batch.Trans.Card card = new Transactionfile.Batch.Trans.Card();
            trans.setNbr(String.valueOf(i++));

            List<String> prodlist = settleEntity.getProduct();
            for (String prod : prodlist) {
                List<String> productDetail = Arrays.asList(prod.split(":"));
                Transactionfile.Batch.Trans.Product product = new Transactionfile.Batch.Trans.Product();
                product.setQuantity(productDetail.get(0));
                product.setCode(productDetail.get(1));
                product.setAmount(productDetail.get(2));
                product.setPrice(productDetail.get(3));
                trans.getProduct().add(product);
            }

            pump.setCat(settleEntity.getCatflag());
            pump.setService(settleEntity.getService());
            pump.setNbr(Integer.parseInt(settleEntity.getPumpNbr()));
            pump.setAmount(settleEntity.getPumpAmount());

            card.setValue(settleEntity.getCardTrack());// card reference not available
            card.setTrack(settleEntity.getCardTrack());

            trans.setTime(settleEntity.getTime());
            trans.setCardCode(settleEntity.getTransactiontype());//card type not available
            trans.setType(settleEntity.getTransactiontype());
            trans.setCard(card);
            trans.setOdometer(settleEntity.getOdometer());
            trans.setAmount(settleEntity.getAmount());
            trans.setAuthref(settleEntity.getAuthRef());
            trans.setDriver(settleEntity.getDriverId());
            trans.setVehicle(settleEntity.getVehicleId());
            trans.setPump(pump);
            trans.setDate(settleEntity.getTime());

            entities.add(trans);
        }
        return entities;
    }

    public Transactionfile.Batch buildBachTag(String tid, List transactionSettleData) {
        Transactionfile.Batch batch = new Transactionfile.Batch();
        String bid = makeBatchId(tid); //getBid(); //get BID Date+time
        batch.setTid(tid);
        batch.setApp("AuthREq");
        batch.setVersion("2");
        batch.setBid(Integer.parseInt(bid));
        batch.getTrans().addAll(buildTransactionTag(transactionSettleData));
        return batch;
    }

    private List buildTransactionTag(List<SettleEntity> wexDataList) {

        List<Transactionfile.Batch.Trans> entities = new ArrayList<Transactionfile.Batch.Trans>();
        int i = 1;

        for (SettleEntity settleEntity : wexDataList) {
            Transactionfile.Batch.Trans trans = new Transactionfile.Batch.Trans();
            Transactionfile.Batch.Trans.Pump pump = new Transactionfile.Batch.Trans.Pump();
            Transactionfile.Batch.Trans.Card card = new Transactionfile.Batch.Trans.Card();
            trans.setNbr(String.valueOf(i++));

//            List<String> nonfuelprodlist = settleEntity.getNonfuelproductgroup();
//            for (String prod : nonfuelprodlist) {
//                List<String> productDetail = Arrays.asList(prod.split(":"));
//                Transactionfile.Batch.Trans.Product product = new Transactionfile.Batch.Trans.Product();
//                product.setQuantity(productDetail.get(0));
//                product.setCode(productDetail.get(1));
//                product.setAmount(productDetail.get(2));
//                product.setPrice(productDetail.get(3));
//                trans.getProduct().add(product);
//            }
//
//            List<String> fuelprodlist = settleEntity.getFuelproductgroup();
//            for (String prod : fuelprodlist) {
//                Transactionfile.Batch.Trans.Product product = new Transactionfile.Batch.Trans.Product();
//                List<String> productDetail = Arrays.asList(prod.split(":"));
//                product.setQuantity(productDetail.get(0));
//                product.setCode(productDetail.get(1));
//                product.setAmount(productDetail.get(2));
//                product.setPrice(productDetail.get(3));
//                trans.getProduct().add(product);
//            }
            List<String> prodlist = settleEntity.getProductgroup();
            for (String prod : prodlist) {
                List<String> productDetail = Arrays.asList(prod.split(":"));
                Transactionfile.Batch.Trans.Product product = new Transactionfile.Batch.Trans.Product();
                product.setQuantity(productDetail.get(0));
                product.setCode(productDetail.get(1));
                product.setAmount(productDetail.get(2));
                product.setPrice(productDetail.get(3));
                trans.getProduct().add(product);
            }

            pump.setCat(settleEntity.getCatflag());
            pump.setService(settleEntity.getService());
            pump.setNbr(Integer.parseInt(settleEntity.getPumpNumber()));
            pump.setAmount(settleEntity.getPaymentAmount());

            card.setValue(settleEntity.getCardReferene());
            card.setTrack(settleEntity.getTrackdata2());

            trans.setTime(settleEntity.getTime());
            trans.setCardCode(settleEntity.getCardType());
            trans.setType(settleEntity.getTransactionType());
            trans.setCard(card);
            trans.setOdometer(settleEntity.getOdometer());
            trans.setAmount(settleEntity.getPaymentAmount());
            trans.setAuthref(settleEntity.getAuthreference());
            trans.setDriver(settleEntity.getDriverId());
            trans.setVehicle(settleEntity.getVehicleId());
            trans.setPump(pump);
            trans.setDate(settleEntity.getDate());

            entities.add(trans);
        }
        return entities;
    }

//    public void createXmlFile(String settlexmlrecord) throws
//            UnsupportedEncodingException, IOException {
//
//        log.info("Entry in createXmlFile method of WexDataGatewayBean..");
//        if (null != settlexmlrecord && !settlexmlrecord.isEmpty()) {
//
//            File file = new File("D:\\Users\\TransactionFile.txt");
//            // if file doesn't exists, then create it
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile())));
//            bw.write(settlexmlrecord);
//            bw.close();
//            log.info("Exit from createFile method of WexDataGatewayBean..");
//        }
//    }
    public String makeFileSequenceId(String fileSequenceId) {

        log.info("Entry in makeBatchId method of FirstDataGatewayBean..");
        try {
            if (fileSequenceId == null || fileSequenceId.trim().isEmpty()) {
                Calendar cal = Calendar.getInstance();
                Date currnetDate = new Date();
                cal.setTime(currnetDate);
                GregorianCalendar gc = new GregorianCalendar();
                gc.set(GregorianCalendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
                gc.set(GregorianCalendar.MONTH, cal.get(Calendar.MONTH));
                gc.set(GregorianCalendar.YEAR, cal.get(Calendar.YEAR));
                DateFormat df = new SimpleDateFormat("yy");
                String year = df.format(currnetDate);
                int JULIAN_DAY = gc.get(GregorianCalendar.DAY_OF_YEAR);
                String pacckedDay = ("000" + Integer.toString(JULIAN_DAY)).substring((Integer.toString(JULIAN_DAY)).length());
                fileSequenceId = year + pacckedDay + "001";
            } else {
                long oldFileNumber = Long.parseLong(fileSequenceId);
                oldFileNumber++;
                fileSequenceId = Long.toString(oldFileNumber);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        log.info("Exit from makeBatchId method of FirstDataGatewayBean..");
        return fileSequenceId;
    }

    private String makeBatchId(String batchId) {

        log.info("Entry in makeBatchId method of FirstDataGatewayBean..");
        try {
            if (batchId == null || batchId.trim().isEmpty()) {
                Calendar cal = Calendar.getInstance();
                Date currnetDate = new Date();
                cal.setTime(currnetDate);
                GregorianCalendar gc = new GregorianCalendar();
                gc.set(GregorianCalendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
                gc.set(GregorianCalendar.MONTH, cal.get(Calendar.MONTH));
                gc.set(GregorianCalendar.YEAR, cal.get(Calendar.YEAR));
                DateFormat df = new SimpleDateFormat("yy");
                String year = df.format(currnetDate);
                int JULIAN_DAY = gc.get(GregorianCalendar.DAY_OF_YEAR);
                String pacckedDay = ("000" + Integer.toString(JULIAN_DAY)).substring((Integer.toString(JULIAN_DAY)).length());
                batchId = year + pacckedDay + "001";
            } else {
                long oldBatch = Long.parseLong(batchId);
                oldBatch++;
                batchId = Long.toString(oldBatch);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        log.info("Exit from makeBatchId method of FirstDataGatewayBean..");
        return batchId;
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
