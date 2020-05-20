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
import java.util.Base64;
import java.util.Objects;

/**
 *
 * @author Chinagorom Mbaraonye
 * @version 1.0
 */
public class LtsRL extends Message {
    private byte[] image; //image of the message
    private String category; //category of the message


    /**
     * Returns a String representation
     * @return LtsRl: category=(category) image=(length)bytes
     */
    @Override
    public String toString(){
        return "LtsRL: category=" + this.getCategory()+ " image="+ this.getImage().length
                +" bytes";
    }


    /**
     * Constructs LtsRL message using given values
     * @param  category LtsRL category
     * @param  image    LtsRl image
     * @throws ValidationException  if validation fails, including null values
     */
    public LtsRL(String category, byte[] image) throws ValidationException{
        validateCategory(category);
        if(image == null){
            throw new ValidationException("image cannot be null", null);
        }
        this.image = image;
    }


    /**
     * Sets image
     * @param image new image
     * @return LtsRL with new image
     * @throws ValidationException  if null or invalid image
     */
    public LtsRL setImage(byte[] image) throws ValidationException {
        validateImage(image);
        return this;
    }


    /**
     * Function validates image
     * @param image hash input
     * @throws ValidationException if image is invalid
     */
    private void validateImage(byte[] image) throws ValidationException {

        if (image == null){
            throw new ValidationException("image cannot be null", Arrays.toString((byte[]) null));
        }

        this.image = image;
    }


    /**
     * Sets category
     * @param category new category
     * @return LtsRL with new category
     * @throws ValidationException  if null or invalid hash
     */
    public LtsRL setCategory(String category) throws ValidationException{
        this.validateCategory(category);
        return this;
    }

    /**
     * Function validates category
     * @param category category input
     * @throws ValidationException if category is invalid
     */
    private void validateCategory(String category) throws ValidationException{
        if (category == null){
            throw new ValidationException("category cannot be null", null);
        }
        if(category.isEmpty()){
            throw new ValidationException("category cannot be empty", category);
        }

        if(!isAlphaNum(category)){
            throw new ValidationException("Invalid format of category", category);
        }
        this.category = category;
    }

    /**
     * Function returns image
     * @return image
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Function returns category
     * @return category
     */
    public String getCategory() {
        return category;
    }


    /**
     *Returns an instance of LtsRL
     * @param chars an array of characters
     * @return a new instance of the LtsRL class
     * @throws ValidationException if it does not match the required string
     * @throws EOFException if I/O problem
     */
    protected static LtsRL getNewLtsRL(char [] chars) throws EOFException, ValidationException {
        if(!isDelimeter(chars)){
            throw new EOFException("Invalid Operation, needs the right delimeter");
        }

        String type = new String(chars, 0, Constants.SIX);

        if(!type.equals(Constants.LTSRL)){
            throw new ValidationException("Invalid LTSRL operation",
                    Arrays.toString(chars));
        }

        String [] arguments = new String(Arrays.copyOfRange
                (chars, Constants.SIX,
                        chars.length-Constants.TWO)).split(" ");

        if (arguments.length != Constants.TWO){
            throw new ValidationException("Invalid LTSRL operation",
                    Arrays.toString(chars));
        }
        try {
            byte[] image = Base64.getDecoder().decode(arguments[1]);
            return new LtsRL(arguments[0], image);
        } catch (Exception a) {
            throw new ValidationException(a.getLocalizedMessage(), Arrays.toString(chars));
        }

    }

    /**
     * Function Serializes message to given output sink
     * @param out - serialization output sink
     * @throws IOException - if I/O problem
     * @throws NullPointerException - if out is null
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
            out.getOutputStream().write(String.valueOf(getCategory()).
                    getBytes(StandardCharsets.ISO_8859_1));
            out.getOutputStream().write(" ".
                    getBytes(StandardCharsets.ISO_8859_1));
            //todo
            out.getOutputStream().write(Base64.getEncoder().withoutPadding().encode(getImage()));
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
        return "LTSRL";
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
        LtsRL a_new = (LtsRL) o;
        return this.getCategory().equals(a_new.getCategory()) && Arrays.equals(this.getImage(), a_new.getImage());
    }

    /**
     *Checks inequality of objects
     * @return the hashcode of an object
     */
    @Override
    public int hashCode() {
        return Objects.hash(category, image);
    }
}
