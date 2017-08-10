package WexStub;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.impl.SimpleTraceGenerator;
import com.solab.iso8583.parse.ConfigParser;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author uikuyr
 */
public class NBSStubImpl implements NBSStub {

    private String result;
    private String responseDetails;
    ISOMsg isoMsg = new ISOMsg();
    boolean correctRequest = true;

    @Override
    public String getResponse(String request) {

        try {
            
            try {
                GenericPackager packager = new GenericPackager("src/XML/NBSLogonPackager.xml");
                
                isoMsg.setPackager(packager);
                isoMsg.unpack(request.getBytes());
                
                for (int index = 0; index < isoMsg.getMaxField(); index++) {
                    if (isoMsg.hasField(index)) {
                        System.out.println(index + " " + isoMsg.getString(index));
                    }
                }
            } catch (ISOException ex) {
                Logger.getLogger(NBSStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                correctRequest=false;
            } catch (Exception ex) {
                Logger.getLogger(NBSStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                correctRequest=false;
            }
            
            result = generateResponse();
            responseDetails = generateNBSResponse();
            return result + responseDetails;
        } catch (ISOException ex) {
            Logger.getLogger(NBSStubImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    private String generateResponse() throws ISOException {
        isoMsg = new ISOMsg();
        GenericPackager packager = new GenericPackager("src/XML/ResponseAcknowledgment.xml");
        isoMsg.setPackager(packager);
        isoMsg.setMTI("0231");
        if (correctRequest) {
            isoMsg.set(10, "c$");
            isoMsg.set(11, "100");
        } else {
            isoMsg.set(10, "c?");
            isoMsg.set(11, "200");
        }
        byte[] byteResult = isoMsg.pack();
        result = new String(byteResult);
        return result;
    }

    private String generateNBSResponse() throws ISOException {
        isoMsg = new ISOMsg();
        GenericPackager packager = new GenericPackager("src/XML/NBSResponse.xml");
        isoMsg.setPackager(packager);
        isoMsg.setMTI("0231");
        isoMsg.set(10, "A");
        isoMsg.set(11, "0278");

        isoMsg.set(12, "3170621071655");
        isoMsg.set(13, "N");
        if (correctRequest) {
            isoMsg.set(14, "00");
            isoMsg.set(15, "Approved");
        } else {
            isoMsg.set(14, "01");
            isoMsg.set(15, "Declined");
        }
        isoMsg.set(16, "WEX");
        isoMsg.set(17, "");
        isoMsg.set(18, "");
        isoMsg.set(19, "");
        isoMsg.set(20, "");
        isoMsg.set(21, "");
        isoMsg.set(22, "5");
        isoMsg.set(23, "0");
        isoMsg.set(24, "308339");
        isoMsg.set(25, "75.00");
        isoMsg.set(26, "1");
        isoMsg.set(27, "75.0000");
        isoMsg.set(28, "001");
        isoMsg.set(29, "78965");

        byte[] byteResult = isoMsg.pack();
        responseDetails = new String(byteResult);
        return responseDetails;
    }
    
    public String unpackIso8583(String req){
        byte[] buf = req.getBytes();
        try {
            MessageFactory mfact = ConfigParser.createFromClasspathConfig("NBSRequest.xml");
            String bitmapByte = javax.xml.bind.DatatypeConverter.printHexBinary(
                Arrays.copyOfRange(buf, 4, 12));
        byte[] mtid = Arrays.copyOfRange(buf, 0, 4);
        byte[] details = Arrays.copyOfRange(buf, 12, buf.length - 1);
        String rspString = bitmapByte;
        byte[] response = ArrayUtils.addAll(mtid, bitmapByte.getBytes());
        response = ArrayUtils.addAll(response, details);
        IsoMessage resp = null;
            resp = mfact.parseMessage(response, 12);
            
        } catch (IOException | ParseException ex) {
            Logger.getLogger(NBSStubImpl.class.getName()).log(Level.SEVERE, null, ex);
            correctRequest=false;
        } catch (Exception ex){
            System.out.println(ex.getMessage() +"-------------------------" + Arrays.toString(ex.getStackTrace()));
            correctRequest=false;
        }
        return generateNewResponse();
    }

    private String generateNewResponse() {
        String logonResponse = logonResponse();
        String nbsResponse = nbsResponse();
        return logonResponse+nbsResponse;
    }

    private String logonResponse() {
        try {
            MessageFactory mfact = ConfigParser.createFromClasspathConfig("NBSAcknowlegment.xml");
            mfact.setTraceNumberGenerator(new SimpleTraceGenerator((int) (System.currentTimeMillis() % 10000)));
            mfact.setAssignDate(true);

            IsoMessage isoMessage = mfact.newMessage(0x100);
        if (correctRequest) {
            isoMessage.setValue(10, "c$",IsoType.ALPHA,2);
            isoMessage.setValue(11, "100",IsoType.ALPHA,3);
        } else {
            isoMessage.setValue(10, "c?",IsoType.ALPHA,2);
            isoMessage.setValue(11, "200",IsoType.ALPHA,3);
        }
        isoMessage.setCharacterEncoding(
                    "UTF-8");
            isoMessage.setBinaryBitmap(
                    true);
            byte[] data = isoMessage.writeData();

            return new String(data);
        } catch (IOException ex) {
            Logger.getLogger(NBSStubImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String nbsResponse() {
        try {
            MessageFactory mfact = ConfigParser.createFromClasspathConfig("NBSResponse.xml");
            mfact.setTraceNumberGenerator(new SimpleTraceGenerator((int) (System.currentTimeMillis() % 10000)));
            mfact.setAssignDate(true);

            IsoMessage isoMessage = mfact.newMessage(0x100);
        isoMessage.setValue(10, "A",IsoType.ALPHA,2);
        isoMessage.setValue(11, "0278",IsoType.ALPHA,2);

        isoMessage.setValue(12, "3170621071655",IsoType.ALPHA,2);
        isoMessage.setValue(13, "N",IsoType.ALPHA,2);
        if (correctRequest) {
            isoMessage.setValue(14, "00",IsoType.ALPHA,2);
            isoMessage.setValue(15, "Approved",IsoType.ALPHA,2);
        } else {
            isoMessage.setValue(14, "01",IsoType.ALPHA,2);
            isoMessage.setValue(15, "Declined",IsoType.ALPHA,2);
        }
        isoMessage.setValue(16, "WEX",IsoType.ALPHA,2);
        isoMessage.setValue(17, "",IsoType.ALPHA,2);
        isoMessage.setValue(18, "",IsoType.ALPHA,2);
        isoMessage.setValue(19, "",IsoType.ALPHA,2);
        isoMessage.setValue(20, "",IsoType.ALPHA,2);
        isoMessage.setValue(21, "",IsoType.ALPHA,2);
        isoMessage.setValue(22, "5",IsoType.ALPHA,2);
        isoMessage.setValue(23, "0",IsoType.ALPHA,2);
        isoMessage.setValue(24, "308339",IsoType.ALPHA,2);
        isoMessage.setValue(25, "75.00",IsoType.ALPHA,2);
        isoMessage.setValue(26, "1",IsoType.ALPHA,2);
        isoMessage.setValue(27, "75.0000",IsoType.ALPHA,2);
        isoMessage.setValue(28, "001",IsoType.ALPHA,2);
        isoMessage.setValue(29, "78965",IsoType.ALPHA,2);

        
        isoMessage.setCharacterEncoding(
                    "UTF-8");
            isoMessage.setBinaryBitmap(
                    true);
            byte[] data = isoMessage.writeData();

            return new String(data);
        } catch (IOException ex) {
            Logger.getLogger(NBSStubImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
