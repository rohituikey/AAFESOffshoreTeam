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

    public String generateResponse(String request) {

        String responsePerLine;
        try {

            //creating connections
            Socket NBSSocket = new Socket("localhost", 2000);
            LOG.info("Connection Established with NBS socket server");
            LOG.info(NBSSocket.getClass() + "-----------" + NBSSocket.getLocalAddress() + "---------------" + NBSSocket.getLocalPort());

            BufferedWriter writeRequest
                    = new BufferedWriter(new OutputStreamWriter(NBSSocket.getOutputStream()));

            writeRequest.write(request + "\n");
            writeRequest.flush();

            BufferedReader readResponse
                    = new BufferedReader(new InputStreamReader(NBSSocket.getInputStream()));

            while ((responsePerLine = readResponse.readLine()) != null) {
                if(response == null) response = new StringBuffer();
                response.append(responsePerLine);
                LOG.log(Level.INFO, "Response recieved as {0}", response);
            }
            return response.toString();
        } catch (IOException ex) {
            Logger.getLogger(NBSClient.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("nbsclient.NBSClient.main()" + ex);
        }
        return response.toString();
    }
}
