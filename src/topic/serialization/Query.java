/*
 * Chinagorom Mbaraonye
 * CSI 4321
 */
package topic.serialization;


import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * @author Chinagorom Mbaraonye
 * @version 1.1
 */
public class Query extends Message {

    private int requestedPosts; //number of requested posts

    /**
     * Creates a new query given individual attributes
     * @param queryID query id
     * @param requestedPosts number of requested posts
     * @throws IllegalArgumentException if values are out of range
     */
    public Query(long queryID, int requestedPosts)throws IllegalArgumentException{
        this.setQueryID(queryID);
        this.setRequestedPosts(requestedPosts);
    }

    /**
     * check if version was right
     * @param buffer byte buffer
     * @throws TopicException if version was wrong
     */
    public static void checkVersion(byte[] buffer) throws TopicException {
        StringBuilder string = new StringBuilder();
        byte curr = buffer[0];

        for (int i = Constants.SEVEN; i> Constants.THREE; i--){
            string.append(getMybit(curr, i));
        }
        if(!TopicConstants.VERSION.contentEquals(string)){
            throw new TopicException(ErrorCode.BADVERSION);
        }
    }

    /**
     * check if reserved for future is right
     * @param buffer byte buffer
     * @throws TopicException if it isnt 000
     */
    public static void checkRSVD(byte[] buffer) throws TopicException{
        StringBuilder string = new StringBuilder();
        byte curr = buffer[0];
        for(int i = 2; i > -1 ; i--){
            string.append(getMybit(curr, i));
        }
        if(!TopicConstants.RSVD.contentEquals(string)){
            throw new TopicException(ErrorCode.NETWORKERROR);
        }
    }

    /**
     * check if error code is 0
     * @param buffer byte buffer
     * @throws TopicException if error code is not 0
     */
    public static void checkErrorCode(byte[] buffer)throws TopicException{
        byte curr = buffer[1];
        StringBuilder string = new StringBuilder();
        for(int i = 7; i > -1; i--){
            string.append(getMybit(curr, i));
        }

        int val = Integer.parseInt(String.valueOf(string), Constants.TWO);
        if(0 != val){
            throw new TopicException(ErrorCode.UNEXPECTEDERRORCODE);
        }
    }

    /**
     * Deserialize Query
     * @param buffer bytes from which to deserialize
     * @throws TopicException if validation fails (treat null buffer like empty array).
     * Validation problems include insufficient/excess bytes (PACKETTOOSHORT/LONG),
     * bad QR field value (UNEXPECTEDPACKETTYPE), incorrect version (BADVERSION), bad reserve (NETWORKERROR),
     * non-zero error code (UNEXPECTEDERRORCODE), or other validation problems (VALIDATIONERROR)
     */
    public Query(byte[] buffer) throws TopicException{
            if(buffer == null || buffer.length < Constants.EIGHT){
            throw new TopicException(ErrorCode.PACKETTOOSHORT);
            }else if (buffer.length > Constants.EIGHT) {
            throw new TopicException(ErrorCode.PACKETTOOLONG);
            }
            byte curr = buffer[0];
            checkVersion(buffer);

            byte QR = getMybit(curr, Constants.THREE);
            if(!TopicConstants.QBIT.equals(String.valueOf(QR))){
            throw new TopicException(ErrorCode.UNEXPECTEDPACKETTYPE);
            }
            //checking resrved bit and error code
            checkRSVD(buffer);
            checkErrorCode(buffer);

            //getting id and number of posts
            long id = getMEQueryID(buffer);
            int posts = getMePosts(buffer);
            this.setQueryID(id);
            this.setRequestedPosts(posts);
        }

    /**
     * Set the number of requested posts in the message
     * @param requestedPosts new number of requested posts
     * @return Query with new requested posts
     * @throws IllegalArgumentException if number of requested posts is out of range
     */
    public final Query setRequestedPosts(int requestedPosts)throws IllegalArgumentException{
        if (requestedPosts < 0 || requestedPosts> TopicConstants.MAXPOSTS)
            throw new IllegalArgumentException("RequestedPosts is out of range");
        this.requestedPosts = requestedPosts;
        return this;
    }
    public int getRequestedPosts(){
        return requestedPosts;
    }

    /**
     * serializes message
     * @return message in bytes
     */
    @Override
    public byte[] encode() {
        byte[] buf = new byte[8];
        buf[0] = 0x20;
        buf[1] = 0;
        //query id
        byte [] queryID = ByteBuffer.allocate(Constants.FOUR).putInt((int)getQueryID()).array();
        System.arraycopy(queryID, 0, buf, Constants.TWO, Constants.FOUR);

        //number of requested post
        byte [] data = ByteBuffer.allocate(Constants.TWO).putShort((short) getRequestedPosts()).array();
        System.arraycopy(data, 0, buf, Constants.SIX, Constants.TWO);
        return buf;
    }

    /**
     * Gets a bit from a byte
     * @param val the byte
     * @param pos position of the bit
     * @return the wanted bit
     */
    public static byte getMybit(byte val, int pos){
        return (byte) ((val >> pos) & 1);
    }

    /**
     * Retrieves Query ID
     * @param bytes byte buffer
     * @return queryID
     * @throws TopicException if id is out of range
     */
    public static long getMEQueryID(byte[] bytes) throws TopicException {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = Constants.TWO; i < Constants.SIX; i++){
            byte curr = bytes[i];
            for(int j = Constants.SEVEN; j >-1; j--){
                    stringBuilder.append(getMybit(curr, j));
            }
        }
        long ret = Long.parseLong(String.valueOf(stringBuilder), 2);
        if (ret < 0 || ret > TopicConstants.MAXID ){
            throw new TopicException(ErrorCode.VALIDATIONERROR);
        }
        return ret;
    }

    /**
     * Retrieves the number of posts
     * @param bytes byte buffer
     * @return number of posts
     * @throws TopicException if value is out of range
     */
    public int getMePosts(byte[]bytes) throws TopicException {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = Constants.SIX; i < Constants.EIGHT; i++){
            byte curr = bytes[i];
            for(int j = Constants.SEVEN; j >-1; j--){
                stringBuilder.append(getMybit(curr, j));
            }
        }
        int ret = Integer.parseInt(String.valueOf(stringBuilder), 2);
        if (ret < 0  || requestedPosts> TopicConstants.MAXPOSTS ){
            throw new TopicException(ErrorCode.VALIDATIONERROR);
        }
        return ret;
    }

    /**
     * Returns a String representation
     * @return Query: QueryID=id ReqPosts=noPosts
     */
    @Override
    public String toString() {
        return "Query: QueryID=" +this.getQueryID()+ " ReqPosts=" + this.getRequestedPosts();
    }


    /**
     *Checks inequality of objects
     * @return the hashcode of an object
     */
    @Override
    public int hashCode() {
        return Objects.hash(requestedPosts, this.getQueryID());
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
        Query a_new = (Query) o;
        return this.requestedPosts== (a_new.getRequestedPosts()) && this.getQueryID() == a_new.getQueryID();
    }
}
