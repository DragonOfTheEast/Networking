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
public class Credentials extends Message {
    private String hash; //hash of servnonce and password

    /**
     * Function Serializes message to given output sink
     * @param out  serialization output sink
     * @throws IOException if I/O problem
     * @throws NullPointerException if out is null
     */
    @Override
    public void encode(MessageOutput out) throws IOException {
        Objects.requireNonNull(out, "out cannot be null");
        try{
            //writing to output stream
            out.getOutputStream().write(getOperation().
                    getBytes(StandardCharsets.ISO_8859_1));
            out.getOutputStream().write(" ".
                    getBytes(StandardCharsets.ISO_8859_1));
            out.getOutputStream().write(getHash().getBytes(StandardCharsets.ISO_8859_1));
            out.getOutputStream().write("\r\n".
                    getBytes(StandardCharsets.ISO_8859_1));
        }catch (IOException e){
            throw new IOException("Error writing to MessageOutput- out");
        }
    }

    /**
     * Returns message operation
     * @return message operation
     */
    @Override
    public String getOperation() {
        return "CRED";
    }

    /**
     * Constructs credential message using given values
     * @param  hash credential hash
     * @throws ValidationException  if validation fails, including null hash
     */
    public Credentials(String hash) throws ValidationException{
       validateHash(hash);
    }

    /**
     * Returns a String representation
     * @return Credentials: hash=(hash)
     */
    @Override
    public String toString(){
        return "Credentials: " + "hash=" + getHash();
    }

    /**
     * Function returns hash
     * @return hash
     */
    public String getHash(){
        return hash;
    }

    /**
     * Function validates hash
     * @param hash hash input
     * @throws ValidationException if hash is invalid
     */
    private void validateHash(String hash) throws ValidationException{
        if (hash == null){
            throw new ValidationException("hash cannot be null", hash);
        }

        if(hash.isEmpty()){
            throw new ValidationException("hash cannot be empty", hash);
        }

        if(!validHash(hash)){
            throw new ValidationException("Invalid format of hash", hash);
        }

        this.hash = hash;
    }

    /**
     * Sets hash
     * @param hash new hash
     * @return credentials with new hash
     * @throws ValidationException  if null or invalid hash
     */
    public Credentials setHash(String hash) throws ValidationException{
        validateHash(hash);
        return this;
    }
    /**
     *Returns an instance of Credentials
     * @param chars an array of characters
     * @return a new instance of the challenge class
     * @throws ValidationException if it does not match the required string
     * @throws EOFException if I/O problem
     */
    public static Credentials getNewCredentials(char [] chars) throws EOFException, ValidationException{

        if(!isDelimeter(chars)){
            throw new EOFException("Invalid Operation, needs the right delimeter");
        }

        if(chars.length < Constants.EIGHT){
            throw new ValidationException("Input is too short", Arrays.toString(chars));
        }

        //getting full type
        String type = new String(chars, 0, Constants.FIVE);

        if(!Constants.CRED.equals(type)){
            throw new ValidationException("Invalid challenge operation",
                    Arrays.toString(chars));
        }

        //getting the hash
        String credentials = new String(Arrays.copyOfRange
                (chars, Constants.FIVE, chars.length-Constants.TWO));

        return new Credentials(credentials);
    }

    /**
     * Function validates hash code
     * @param hash string of hash
     * @return if hash is valid or not
     */
    public static boolean validHash(String hash){
        Objects.requireNonNull(hash);

        if (hash.length() % Constants.TWO != 0 || hash.isEmpty()){
            return false;
        }
        return hash.matches("^[A-F0-9]*$");
    }

    /**
     *Checks the equality of objects
     * @param o object to compare to this
     * @return equality of the two objects
     */
    @Override
    public boolean equals(Object o) {
        if(o == null){
            return false;
        }

        if (this == o) { return true; }
        if (getClass() != o.getClass()) { return false; }
        Credentials a_new = (Credentials) o;
        return this.getHash().equals(a_new.getHash());
    }

    /**
     *Checks inequality of objects
     * @return the hashcode of an object
     */
    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }
}
