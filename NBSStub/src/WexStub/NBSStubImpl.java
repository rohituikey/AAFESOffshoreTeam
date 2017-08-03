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
        isoMsg.set(10, "AuthRequest");
        isoMsg.set(11, "465987");

        isoMsg.set(12, "01");
        isoMsg.set(13, "100");
        isoMsg.set(14, "00");
        isoMsg.set(15, "Approved");
        isoMsg.set(16, "cardType");
        isoMsg.set(17, "01");
        isoMsg.set(18, "4host");
        isoMsg.set(19, "47596587");
        isoMsg.set(20, "45874569");
        isoMsg.set(21, "78569");
        isoMsg.set(22, "7896");
        isoMsg.set(23, "5896");
        isoMsg.set(24, "75391");
        isoMsg.set(25, "789645");
        isoMsg.set(26, "79");
        isoMsg.set(27, "7896");
        isoMsg.set(28, "98563");
        isoMsg.set(29, "78965");

        byte[] byteResult = isoMsg.pack();
        responseDetails = new String(byteResult);
        return responseDetails;
    }

}
