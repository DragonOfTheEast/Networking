/*
 * Author: Chinagorom Mbaraonye
 * Program: 2
 * CSI 4321 - Data communications
 */
package tiktak.app.server;

import tiktak.app.client.Client;
import tiktak.serialization.*;
import tiktak.serialization.Error;
import yipper.Yipper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;
import java.util.logging.Logger;

import static java.lang.System.*;

/**
 * @author Chinagorom Mbaraonye
 * @version 1.0
 */
public class Server {
    private final static Logger logger = Logger.getLogger(String.valueOf(Server.class)); //logger
    private final static HashMap<String, String> users = new HashMap<>(); //hashmap of users
    private final static HashMap<String, Integer> seq = new HashMap<>();  //hashmap of sequence numbers
    private static final String LTSRL = "LTSRL"; //ltsrl string
    private static final String CRED = "CRED"; //credentials string
    private static final String TOST = "TOST"; //tost string
    private static final String ID = "ID"; //Id string
    private static final Integer MAX_WAIT_TIME = 20000; //max time allowed
    private static final String ERROR = "ERROR"; //error string
    private static final int ERRORSTR = 404;
    private static CopyOnWriteArrayList<String> posts = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws IOException {

        //creating file handler for log
        Handler handler = new FileHandler("connections.log", true);
        handler.setFormatter(new SimpleFormatter());
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
        if (args.length != 3) {
            logger.log(Level.SEVERE, "Unable to Start: Parameters(s): <Port> <Threads> <Password File>");
        }

        if ( Integer.parseInt(args[0]) < 1024){
            logger.log(Level.SEVERE, "Unable to start: Invalid port");
            return;
        }
        int serverPort = Integer.parseInt(args[0]);
        final ServerSocket serverSocket;
        try{
             serverSocket = new ServerSocket(serverPort);
        }catch (IOException | IllegalArgumentException e){
            logger.log(Level.SEVERE, "Unable to start: Invalid port");
            return;
        }

        int threadPoolSize = Integer.parseInt(args[1]);

        if(threadPoolSize < 1){
            logger.log(Level.SEVERE, "Unable to start: Invalid number of threads");
            exit(-1);
        }

        File file = new File(args[2]);

        //getting password file
        try(Scanner scanner = new Scanner(file, StandardCharsets.ISO_8859_1).useDelimiter(Constants.NEW_DELIM)){
            while(scanner.hasNextLine()){
                String string = scanner.next();
                if(!goodLine(string)){
                    logger.log(Level.SEVERE, "Unable to start: Bad password file" );
                    exit(-1);
                }
                String [] arguments = string.split(":");
                users.put(arguments[0], arguments[1].substring(0, arguments[1].length()-2));
            }
        }catch (FileNotFoundException e){
            logger.log(Level.SEVERE, "Unable to start: Bad password file" );
            exit(-1);
        }

        //filling up sequence hashmap
        for(String i : users.keySet()){
            seq.put(i, 0);
        }

        //spawning threads
        try{
            ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize + 1);
            Thread thread = new Thread(new topic.app.server.Server(serverPort, posts));
            thread.start();
            Socket socket= null;
            while(true){
                try{
                    socket = serverSocket.accept();
                    socket.setSoTimeout(MAX_WAIT_TIME);
                    executorService.execute(new Connection(socket));
                    logger.log(Level.INFO, Thread.currentThread().getId() + " New client just connected");
                }catch (IOException ex){
//                    socket.close();
                    logger.log(Level.SEVERE,    Thread.currentThread().getId() + " Unable to Communicate: Client accept failed", ex);
                    break;
                }
            }
            serverSocket.close();
        }catch (IOException ex){
            logger.log(Level.SEVERE, Thread.currentThread().getId() + " Unable to Communicate: Client accept failed", ex);
        }


    }

    /**
     * @author Chinagorom Mbaraonye
     * @version 1.0
     */
    static class Connection implements Runnable{
        private Thread thread;
        private static Socket socket; //socket for connection
        //private static Queue<String> queue = new LinkedList<>(); //queue for operations
        private static String hash; //credentials hash
        private static String curr_user;  //current user

        /**
         * Function constructs a new connection
         * @param socket
         */
        Connection(Socket socket){

            this.socket = socket;

        }

        /**
         * Function that does the thread operations
         */
        @Override
        public void run() {
            try{
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                Version version = new Version();

                logger.log(Level.INFO, Thread.currentThread().getId() + " Sending Version message");
                version.encode(new MessageOutput(outputStream));
                Queue<String> queue = new LinkedList<>(); //queue for operations
                queue.add(ID);
                queue.add(CRED);
                queue.add(TOST);
                int length = queue.size();

                //while client is open
                while (!socket.isClosed()){

                    Message message = Message.decode(new MessageInput(inputStream));
                    String string = queue.peek();
                    String op = message.getOperation();

                    logger.log(Level.INFO, "Received: " + op);
                    switch (op){
                        case ID:

                            check_queue(op, string, message.toString(), queue);
                            if (length > queue.size()) {
                                length--;

                                ID id =  (tiktak.serialization.ID) message;
                                if (!clng_message(outputStream,id)){

                                    logger.log(Level.SEVERE, Thread.currentThread().getId() + " No such user " + ((tiktak.serialization.ID) message).getID());
                                    Error error = new Error(ERRORSTR, "No such user " +
                                            ((tiktak.serialization.ID) message).getID());

                                    error.encode(new MessageOutput(outputStream));

                                    socket.close();
                                }
                            }
                            break;
                        case CRED:
                            out.println("HEEEEEE");
                            check_queue(op, string, message.toString(), queue);
                            if (length > queue.size()) {
                                length--;

                                Credentials credentials = (tiktak.serialization.Credentials) message;
                                if (!cred_message(outputStream,credentials)){
                                    logger.log(Level.SEVERE, Thread.currentThread().getId() + " Unable to authenticate: Invalid Credentials message" );
                                    Error error = new Error(ERRORSTR, "Unable to authenticate");
                                    error.encode(new MessageOutput(outputStream));
                                    socket.close();
                                }
                            }
                            break;
                        case ERROR:
                            logger.log(Level.SEVERE, message.toString());
                            socket.close();
                            break;
                        case TOST:
                            check_queue_special(op, string, message.toString(), queue);
                            if (length > queue.size()) {
                                length--;
                                if (!tost_message(outputStream)) {
                                    logger.log(Level.SEVERE, Thread.currentThread().getId()+ " Invalid message: Invalid TOST message" );
                                    socket.close();
                                }
                                int temp = seq.get(curr_user);
                                temp++;
                                seq.put(curr_user, temp);
                                Yipper yipper = new Yipper("fileTest.html");
                                String string1 = curr_user+": "+TOST + " " + temp;
                                posts.add(string1);
                                yipper.update(string1);
                                socket.close();
                            }
                            break;
                        case LTSRL:
                            check_queue_special(op, string, message.toString(), queue);
                            byte[] image = ((LtsRL)message).getImage();
                            if (length > queue.size()){
                                length--;
                                if(!ltrsl_message(image,((LtsRL)message).getCategory(), outputStream)){
                                    logger.log(Level.SEVERE, Thread.currentThread().getId()+ " Invalid message: Invalid LTSRL message" );
                                    socket.close();
                                }
                                socket.close();
                            }
                            break;
                        default:
                            Server.logger.log(Level.WARNING, Thread.currentThread().getId() + " + Invalid  message: " + message.toString());
                            socket.close();
                            break;

                    }

                }

            } catch (IOException e) {
                Server.logger.log(Level.SEVERE, Thread.currentThread().getId()+ " Unable to communicate: Problem connecting with client");

            }catch (ValidationException e){
                Server.logger.log(Level.SEVERE, Thread.currentThread().getId()+" Invalid message: Unrecognized message");
                try {
                    socket.close();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, Thread.currentThread().getId()+ " Unable to communicate: Problem connecting with client");
                }
            }
        }

        /**
         * Handles a challenge message
         * @param outputStream output stream
         * @param id user id
         * @return if valid or not
         */
        public static boolean clng_message(OutputStream outputStream, ID id){


            if(!users.containsKey(id.getID())){
                return false;
            }
            try{
                //getting nonce and current user
                String curr_nonce = String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
                hash = curr_nonce + users.get(id.getID()) ;
                hash = Client.hash_func(hash);
                curr_user = id.getID();

                Challenge challenge = new Challenge(curr_nonce);
                challenge.encode(new MessageOutput(outputStream));
                logger.log(Level.INFO, Thread.currentThread().getId()+" Writing to Client: Challenge");
                return true;
            } catch (ValidationException e) {
                Server.logger.log(Level.SEVERE, Thread.currentThread().getId()+" Invalid message: Unrecognized message ");
                return false;
            }catch (IOException e){
                Server.logger.log(Level.SEVERE, Thread.currentThread().getId()+ " Unable to communicate: Problem connecting with client");
                return false;
            }
        }

        /**
         * handles a lets roll message
         * @param image image passed
         * @param category category of message
         * @param outputStream message outputstream
         * @return if valid or not
         */
        public static boolean ltrsl_message(byte[] image, String category, OutputStream outputStream) {
            try{
                Yipper yipper = new Yipper("fileTest.html");
                String string = curr_user+": LTRSL #"+category;
                yipper.updateWithImage(string, image);
                posts.add(string);
                Ack ack = new Ack();
                ack.encode(new MessageOutput(outputStream));
                logger.log(Level.INFO, Thread.currentThread().getId()+" Writing to Client: ACK");
                return true;
            }catch (Exception e){
                Server.logger.log(Level.SEVERE, Thread.currentThread().getId()+" Unable to communicate: Problem connecting with client");
                return false;
            }
        }

        /**
         * handles a tost message
         * @param outputStream message outputstream
         * @return if valid or not
         */
        public static boolean tost_message(OutputStream outputStream){
            try{
                Ack ack = new Ack();
                ack.encode(new MessageOutput(outputStream));
                logger.log(Level.INFO, "Writing to Client: ACK");
                return true;
            }catch (IOException e){
                Server.logger.log(Level.SEVERE, Thread.currentThread().getId()+" Unable to communicate: Problem connecting with client");
                return false;
            }

        }

        /**
         * Handles a credentials message
         * @param outputStream message outputstream
         * @param credentials user credentials
         * @return if valid or not
         */
        public static boolean cred_message(OutputStream outputStream, Credentials credentials){
            try{
                String string = credentials.getHash();
                if (!hash.equals(string)){

                    return false;

                }

                Ack ack = new Ack();
                ack.encode(new MessageOutput(outputStream));
                return true;
            } catch (IOException e){
                Server.logger.log(Level.SEVERE, Thread.currentThread().getId()+" Unable to communicate: Problem connecting with client");
                return false;
            }
        }

        /**
         * Checking if expected operation
         * @param op operation
         * @param string top of queue
         * @param message toString of message
         * @param queue operations queue
         * @throws IOException if I/O error occurs
         */
        private static void check_queue(String op, String string, String message, Queue<String> queue) throws IOException {

            if (string.equals(op)){
                queue.remove();
            }else{
                Server.logger.log(Level.WARNING, Thread.currentThread().getId()+" Unexpected message " + message);
                socket.close();
            }
        }


        /**
         * Checking if expected operation for tost and ltsrl
         * @param op operation
         * @param string top of queue
         * @param message toString of message
         * @param queue operations queue
         * @throws IOException if I/O error occurs
         */
        private static void check_queue_special(String op, String string, String message, Queue<String> queue) throws IOException {

            //if it is tost or lets roll
            if (op.equals(TOST) || op.equals(LTSRL)){
                queue.remove();
            }else{
                Server.logger.log(Level.WARNING, Thread.currentThread().getId() +" Unexpected message " + message);
                socket.close();
            }
        }
    }


    /**
     * Function to check if line in password file was valid
     * @param string line of file
     * @return if valid or not
     */
    public static boolean goodLine(String  string){
        String [] arguments = string.split(":");

        if(!tiktak.serialization.ID.isAlphaNum(arguments[0])){
            return false;
        }

        if(!tiktak.serialization.ID.isDelimeter(string.toCharArray())){
            return false;
        }
        String pass = arguments[1].substring(0, arguments[1].length()-2);
        return isGoodPass(pass);
    }

    /**
     * Function to check if valid password or not
     * @param string password
     * @return if valid or not
     */
    public static boolean isGoodPass(String string){
        Objects.requireNonNull(string, "string cannot be null");
        if (string.isEmpty()) return true;
        return string.matches("^[a-zA-Z0-9]*$");
    }
}



