package topic.serialization.test;

//import static org.junit.Assert.assertArrayEquals;

import org.junit.jupiter.api.Test;

import topic.serialization.Query;
import topic.serialization.Response;
import topic.serialization.TopicException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SimpleTest {
    //Query with errorcode = NOERROR, id = 5, and requested posts of 2
    private byte[] encodedQuery = new byte[] {0x20, 0, 0, 0, 0, 5, 0, 2};
    private byte[] encodeResponse = new byte[]{'x', 'y'};
    private long queryid = 5;
    private int reqpost = 2;
    private int queryID2 = 0;


    //    @Test
////    void testEncode() {
////        assertArrayEquals(encodedQuery, new Query(queryid, reqpost).encode());
////    }d
    @Test
    void testDecode() throws TopicException {
        Query q = new Query(encodedQuery);
        assertEquals(queryid, q.getQueryID());
        assertEquals(reqpost, q.getRequestedPosts());
        assertArrayEquals(encodedQuery, q.encode());

        //Response response = new Response(encodeResponse);
        //System.out.println(response.getPosts().size());
        //System.out.println(response.getPosts());
        //assertThrows(TopicException.class, () -> new Response(encodeResponse));

        try{
            new Response(encodeResponse);
        }catch (TopicException e){
            System.out.println(e.getErrorCode().getErrorMessage());
        }
//        assertEquals(queryID2, response.getQueryID());
//        //System.out.println(response.getPosts());
//        assertEquals(0, response.getPosts().size());
//        assertArrayEquals(encodeResponse, response.encode());

    }
    @Test
    void testTooShort() throws TopicException{
        byte [] bytes = new byte[]{0x20, 5,9,9,7,8,8,8};
            try{
                new Query(bytes);
            }catch (TopicException e){
                //System.out.println(e.getErrorCode());
            }

//        assertThrows(TopicException.class,()-> new Query(bytes));
//        assertDoesNotThrow(()->new Query(bytes));
    }


    @Test
    void testToString() throws TopicException {
        String string = "Query: QueryID=5 ReqPosts=2";
        String string1 = "Response: QueryID=5 Posts=2: AAA, BBB";
        Query query = new Query(5,2);
        Response response = new Response(encodeResponse);
        //assertEquals(string,  query.toString());
        assertEquals(string1, response.toString());
    }


    @Test
    void testEquals(){
        Query query = new Query(5,21);
        Query query2 = new Query(5,2);

        assertNotEquals(query, query2);
    }

}




