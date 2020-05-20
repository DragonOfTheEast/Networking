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
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Challenge Tests")
public class CredentialsTest {
    final String CHR_ENC = "ISO-8859-1";
    Credentials credentials = new Credentials("000A0F");
    Credentials credentials1 = new Credentials("000A0F");
    Credentials credentials2 = new Credentials("010B0E");

    CredentialsTest() throws ValidationException {
    }

    @Nested
    @DisplayName("Credentials Decode")
    class CredentialsDecode {
        @Test
        @DisplayName("should work")
        void testDecode() throws ValidationException, IOException {
            String testString = "CRED 000B0E\r\n";
            MessageInput input = new MessageInput(
                    new ByteArrayInputStream(
                            testString.getBytes(CHR_ENC)));
            assertEquals(new Credentials("000B0E"), Message.decode(input));
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
            String testStringInvalid = "CRED 000b0f\r\n";
            assertThrows(ValidationException.class,
                    () -> Message.decode(new MessageInput(
                            new ByteArrayInputStream(
                                    testStringInvalid.getBytes(CHR_ENC)))));
        }

        @Test
        @DisplayName("doublePrematureEOS")
        void testDecodeDoublePrematureEOS() throws IOException,
                ValidationException {
            String testStringValid = "CRED 000B0E\r\n";
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
    @DisplayName("Credentials Encode")
    class CredentialsEncode {
        @Test
        @DisplayName("should work")
        void testEncode() throws IOException {
            String string = "CRED 010B0E\r\n";
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            credentials2.encode(new MessageOutput(output));
            assertEquals(string, new String(output.toByteArray()));
        }

        @Test
        @DisplayName("null")
        void testEncodeNull() {
            assertThrows(NullPointerException.class,
                    () -> credentials.encode(new MessageOutput(null)));
        }
    }

    @Test
    @DisplayName("Testing getOperation")
    void testGetOperation() {
        String expected = "CRED";
        assertEquals(expected, credentials.getOperation());
    }

    @Test
    @DisplayName("toString")
    void testToString() {
        String expected = "Credentials: hash=000A0F";
        assertEquals(expected, credentials.toString());
    }

    @Nested
    @DisplayName("Equals")
    class CredentialsEquals {
        @Test
        @DisplayName("equal")
        void testEquals() {
            assertEquals(credentials, credentials1);
        }

        @Test
        @DisplayName("!equal")
        void testEqualsDiffer() {
            assertNotEquals(credentials, credentials2);
        }
    }

    @Nested
    @DisplayName("Hashcode")
    class CredentialsHashCode {
        @Test
        @DisplayName("equal")
        void testHashCode() {
            assertEquals(credentials.hashCode(), credentials1.hashCode());
        }

        @Test
        @DisplayName("!equal")
        void testHashCodeDiffer() {
            assertNotEquals(credentials.hashCode(), credentials2.hashCode());
        }
    }

    @Test
    @DisplayName("Credentials getHash")
    void testGetHash() {
        String expected = "000A0F";
        assertEquals(expected, credentials.getHash());
    }

    @Nested
    @DisplayName("Credentials setHash")
    class CredentialsSetCredentials {
        @Test
        @DisplayName("should work")
        void testSetCredentials() throws ValidationException {
            String newHash = "000A0E";
            credentials.setHash(newHash);
            assertEquals(newHash, credentials.getHash());
        }

        @Test
        @DisplayName("null")
        void testSetCredentialsNull() {
            assertThrows(ValidationException.class,
                    () -> credentials.setHash(null));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "bad one", "000AF"})
        @DisplayName("bad Credentials")
        void setCredentialsTest(String input) {
            assertThrows(ValidationException.class,
                    () -> credentials.setHash(input));
        }
    }

    @Nested
    @DisplayName("hash String Validation")
    class CredentialsValidation {
        @ParameterizedTest
        @ValueSource(strings = {"0000", "12345678",
                "0A0B0C0D0E0F"})
        @DisplayName("valid")
        void testValidCredentialsString(String input) {
            assertTrue(Credentials.validHash(input));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "messi10"})
        @DisplayName("invalid")
        void testInvalidHash(String input) {
            assertFalse(Credentials.validHash(input));
        }
    }

}
