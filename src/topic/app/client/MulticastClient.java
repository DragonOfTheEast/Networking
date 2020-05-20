/*
    Chinagorom Mbaraonye
    CSI 4321
 */
package topic.app.client;

import topic.serialization.Constants;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

/**
 * @author Chinagorom Mbaraonye
 * @version 1.0
 */
public class MulticastClient {

    public static void main(String [] args){
        if(args.length != Constants.TWO){
            System.err.println("Parameter(s): <Server> <Port>");
            System.exit(-1);
        }


        try{
            MulticastSocket multicastSocket = new MulticastSocket(Integer.parseInt(args[1]));
            InetAddress inetAddress = InetAddress.getByName(args[0]);

            new Thread(new ClientThread(multicastSocket, inetAddress)).start();

            Scanner scanner = new Scanner(System.in);

            while(true){
                String string = scanner.nextLine();

                //if client wants to quit
                if(string.equals("quit")){
                    System.err.println("Quitting");
                    break;
                }
            }

            multicastSocket.leaveGroup(inetAddress);
            multicastSocket.close();
        } catch (IOException | NumberFormatException exception) {
            System.err.println("Communication problem: " + exception.getMessage());
        }
    }
}

