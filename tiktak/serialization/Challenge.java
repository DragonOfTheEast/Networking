/*
 * Author: Chinagorom Mbaraonye
 * Program: 0
 * CSI 4321 - Data communications
 */
package tiktak.serialization;

import java.io.IOException;
import java.lang.String;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Chinagorom Mbaraonye
 * @version 1.0
 */
public class Challenge extends Message {
    private String nonce;

    /**
     * Constructs challenge message using given values
     * @param  nonce - challenge nonce
     * @throws ValidationException -  if validation fails, including null nonce
     */
    public Challenge(String nonce) throws ValidationException{
        try{
            Objects.requireNonNull(nonce);
        }catch (NullPointerException e){
            throw new ValidationException("servnonce cannot be null", nonce);
        }

        if(nonce.isEmpty()){
            throw new ValidationException("servnoce cannot be empty", nonce);
        }

        if(!isNum(nonce)){
            throw new ValidationException
                    ("Invalid servnonce, servnonce has to be numeric", nonce);
        }
        this.nonce = nonce;
    }

    /**
     * Returns nonce
     * @return nonce
     */
    public String getNonce(){
        return nonce;
    }

    /**
     * Sets nonce
     * @param nonce - new nonce
     * @return challenge with new nonce
     * @throws ValidationException - if null or invalid nonce
     */
    public Challenge setNonce(String nonce) throws ValidationException{
        try{
            Objects.requireNonNull(nonce);
        }catch (NullPointerException e){
            throw new ValidationException
                    ("servnonce cannot be null", nonce);
        }

        if(nonce.isEmpty()){
            throw new ValidationException
                    ("servnoce cannot be empty", nonce);
        }

        if(!isNum(nonce)){
            throw new ValidationException
                    ("Invalid servnonce, servnonce has to be numeric", nonce);
        }
        this.nonce = nonce;
        return this;
    }

    /**
     * Returns a String representation
     * @return Challenge: nonce= 1+ (numeric)
     */
    @Override
    public String toString() {
        return "Challenge: "
                + "nonce="+ this.nonce;
    }


    /**
     * Function Serializes message to given output sink
     * @param out - serialization output sink
     * @throws IOException if I/O problem
     * @throws NullPointerException if out is null
     */
    @Override
    public void encode(MessageOutput out) throws IOException {
        Objects.requireNonNull(out, "out cannot be null");
        try{
            //writing to output stream
            out.getOutputStream().
                    write(getOperation().getBytes(StandardCharsets.ISO_8859_1));
            out.getOutputStream().
                    write(" ".getBytes(StandardCharsets.ISO_8859_1));
            out.getOutputStream().
                    write(nonce.getBytes(StandardCharsets.ISO_8859_1));
            out.getOutputStream().
                    write("\r\n".getBytes(StandardCharsets.ISO_8859_1));
        }catch (IOException e){
                throw new IOException("error in stream - out");
        }
    }

    /**
     * Returns message operation
     * @return message operation
     */
    @Override
    public String getOperation() {
        return "CLNG";
    }

    /**
     *Returns an instance of Challenge
     * @param chars an array of characters
     * @return a new instance of the challenge class
     * @throws ValidationException if it does not match the required string
     */
    public static Challenge getNewChallenge(char[] chars) throws ValidationException {
        if(chars.length < Constants.EIGHT){
            throw new ValidationException
                    ("Invalid ID operation(less than minimum" +
                            " characters allowed)", Arrays.toString(chars));
        }
        //getting full type
        String type = new String(chars, 0, Constants.FOUR);
        if(!type.equals(Constants.CLNG)){
            throw new ValidationException("Invalid challenge operation",
                    Arrays.toString(chars));
        }
        //getting the nonce
        String servnonce = new String(Arrays.copyOfRange(chars,
                Constants.FIVE, chars.length-Constants.TWO));

        if(!Message.isNum(servnonce)){
            throw new ValidationException("Invalid servnonce, needs to be numeric", servnonce);
        }
        if(isDelimeter(chars)){
            return new Challenge(servnonce);
        }
        else {
            throw new ValidationException("Invalid Operation, needs the right delimeter", servnonce);
        }
    }

    /**
     *Checks the equality of objects
     * @param o object to compare to this
     * @return equality of the two objects
     */
    @Override
    public boolean equals(Object o) {
        Objects.requireNonNull(o,"o cannot be null");
        if (this == o) { return true; }
        if (getClass() != o.getClass()) { return false; }
        Challenge a_new = (Challenge) o;
        return this.getNonce().equals(a_new.getNonce());
    }

    /**
     *Checks inequality of objects
     * @return the hashcode of an object
     */
    @Override
    public int hashCode() {
        return Objects.hash(nonce);
    }
}
