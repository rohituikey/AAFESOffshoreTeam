package com.aafes.stargate.gateway.wex.simulator;

import com.aafes.stargate.control.Configurator;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import static org.jboss.resteasy.test.EmbeddedContainer.start;

/**
 *
 * @author uikuyr
 */
public class NBSConnector {

    private static final Logger LOG = Logger.getLogger(NBSConnector.class.getName());
    //private StringBuffer response;
    byte[] isoFormat;
    private Socket nbsSocket;
    @EJB
    private Configurator configurator;
    private String wexNbsHostName = "";
    private int wexNbsPortNumber = 0, wexNbsTimeout = 0;

    public String sendRequest(byte[] request) throws SocketTimeoutException {
        String strResponse = "";
        try {
            populateValuesFromPropertiesFile();
            //creating connections
            if (null == nbsSocket || nbsSocket.isClosed()) {
                nbsSocket = new Socket();
            }
            if (nbsSocket != null) {
                nbsSocket.connect(new InetSocketAddress(wexNbsHostName, wexNbsPortNumber), wexNbsTimeout);
                nbsSocket.setSoTimeout(wexNbsTimeout);
                LOG.info("Connection Established with NBS socket server");
                LOG.log(Level.INFO, "{0}-----------{1}---------------{2}",
                        new Object[]{nbsSocket.getClass(), nbsSocket.getLocalAddress(), nbsSocket.getLocalPort()});
            } else {
                LOG.info("Socket client object is null!!");
            }

//            BufferedWriter writeRequest = new BufferedWriter(new OutputStreamWriter(nbsSocket.getOutputStream()));
//            writeRequest.write(request + "\n");
//            writeRequest.flush();
            OutputStream out = nbsSocket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(out);

            dos.writeInt(request.length);
            if (request.length > 0) {
                dos.write(request, 0, request.length);
            }
            strResponse = getResponse();
            nbsSocket.close();
//            isoFormat = response.toString().getBytes();
//            return isoFormat;
            ///if(response != null) strResponse = response.toString();
        } catch (SocketTimeoutException ex) {
            Logger.getLogger(NBSConnector.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("nbsclient.NBSClient.main()" + ex);
            throw ex;
        } catch (IOException ex) {
            Logger.getLogger(NBSConnector.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("nbsclient.NBSClient.main()" + ex);
        } catch (Exception ex) {
            Logger.getLogger(NBSConnector.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("nbsclient.NBSClient.main()" + ex);
            throw ex;
        }
        return strResponse;
    }

    public String getResponse() throws IOException {
        String responsePerLine = "";
        StringBuilder response = null;
        BufferedReader readResponse = new BufferedReader(new InputStreamReader(nbsSocket.getInputStream()));

        while ((responsePerLine = readResponse.readLine()) != null) {
            if (response == null) {
                response = new StringBuilder();
            }
            response.append(responsePerLine);
            LOG.log(Level.INFO, "Response recieved as {0}", response);
        }
        if (response != null) {
            return response.toString();
        } else {
            return "";
        }
    }

    private void populateValuesFromPropertiesFile() {
        LOG.info("Method populateValuesFromPropertiesFile started");
        if (configurator == null) {
            configurator = new Configurator();
        }
        if (configurator.get("WEX_NBS_HOST_NAME") != null) {
            wexNbsHostName = configurator.get("WEX_NBS_HOST_NAME");
        } else {
            LOG.info("WEX_NBS_HOST_NAME is not present in stargate.properties. Setting default value.");
            wexNbsHostName = "localhost";
        }
        if (configurator.get("WEX_NBS_HOST_NAME") != null) {
            wexNbsPortNumber = Integer.parseInt(configurator.get("WEX_NBS_HOST_PORT"));
        } else {
            LOG.info("WEX_NBS_HOST_NAME is not present in stargate.properties. Setting default value.");
            wexNbsPortNumber = 2000;
        }
        if (configurator.get("WEX_REQUEST_TIMEOUT_VAL") != null) {
            wexNbsTimeout = Integer.parseInt(configurator.get("WEX_REQUEST_TIMEOUT_VAL"));
        } else {
            LOG.info("WEX_REQUEST_TIMEOUT_VAL is not present in stargate.properties. Setting default value.");
            wexNbsTimeout = 0;
        }
        LOG.info("Method populateValuesFromPropertiesFile ended");
    }

    public Configurator getConfigurator() {
        return configurator;
    }

    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }

}
