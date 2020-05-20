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
import static tiktak.serialization.Constants.CHR_ENC;

public class TostTest {

    private Tost tost= new Tost();
    private Tost tost1 = new Tost();

    private Challenge challenge = new Challenge("123");

    TostTest() throws ValidationException {
    }

    @Nested
    @DisplayName("Decode for Tests")
    class AckDecode{
        @Test
        @DisplayName("should work")
        void testDecode() throws ValidationException, IOException {
            String string = "TOST\r\n";

            MessageInput messageInput = new MessageInput(new ByteArrayInputStream(
                    string.getBytes(CHR_ENC)));
            assertEquals(new Tost(), Message.decode(messageInput));
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
        String string = "TOST \r\n";
        assertThrows(ValidationException.class,
                () -> Message.decode(new MessageInput(
                        new ByteArrayInputStream(
                                string.getBytes(CHR_ENC)))));
    }

    @Test
    @DisplayName("doublePrematureEOS")
    void testDecodeDoublePrematureEOS() throws IOException, ValidationException {
        String testStringValid = "TOST\r\n";
        MessageInput messageInput = new MessageInput(
                new ByteArrayInputStream(
                        testStringValid.getBytes(CHR_ENC)));
        Message.decode(messageInput);
        assertThrows(EOFException.class,
                () -> Message.decode(messageInput));
        messageInput.close();
    }

    @Nested
    @DisplayName("Encode for Tost")
    class TostEncode {

        @Test
        @DisplayName("Null test")
        void testNull(){
            assertThrows(NullPointerException.class, ()-> tost.encode(new MessageOutput(null)));
        }
        @Test
        @DisplayName("should work")
        void testEncode() throws IOException{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            tost.encode(new MessageOutput(byteArrayOutputStream));
            assertEquals("TOST\r\n", new String(byteArrayOutputStream.toByteArray()));
        }
    }



    @Nested
    @DisplayName("Hashcode")
    class VersionHashCode {
        @Test
        @DisplayName("equal")
        void testSameVer() {
            assertEquals(tost.hashCode(), tost1.hashCode());
        }
        @Test
        @DisplayName("!equal")
        void testDiffVer() {
            assertNotEquals(tost.hashCode(), challenge.hashCode());
        }
    }
    @Test
    @DisplayName("getOperation")
    void testGetOperation(){
        assertEquals("TOST", tost.getOperation());
    }

    @Test
    @DisplayName("toString")
    void testToString(){
        assertEquals("Tost", tost.toString());
    }

    @Test
    @DisplayName("Valid Tost")
    void testValidVersion(){
        String string = "TOST\r\n";
        assertEquals(string, Constants.TOST);
    }

    @ParameterizedTest
    @DisplayName("invalid Tost")
    @ValueSource(strings = {"", " ", "Tost\r\n", "Tost\r\n"})
    void testInvalidVersion(String string){
        assertNotEquals(Constants.TOST, string);
    }
}
