/*
    Chinagorom Mbaraonye
    CSI 4321
 */
package topic.app.client;

import tiktak.serialization.Error;
import topic.serialization.*;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;
import java.util.Arrays;
import java.util.Random;

import static java.lang.System.*;

/**
 * @author Chinagorom Mbaraonye
 * @version 1.0
 */
public class Client {
    private static final int TIMEOUT = 3000;
    private static final int MAXTRIES = 5;
    private static final int params = 3;

    /**
     * The main of the client
     * @param args the command line arguments
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        if(args.length != params){
            System.err.println("Wrong parameters : <server IP/name> <server port> <requestedPosts>");
            exit(-1);
        }
        client(args);
    }

    /**
     * The client method
     * @param args command line parameters
     * @throws IOException if an I/O error occurs
     */
    private static void client(String[] args) throws IOException {
        InetAddress inetAddress = null;
        //getting address of server
        try{
            inetAddress = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            System.err.println("Unknown Host trying to connect");
            exit(-1);
        }catch (SecurityException e){
            System.err.println("Access to connect not allowed");
            exit(-1);
        }

        //getting the other command line arguments
        int portNumber = Integer.parseInt(args[1]);
        int requested = Integer.parseInt(args[2]);

        //if requested posts is less than 0
        if (requested < 0 || requested > TopicConstants.MAXPOSTS){
            err.println("Requested posts has to be 0<= x >= 65535");
            exit(-1);
        }
        //getting queryId in range
        long min = 0;
        long max = TopicConstants.MAXID;
        long queryID = (long)(Math.random()*((max-min)+1)) +min;


        Query query = new Query(queryID, requested);

        DatagramSocket datagramSocket = null;
        try{
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            System.err.println("There was a socket error");
            exit(-1);
        }
        datagramSocket.setSoTimeout(TIMEOUT);
        DatagramPacket datagramPacket= new DatagramPacket(query.encode(), query.encode().length, inetAddress, portNumber);
        DatagramPacket rec = new DatagramPacket(new byte[TopicConstants.MAXPOSTS], TopicConstants.MAXPOSTS, inetAddress, portNumber);

        int trials = 0;
        boolean stop = false;

        //getting reply from server
        do{
            try {
                datagramSocket.send(datagramPacket);
            } catch (IOException e) {
                System.err.println("Error sending packet");
                exit(-1);
            }
            if(trials == MAXTRIES){
                System.err.println("Could not get a reply from server in 5 attempts");
                exit(-1);
            }
            try{
                //getting reply packet
                datagramSocket.receive(rec);
                if(!rec.getAddress().equals(inetAddress)){
                    System.err.println("Received reply from unknown source");
                    exit(-1);
                }
                stop = true;
            }catch (InterruptedIOException e){
                trials++;
                System.err.println("Timed out, " + (MAXTRIES - trials) + " more tries...");
            }
            if(stop){
                try{
                    byte [] message = Arrays.copyOfRange(rec.getData(), 0, rec.getLength());
                    Response response = new Response(message);
                    if(response.getErrorCode() != ErrorCode.NOERROR){
                        err.println(response.getErrorCode().getErrorMessage());
                        exit(-1);
                    }
                    if(response.getQueryID() != queryID){
                        err.println("Non-matching query ids");
                        stop = false;
                        trials++;
                    }
                    if (stop) {
                        out.println(response.toString());
                    }
                }catch (TopicException e){
                    if(e.getErrorCode() == ErrorCode.NETWORKERROR){
                        err.println("Non-zero reserve");
                        exit(-1);
                    }
                    err.println(e.getErrorCode().getErrorMessage());
                    exit(-1);
                }
            }
        }while(!stop);
        datagramSocket.close();
    }
}
