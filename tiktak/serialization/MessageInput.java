/*
 * Author: Chinagorom Mbaraonye
 * Program: 0
 * CSI 4321 - Data communications
 */
package tiktak.serialization;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 *
 * @author Chinagorom Mbaraonye
 * @version 1.0
 */
public class MessageInput{
    private BufferedInputStream bufferedInputStream;

    /**
     * Constructs a new input source from an InputStream. Must be non-blocking.
     * @param in - byte input source
     * @throws IOException - if in is null
     */
    public MessageInput(InputStream in) throws IOException {
        Objects.requireNonNull(in, "in should not be null");
        bufferedInputStream = new BufferedInputStream(in);
    }

    /**
     * Reads all the bytes of the InputStream
     * @return all the bytes in the InputStream
     * @throws IOException - if in is null
     */
    public byte[] getBufferedInputStream() throws IOException {
        return bufferedInputStream.readAllBytes();
    }
}
