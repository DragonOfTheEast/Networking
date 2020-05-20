/*
 * Author: Chinagorom Mbaraonye
 * Program: 0
 * CSI 4321 - Data communications
 */
package tiktak.serialization;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Chinagorom Mbaraonye
 * @version 1.0
 */
public class Version extends Message {

    /**
     * Function Serializes message to given output sink
     * @param out - serialization output sink
     * @throws IOException - if I/O problem
     * @throws NullPointerException - if out is null
     */
    @Override
    public void encode(MessageOutput out) throws IOException {
        Objects.requireNonNull(out, "out cannot be null");
        try {
            //writing to output stream
            out.getOutputStream().write(Constants.VERSION.getBytes(StandardCharsets.ISO_8859_1));
        }catch (IOException e){
            throw new IOException("error in writing to MessageOutput - out");
        }

    }

    /**
     * Returns message operation
     * @return message operation
     */
    @Override
    public String getOperation() {
        return "TIKTAK";
    }

    /**
     * Returns a String representation
     * @return TikTak
     */
    @Override
    public String toString(){
        return "TikTak";
    }

    /**
     *Returns an instance of Version
     * @param chars an array of characters
     * @return a new instance of the version class
     * @throws ValidationException if it does not match the required string
     */
    public static Version getNewVersion(char[] chars) throws ValidationException, EOFException {

        if(!isDelimeter(chars)){
            throw new EOFException("Unexpected end of stream");
        }
        else if (!Arrays.equals(chars, Constants.VERSION.toCharArray())){
            throw new ValidationException("invalid message", Arrays.toString(chars));
        }
        else{
            return new Version();
        }
    }

    /**
     *Checks inequality of objects
     * @return the hashcode of an object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getOperation());
    }

    /**
     *Checks the equality of objects
     * @param o object to compare to this
     * @return equality of the two objects
     */
    @Override
    public boolean equals(Object o) {
        try{
            Objects.requireNonNull(o, "o cannot be null");
        }catch(NullPointerException e){
            return false;
        }
        if (this == o) { return true; }
        return getClass() == o.getClass();
    }
}
