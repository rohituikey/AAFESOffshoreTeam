/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import WexStub.NBSStub;
import WexStub.NBSStubImpl;

/**
 *
 * @author uikuyr
 */
public class NBSServer {

    private static final Logger LOG = Logger.getLogger(NBSServer.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Socket connectionSocket = null;
        try {
            NBSStub nBSStub = new NBSStubImpl();
            LOG.info("Logging from NBS Server. Initiating Socket Server");
            ServerSocket NbsSocket = new ServerSocket(2000);

            connectionSocket = NbsSocket.accept();
            LOG.info("Connection successfully established");

            BufferedReader readRequest
                    = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            String Input = readRequest.readLine().trim();
            LOG.log(Level.INFO, "recieved request as {0}", Input);

            BufferedWriter writeResponse
                    = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));

            String response = null;
            try{
                response = nBSStub.unpackIso8583(Input);
            }catch(Exception e){
                e.printStackTrace();
            }

            LOG.log(Level.INFO, "sending response as {0}", response);
            writeResponse.write(response+ " \r");
            writeResponse.flush();
        } catch (IOException ex) {
            Logger.getLogger(NBSServer.class.getName()).log(Level.SEVERE, null, ex);
        }catch (Exception ex) {
            Logger.getLogger(NBSServer.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try { 
                if(connectionSocket != null) connectionSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(NBSServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
