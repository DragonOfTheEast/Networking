/*
 * Chinagorom Mbaraonye
 * CSI 4321
 */
package topic.app.server;

import topic.serialization.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static java.lang.System.*;

/**
 * @author Chinagorom Mbaraonye
 * @version 1.0
 */
public class Server implements Runnable{
    private int port; //server port
    private CopyOnWriteArrayList<String> posts; //list of posts
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName()); //logger


    /**
     * Constructor for server
     * @param port server port
     * @param posts list of posts
     */
    public Server(int port, CopyOnWriteArrayList<String> posts){
        LOGGER.setUseParentHandlers(false);
        this.port = port;
        this.posts = posts;
    }


    /**
     * Method ran by the server thread to respond to clients
     */
    @Override
    public void run() {
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler("topic.log", true);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
        LOGGER.addHandler(fileHandler);
        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);
        LOGGER.log(Level.INFO, "Topic log");

        DatagramSocket datagramSocket = null;
        //creating socket
        try{
            datagramSocket = new DatagramSocket(port);
        }catch (NumberFormatException | SocketException e){
            LOGGER.log(Level.SEVERE, "Communication problem: " + e.getMessage());
            exit(-1);
        }

        byte[] packet = new byte[TopicConstants.MAXRES];
        DatagramPacket datagramPacket;
        Response response = null;

        while(true){
            //packet to send
            datagramPacket = new DatagramPacket(packet, TopicConstants.MAXRES);
            try {
                //receiving packet
                datagramSocket.receive(datagramPacket);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, datagramPacket.getAddress().getHostAddress() + " on port "
                        + datagramPacket.getPort() + " " + e.getMessage());
            }

            byte [] msg = Arrays.copyOfRange(datagramPacket.getData(), 0, datagramPacket.getLength());

            Query query = null;
            try{
                query = new Query(msg);
                ArrayList<String> temp = new ArrayList<>();

                int i = 0;
                int len = 0;
                if (query.getRequestedPosts() > 0){
                    //get all the posts or just the requested number or the maximum payload allowed(whichever comes first)
                    for(String a: posts){
                        if (len + a.length() + Constants.TWO > TopicConstants.MAXRES){
                            break;
                        }
                        temp.add(a);
                        len+= a.length() + Constants.TWO;
                        i++;
                        if(i==query.getRequestedPosts()){
                            break;
                        }
                    }
                }
                //making response
                response = new Response(query.getQueryID(), ErrorCode.NOERROR, temp);
                solve(datagramSocket, response, datagramPacket);
                LOGGER.log(Level.INFO, datagramPacket.getAddress().getHostAddress() + " on port "
                        + datagramPacket.getPort() + "Sending posts");

            } catch (TopicException e) {
                LOGGER.log(Level.WARNING, datagramPacket.getAddress().getHostAddress() + " on port "
                        + datagramPacket.getPort() + " " + e.getErrorCode().getErrorMessage());
                //making response
                response = new Response(0, e.getErrorCode(), new ArrayList<>());
                solve(datagramSocket, response, datagramPacket);
            }
        }

    }

    /**
     * Method to attempt sending the response packet
     * @param datagramSocket socket used to send the packet
     * @param response response packet
     * @param rec received packet
     */
    public static void solve(DatagramSocket datagramSocket, Response response, DatagramPacket rec) {
        DatagramPacket datagramPacket = new DatagramPacket(response.encode(), response.encode().length, rec.getAddress(), rec.getPort());
        try{
            datagramSocket.send(datagramPacket);
        }catch (IOException io){
            LOGGER.log(Level.SEVERE, datagramPacket.getAddress().getHostAddress() + " on port "
                    + datagramPacket.getPort() + " Error sending packet");
        }
    }
}
