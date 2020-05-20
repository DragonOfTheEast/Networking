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
public class Error extends Message {
    private int code; // error code
    private String message ; //error message

    /**
     * Constructs error message using given values
     * @param  code error hash
     * @param  message error message
     * @throws ValidationException  if validation fails, including null hash
     */
    public Error(int code, String message)throws ValidationException{
        validateCode(code);
        validateMessage(message);
    }

    /**
     *Returns an instance of Error
     * @param chars an array of characters
     * @return a new instance of the error class
     * @throws ValidationException if it does not match the required string
     * @throws EOFException if I/O problem
     */
    public static Error getNewError(char [] chars) throws EOFException, ValidationException {

        if(!isDelimeter(chars)){
            throw new EOFException("Invalid Operation, needs the right delimeter");
        }

        if(chars.length < Constants.THIRTEEN){
            throw new ValidationException("Input is too short", Arrays.toString(chars));
        }

        //getting type with space
        String type = new String(chars, 0, Constants.SIX);
        if(!Constants.ERROR.equals(type)){
            throw new ValidationException("Invalid Error operation",
                    Arrays.toString(chars));
        }


        //getting arguments
        String [] arguments = new String(Arrays.copyOfRange
                (chars, Constants.SIX,
                        chars.length-Constants.TWO)).split(" ");

        if (arguments.length < Constants.TWO){
            throw new ValidationException("Invalid Error operation",
                    Arrays.toString(chars));
        }
        if(arguments[0].length() != Constants.THREE){
            throw new ValidationException("Invalid code", arguments[0]);
        }
        //getting code
        int code = Integer.parseInt(arguments[0]);
        String message = new String(Arrays.copyOfRange
                (chars, Constants.TEN,
                        chars.length-Constants.TWO));

        return new Error(code, message);

    }

    /**
     * Function validates error code
     * @param code string of hash
     * @return if code is valid or not
     */
    public boolean isValidCode(String code){
        if(code.length() !=3){
            return false;
        }
        char [] chars = code.toCharArray();

        if (chars[0] == 0){
            return false;
        }
        return true;
    }

    /**
     * Function validates if alphanum or space
     * @param string string of input
     * @return if matches criteria or not
     */
    public static boolean isAlphaNumAndSP(String string){
        Objects.requireNonNull(string, "string cannot be null");
        if (string.isEmpty()) return false;
        return string.matches("^[\\w\\s]+$");
    }

    /**
     * Function returns message
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Function validates message
     * @param message message input
     * @throws ValidationException if message is invalid
     */
    private void validateMessage(String message) throws ValidationException{
        if (message== null){
            throw new ValidationException("message cannot be null", null);
        }

        if(message.isEmpty()){
            throw new ValidationException("message cannot be empty", message);
        }

        if(!isAlphaNumAndSP(message)){
            throw new ValidationException("Invalid format of message", message);
        }

        this.message = message;
    }

    /**
     * Sets messgae
     * @param message new message
     * @return error with new message
     * @throws ValidationException  if null or invalid message
     */
    public Error setMessage(String message) throws ValidationException{
        validateMessage(message);
        return this;

    }

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
            out.getOutputStream().write(String.valueOf(getCode()).
                    getBytes(StandardCharsets.ISO_8859_1));
            out.getOutputStream().write(" ".
                    getBytes(StandardCharsets.ISO_8859_1));
            out.getOutputStream().write(getMessage().
                    getBytes(StandardCharsets.ISO_8859_1));
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
        return "ERROR";
    }

    /**
     * Returns a String representation
     * @return Error: code=(hash) message=(message)
     */
    @Override
    public String toString(){
        return "Error: code="+
                String.valueOf(this.getCode())+" message="+this.getMessage();
    }

    /**
     * Function returns code
     * @return code
     */
    public int getCode() {
        return code;
    }


    /**
     * Sets code
     * @param code new code
     * @return Error with new code
     * @throws ValidationException  if null or invalid code
     */
    public Error setCode(int code) throws ValidationException {
        validateCode(code);
        return this;
    }

    /**
     * Function validates code
     * @param code code input
     * @throws ValidationException if code is invalid
     */
    private void validateCode(int code)throws ValidationException{

        String number = String.valueOf(code);
        if(!isValidCode(number)){
            throw new ValidationException("Invalid code", number);
        }
        this.code = code;
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
        Error a_new = (Error) o;
        return this.getMessage().equals(a_new.getMessage()) && this.getCode()==a_new.getCode();
    }

    /**
     *Checks inequality of objects
     * @return the hashcode of an object
     */
    @Override
    public int hashCode() {
        return Objects.hash(code, message);
    }
}
