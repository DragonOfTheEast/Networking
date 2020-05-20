/*
 * Author: Chinagorom Mbaraonye
 * Program: 0
 * CSI 4321 - Data communications
 */
package tiktak.serialization;

/**
 *
 * @author Chinagorom Mbaraonye
 * @version 1.0
 */
public class ValidationException extends Exception {
    private String badToken;

    /**
     *
     * @param message - exception message
     * @param cause - exception cause
     * @param badToken - string that caused the exception
     *                  (null if no such string)
     */
    public ValidationException(String message, Throwable cause,
                               String badToken){
        super(message, cause);
        this.badToken = badToken;
    }

    /**
     *
     * @param message - exception message
     * @param badToken - string that caused the exception
     *                (null if no such string)
     */
    public ValidationException(String message, String badToken){
        super(message);
        this.badToken = badToken;
    }

    /**
     * Returns bad token
     * @return bad token
     */
    public String getBadToken() {
        return badToken;
    }
}
