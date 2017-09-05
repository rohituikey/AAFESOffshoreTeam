package WexStub;

import java.util.logging.Level;
import java.util.logging.Logger;
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

    private String[] result = new String[2];
    private String responseDetails;
    ISOMsg isoMsg = new ISOMsg();
    boolean correctRequest = true;
    GenericPackager packager;
    private String SCHEMA_PATH = "src/XML/NBSLogonPackager.xml";

    @Override
    public String[] getResponse(byte[] request) {
        try {
            try {
                try {
                    packager = new GenericPackager(SCHEMA_PATH);
                } catch (Exception e) {
                    SCHEMA_PATH = System.getProperty("jboss.server.config.dir") + "/NBSLogonPackager.xml";
                    packager = new GenericPackager(SCHEMA_PATH);
                }
                isoMsg.setPackager(packager);
                isoMsg.unpack(request);

                for (int index = 0; index < isoMsg.getMaxField(); index++) {
                    if (isoMsg.hasField(index)) {
                        System.out.println(index + " " + isoMsg.getString(index));
                    }
                }
            } catch (ISOException ex) {
                Logger.getLogger(NBSStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                checkIfLogOff(request);
                correctRequest = false;
            } catch (Exception ex) {
                Logger.getLogger(NBSStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                correctRequest = false;
            }

            result[0] = generateResponse();
            result[1] = generateNBSResponse();
            return result;
        } catch (ISOException ex) {
            Logger.getLogger(NBSStubImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private String generateResponse() throws ISOException {
        isoMsg = new ISOMsg();
        SCHEMA_PATH = "src/XML/ResponseAcknowledgment.xml";
        try {
            packager = new GenericPackager(SCHEMA_PATH);
        } catch (Exception e) {
            SCHEMA_PATH = System.getProperty("jboss.server.config.dir") + "/ResponseAcknowledgment.xml";
            packager = new GenericPackager(SCHEMA_PATH);
        }

        isoMsg.setPackager(packager);
        isoMsg.setMTI("0200");
        if (correctRequest) {
            isoMsg.set(2, "c$");
            isoMsg.set(3, "100");
        } else {
            isoMsg.set(2, "c?");
            isoMsg.set(3, "200");
        }
        byte[] byteResult = isoMsg.pack();
        result[0] = new String(byteResult);
        return result[0];
    }

    public String generateNBSResponse() throws ISOException {
        isoMsg = new ISOMsg();
        SCHEMA_PATH = "src/XML/NBSResponse.xml";
        try {
            packager = new GenericPackager(SCHEMA_PATH);
        } catch (Exception e) {
            SCHEMA_PATH = System.getProperty("jboss.server.config.dir") + "/NBSResponse.xml";
            packager = new GenericPackager(SCHEMA_PATH);
        }
        isoMsg.setPackager(packager);
        isoMsg.setMTI("0200");
        isoMsg.set(2, "A");
        isoMsg.set(3, "0278");

        isoMsg.set(4, "3170621071655");
        isoMsg.set(5, "N");
        if (correctRequest) {
            isoMsg.set(6, "00");
            isoMsg.set(7, "Approved");
        } else {
            isoMsg.set(6, "01");
            isoMsg.set(7, "Declined");
        }
        isoMsg.set(8, "WEX");
        isoMsg.set(9, "");
        isoMsg.set(10, "");
        isoMsg.set(11, "");
        isoMsg.set(12, "");
        isoMsg.set(13, "");
        isoMsg.set(14, "5");
        isoMsg.set(15, "0");
        isoMsg.set(16, "308339");
        isoMsg.set(17, "75.00");
        isoMsg.set(18, "1");
        isoMsg.set(19, "75.0000");
        isoMsg.set(20, "001");
        isoMsg.set(21, "78965");

        byte[] byteResult = isoMsg.pack();
        responseDetails = new String(byteResult);
        return responseDetails;
    }

//    public String unpackIso8583(String req) {
//        byte[] buf = req.getBytes();
//        try {
//            MessageFactory mfact;
//            SCHEMA_PATH = "src/XML/NBSConfig.xml";
//            try {
//                mfact = ConfigParser.createFromClasspathConfig(SCHEMA_PATH);
//            } catch (IOException e) {
//                SCHEMA_PATH = System.getProperty("jboss.server.config.dir") + "/NBSConfig.xml";
//                mfact = ConfigParser.createFromClasspathConfig(SCHEMA_PATH);
//            } catch (Exception e) {
//                SCHEMA_PATH = System.getProperty("jboss.server.config.dir") + "/NBSConfig.xml";
//                mfact = ConfigParser.createFromClasspathConfig(SCHEMA_PATH);
//            }
//            String bitmapByte = javax.xml.bind.DatatypeConverter.printHexBinary(Arrays.copyOfRange(buf, 4, 12));
//            byte[] mtid = Arrays.copyOfRange(buf, 0, 4);
//            byte[] details = Arrays.copyOfRange(buf, 4, buf.length - 1);
//            String rspString = bitmapByte;
//            byte[] response = ArrayUtils.addAll(mtid, bitmapByte.getBytes());
//            response = ArrayUtils.addAll(response, details);
//            IsoMessage resp = null;
//            resp = mfact.parseMessage(response, 0);
//
//        } catch (IOException | ParseException ex) {
//            Logger.getLogger(NBSStubImpl.class.getName()).log(Level.SEVERE, null, ex);
//            correctRequest = false;
//        } catch (Exception ex) {
//            System.out.println(ex.getMessage() + "-------------------------" + Arrays.toString(ex.getStackTrace()));
//            correctRequest = false;
//        }
//        return generateNewResponse();
//    }
//
//    private String generateNewResponse() {
//        String logonResponse = logonResponse();
//        String nbsResponse = nbsResponse();
//        return logonResponse + nbsResponse;
//    }
//
//    private String logonResponse() {
//        try {
//            MessageFactory mfact;
//            SCHEMA_PATH = "src/XML/NBSAcknowlegment.xml";
//            try {
//                mfact = ConfigParser.createFromClasspathConfig(SCHEMA_PATH);;
//            } catch (Exception e) {
//                SCHEMA_PATH = System.getProperty("jboss.server.config.dir") + "/NBSAcknowlegment.xml";
//                mfact = ConfigParser.createFromClasspathConfig(SCHEMA_PATH);
//            }
//            mfact.setTraceNumberGenerator(new SimpleTraceGenerator((int) (System.currentTimeMillis() % 10000)));
//            mfact.setAssignDate(true);
//
//            IsoMessage isoMessage = mfact.newMessage(0x100);
//            if (correctRequest) {
//                isoMessage.setValue(10, "c$", IsoType.ALPHA, 2);
//                isoMessage.setValue(11, "100", IsoType.ALPHA, 3);
//            } else {
//                isoMessage.setValue(10, "c?", IsoType.ALPHA, 2);
//                isoMessage.setValue(11, "200", IsoType.ALPHA, 3);
//            }
//            isoMessage.setCharacterEncoding(
//                    "UTF-8");
//            isoMessage.setBinaryBitmap(
//                    true);
//            byte[] data = isoMessage.writeData();
//
//            return new String(data);
//        } catch (IOException ex) {
//            Logger.getLogger(NBSStubImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
//
//    private String nbsResponse() {
//        try {
//            MessageFactory mfact = ConfigParser.createFromClasspathConfig("NBSResponse.xml");
//            mfact.setTraceNumberGenerator(new SimpleTraceGenerator((int) (System.currentTimeMillis() % 10000)));
//            mfact.setAssignDate(true);
//
//            IsoMessage isoMessage = mfact.newMessage(0x100);
//            isoMessage.setValue(10, "A", IsoType.ALPHA, 25);
//            isoMessage.setValue(11, "0278", IsoType.NUMERIC, 4);
//
//            isoMessage.setValue(12, "3170621071655", IsoType.ALPHA, 13);
//            isoMessage.setValue(13, "N", IsoType.ALPHA, 1);
//            if (correctRequest) {
//                isoMessage.setValue(14, "00", IsoType.ALPHA, 2);
//                isoMessage.setValue(15, "Approved", IsoType.ALPHA, 32);
//            } else {
//                isoMessage.setValue(14, "01", IsoType.ALPHA, 2);
//                isoMessage.setValue(15, "Declined", IsoType.ALPHA, 32);
//            }
//            isoMessage.setValue(16, "WEX", IsoType.ALPHA, 4);
//            isoMessage.setValue(17, "", IsoType.ALPHA, 6);
//            isoMessage.setValue(18, "", IsoType.ALPHA, 7);
//            isoMessage.setValue(19, "", IsoType.ALPHA, 4);
//            isoMessage.setValue(20, "", IsoType.ALPHA, 4);
//            isoMessage.setValue(21, "", IsoType.ALPHA, 4);
//            isoMessage.setValue(22, "5", IsoType.ALPHA, 2);
//            isoMessage.setValue(23, "0", IsoType.ALPHA, 1);
//            isoMessage.setValue(24, "308339", IsoType.ALPHA, 6);
//            isoMessage.setValue(25, "75.00", IsoType.AMOUNT, 10);
//            isoMessage.setValue(26, "1", IsoType.ALPHA, 2);
//            isoMessage.setValue(27, "75.0000", IsoType.ALPHA, 10);
//            isoMessage.setValue(28, "001", IsoType.ALPHA, 3);
//            isoMessage.setValue(29, "78965", IsoType.ALPHA, 7);
//
//            isoMessage.setCharacterEncoding(
//                    "UTF-8");
//            isoMessage.setBinaryBitmap(
//                    true);
//            byte[] data = isoMessage.writeData();
//
//            return new String(data);
//        } catch (IOException ex) {
//            Logger.getLogger(NBSStubImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
    private void checkIfLogOff(byte[] request) throws ISOException {
        try {
            packager = new GenericPackager("src/XML/NBSLogOff.xml");
        } catch (Exception e) {
            SCHEMA_PATH = System.getProperty("jboss.server.config.dir") + "/NBSLogOff.xml";
            packager = new GenericPackager(SCHEMA_PATH);
        }
        NBSServer.logOff = true;
    }

}
