/*
 * Author: Chinagorom Mbaraonye
 * Program: 2
 * CSI 4321 - Data communications
 */
package tiktak.app.client;

import tiktak.serialization.*;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Queue;

import static java.lang.System.*;

/**
 * @author Chinagorom Mbaraonye
 * @version 1.0
 */
public class Client {
    private static Queue<String> queue = new LinkedList<>(); //queue to maintain order
    private static final int MIN_ARGS = 5 ;  //minimum amount of arguments
    private static final int MAX_ARGS = 7;   //maximum amount of arguments
    private static final String VERSION_MESSAGE = "TIKTAK"; //version message
    private static final String ID = "ID"; //Id string
    private static final String CLNG = "CLNG"; //clng string
    private static final String ACK = "ACK"; //ack string
    private static final String LTSRL = "LTSRL"; //ltsrl string
    private static final String CRED = "CRED"; //credentials string
    private static final String ERROR = "ERROR"; //error string
    private static final String TOST = "TOST"; //tost string
    private static boolean worked = true;
    public static void main(String[] args){
        if (args.length != MIN_ARGS && args.length != MAX_ARGS){
            throw new IllegalArgumentException("Parameters: <server><port><userid><password><request>");
        }

        String server = args[0];
        int port = Integer.parseInt(args[1]);
        try{
            Socket socket = new Socket(server, port);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            //adding queue priority
            queue.add(VERSION_MESSAGE);
            queue.add(CLNG);
            queue.add(ACK);
            queue.add(ACK);
            int length = queue.size();

            Message message = null;
            //while queue has values and socket is connected
            while(!queue.isEmpty() && socket.isConnected()){
                try{
                    if(worked) message = Message.decode(new MessageInput(inputStream));
                    String string = queue.peek();
                    String op =   message.getOperation();
                    switch (op){
                        //if message was a version
                        case VERSION_MESSAGE:
                           check_queue(op,string, message.toString());
                           if (length  > queue.size()){
                                length--;
                                if(!version_message(outputStream,args[2])){
                                    exit(-1);
                                }
                           }
                           break;
                        //if an error occurred
                        case ERROR:
                            System.err.println(message.toString());
                            exit(-1);
                        //if message was an ACK
                        case ACK:
                            check_queue(op, string, message.toString());
                            if(length > queue.size()) {
                                length--;
                                if (queue.size() == 1) {
                                    if (args.length == MIN_ARGS) {
                                        if (!tost_message(outputStream, args[Constants.FOUR])) {
                                            exit(-1);
                                        }
                                    } else {

                                        Path path = Paths.get(args[Constants.SIX]);
                                        if (!ltsrl_message(outputStream, args[Constants.FOUR], args[Constants.FIVE],
                                                Files.readAllBytes(path))){
                                            exit(-1);
                                        }
                                    }
                                } else {
                                    socket.close();
                                    exit(0);
                                }
                            }
                            break;
                        //if message was a challenge
                        case CLNG:
                            check_queue(op, string, message.toString());
                            if(length > queue.size()){
                                length--;
                                if(!cred_message(outputStream, ((Challenge) message).getNonce(), args[Constants.THREE])){
                                    exit(-1);
                                }
                            }
                            break;
                        default:
                            err.println("Unexpected message: " + message.toString());
                    }
                }catch (ValidationException | NullPointerException e){
                  System.err.println("Invalid message: Message could not be decoded");
                  exit(-1);
                }catch(IOException e){
                    if(e.getMessage().equals(args[6])){
                        err.println("Unable to communicate: Problem with file");
                        exit(-1);
                    }
                    System.err.println("Unable to communicate: Error with message gotten from server " + message.toString());
                    exit(-1);;
                }
            }
        }catch (Exception e){
            System.err.println("Unable to communicate: cant connect to socket");
        }
    }

    /**
     * function validates a version message and continues the conversation
     * @param outputStream serialization output sink
     * @param string id string
     * @return if an error occurred or not
     */
    private static boolean version_message(OutputStream outputStream, String string){
        try {
            ID id = new ID(string);
            id.encode(new MessageOutput(outputStream));
            return true;
        } catch (ValidationException e) {
            System.err.println("Validation failed: Bad ID");
            return false;
        }catch (IOException e){
            System.err.println("Unable to communicate: Error with message gotten from server");
            return false;
        }
    }

    /**
     * function validates a credential message and continues the conversation
     * @param outputStream serialization output sink
     * @param nonce nonce for the credentials
     * @param password user password
     * @return if an error occurred or not
     */
    private static boolean cred_message(OutputStream outputStream, String nonce,
                                        String password){
            String string = nonce + password;
            String hash = hash_func(string);
            if (hash == null){
                return false;
            }
            try{
                Credentials credentials = new Credentials(hash);

                credentials.encode(new MessageOutput(outputStream));
                return true;
            }catch (ValidationException e){
                System.err.println("Validation failed: Bad Hash");
                return false;
            }
            catch (IOException e){
                System.err.println("Unable to communicate: Error with message gotten from server");
                return false;
            }


    }

    /**
     * function returns the MD5 hash of a string
     * @param string input string
     * @return MDS hash of string
     */
    public static String hash_func(String string){
        try{
            //out.println(string.length() + " " + string);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte [] bytes = messageDigest.digest(string.getBytes(StandardCharsets.ISO_8859_1));
            BigInteger bigInteger = new BigInteger(1, bytes);
            String hash = bigInteger.toString(16);

            while(hash.length() < 32){
                hash = "0" + hash;
            }
            return hash.toUpperCase();
        }catch (NoSuchAlgorithmException e){
            System.err.println("MD5 hash failed");
            return null;
        }
    }

    /**
     * Function checks if that was the expected message
     * @param op operation
     * @param string top of the queue
     * @param message message of the operation
     */
    private static void check_queue(String op, String string, String message){
        if (string.equals(op)){
            out.println(message);
            queue.remove();
            worked = true;
        }else{
            System.err.println("Unexpected message: " + message);
            worked = false;
        }
    }


    /**
     * Function validates a tost message
     * @param outputStream serialization output sink
     * @param string string from command line
     * @return if error occurred or not
     */
    private static boolean tost_message(OutputStream outputStream, String string){
        if (!TOST.equals(string)){
            System.err.println("Validation failed: Wrong Request");
            return false;
        }else{
            Tost tost = new Tost();
            try{
                tost.encode(new MessageOutput(outputStream));
                return true;
            }catch (IOException e){
                System.err.println("Unable to communicate: Error with message gotten from server");
                return false;
            }
        }
    }

    /**
     * Function validates an ltrsl message and continues conversation
     * @param outputStream serialization output sink
     * @param message message from command line
     * @param category category of image
     * @param image image bytes
     * @return if an error occurred or not
     */
    private static boolean ltsrl_message(OutputStream outputStream, String message, String category, byte[] image){
        if(!LTSRL.equals(message)){
            System.err.println("Validation failed: Wrong Request");
            return false;
        }else{
            try{

                LtsRL ltsRL = new LtsRL(category, image);

                ltsRL.encode(new MessageOutput(outputStream));
                return true;
            } catch(ValidationException e){
                System.err.println("Validation failed: Bad Hash");
                return false;
            }
            catch (IOException e){
                System.err.println("Unable to communicate: Error with message gotten from server");
                return false;
            }
        }
    }

}
