/*
    Chinagorom Mbraonye
    CSI 4321
 */
package topic.serialization;

import java.util.Objects;

/**
 * @author Chinagorom Mbaraonye
 * @version 1.1
 */
@SuppressWarnings("serial")
public class TopicException extends Exception{
    private ErrorCode errorCode; //class error code

    /**
     * Class constructor
     * @param errorCode wanted error code
     */
    public TopicException(ErrorCode errorCode){
        super(errorCode.getErrorMessage());
        Objects.requireNonNull(errorCode, "Errorcode cannot be null");
        this.errorCode = errorCode;
    }

    /**
     * Default constructor
     * @param errorCode error code wanted
     * @param cause cause of error
     */
    public TopicException(ErrorCode errorCode, Throwable cause){
        super(errorCode.getErrorMessage(), cause);
        Objects.requireNonNull(errorCode, "Errorcode cannot be null");
        this.errorCode = errorCode;
    }

    /**
     * get error code
     * @return return error code
     */
    public ErrorCode getErrorCode(){
        return errorCode;
    }
}
