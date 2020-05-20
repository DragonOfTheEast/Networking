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


@DisplayName("Challenge Tests")
public class ChallengeTest {

    Challenge challenge = new Challenge("123");
    Challenge challenge1 = new Challenge("123");
    Challenge challenge2 = new Challenge("1234");


    public ChallengeTest() throws ValidationException {
    }


    @Test
    @DisplayName("Null for constructor")
    void testNullConstructor() throws ValidationException{
        assertThrows(ValidationException.class, ()-> new Challenge(null));
    }

    @Test
    @DisplayName("Invalid nonce for constructor")
    void testInvalidNonceConstructor()throws ValidationException{
        String string = "a123";
        assertThrows(ValidationException.class, ()-> new Challenge(string));
    }
    @Nested
    @DisplayName("Challenge Decode")
    class ChallengeDecode {
        @Test
        @DisplayName("should work")
        void testDecode() throws ValidationException, IOException {
            String string = "CLNG 123\r\n";
            MessageInput input = new MessageInput(
                    new ByteArrayInputStream(
                            string.getBytes(CHR_ENC)));
            assertEquals(new Challenge("123"), Message.decode(input));
        }
        @Test
        @DisplayName("testing null")
        void testNull() {
            assertThrows(NullPointerException.class,
                    () -> Message.decode(null));
        }
        @ParameterizedTest
        @ValueSource(strings ={"CLNG\r\n", "CLNG10\r\n", "CM\r\n" })
        @DisplayName("invalid nonce")
        void testInvalidDecode(String string) {
            assertThrows(ValidationException.class,
                    () -> Message.decode(new MessageInput(
                            new ByteArrayInputStream(
                                    string.getBytes(CHR_ENC)))));
        }

        @Test
        @DisplayName("doublePrematureEOS")
        void testDecodeDoublePrematureEOS() throws IOException, ValidationException {
            String testStringValid = "CLNG 123\r\n";
            MessageInput messageInput = new MessageInput(new ByteArrayInputStream(
                    testStringValid.getBytes(CHR_ENC)));
            Message.decode(messageInput);
            assertThrows(EOFException.class,
                    () -> Message.decode(messageInput));
            messageInput.close();
        }
    }

    @Nested
    @DisplayName("Challenge Encode")
    class ChallengeEncode {
        @Test
        @DisplayName("should word")
        void testEncode() throws IOException {
            String expected = "CLNG 123\r\n";
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            challenge.encode(new MessageOutput(output));
            assertEquals(expected, new String(output.toByteArray()));
        }
        @Test
        @DisplayName("null")
        void testEncodeNull() {
            assertThrows(NullPointerException.class,
                    () -> challenge.encode(new MessageOutput(null)));
        }
    }

    @Nested
    @DisplayName("Challenge Equality")
    class IDEquals {
        @Test
        @DisplayName("equal")
        void testEquals() {
            assertEquals(challenge, challenge1);
        }

        @Test
        @DisplayName("!equal")
        void testEqualsDiffer() {
            assertNotEquals(challenge, challenge2);
        }
    }

    @Test
    @DisplayName("getOperation")
    void testGetOperation() {
        String string = "CLNG";
        assertEquals(string, challenge.getOperation());
    }

    @Test
    @DisplayName("toString")
    void testToString() {
        String expected = "Challenge: nonce=123";
        assertEquals(expected, challenge.toString());
    }

    @Test
    @DisplayName("Challenge getNonce")
    void testGetNonce() {
        String expected = "123";
        assertEquals(expected, challenge.getNonce());
    }


    @Nested
    @DisplayName("setNonce")
    class SetNonce{
        @Test
        @DisplayName("test null")
        void testSetNonceNull() {
            assertThrows(ValidationException.class,
                    () -> challenge.setNonce(null));
        }

        @Test
        @DisplayName("should work")
        void testSetNonce() throws ValidationException{
            String string = "134";
            challenge.setNonce(string);
            assertEquals(string, challenge.getNonce());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "not work"})
        @DisplayName("Invalid Nonces")
        void setNonceFails(String input) {
            assertThrows(ValidationException.class,
                    () -> challenge.setNonce(input));
        }
    }

    @Nested
    @DisplayName("HashCode")
    class ChallengeHashCode {
        @Test
        @DisplayName("equal")
        void testHashCode() {
            assertEquals(challenge.hashCode(), challenge1.hashCode());
        }
        @Test
        @DisplayName("!equal")
        void testHashCodeDiffer() {
            assertNotEquals(challenge.hashCode(), challenge2.hashCode());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"CLNNNNNNN\r\n", "CLNG 12aa\r\n"})
    void testGetNewChallenge(String string){
        assertThrows(ValidationException.class,
                ()->Challenge.getNewChallenge(string.toCharArray()));
    }




}
