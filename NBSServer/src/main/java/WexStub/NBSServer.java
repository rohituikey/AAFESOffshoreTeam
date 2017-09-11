package WexStub;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.util.logging.Logger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;

/**
 *
 * @author uikuyr
 */
public class NBSServer {

    private static final Logger LOG = Logger.getLogger(NBSServer.class.getName());
    public static boolean logOff = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //Socket connectionSocket = null;
            int PORT = 2000;
            byte[] buf = new byte[1000];
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
            DatagramSocket datagramSocket;
            datagramSocket = new DatagramSocket(PORT);
            NBSStubFieldSeperator nBSStub = new NBSStubFieldSeperator();
            System.out.println("Server started");
            datagramSocket.receive(datagramPacket);
            String results[];
            results = nBSStub.createResponse(datagramPacket.getData());
            int index = 0;
            while (index < 2) {
                byte[] result = results[index].getBytes();
                DatagramPacket acknowledgment = new DatagramPacket(result, result.length, datagramPacket.getAddress(), datagramPacket.getPort());
                System.out.println(results[index]);
                datagramSocket.send(acknowledgment);
                if(results[index].contains("c$"))
                index++;
                else
                    index=index+2;
            }
//            try {
//                
//                LOG.info("Logging from NBS Server. Initiating Socket Server");
//                ServerSocket NbsSocket = new ServerSocket(2000);
//                //NbsSocket.setSoTimeout(1);
//
//                connectionSocket = NbsSocket.accept();
//                LOG.info("Connection successfully established");
//
//                InputStream inputStream = connectionSocket.getInputStream();
//                DataInputStream readRequest = new DataInputStream(inputStream);
//                byte[] input = new byte[readRequest.readInt()];
//                if (readRequest.readInt() > 0) {
//                    readRequest.readFully(input, 0, readRequest.readInt());
//                    LOG.log(Level.INFO, "recieved request as {0}", new String(input));
//                }
//                byte[] response = null;
//                try {
//                    response = nBSStub.getResponse(input);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                DataOutputStream writeResponse = new DataOutputStream(connectionSocket.getOutputStream());
////                BufferedWriter writeResponse
////                        = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
//                LOG.log(Level.INFO, "sending response as {0}", response);
//                writeResponse.writeInt(response.length);
//                if (response.length > 0) {
//                    writeResponse.write(response, 0, response.length);
//                }
//                writeResponse.close();
//                writeResponse.flush();
//            } catch (IOException ex) {
//                Logger.getLogger(NBSServer.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (Exception ex) {
//                Logger.getLogger(NBSServer.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        try {
//            connectionSocket.close();
//        } catch (IOException ex) {
//            Logger.getLogger(NBSServer.class.getName()).log(Level.SEVERE, null, ex);
//        }

        } catch (SocketException ex) {
            Logger.getLogger(NBSServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NBSServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
