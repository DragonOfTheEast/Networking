/*
 * Chinagorom Mbaraonye
 * CSI 4321
 */
package topic.serialization;


import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static topic.serialization.Query.*;

/**
 * @author Chinagorom Mbaraonye
 * @version 1.1
 */
public class Response extends Message {
    private ErrorCode errorCode; //error code
    private List<String> posts; //lists of posts

    /**
     * Creates a new response given individual attributes
     * @param queryID ID for response
     * @param errorCode error code for response
     * @param posts  list of posts
     * @throws IllegalArgumentException See setters for specific validation of arguments
     */
    public Response(long queryID, ErrorCode errorCode, List<String> posts) throws IllegalArgumentException{
        this.setQueryID(queryID);
        this.setErrorCode(errorCode);
        this.setPosts(posts);
    }

    /**
     * Deserialize Response
     * @param buffer bytes from which to deserialize
     * @throws TopicException if validation fails (treat null buffer like empty array).
     * Validation problems include insufficient/excess bytes (PACKETTOOSHORT/LONG), bad QR field value (UNEXPECTEDPACKETTYPE), incorrect version (BADVERSION),
     * bad reserve (NETWORKERROR), unexpected error code (UNEXPECTEDERRORCODE), or other validation problems (VALIDATIONERROR)
     */
    public Response(byte[] buffer) throws TopicException{
        if(buffer == null || buffer.length < Constants.EIGHT){
            throw new TopicException(ErrorCode.PACKETTOOSHORT);
        }
        byte curr = buffer[0];
        Query.checkVersion(buffer);
        byte QR = getMybit(curr, Constants.THREE);
        if(!TopicConstants.RBIT.equals(String.valueOf(QR))){
            throw new TopicException(ErrorCode.UNEXPECTEDPACKETTYPE);
        }
        checkRSVD(buffer);
        checkErrorCode(buffer);
        long id = getMEQueryID(buffer);
        this.setQueryID(id);
        int num = postNum(buffer);


        List<String> temp = new ArrayList<>();
        int i = Constants.EIGHT;

          while( i < buffer.length){
              int len = postLen(buffer, i);

              if (num == 0){
                  throw new TopicException(ErrorCode.PACKETTOOLONG);
              }
              i += Constants.TWO;

              if (i > buffer.length && len != 0){
                  throw new TopicException(ErrorCode.PACKETTOOLONG);
              }

              StringBuilder stringBuilder = new StringBuilder();
              int trash = i;

              while(i < trash + len){
                  stringBuilder.append(getPost(buffer,i));
                  i++;
              }



              temp.add(String.valueOf(stringBuilder));

          }


        this.setPosts(temp);
    }

    /**
     * check if error code is in range
     * @param buffer byte buffer
     * @throws TopicException if error code is in range
     */
    public void checkErrorCode(byte[] buffer)throws TopicException{
        byte curr = buffer[1];
        StringBuilder string = new StringBuilder();
        for(int i = 7; i > -1; i--){
            string.append(getMybit(curr, i));
        }
        int val = Integer.parseInt(String.valueOf(string), Constants.TWO);
        if(val < 0 || val > 8 || val == 6){
            throw new TopicException(ErrorCode.UNEXPECTEDERRORCODE);
        }
        this.setErrorCode(ErrorCode.getErrorCode(val));
    }

    /**
     * get individual post
     * @param bytes byte buffer
     * @param val starting postion
     * @return post
     * @throws TopicException if there was an error getting post
     */
    public String getPost(byte[] bytes, int val) throws TopicException {

        byte curr;
        try{
            curr= bytes[val];
        } catch (ArrayIndexOutOfBoundsException e){
            throw new TopicException(ErrorCode.PACKETTOOSHORT);
        }
        StringBuilder stringBuilder = new StringBuilder();
        if((int)curr < 32 || (int)curr > 126){
            throw new TopicException(ErrorCode.VALIDATIONERROR);
        }
        char ch = (char) curr;
        stringBuilder.append(ch);
        return String.valueOf(stringBuilder);
    }


    /**
     * get post length
     * @param bytes byte buffer
     * @param val position to start from
     * @return length of post
     * @throws TopicException if there was an error in post length
     */
    public int postLen(byte[] bytes, int val) throws TopicException{
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = val; i < val + Constants.TWO && i < bytes.length; i++){
            byte curr;
            try{
                curr = bytes[i];
            }catch (ArrayIndexOutOfBoundsException e){
                throw new TopicException(ErrorCode.PACKETTOOSHORT);
            }
            for(int j = Constants.SEVEN; j >-1; j--){
                stringBuilder.append(getMybit(curr, j));
            }
        }
        int ret = Integer.parseInt(String.valueOf(stringBuilder), Constants.TWO);
        if (ret < 0  || ret > TopicConstants.MAXPOSTS){
            throw new TopicException(ErrorCode.VALIDATIONERROR);
        }
        return ret;
    }

    /**
     * Number of posts
     * @param bytes byte buffer
     * @return number of posts
     * @throws TopicException if it is out of range
     */
    public int postNum(byte[]bytes) throws TopicException {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = Constants.SIX; i < Constants.EIGHT; i++){
            byte curr = bytes[i];
            for(int j = Constants.SEVEN; j >-1; j--){
                stringBuilder.append(getMybit(curr, j));
            }
        }
        int ret = Integer.parseInt(String.valueOf(stringBuilder), Constants.TWO);
        return ret;
    }

    /**
     * Set the response list of posts
     * @param posts new list of posts
     * @return Response with new posts
     * @throws IllegalArgumentException if (list is null or outside length range) OR
     * (an individual post is null, outside length range,
     * or post contains illegal characters)
     */
    public final Response setPosts(List<String> posts) throws IllegalArgumentException{
        if (posts == null || posts.size() > TopicConstants.MAXPOSTS) {
            throw new IllegalArgumentException
                    ("Post cannot be null and has to be in range of allowed values");
        }
         for(String i : posts){
             if (i == null){
                 throw new IllegalArgumentException("A post is null");
             }else if(i.length() > TopicConstants.MAXPOSTS){
                 throw new IllegalArgumentException("A post is too long");
             }
             boolean good = StandardCharsets.US_ASCII.newEncoder().canEncode(i);
             if(!good){
                 throw new IllegalArgumentException("Illegal Character");
             }
             char [] post = i.toCharArray();

             for(char ch : post){
                 if (ch < 32 || ch > 126){
                     throw new IllegalArgumentException("Illegal Character");
                 }
             }
         }
         this.posts = posts;
         return this;
    }

    /**
     * Set the response error code
     * @param errorCode new error code
     * @return Response with new error code
     * @throws IllegalArgumentException if errorCode is null
     */
    public Response setErrorCode(ErrorCode errorCode) throws IllegalArgumentException{
        if (errorCode == null){
            throw new IllegalArgumentException("Error code cannot be null");
        }
        this.errorCode = errorCode;
        return this;
    }

    /**
     * Get the error code
     * @return error code
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Get the response list of posts
     * @return current list of posts
     */
    public List<String> getPosts() {
        return posts;
    }


    /**
     * serializes message
     * @return message in bytes
     */
    @Override
    public byte[] encode() {
        int array_size = Constants.EIGHT;
        for (String string : getPosts()){
            array_size+= string.length() +Constants.TWO;
        }
        byte[] buf = new byte[array_size];
        buf[0] = 0x28;
        buf[1] = (byte) errorCode.getErrorCodeValue();
        //encoding query ID
        byte [] queryID = ByteBuffer.allocate(Constants.FOUR).putInt((int)getQueryID()).array();
        System.arraycopy(queryID, 0, buf, Constants.TWO, Constants.FOUR);
        byte [] data = ByteBuffer.allocate(Constants.TWO).putShort((short) posts.size()).array();
        System.arraycopy(data, 0, buf, Constants.SIX, Constants.TWO);
        int i = Constants.EIGHT;
        //encoding posts
        for(String string:getPosts()){
            byte[] size = ByteBuffer.allocate(Constants.TWO).putShort((short)string.length()).array();
            byte[] val = string.getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(size, 0, buf, i, Constants.TWO);
            System.arraycopy(val,0,buf,i+Constants.TWO, string.length());
            i+= string.length() + Constants.TWO;
        }
        return buf;
    }

    /**
     * Returns a String representation
     * @return QueryID= id Posts= noPosts: post1, ..., postn
     */
    @Override
    public String toString() {
        String string = "Response: QueryID=" +this.getQueryID()+ " Posts=" + posts.size()+ ": ";
        StringBuilder stringBuilder = new StringBuilder(string);
        String string1 = String.join(", ", posts);
        stringBuilder.append(string1);
        return String.valueOf(stringBuilder);
    }


    /**
     *Checks inequality of objects
     * @return the hashcode of an object
     */
    @Override
    public int hashCode() {
        return Objects.hash(errorCode, this.getQueryID(), posts);
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
        Response a_new = (Response) o;
        return this.errorCode.equals(a_new.errorCode) && this.getQueryID() == a_new.getQueryID()
        && this.posts.equals(a_new.getPosts());
    }
}
