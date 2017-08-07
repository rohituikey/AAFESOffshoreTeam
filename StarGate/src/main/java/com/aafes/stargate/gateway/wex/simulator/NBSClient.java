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
public class NBSClient {

    private static final Logger LOG = Logger.getLogger(NBSClient.class.getName());
    private StringBuffer response;
    byte[] isoFormat;
private Socket nbsSocket;

    public byte[] generateResponse(String request) {

        String responsePerLine;
        try {

            //creating connections
            if(null==nbsSocket || nbsSocket.isClosed()){
            nbsSocket = new Socket("localhost", 2000);
            LOG.info("Connection Established with NBS socket server");
            LOG.info(nbsSocket.getClass() + "-----------" + nbsSocket.getLocalAddress() + "---------------" + nbsSocket.getLocalPort());
            } 

            BufferedWriter writeRequest
                    = new BufferedWriter(new OutputStreamWriter(nbsSocket.getOutputStream()));

            writeRequest.write(request + "\n");
            writeRequest.flush();

            BufferedReader readResponse
                    = new BufferedReader(new InputStreamReader(nbsSocket.getInputStream()));

            while ((responsePerLine = readResponse.readLine()) != null) {
                if(response == null) response = new StringBuffer();
                response.append(responsePerLine);
                LOG.log(Level.INFO, "Response recieved as {0}", response);
            }
            nbsSocket.close();
            isoFormat = response.toString().getBytes();
            return isoFormat;
        } catch (IOException ex) {
            Logger.getLogger(NBSClient.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("nbsclient.NBSClient.main()" + ex);
        }
        return isoFormat;
    }
}
