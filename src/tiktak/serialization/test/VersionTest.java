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

@DisplayName("Version Tests")
public class VersionTest {


    private Version version = new Version();
    private Version version1 = new Version();
    private Challenge challenge = new Challenge("123");


    public VersionTest() throws ValidationException {
    }



    @Nested
    @DisplayName("Decode for Version")
    class VersionDecode{
        @Test
        @DisplayName("Should work")
        void testDecode() throws ValidationException, IOException{
            String string = "TIKTAK 1.0\r\n";

            MessageInput messageInput = new MessageInput(new ByteArrayInputStream(
                    string.getBytes(CHR_ENC)));
            assertEquals(new Version(), Message.decode(messageInput));
        }

        @Test
        @DisplayName("Null Decode")
        void testNullDecode(){
            assertThrows(NullPointerException.class, ()-> Message.decode(null));
        }

        @Test
        @DisplayName("invalid Decode")
        void testInvalidDecode(){
            String string ="Tik_tak 1.1\r\n";
            assertThrows(ValidationException.class,
                    () -> Message.decode(new MessageInput(
                            new ByteArrayInputStream(
                                    string.getBytes(CHR_ENC)))));
        }

        @Test
        @DisplayName("Premature EOS")
        void testDecodeDoublePrematureEOS() throws IOException, ValidationException {
            String test = "TIKTAK 1.0\r\n";
            MessageInput messageInput = new MessageInput(new ByteArrayInputStream(test.getBytes(CHR_ENC)));
            Message.decode(messageInput);
            assertThrows(EOFException.class, ()-> Message.decode(messageInput));
            messageInput.close();

        }

        @Test
        @DisplayName("Premature EOS")
        void testDecodePrematureEOS() throws IOException {
            String test = "";
            MessageInput messageInput = new MessageInput(new ByteArrayInputStream(test.getBytes(CHR_ENC)));
            assertThrows(EOFException.class, ()-> Message.decode(messageInput));
            messageInput.close();

        }
    }

    @Nested
    @DisplayName("Encode for version")
    class VersionEncode {

        @Test
        @DisplayName("Null test")
        void testNull(){
            assertThrows(NullPointerException.class, ()-> version.encode(new MessageOutput(null)));
        }
        @Test
        @DisplayName("should work")
        void testEncode() throws IOException{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            version.encode(new MessageOutput(byteArrayOutputStream));
            assertEquals("TIKTAK 1.0\r\n", new String(byteArrayOutputStream.toByteArray()));
        }
    }

    @Nested
    @DisplayName("Hashcode")
    class VersionHashCode {
        @Test
        @DisplayName("equal")
        void testSameVer() {
            assertEquals(version.hashCode(), version1.hashCode());
        }
        @Test
        @DisplayName("!equal")
        void testDiffVer() {
            assertNotEquals(version.hashCode(), challenge.hashCode());
        }
    }
    @Test
    @DisplayName("getOperation")
    void testGetOperation(){
        assertEquals("TIKTAK", version.getOperation());
    }

    @Test
    @DisplayName("toString")
    void testToString(){
        assertEquals("TikTak", version.toString());
    }

   @Test
   @DisplayName("Valid Version")
    void testValidVersion(){
        String string = "TIKTAK 1.0\r\n";
        assertEquals(string, Constants.VERSION);
   }

   @ParameterizedTest
    @DisplayName("invalid version")
    @ValueSource(strings = {"", " ", "TikTak 1.0\r\n", "TIKTAK\r\n"})
    void testInvalidVersion(String string){
        assertNotEquals(Constants.VERSION, string);
   }
}
