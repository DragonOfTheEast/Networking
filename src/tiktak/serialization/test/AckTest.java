/*
 * Author: Chinagorom Mbaraonye
 * Program: 0
 * CSI 4321 - Data communications
 */
package tiktak.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tiktak.serialization.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tiktak.serialization.Constants.CHR_ENC;

@DisplayName("Ack Tests")
public class AckTest {

    private Ack ack = new Ack();
    private Ack ack1 = new Ack();

    private Challenge challenge = new Challenge("123");

    AckTest() throws ValidationException {
    }

    @Nested
    @DisplayName("Decode for Tests")
    class AckDecode{
        @Test
        @DisplayName("should work")
        void testDecode() throws ValidationException, IOException{
            String string = "ACK\r\n";

            MessageInput messageInput = new MessageInput(new ByteArrayInputStream(
                    string.getBytes(CHR_ENC)));
            assertEquals(new Ack(), Message.decode(messageInput));
        }
    }

    @Test
    @DisplayName("Null Decode")
    void testNullDecode(){
        assertThrows(NullPointerException.class, ()->Message.decode(null));
    }


    @Test
    @DisplayName("invalid decode")
    void testInvalidDecode(){
        String string = "ACK \r\n";
        assertThrows(ValidationException.class,
                () -> Message.decode(new MessageInput(
                        new ByteArrayInputStream(
                                string.getBytes(CHR_ENC)))));
    }

    @Test
    @DisplayName("doublePrematureEOS")
    void testDecodeDoublePrematureEOS() throws IOException, ValidationException {
        String testStringValid = "ACK\r\n";
        MessageInput messageInput = new MessageInput(
                new ByteArrayInputStream(
                        testStringValid.getBytes(CHR_ENC)));
        Message.decode(messageInput);
        assertThrows(EOFException.class,
                () -> Message.decode(messageInput));
        messageInput.close();
    }

    @Nested
    @DisplayName("Encode for Ack")
    class AckEncode {

        @Test
        @DisplayName("Null test")
        void testNull(){
            assertThrows(NullPointerException.class, ()-> ack.encode(new MessageOutput(null)));
        }
        @Test
        @DisplayName("should work")
        void testEncode() throws IOException{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            ack.encode(new MessageOutput(byteArrayOutputStream));
            assertEquals("ACK\r\n", new String(byteArrayOutputStream.toByteArray()));
        }
    }



    @Nested
    @DisplayName("Hashcode")
    class AckHashCode {
        @Test
        @DisplayName("equal")
        void testSameVer() {
            assertEquals(ack.hashCode(), ack1.hashCode());
        }
        @Test
        @DisplayName("!equal")
        void testDiffVer() {
            assertNotEquals(ack.hashCode(), challenge.hashCode());
        }
    }
    @Test
    @DisplayName("getOperation")
    void testGetOperation(){
        assertEquals("ACK", ack.getOperation());
    }

    @Test
    @DisplayName("toString")
    void testToString(){
        assertEquals("Ack", ack.toString());
    }

    @Test
    @DisplayName("Valid ack")
    void testValidAck(){
        String string = "ACK\r\n";
        assertEquals(string, Constants.ACK);
    }

    @ParameterizedTest
    @DisplayName("invalid ack")
    @ValueSource(strings = {"", " ", "ACk\r\n", "Ack\r\n"})
    void testInvalidAck(String string){
        assertNotEquals(Constants.ACK, string);
    }
}
