/*
 * Author: Chinagorom Mbaraonye
 * Program: 0
 * CSI 4321 - Data communications
 */
package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Chinagorom Mbaraonye
 * @version 1.0
 */
public class ID extends Message{
    private String ID;


    /**
     * Constructs ID message using set values
     * @param ID - ID for user
     * @throws ValidationException - if validation fails
     */
    public ID(String ID) throws ValidationException {
        try{
            Objects.requireNonNull(ID);
        }catch (NullPointerException e){
            throw new ValidationException("ID cannot be null", ID);
        }

        if (ID.isEmpty()){
            throw new ValidationException("ID cannot be empty", ID);
        }

        if(!isAlphaNum(ID)){
            throw new ValidationException("ID has to be alphanumeric", ID);
        }
        this.ID = ID;
    }

    /**
     * Returns ID
     * @return ID
     */
    public String getID() {
        return ID;
    }

    /**
     * Sets ID
     * @param ID- new identifier
     * @return ID with new identifier
     * @throws ValidationException - if null or invalid ID
     */
    public ID setID(String ID) throws ValidationException{
        try{
            Objects.requireNonNull(ID);
        }catch (NullPointerException e){
            throw new ValidationException("ID cannot be null", ID);
        }

        if (ID.isEmpty()){
            throw new ValidationException("ID cannot be empty", ID);
        }

        if(!isAlphaNum(ID)){
            throw new ValidationException("ID has to be alphanumeric", ID);
        }
        this.ID = ID;
        return this;
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
                out.getOutputStream().write(getOperation().
                        getBytes(StandardCharsets.ISO_8859_1));
                out.getOutputStream().write(" ".
                        getBytes(StandardCharsets.ISO_8859_1));
                out.getOutputStream().write(getID().
                        getBytes(StandardCharsets.ISO_8859_1));
                out.getOutputStream().write("\r\n".
                        getBytes(StandardCharsets.ISO_8859_1));
            }catch (IOException e){
                throw new IOException("Error writing to MessageOutput- out");
            }
    }

    /**
     *Returns an instance of ID
     * @param chars an array of characters
     * @return a new instance of the ID class
     * @throws ValidationException if it does not match the required string
     */
    public static ID getNewID(char [] chars) throws ValidationException {
        if(chars.length < Constants.SIX){
            throw new ValidationException("Invalid ID operation(less than minimum characters allowed)", Arrays.toString(chars));
        }
        String type = new String(chars,0,Constants.TWO);

        if (!type.equals(Constants.ID_VAR)){
            throw new ValidationException("Invalid ID operation", Arrays.toString(chars));
        }

        String goodID = new String(Arrays.copyOfRange(chars, Constants.THREE, chars.length-Constants.TWO));


        if(!isAlphaNum(goodID)){
            throw new ValidationException("Invalid ID, needs to be alphanumeric", goodID);
        }

        if(Message.isDelimeter(chars)){
            return new ID(goodID);
        }
        else{
            throw new ValidationException("Invalid Operation, needs the right delimeter", goodID);
        }
    }

    /**
     * Returns message operation
     * @return message operation
     */
    @Override
    public String getOperation() {
        return "ID";
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
        ID a_new = (ID) o;
        return this.getID().equals(a_new.getID());
    }

    /**
     * Returns a String representation
     * @return Challenge: nonce= 1+ (numeric)
     */
    public String toString(){
        return "ID: id=" + ID;
    }

    /**
     *Checks inequality of objects
     * @return the hashcode of an object
     */
    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }
}
