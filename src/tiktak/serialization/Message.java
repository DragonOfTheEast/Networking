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

import static tiktak.serialization.Constants.*;

/**
 *
 * @author Chinagorom Mbaraonye
 * @version 1.0
 */
public abstract class Message {

   // private static char [] chars;

//    public char []getChars(){
//        return chars;
//    }
    /**
     * Deserializes message from input source
     * @param in - deserialization input source
     * @return a specific message resulting from deserialization
     * @throws NullPointerException - deserialization input source
     * @throws ValidationException - if I/O problem
     * @throws IOException - if in is null
     */
    public static Message decode(MessageInput in) throws NullPointerException, ValidationException, IOException {
        Objects.requireNonNull(in, "in cannot be null");
         char[] chars = in.getBufferedInputStream().toCharArray();

            if (chars.length==0 || !isDelimeter(chars)){
                throw new EOFException(Arrays.toString(chars) + chars.length);
            }
            char type = chars[0];
            char second_char = chars[1];

            //getting the right message type
            switch (type){
                case T:
                    switch (second_char){
                        case I:
                            return Version.getNewVersion(chars);
                        case O:
                            return Tost.getNewTost(chars);
                        default:
                            throw new ValidationException("Invalid message type", Arrays.toString(chars));
                    }


                case I:
                    return ID.getNewID(chars);

                case C:
                    switch (second_char){
                        case L:
                            return Challenge.getNewChallenge(chars);
                        case R:
                            return Credentials.getNewCredentials(chars);
                        default:
                            throw new ValidationException("Invalid message type", Arrays.toString(chars));

                    }
                case A:
                    return Ack.getNewAck(chars);

                case E:
                    return Error.getNewError(chars);

                case L:
                    return LtsRL.getNewLtsRL(chars);

                default:
                    throw new ValidationException("Invalid message type", Arrays.toString(chars));
            }



    }

    /**
     * Function Serializes message to given output sink
     * @param out - serialization output sink
     * @throws IOException if I/O problem
     * @throws NullPointerException if out is null
     */
    public abstract void encode (MessageOutput out) throws IOException;

    /**
     * Returns message operation
     * @return message operation
     */
    public abstract String getOperation();

    /**
     * checks if a string is alphanumeric
     * @param string - string to check
     * @return if the string is alphanumeric or not
     */
    public static boolean isAlphaNum(String string){
        Objects.requireNonNull(string, "string cannot be null");
        if (string.isEmpty()) return false;
        return string.matches("^[a-zA-Z0-9]*$");
    }

    /**
     * checks if a string is numeric
     * @param string - string to check
     * @return if the string is numeric or not
     */
    public static boolean isNum(String string){
        Objects.requireNonNull(string, "string cannot be null");
        return string.matches("\\d+");
    }
    /**
     * checks if a character array has the right delimeters
     * @param chars - the character array to check
     * @return if the character array has the right delimeters
     */
    public static boolean isDelimeter(char[] chars){
        Objects.requireNonNull(chars, "string cannot be null");
        if(chars.length==0)return false;
        return chars[chars.length-1] == '\n' && chars[chars.length-TWO] == '\r';
    }




}
