/*
 * Author: Chinagorom Mbaraonye
 * Program: 0
 * CSI 4321 - Data communications
 */
package tiktak.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tiktak.serialization.Error;
import tiktak.serialization.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Error Tests")
class ErrorTest {
    final String CHR_ENC = "ISO-8859-1";
    Error error = new Error(404, "page not found");
    Error error1 = new Error(404, "page not found");
    Error error2 = new Error(202, "accepted");

    ErrorTest() throws ValidationException {
    }

    @Nested
    @DisplayName("Error Decode")
    class ErrorDecode {
        @Test
        @DisplayName("should work")
        void testDecode() throws ValidationException, IOException {
            String testString = "ERROR 404 page not found\r\n";
            MessageInput input = new MessageInput(
                    new ByteArrayInputStream(
                            testString.getBytes(CHR_ENC)));
            assertEquals(error, Message.decode(input));
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
            String testStringInvalid = "ERROR 202accpted\r\n";
            assertThrows(ValidationException.class,
                    () -> Message.decode(new MessageInput(
                            new ByteArrayInputStream(
                                    testStringInvalid.getBytes(CHR_ENC)))));
        }

        @Test
        @DisplayName("doublePrematureEOS")
        void testDecodeDoublePrematureEOS() throws IOException,
                ValidationException {
            String testStringValid = "ERROR 404 page not found\r\n";
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
    @DisplayName("Error Encode")
    class ErrorEncode {
        @Test
        @DisplayName("should work")
        void testEncode() throws IOException {
            String expected = "ERROR 404 page not found\r\n";
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            error.encode(new MessageOutput(output));
            assertEquals(expected, new String(output.toByteArray()));
        }

        @Test
        @DisplayName("null")
        void testEncodeNull() {
            assertThrows(NullPointerException.class,
                    () -> error.encode(new MessageOutput(null)));
        }
    }

    @Test
    @DisplayName("getOperation")
    void testGetOperation() {
        String expected = "ERROR";
        assertEquals(expected, error.getOperation());
    }

    @Test
    @DisplayName("Error toString")
    void testToString() {
        String expected = "Error: code=404 message=page not found";
        assertEquals(expected, error.toString());
    }

    @Test
    @DisplayName("getCode")
    void testGetCode() {
        int expected = 404;
        assertEquals(expected, error.getCode());
    }

    @Test
    @DisplayName("setCode")
    void testSetCode() throws ValidationException {
        int newCode = 500;
        error.setCode(newCode);
        assertEquals(newCode, error.getCode());
    }

    @Test
    @DisplayName("getMessage")
    void testGetMessage() {
        String expected = "page not found";
        assertEquals(expected, error.getMessage());
    }

    @Test
    @DisplayName("setMessage")
    void testSetMessage() throws ValidationException {
        String newMessage = "message setting";
        error.setMessage(newMessage);
        assertEquals(newMessage, error.getMessage());
    }

    @Nested
    @DisplayName("Error Equals")
    class ErrorEquals {
        @Test
        @DisplayName("equal")
        void testEquals() {
            assertEquals(error, error1);
        }

        @Test
        @DisplayName("!equal")
        void testEqualsDiffer() {
            assertNotEquals(error, error2);
        }
    }

    @Nested
    @DisplayName("Error Hashcode")
    class ErrorHashCode {
        @Test
        @DisplayName("equal")
        void testHashCode() {
            assertEquals(error.hashCode(), error1.hashCode());
        }

        @Test
        @DisplayName("!equal")
        void testHashCodeDiffer() {
            assertNotEquals(error.hashCode(), error2.hashCode());
        }
    }

}