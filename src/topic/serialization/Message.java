/*
Chinagorom Mbaraonye
CSI 4321
 */
package topic.serialization;

/**
 * @author Chinagorom Mbaraonye
 * @version 1.1
 */
public abstract class Message {
    private long queryID; //id of each query/response

    /**
     * Message constructor
     * @param queryID id of the message constructed
     * @return instance created
     * @throws IllegalArgumentException if query id is out of range
     */
    public final Message setQueryID(long queryID) throws IllegalArgumentException{
            if(queryID < 0 || queryID > TopicConstants.MAXID){
                throw new IllegalArgumentException("QueryID has to be in range");
            }
            this.queryID = queryID;
            return this;
    }

    /**
     * returns query id
     * @return query id
     */
    public long getQueryID() {
        return queryID;
    }

    /**
     * serializes message
     * @return message in bytes
     */
    public abstract byte[] encode();

    /**
     * String representation of the class
     * @return a string representing the class
     */
    public abstract String toString();

    /**
     *Checks inequality of objects
     * @return the hashcode of an object
     */
    public abstract int hashCode();


    /**
     *Checks the equality of objects
     * @param o object to compare to this
     * @return equality of the two objects
     */
    public abstract boolean equals(Object o);
}
