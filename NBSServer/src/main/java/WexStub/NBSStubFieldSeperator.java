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

    String aknowApprove = "<SX>c$<FS>100<EX>";
    String aknowDecline = "<SX>c?<FS>200<EX>";
    String aknowCancel = "<SX>c!<FS><EX>";

    String resApprove = "<SX>A<FS>0278<FS>3170621071655<FS>N<FS>00<FS>APPROVED<FS>WEX<FS><FS><FS><FS><FS><FS>5<FS>0<FS>308339<FS>75.00<FS>1<FS>75.0000<FS>001<EX>";
    String finalAuthApprove = "<SX>C<FS>0353<FS>3170621060228<FS>N<FS>00<FS>APPROVED<EX>\"";
    String finalAuthDeclined = "<SX>C<FS>0353<FS>3170621060228<FS>N<FS>01<FS>DECLINED<EX>\"";
    String resDecline = "<SX>A<FS>0278<FS>3170621071655<FS>N<FS>01<FS>DECLINED<FS>WEX<FS><FS><FS><FS><FS><FS>5<FS>0<FS>308339<FS>75.00<FS>1<FS>75.0000<FS>001<EX>";

    String[] requestDetails = new String[52];
    String[] response = new String[2];

    public String[] createResponse(byte[] request) {
        try {
            String requestString = new String(request);
            if (requestString.contains("<!1C>") || requestString.contains("<!02>")) {
                requestString = requestString.replaceAll("<!1C>", "<FS>");
                requestString = requestString.replaceAll("<!02>", "<SX>");
                requestString = requestString.replaceAll("<!03>", "<EX><LF>");
            }
            requestDetails = requestString.split("<FS>");
            if (requestString.contains("c$") || requestString.contains("c?") || requestString.contains("c!") || requestString.contains("a$") || requestString.contains("a?") || requestString.contains("a!")) {
                response[0] = "";
                response[1] = "";
                return response;
            }
            if (requestDetails[0].contains("<SX>O<EX><LF>")) {
                NBSServer.logOff = true;
                return response;
            }
            response[0] = getAcknowledgment();
            response[1] = getResponse();

        } catch (ISOException ex) {
            Logger.getLogger(NBSStubFieldSeperator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    private String getAcknowledgment() throws ISOException {
        if (requestDetails[12].equals("6006496628299904508=20095004100210123")) {
            return aknowApprove;
        } else if (requestDetails[12].equals("6006496628299904508=20095004100219999")) {
            return aknowCancel;
        } else if (requestDetails[12].equals("6006496628299904508=20095004100210000")) {
            return aknowCancel;
            //isoMsg.set(2, "c!");
            //isoMsg.set(3, "200");
        } else {
            return "<SX>c$<FS>100<EX>";
        }
    }

    private String getResponse() {
        if (requestDetails[12].equals("6006496628299904508=20095004100210123")) {
            if (requestDetails[6].contains("08")) {
                return resApprove;
            } else {
                return finalAuthApprove;
            }
        } else if (requestDetails[12].equals("6006496628299904508=20095004100210124")) {
            if (requestDetails[6].contains("08")) {
                return resDecline;
            } else {
                return finalAuthDeclined;
            }
        }
        return resApprove;
    }
}
