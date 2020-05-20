/*
 * Author: Chinagorom Mbaraonye
 * Program: 0
 * CSI 4321 - Data communications
 */
package tiktak.serialization;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Chinagorom Mbaraonye
 * @version 1.0
 */
public class MessageInput {
    private BufferedInputStream bufferedInputStream;

    /**
     * Constructs a new input source from an InputStream. Must be non-blocking.
     *
     * @param in - byte input source
     */
    public MessageInput(InputStream in) {
        Objects.requireNonNull(in, "in should not be null");
        bufferedInputStream = new BufferedInputStream(in);
       // System.out.println(bufferedInputStream);
    }

    /**
     * Reads all the bytes of the InputStream
     *
     * @return all the bytes in the InputStream
     * @throws EOFException - if in is null
     */
    public String getBufferedInputStream() throws EOFException{
        try {
            StringBuilder stringBuilder = new StringBuilder();
            int ch;
            while((ch = bufferedInputStream.read()) != '\n'){
                if (ch == -1){
                    throw new EOFException("no bytes available");
                }
                stringBuilder.append((char) ch);
                //System.out.println(ch);

            }
            stringBuilder.append('\n');
            return stringBuilder.toString();
        }catch (IOException e){
            throw new EOFException("no bytes available");
        }
    }

    public void close() throws IOException {
        bufferedInputStream.close();
    }
}

