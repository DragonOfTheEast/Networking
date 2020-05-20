package tiktak.app.server;

import tiktak.serialization.Message;
import tiktak.serialization.MessageInput;
import tiktak.serialization.NIODeframer;
import tiktak.serialization.ValidationException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static tiktak.serialization.Constants.CHR_ENC;

public class AIOHelper {

    private Path path;
    private Logger logger;
    private final static int TIMEOUT = 20000;
    private static final int BUFSIZE = 256;

    public AIOHelper(Logger logger, Path path){
        this.logger = logger;
        this.path = path;
    }

    public void handleAccept(final AsynchronousSocketChannel asynchronousSocketChannel, NIODeframer nioDeframer) throws IOException{
        ByteBuffer buf= ByteBuffer.allocate(BUFSIZE);
        ReadAndWriteHelper readAndWriteHelper = new ReadAndWriteHelper();
        asynchronousSocketChannel.read(buf, TIMEOUT, TimeUnit.MILLISECONDS, buf, new CompletionHandler<Integer, ByteBuffer>(){
            public void completed (Integer bytesRead, ByteBuffer buf){
                try{
                    handleRead(asynchronousSocketChannel, buf, bytesRead, readAndWriteHelper );

                } catch (IOException | ValidationException e) {
                    logger.log(Level.WARNING, "Handle Read Failed", e);
                }
            }

            public void failed(Throwable exc, ByteBuffer attachment) {
                try{
                    asynchronousSocketChannel.close();
                }catch (IOException e){
                    logger.log(Level.WARNING, "Handle Read Failed", e);
                }
            }
        });
    }

    public void handleRead(final AsynchronousSocketChannel asynchronousSocketChannel, ByteBuffer buf, int bytesRead, ReadAndWriteHelper readAndWriteHelper) throws IOException, ValidationException {
        if(bytesRead == -1){
            logger.log(Level.WARNING, "connection is closed");
            asynchronousSocketChannel.close();
        }else if(bytesRead > 0){
            buf.flip();
            byte[] msg = new byte[buf.remaining()];
            buf.get(msg);



        }else{
            handleMessage(asynchronousSocketChannel, buf, bytesRead,readAndWriteHelper);
        }
    }

    public void handleMessage(final AsynchronousSocketChannel asynchronousSocketChannel, ByteBuffer buf, int bytesRead, ReadAndWriteHelper readAndWriteHelper) throws IOException, ValidationException{
        String res = null;
        buf.flip();
        byte [] message = new byte[buf.remaining()];
        buf.get(message);
        buf.clear();

        try{
            res = readAndWriteHelper.nioDeframer.getMessage(message);
        } catch (NullPointerException | IllegalArgumentException e) {
            //todo
        }

        while(res !=null){
            Message clientMessage = null;

            try{
                clientMessage = Message.decode(new MessageInput(new ByteArrayInputStream(res.getBytes(CHR_ENC))));
            }catch (ValidationException e){
                clientMessage = null;
                logger.log(Level.WARNING, "Invalid message: " + e.getMessage());
            }

        }
    }



}
