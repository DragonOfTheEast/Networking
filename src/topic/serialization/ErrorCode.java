package topic.serialization;

/*
    Chinagorom Mbaraonye
    CSI 4321
 */
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Chinagorom Mbaraonye
 * @version 1.1
 */
public enum ErrorCode{
    NOERROR(0, "No error"),
    BADVERSION(1, "Bad version"),
    UNEXPECTEDERRORCODE(2, "Unexpected error code"),
    UNEXPECTEDPACKETTYPE(3, "Unexpected packet type"),
    PACKETTOOLONG(4, "Packet too long"),
    PACKETTOOSHORT(5, "Packet too short"),
    NETWORKERROR(7, "Network error"),
    VALIDATIONERROR(8, "Validation error");


    private final int errorCodeValue; //error code value
    private final String errorMessage; //error message

    /**
     * Constructor of class
     * @param errorCodeValue error code value
     * @param errorMessage   error message
     */
    ErrorCode(int errorCodeValue, String errorMessage) {
        Objects.requireNonNull(errorMessage);
        this.errorCodeValue = errorCodeValue;
        this.errorMessage = errorMessage;
    }

    /**
     * gets error code value
     * @return error code value
     */
    public int getErrorCodeValue(){
        return errorCodeValue;
    }

    /**
     * gets error message
     * @return  error message
     */
    public String getErrorMessage(){
        return errorMessage;
    }

    /**
     * Finds error code linked with the value
     * @param errorCodeValue value in question
     * @return  ErrorCode
     * @throws IllegalArgumentException if error code value does not exist
     */
    public static ErrorCode getErrorCode(int errorCodeValue) throws IllegalArgumentException{
        if (errorCodeValue < 0 || errorCodeValue > 8){
            throw new IllegalArgumentException();
        }
        if(errorCodeValue==6)throw new IllegalArgumentException("Illegal error code");
        return Arrays.stream(values()).filter(n-> n.errorCodeValue==errorCodeValue).findFirst().get();
    }
}
