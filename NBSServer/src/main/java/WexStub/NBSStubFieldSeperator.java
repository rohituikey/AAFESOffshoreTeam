/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WexStub;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

/**
 *
 * @author uikuyr
 */
public class NBSStubFieldSeperator {

    String[] requestDetails = new String[52];
    ISOMsg isoMsg;
    GenericPackager packager;
    String[] response = new String[2];
    private String SCHEMA_PATH = "src/XML/NBSLogonPackager.xml";

    public String[] getResponse(byte[] request) {
        try {
            String requestString = new String(request);
            requestDetails = requestString.split("<FS>");
            response[0] = generateAcknowledgment();
            response[1] = generateResponse();
            
        } catch (ISOException ex) {
            Logger.getLogger(NBSStubFieldSeperator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    private String generateAcknowledgment() throws ISOException {
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
        if (requestDetails[17].equals("6006496628299904508=20095004100210123")) {
            isoMsg.set(2,"<SX>"+"c$"+ "<FS>");
            isoMsg.set(3, "100");
        } else if (requestDetails[17].equals("6006496628299904508=20095004100219999")) {
            isoMsg.set(2, "c?");
            isoMsg.set(3, "200");
        } else if (requestDetails[17].equals("6006496628299904508=20095004100210000")) {
            isoMsg.set(2, "c!");
            //isoMsg.set(3, "200");
        } else {
            isoMsg.set(2, "c$");
            isoMsg.set(3, "100");
        }
        byte[] byteResult = isoMsg.pack();
        String result = new String(byteResult);
        return result;
    }

    private String generateResponse() {
return null;
    }
}
