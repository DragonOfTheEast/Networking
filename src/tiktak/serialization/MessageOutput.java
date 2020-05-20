/*
 * Author: Chinagorom Mbaraonye
 * Program: 0
 * CSI 4321 - Data communications
 */
package tiktak.serialization;

import java.io.*;
import java.util.Objects;

/**
 *
 * @author Chinagorom Mbaraonye
 * @version 1.0
 */
public class MessageOutput{
    private OutputStream outputStream;

    /**
     *
     * @param out - byte output sink
     * @throws NullPointerException - - if in is null
     */
    public MessageOutput(OutputStream out) throws NullPointerException{
        Objects.requireNonNull(out,"in cannot be null");
        outputStream = out;
    }

    /**
     * Returns the output stream
     * @return outputStream
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }
}
