package com.aafes.stargate.gateway.wex.simulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uikuyr
 */
public class NBSConnector {

    private static final Logger LOG = Logger.getLogger(NBSConnector.class.getName());
    private StringBuffer response;
    byte[] isoFormat;
    private Socket nbsSocket;

    public String sendRequest(String request) {
        String strResponse = "";
        try {

            //creating connections
            if(null==nbsSocket || nbsSocket.isClosed()){
            nbsSocket = new Socket("localhost", 2000);
            LOG.info("Connection Established with NBS socket server");
            LOG.log(Level.INFO, "{0}-----------{1}---------------{2}", new Object[]{nbsSocket.getClass(), nbsSocket.getLocalAddress(), nbsSocket.getLocalPort()});
            } 

            BufferedWriter writeRequest
                    = new BufferedWriter(new OutputStreamWriter(nbsSocket.getOutputStream()));

            writeRequest.write(request + "\n");
            writeRequest.flush();

            getResponse();
            nbsSocket.close();
//            isoFormat = response.toString().getBytes();
//            return isoFormat;
        if(response != null) strResponse = response.toString();
        } catch (IOException ex) {
            Logger.getLogger(NBSConnector.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("nbsclient.NBSClient.main()" + ex);
        }
        return strResponse;
    }
    
    public void getResponse() throws IOException{
        String responsePerLine = "";
        BufferedReader readResponse = new BufferedReader(new InputStreamReader(nbsSocket.getInputStream()));

        while ((responsePerLine = readResponse.readLine()) != null) {
            if(response == null) response = new StringBuffer();
            response.append(responsePerLine);
            LOG.log(Level.INFO, "Response recieved as {0}", response);
        }
    }
    
}
