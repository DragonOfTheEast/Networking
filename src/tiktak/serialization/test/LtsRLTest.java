/*
 * Author: Chinagorom Mbaraonye
 * Program: 0
 * CSI 4321 - Data communications
 */
package tiktak.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tiktak.serialization.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static tiktak.serialization.Constants.CHR_ENC;

@DisplayName("LtsRL Tests")
class LtsRLTest {
    private String string = "dGVzdFN0cmluzw";
    private byte[] image = Base64.getDecoder().decode(string);
    private String string1 = "bmV3SW1HZ2U";
    private byte[] newImage = Base64.getDecoder().decode(string1);
    private LtsRL ltsRL = new LtsRL("music", image);
    private LtsRL ltsRL1 = new LtsRL("music", image);
    private LtsRL ltsRL2 = new LtsRL("animation", newImage);

    LtsRLTest() throws ValidationException, IOException {
    }

    @Nested
    @DisplayName("LtsRL Decode")
    class LtsRLDecode {
        @Test
        @DisplayName("should work")
        void testDecode() throws ValidationException, IOException {
            String testString = "LTSRL music " + string + "\r\n";
            MessageInput messageInput = new MessageInput(
                    new ByteArrayInputStream(
                            testString.getBytes(CHR_ENC)));
            assertEquals(ltsRL.getCategory(),
                    ((LtsRL) Message.decode(messageInput)).getCategory());
            messageInput = new MessageInput(
                    new ByteArrayInputStream(
                            testString.getBytes(CHR_ENC)));
            assertArrayEquals(ltsRL.getImage(),
                    ((LtsRL) Message.decode(messageInput)).getImage());
        }

        @Test
        @DisplayName("null")
        void testDecodeNull() {
            assertThrows(NullPointerException.class,
                    () -> Message.decode(null));
        }

        @Test
        @DisplayName("invalid")
        void testDecodeInvalid() {
            String testStringInvalid = "LTSRL music" + string + "\r\n";
            assertThrows(ValidationException.class,
                    () -> Message.decode(new MessageInput(
                            new ByteArrayInputStream(
                                    testStringInvalid.getBytes(CHR_ENC  )))));
        }

        @Test
        @DisplayName("doublePrematureEOS")
        void testDecodeDoublePrematureEOS() throws IOException,
                ValidationException {
            String testStringValid = "LTSRL music " + string + "\r\n";
            MessageInput messageInput = new MessageInput(
                    new ByteArrayInputStream(
                            testStringValid.getBytes(CHR_ENC)));
            Message.decode(messageInput);
            assertThrows(EOFException.class,
                    () -> Message.decode(messageInput));
            messageInput.close();
        }
    }

    @Nested
    @DisplayName("LtsRL Encode")
    class LtsRLEncode {
        @Test
        @DisplayName("should work")
        void testEncode() throws IOException {
            String expected = "LTSRL music " + string + "\r\n";
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ltsRL.encode(new MessageOutput(output));
            assertEquals(expected, new String(output.toByteArray()));
        }

        @Test
        @DisplayName("null")
        void testEncodeNull() {
            assertThrows(NullPointerException.class,
                    () -> ltsRL.encode(new MessageOutput(null)));
        }
    }

    @Test
    @DisplayName("getOperation")
    void testGetOperation() {
        String expected = "LTSRL";
        assertEquals(expected, ltsRL.getOperation());
    }

    @Test
    @DisplayName("toString")
    void testToString() {
        String expected = "LtsRL: category=music image=10 bytes";
        assertEquals(expected, ltsRL.toString());
    }

    @Test
    @DisplayName("getCategory")
    void testGetCategory() {
        String expected = "music";
        assertEquals(expected, ltsRL.getCategory());
    }

    @Test
    @DisplayName("setCategory")
    void testSetCategory() throws ValidationException {
        String newCate = "soccer";
        ltsRL.setCategory(newCate);
        assertEquals(newCate, ltsRL.getCategory());
    }

    @Test
    @DisplayName("getImage")
    void testGetImage() {
        assertEquals(image, ltsRL.getImage());
    }

    @Test
    @DisplayName("setImage")
    void testSetImage() throws ValidationException {
        ltsRL.setImage(newImage);
        assertEquals(newImage, ltsRL.getImage());
    }

    @Nested
    @DisplayName("Equals")
    class LtsRLEquals {
        @Test
        @DisplayName("equal")
        void testEquals() {
            assertEquals(ltsRL, ltsRL1);
        }

        @Test
        @DisplayName("!equal")
        void testEqualsDiffer() {
            assertNotEquals(ltsRL, ltsRL2);
        }
    }

    @Nested
    @DisplayName("LtsRL Hashcode")
    class LtsRLHashCode {
        @Test
        @DisplayName("equal")
        void testHashCode() {
            assertEquals(ltsRL.hashCode(), ltsRL1.hashCode());
        }

        @Test
        @DisplayName("!equal")
        void testHashCodeDiffer() {
            assertNotEquals(ltsRL.hashCode(), ltsRL2.hashCode());
        }
    }
    
}