/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import com.aafes.credit.Message;
import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.AuthorizerException;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.gateway.wex.simulator.NBSFormatterFS;
import com.aafes.stargate.util.ConstantsUtil;
import com.aafes.stargate.util.RequestType;
import com.aafes.stargate.util.SvsUtil;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author alugumetlas
 */
public class TestNBSFormat {

    String requestXMLPreAuth;
    String requestXMLFinalAuth;

    @Before
    public void setUp() {
        requestXMLPreAuth = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><cm:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:cm='http://www.aafes.com/credit' xsi:schemaLocation='http://www.aafes.com/credit file:///D:/Users/alugumetlas/Downloads/wildfly-10.1.0.Final/wildfly-10.1.0.Final/standalone/configuration/CreditMessageGSA.xsd' TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\"><cm:Header><cm:IdentityUUID>eacbc625-6fef-479e-8738-92adcfed7c65</cm:IdentityUUID><cm:LocalDateTime>2017-07-02T09:04:01</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>54163254</cm:OrderNumber>"
                + "<cm:transactionId>02464154</cm:transactionId>"
                + "<cm:termId>WE1055214503801</cm:termId></cm:Header>"
                + "<cm:Request RRN=\"TkFwxJKiaTwf\">"
                + "<cm:Media>WEX</cm:Media>  "
                + "<cm:RequestType>PreAuth</cm:RequestType>"
                + "<cm:InputType>Swiped</cm:InputType>"
                + "<cm:Pan>Pan</cm:Pan>"
                + "<cm:Account>6006496628299904508</cm:Account>"
                + "<cm:Expiration>2103</cm:Expiration>"
                + "<cm:CardVerificationValue>837</cm:CardVerificationValue>  "
                + "<cm:TrackData2>6006496628299904508=20095004100210123</cm:TrackData2>"
                + "<cm:AmountField>75.00</cm:AmountField>"
                + "<cm:WEXRequestData> "
                + "<cm:CardSeqNumber>12345</cm:CardSeqNumber>"
                + "<cm:ServiceCode>S</cm:ServiceCode> "
                + "<cm:CATFlag>1</cm:CATFlag> "
                + "<cm:PromptDetailCount>2</cm:PromptDetailCount>"
                + " <cm:DriverId>3692</cm:DriverId> "
                + "<cm:Odometer>28811</cm:Odometer>"
                + "<cm:ProdDetailCount>1</cm:ProdDetailCount> <cm:FuelProdGroup>  <cm:PricePerUnit>0.000</cm:PricePerUnit><cm:Quantity>0.000</cm:Quantity>"
                + "<cm:FuelProdCode>001</cm:FuelProdCode><cm:FuelDollarAmount>0.00</cm:FuelDollarAmount></cm:FuelProdGroup></cm:WEXRequestData>"
                + "<cm:pumpNmbr>36</cm:pumpNmbr>"
                + "<cm:DescriptionField>PreAuth</cm:DescriptionField>"
                + "<cm:origAuthCode>130362</cm:origAuthCode>"
                + "<cm:AmtPreAuthorized>75.00</cm:AmtPreAuthorized></cm:Request></cm:Message>";
        
       requestXMLFinalAuth= "<?xml version=\"1.0\" encoding=\"utf-8\" ?><cm:Message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:cm='http://www.aafes.com/credit' xsi:schemaLocation='http://www.aafes.com/credit file:///D:/Users/alugumetlas/Downloads/wildfly-10.1.0.Final/wildfly-10.1.0.Final/standalone/configuration/CreditMessageGSA.xsd' TypeCode=\"Request\" MajorVersion=\"3\" MinorVersion=\"1\" FixVersion=\"0\"><cm:Header><cm:IdentityUUID>eacbc625-6fef-479e-8738-92adcfed7c65</cm:IdentityUUID><cm:LocalDateTime>2017-07-02T09:04:01</cm:LocalDateTime><cm:SettleIndicator>true</cm:SettleIndicator><cm:OrderNumber>54163254</cm:OrderNumber>"
                + "<cm:transactionId>00724154</cm:transactionId>"
                + "<cm:termId>WE1055366612301</cm:termId></cm:Header>"
                + "<cm:Request RRN=\"TkFwxJKiaTwf\">"
                + "<cm:Media>WEX</cm:Media>  "
                + "<cm:RequestType>FinalAuth</cm:RequestType>"
                + "<cm:InputType>Swiped</cm:InputType>"
                + "<cm:Pan>Pan</cm:Pan>"
                + "<cm:Account>6006496628299904508</cm:Account>"
                + "<cm:Expiration>2103</cm:Expiration>"
                + "<cm:CardVerificationValue>837</cm:CardVerificationValue>  "
                + "<cm:TrackData2>6006496628299904508=20095004100210123</cm:TrackData2>"
                + "<cm:AmountField>13.40</cm:AmountField>"
                + "<cm:WEXRequestData> "
                + "<cm:CardSeqNumber>12345</cm:CardSeqNumber>"
                + "<cm:ServiceCode>S</cm:ServiceCode> "
                + "<cm:CATFlag>1</cm:CATFlag> "
                + "<cm:PromptDetailCount>2</cm:PromptDetailCount>"
                + " <cm:DriverId>3692</cm:DriverId> "
                + "<cm:Odometer>28811</cm:Odometer>"
                + "<cm:ProdDetailCount>1</cm:ProdDetailCount> <cm:FuelProdGroup>  <cm:PricePerUnit>0.000</cm:PricePerUnit><cm:Quantity>0.000</cm:Quantity>"
                + "<cm:FuelProdCode>001</cm:FuelProdCode><cm:FuelDollarAmount>0.00</cm:FuelDollarAmount></cm:FuelProdGroup></cm:WEXRequestData>"
                + "<cm:pumpNmbr>23</cm:pumpNmbr>"
                + "<cm:DescriptionField>PreAuth</cm:DescriptionField>"
                + "<cm:origAuthCode>130362</cm:origAuthCode>"
                + "<cm:AmtPreAuthorized>75.00</cm:AmtPreAuthorized></cm:Request></cm:Message>";
       
       //<SX>WE1055366612301
//<FS>AUTHREQ
//<FS>0001
//<FS>06001
//<FS>C
//<FS>0072
//<FS>10
//<FS>WI
//<FS>1
//<FS>23
//<FS>S
//<FS>13.40//amount
//<FS>75.00//catAmount
//<FS>0072//transanumber tid(0,4)
//<FS>170726000237//date and Time
//<FS>2//track
//<FS>6900460XXXXXXXX4262=1811*************//track 2
//<FS>0230072//recieptNumber
//<FS>658340//authref
//<FS>2//
//<FS>3
//<FS>430620
//<FS>4
//<FS>35046
//<FS>1
//<FS>1.970
//<FS>6.803
//<FS>1
//<FS>13.40<EX><LF>
    }

    @Test
    public void testNbsRequestFormat_PreAuth() {
        Transaction transaction = new Transaction();
        Message creditMessage = this.unmarshalCreditMessage(requestXMLPreAuth);
        transaction = mapRequest(transaction, creditMessage);
        NBSFormatterFS nBSFormatterFS = new NBSFormatterFS();
        String request = nBSFormatterFS.createPreAuthRequestForNBS(transaction);
        String req = "<SX>WE1055214503801<FS>AUTHREQ<FS>0002<FS>"+createDateFormat()+"<FS>A<FS>0246<FS>08<FS>WI<FS>1<FS>36<FS>S<FS>2<FS>6006496628299904508=20095004100210123<FS>75.00<FS>2<FS>3<FS>3692<FS>4<FS>28811<FS>1<FS>0.000<FS>0.000<FS>001<FS>0.00<EX><LF>";
        Assert.assertEquals(request ,req);
    }
    @Test
    public void testNbsRequestFormat_FinalAuth() {
        Transaction transaction = new Transaction();
        Message creditMessage = this.unmarshalCreditMessage(requestXMLPreAuth);
        transaction = mapRequest(transaction, creditMessage);
        NBSFormatterFS nBSFormatterFS = new NBSFormatterFS();
        String request = nBSFormatterFS.createFinalRequestForNbs(transaction);
        String req = "<SX>WE1055366612301<FS>AUTHREQ<FS>0001<FS>06001<FS>C<FS>0072<FS>10<FS>WI<FS>1<FS>23<FS>S<FS>13.40<FS>75.00<FS>0072<FS>170726000237<FS>2<FS>6900460XXXXXXXX4262=1811*************<FS>0230072<FS>658340<FS>2<FS>3<FS>430620<FS>4<FS>35046<FS>1<FS>1.970<FS>6.803<FS>1<FS>13.40<EX><LF>";
        System.out.println(req);
        System.out.println(request);
        if(req == request)
            System.out.println("sucess");
        Assert.assertEquals(request ,req);
//<SX>WE1055366612301
//<FS>AUTHREQ
//<FS>0001
//<FS>06001
//<FS>C
//<FS>0072
//<FS>10
//<FS>WI
//<FS>1
//<FS>23
//<FS>S
//<FS>13.40
//<FS>75.00
//<FS>0072
//<FS>170726000237
//<FS>2
//<FS>6900460XXXXXXXX4262=1811*************
//<FS>0230072
//<FS>658340
//<FS>2
//<FS>3
//<FS>430620
//<FS>4
//<FS>35046
//<FS>1
//<FS>1.970
//<FS>6.803
//<FS>1
//<FS>13.40<EX><LF>
    }


    private Message unmarshalCreditMessage(String content) {
        Message request = new Message();
        try {
            StringReader reader = new StringReader(content);
            JAXBContext jc = JAXBContext.newInstance(Message.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            JAXBContext jaxbContext = JAXBContext.newInstance(Message.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            request = (Message) jaxbUnmarshaller.unmarshal(reader);
        } catch (JAXBException ex) {
            System.out.println(ex.toString());
        }
        return request;
    }

    private Transaction mapRequest(Transaction transaction, Message requestMessage) {
        String[] decimalPart;
        transaction.setRequestXmlDateTime(SvsUtil.formatLocalDateTime());

        Message.Header header = requestMessage.getHeader();

        // Mapping Header Fields
        if (header.getIdentityUUID() != null) {
            transaction.setIdentityUuid(header.getIdentityUUID());
        }
        transaction.setLocalDateTime(SvsUtil.formatLocalDateTime());
        boolean settleIndicator = header.isSettleIndicator();
        if (settleIndicator) {
            transaction.setSettleIndicator("true");
        } else {
            transaction.setSettleIndicator("false");
        }
        transaction.setOrderNumber(header.getOrderNumber());
        transaction.setTransactionId(header.getTransactionId());
        if (header.getTermId() != null) {
            transaction.setTermId(header.getTermId());
        }
        transaction.setComment(header.getComment());
        transaction.setCustomerId(header.getCustomerID());

        // Mapping Request Fields
        if (requestMessage.getRequest() != null && requestMessage.getRequest().size() > 1) {
            throw new AuthorizerException("MULTIPLE_REQUESTS");
        }
        Message.Request request = requestMessage.getRequest().get(0);
        transaction.setRrn(request.getRRN());
        transaction.setMedia(request.getMedia());
        if (request.getRequestType() != null && !request.getRequestType().value().isEmpty()) {
            transaction.setRequestType(request.getRequestType().value());
        }
        if (request.getReversal() != null && !request.getReversal().value().isEmpty()) {
            transaction.setReversal(request.getReversal().value());
        }
        if (request.getVoid() != null && !request.getVoid().value().isEmpty()) {

            transaction.setVoidFlag(request.getVoid().value());
        }

        transaction.setAccount(request.getAccount());
        if (request.getPan() != null) {
            if (request.getPan().value().equalsIgnoreCase("PAN")) {
                transaction.setAccountTypeType(request.getPan().value());
                transaction.setPan(request.getPan().value());
            } else {
                throw new AuthorizerException("INVALID_PAN_TAG");
            }

        }

        if (request.getToken() != null) {
            transaction.setTokenId(request.getToken().value());
            if (request.getToken().value().equalsIgnoreCase("TOKEN")) {
                transaction.setAccountTypeType(request.getToken().value());
                transaction.setTokenId(request.getAccount());
            } else {
                throw new AuthorizerException("INVALID_TOKEN_TAG");
            }
        }
        if (request.getEncryptedPayload() != null) {
            transaction.setEncryptedPayLoad(request.getEncryptedPayload().value());
        }
        transaction.setCvv(request.getCardVerificationValue());
        transaction.setKsn(request.getKSN());
        transaction.setPinBlock(request.getPinBlock());
        if (request.getExpiration() != null) {
            //TODO : check for valid expiration date
            String exp = request.getExpiration().toString();
            if (exp != null && exp.length() == 4) {
                String month = exp.substring(2, 4);
                if (Integer.parseInt(month) > 12 || Integer.parseInt(month) < 1) {
                    throw new AuthorizerException("INVALID_EXPIRATION_DATE");
                }
            } else {
                throw new AuthorizerException("INVALID_EXPIRATION_DATE");
            }
            transaction.setExpiration(request.getExpiration().toString());
        }
        //TODO : check amount handling in MPG
        try {
            BigDecimal amt;
            amt = request.getAmountField();
            if (amt != null) {
                amt = amt.movePointRight(2);
                if (amt.longValueExact() <= 9999999) {
                    if (transaction.getRequestType() != null
                            && !transaction.getRequestType().trim().isEmpty()
                            && !transaction.getRequestType().equalsIgnoreCase(RequestType.REFUND)) {
                        if (amt.longValueExact() < 0) {
                            throw new AuthorizerException("INVALID_AMOUNT");
                        }
                    }
                    transaction.setAmount(amt.longValueExact());
                } else {
                    throw new AuthorizerException("INVALID_AMOUNT");
                }
            }
        } catch (ArithmeticException e) {
            throw new AuthorizerException("INVALID_AMOUNT");
        }
        transaction.setGcpin(request.getGCpin());
        transaction.setInputType(request.getInputType());
        transaction.setDescriptionField(request.getDescriptionField());
        transaction.setTrack1(request.getTrackData1());
        transaction.setTrack2(request.getTrackData2());
        transaction.setEncryptTrack(request.getEncryptTrack());
        if (request.getPlanNumbers() != null
                && request.getPlanNumbers().getPlanNumber() != null
                && request.getPlanNumbers().getPlanNumber().get(0) != null) {
            transaction.setPlanNumber(request.getPlanNumbers().getPlanNumber().get(0).toString());
        }

        if (request.getPumpNmbr() != null) {
            transaction.setPumpNmbr(request.getPumpNmbr().toString());
        }
        if (request.getWEXRequestData() != null) {
            Message.Request.WEXRequestData wexReqPayAtPump = request.getWEXRequestData();
            if (transaction.getDriverId() != null) {
                transaction.setDriverId(wexReqPayAtPump.getDriverId());
            }
            
            if(wexReqPayAtPump.getCATFlag() != null)
            {
              transaction.setCatFlag(wexReqPayAtPump.getCATFlag().get(0));
            }
            /* NEW FIELDS ADDED IN CLASS AFTER MODIFICATIONS IN CreditMessageGSA.XSD - start */
            if (wexReqPayAtPump.getRestrictCode() != null) {
                transaction.setRestrictCode(wexReqPayAtPump.getRestrictCode());
            }
            if (wexReqPayAtPump.getFuelProdGroup() != null && wexReqPayAtPump.getFuelProdGroup().size() > 0) {
                StringBuilder prodCodeDetailsStr = null;
                List<String> ProdDataList = new ArrayList<>();
                List<Message.Request.WEXRequestData.FuelProdGroup> list = wexReqPayAtPump.getFuelProdGroup();
                //list.size()>2 throws exception
                for (Message.Request.WEXRequestData.FuelProdGroup tmp : list) {
                    prodCodeDetailsStr = new StringBuilder();
                    prodCodeDetailsStr.append(tmp.getFuelProdCode());
                    prodCodeDetailsStr.append(":");
                    prodCodeDetailsStr.append(tmp.getQuantity());
                    prodCodeDetailsStr.append(":");
                    prodCodeDetailsStr.append(tmp.getPricePerUnit());
                    prodCodeDetailsStr.append(":");
                    prodCodeDetailsStr.append(tmp.getFuelDollarAmount());
                    ProdDataList.add(prodCodeDetailsStr.toString());
                    prodCodeDetailsStr = null;
                }
                List<Message.Request.WEXRequestData.NonFuelProductGroup> nList = wexReqPayAtPump.getNonFuelProductGroup();
                //  //list.size()>4 throws exception
                for (Message.Request.WEXRequestData.NonFuelProductGroup tmp : nList) {
                    prodCodeDetailsStr = new StringBuilder();
                    prodCodeDetailsStr.append(tmp.getNonFuelProdCode());
                    prodCodeDetailsStr.append(":");
                    prodCodeDetailsStr.append(tmp.getNonFuelQty());
                    prodCodeDetailsStr.append(":");
                    prodCodeDetailsStr.append(tmp.getNonFuelPricePerUnit());
                    prodCodeDetailsStr.append(":");
                    prodCodeDetailsStr.append(tmp.getNonFuelAmount());
                    ProdDataList.add(prodCodeDetailsStr.toString());
                    prodCodeDetailsStr = null;
                }
                transaction.setProducts(ProdDataList);
                ProdDataList = null;
            }

            if (wexReqPayAtPump.getVehicleId() != null) {
                transaction.setVehicleId(wexReqPayAtPump.getVehicleId());
            }
            if (wexReqPayAtPump.getLicenseNumber() != null) {
                transaction.setLicenceNumber(wexReqPayAtPump.getLicenseNumber());
            }
            if (wexReqPayAtPump.getDeptNumber() != null) {
                transaction.setDeptNumber(wexReqPayAtPump.getDeptNumber().toString());
            }
            transaction.setJobValueNumber(wexReqPayAtPump.getJobValueNumber());
            transaction.setDataNumber(wexReqPayAtPump.getDataNumber());
            transaction.setUserId(wexReqPayAtPump.getUserId());
            if (wexReqPayAtPump.getProdDetailCount() != null) {
                transaction.setProdDetailCount(wexReqPayAtPump.getProdDetailCount().toString());
            }

            if (wexReqPayAtPump.getServiceCode() != null && wexReqPayAtPump.getServiceCode().size() > 0) {
                transaction.setServiceCode(wexReqPayAtPump.getServiceCode().get(0));
            }

            if (wexReqPayAtPump.getOdometer() != null) {
                transaction.setOdoMeter(wexReqPayAtPump.getOdometer());
            }

            if (wexReqPayAtPump.getCardSeqNumber() != null) {
                transaction.setCardSeqNumber(wexReqPayAtPump.getCardSeqNumber());
            }
            transaction.setPromptDetailCount(wexReqPayAtPump.getPromptDetailCount());
        }
        //*Uncommented from 502 to 551 and modified some code

        transaction.setZipCode(request.getZipCode());
        transaction.setUpc(request.getUPC());
        transaction.setEncryptMgmt(request.getEncryptMgmt());
        transaction.setEncryptAlgo(request.getEncryptAlgorithm());
        transaction.setSettleRq(request.getSettleRq());
        transaction.setOriginalOrder(request.getOriginalOrder());
        transaction.setOrigTransId(request.getOrigTransId());
        transaction.setOrigAuthCode(request.getOrigAuthCode());
        BigDecimal amtPreAuth;
        amtPreAuth = request.getAmtPreAuthorized();
        if (amtPreAuth != null) {
            amtPreAuth = amtPreAuth.movePointRight(2);
            long n = amtPreAuth.longValueExact();
            transaction.setAmtPreAuthorized(n);
        }
        transaction.setPaymentType(request.getPymntType());
        // Adding origininal rrn, ordernuber etc
        if (request.getOriginalOrder() != null && !request.getOriginalOrder().isEmpty()) {

            transaction.setOriginalOrder(request.getOriginalOrder());
        }

        if (request.getOrigRRN() != null && !request.getOrigRRN().isEmpty()) {
            transaction.setOrigRRN(request.getOrigRRN().get(0));
        }

        if (request.getOrigTransId() != null && !request.getOrigTransId().isEmpty()) {

            transaction.setOrigTransId(request.getOrigTransId());
        }

        if (request.getOrigAuthCode() != null && !request.getOrigAuthCode().isEmpty()) {

            transaction.setOrigAuthCode(request.getOrigAuthCode());
        }
        return transaction;
    }
       private String createDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat(ConstantsUtil.DATEFORMAT);
        Date date = new Date();
        String ts = dateFormat.format(date);
        //2017-08-08 08:39:30.967
        ts = ts.substring(11, 13) + ts.substring(14, 16) + "1";
        return ts;
    }
}
//                "<SX>WE1055214503801<FS>"//termID
//                + "AUTHREQ<FS>"//app name
//                + "0001<FS>"// appversion
//                + "06001<FS>"//timezone
//                + "A<FS>"//sessiontype
//                + "0246<FS>"//key
//                + "08<FS>"//transCode /transtype
//                + "WI<FS>"//cardtype
//                + "1<FS>"//catflag
//                + "36<FS>"//pumpregno
//                + "S<FS>"//serviceType
//                + "2<FS>"//Traac2
//                + "6006496628299904508=20095004100210123<FS>"//track2//accinfo
//                + "75.00<FS>"//amountField
//                + "2<FS>"//promptcount
//                + "3<FS>"//promptID/driverID
//                + "3692<FS>"//promptValue
//                + "4<FS>"//promptID//ODOMETER
//                + "28811<FS>"//promptValue
//                + "1<FS>"//productdetailcount
//                + "0.000<FS>"//price
//                + "0.000<FS>"//quantity
//                + "001<FS>"//prodCode
//                + "0.00<EX><LF>";//fuelDolleramount
//  