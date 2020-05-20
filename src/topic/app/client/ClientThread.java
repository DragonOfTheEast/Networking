/*
 *Chinagorom Mbaraonye
 * CSI 4321
 */
package topic.app.client;

import topic.serialization.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

/**
 * @author Chinagorom Mbaraonye
 * @version 1.0
 */
public class ClientThread implements Runnable {
    private MulticastSocket multicastSocket;
    private DatagramPacket datagramPacket;
    private InetAddress inetAddress;

    /**
     * constructor of client thread
     * @param multicastSocket multicast socket
     * @param inetAddress inet address
     */
    public ClientThread(MulticastSocket multicastSocket, InetAddress inetAddress){
        this.multicastSocket = multicastSocket;
        this.datagramPacket = new DatagramPacket(new byte[TopicConstants.MAXPOSTS], TopicConstants.MAXPOSTS);
        this.inetAddress = inetAddress;


        //attempting to join group
        try{
            this.multicastSocket.joinGroup(inetAddress);
        }catch (IOException e){
            System.err.println("Error joining group");
            System.exit(-1);
        }
    }

    /**
     * run method of thread
     */
    @Override
    public void run(){
        while(true){
            try{
                multicastSocket.receive(datagramPacket);
            }catch (IOException e){
                break;
            }

           Response response = null;
           byte [] msg = Arrays.copyOfRange(datagramPacket.getData(), 0, datagramPacket.getLength());
            try{
                response = new Response(msg);
                System.out.println(response.toString());
            }catch (TopicException e){
                //todo "error code"
                System.err.println("Invalid message");
                continue;
            }

        }
    }
}
