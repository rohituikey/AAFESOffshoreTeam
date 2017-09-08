/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WexStub;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jpos.iso.ISOException;

/**
 *
 * @author uikuyr
 */
public class NBSStubFieldSeperator {

    String[] requestDetails = new String[52];
    String[] response = new String[2];

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
        if (requestDetails[17].equals("6006496628299904508=20095004100210123")) {
            return "<SX>c$<FS>100<EX>";
        } else if (requestDetails[17].equals("6006496628299904508=20095004100219999")) {
            return "<SX>c?<FS>200<EX>";
        } else if (requestDetails[17].equals("6006496628299904508=20095004100210000")) {
            return "<SX>c!<FS><EX>";
            //isoMsg.set(2, "c!");
            //isoMsg.set(3, "200");
        } else {
            return "<SX>c$<FS>100<EX>";
        }
    }

    private String generateResponse() {
        if (requestDetails[17].equals("6006496628299904508=20095004100210123")) {
            return "<SX>A<FS>0278<FS>3170621071655<FS>N<FS>00<FS>APPROVED<FS>WEX<FS><FS><FS><FS><FS><FS>5<FS>0<FS>308339<FS>75.00<FS>1<FS>75.0000<FS>001<EX>";
        } else if (requestDetails[17].equals("6006496628299904508=20095004100219999")) {
            return "<SX>A<FS>0278<FS>3170621071655<FS>N<FS>01<FS>CANCELED<FS>WEX<FS><FS><FS><FS><FS><FS>5<FS>0<FS>308339<FS>75.00<FS>1<FS>75.0000<FS>001<EX>";
        } else if (requestDetails[17].equals("6006496628299904508=20095004100210000")) {
            return "<SX>A<FS>0278<FS>3170621071655<FS>N<FS>01<FS>REJECTED<FS>WEX<FS><FS><FS><FS><FS><FS>5<FS>0<FS>308339<FS>75.00<FS>1<FS>75.0000<FS>001<EX>";
        } else {
            return "<SX>A<FS>0278<FS>3170621071655<FS>N<FS>00<FS>APPROVED<FS>WEX<FS><FS><FS><FS><FS><FS>5<FS>0<FS>308339<FS>75.00<FS>1<FS>75.0000<FS>001<EX>";
        }
    }
}
