/*
 * Author: Chinagorom Mbaraonye
 * Program: 0
 * CSI 4321 - Data communications
 */
package tiktak.serialization.test;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tiktak.serialization.Message;
import tiktak.serialization.MessageInput;
import tiktak.serialization.ValidationException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static tiktak.serialization.Constants.CHR_ENC;


@DisplayName("Message Test")
public class MessageTest {

    @Test
    @DisplayName("Null testing")
    void testMsgNull(){
        assertThrows(NullPointerException.class, ()-> Message.decode(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Invalid msg\r\n", "11111223cff\r\n"})
    @DisplayName("Invalid Decoding")
    void testInvalidDecoding(String string){
        assertThrows(ValidationException.class, ()-> Message.decode(
                new MessageInput(new ByteArrayInputStream(string.getBytes(CHR_ENC)))));
    }

    @Test
    @DisplayName("Version Decode")
    void testVersionDecode() throws ValidationException, IOException {
        String op = "TIKTAK";
        String version_op = "TIKTAK 1.0\r\n";
        assertEquals(op, Message.decode(new MessageInput(
                new ByteArrayInputStream(version_op.getBytes(CHR_ENC)))).getOperation());
    }

    @Test
    @DisplayName("ID decode")
    void testIDDecode() throws ValidationException, IOException {
        String op = "ID";
        String id_op = "ID joe\r\n";

      assertEquals(op, Message.decode(new MessageInput(
              new ByteArrayInputStream(id_op.getBytes(CHR_ENC)))).getOperation());
    }

    @Test
    @DisplayName("Challenge Decode")
    void testChallengeDecode() throws ValidationException,IOException{
        String op = "CLNG";
        String clng_op = "CLNG 321\r\n";

        assertEquals(op, Message.decode(new MessageInput(
              new ByteArrayInputStream(clng_op.getBytes(CHR_ENC)))).getOperation());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234", "1", "10"})
    @DisplayName("testing isNum- pass")
    void testisNum(String string){
        assertTrue(Message.isNum(string));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234a", "", " "})
    @DisplayName("testing isNum- fail")
    void testisNumFail(String string)
    {
        assertFalse(Message.isNum(string));
    }

    @Test
    @DisplayName("testing null")
    void testIsNumNull(){
        assertThrows(NullPointerException.class, ()->Message.isNum(null));
    }


    @ParameterizedTest
    @ValueSource(strings = {"1234\r\n", "1a\r\n", "10bb\r\n"})
    @DisplayName("testing isDelimeter- pass")
    void testIsADelimeter(String string){
        assertTrue(Message.isDelimeter(string.toCharArray()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234a!", "", " "})
    @DisplayName("testing isAlphaNum- fail")
    void testIsAlphaNumFail(String string){
        assertFalse(Message.isAlphaNum(string));
    }

    @Test
    @DisplayName("testing null")
    void testIsAlphaNumNull(){
        assertThrows(NullPointerException.class, ()->Message.isAlphaNum(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234", "1a", "10bb"})
    @DisplayName("testing isAlphaNum- pass")
    void testIsAlphaNum(String string){
        assertTrue(Message.isAlphaNum(string));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234a!", "", " "})
    @DisplayName("testing isDelimeter- fail")
    void testIsDelimterFail(String string){
        assertFalse(Message.isAlphaNum(string));
    }

    @Test
    @DisplayName("testing null")
    void testIsDelimeterNull(){
        assertThrows(NullPointerException.class, ()->Message.isDelimeter(null));
    }



    @Test
    void testCredentialsDecode() throws ValidationException, IOException{
        String op ="CRED";
        String cred_op = "CRED 000B0E\r\n";

        assertEquals(op, Message.decode(new MessageInput(
                new ByteArrayInputStream(cred_op.getBytes(CHR_ENC)))).getOperation());
    }

    @Test
    void testLtsRLDecode() throws ValidationException, IOException{
        String op= "LTSRL";
        String ltsrl_op = "LTSRL category dGVzdFN0cmluzw\r\n";

        assertEquals(op, Message.decode(new MessageInput(
                new ByteArrayInputStream(ltsrl_op.getBytes(CHR_ENC)))).getOperation());
    }

    @Test
    void testTostDecode() throws ValidationException, IOException{
        String op = "TOST";
        String tost_op = "TOST\r\n";

        assertEquals(op, Message.decode(new MessageInput(
                new ByteArrayInputStream(tost_op.getBytes(CHR_ENC)))).getOperation());
    }

    @Test
    void testACKDecode() throws ValidationException, IOException{
        String op = "ACK";
        String ack_op = "ACK\r\n";

        assertEquals(op, Message.decode(new MessageInput(
                new ByteArrayInputStream(ack_op.getBytes(CHR_ENC)))).getOperation());
    }

    @Test
    void testErrorDecode() throws ValidationException, IOException{
        String op = "ERROR";
        String error_op = "ERROR 202 HELP ME\r\n";

        assertEquals(op, Message.decode(new MessageInput(
                new ByteArrayInputStream(error_op.getBytes(CHR_ENC)))).getOperation());
    }


}
