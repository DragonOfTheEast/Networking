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

@DisplayName("ID Test")
public class IDTest  {

   private ID id = new ID("chigo");
   private ID id1 = new ID("chigo");
   private ID id2 = new ID("notchigo");

    public IDTest() throws ValidationException {
    }

    @Nested
   @DisplayName("Decode for ID")
   class IDDecode{

       @Test
       @DisplayName("null test")
       void testNull(){
           assertThrows(NullPointerException.class, ()->Message.decode(null));
       }

       @Test
       @DisplayName("null constructor")
       void testNullConstructor(){
           assertThrows(ValidationException.class, ()->new ID(null));
       }
       @Test
       @DisplayName("should work")
       void testDecode() throws ValidationException, IOException{
           String string = "ID messi10\r\n";
           MessageInput messageInput = new MessageInput( new ByteArrayInputStream(
                   string.getBytes(CHR_ENC)));
           assertEquals(new ID("messi10"), Message.decode(messageInput));
       }


        @Test
        @DisplayName("Premature EOS")
        void testDecodeDoublePrematureEOS() throws IOException, ValidationException {
            String test = "ID messi10\r\n";
            MessageInput messageInput = new MessageInput(new ByteArrayInputStream(test.getBytes(CHR_ENC)));
            Message.decode(messageInput);
            assertThrows(EOFException.class, ()-> Message.decode(messageInput));
            messageInput.close();

        }


      @ParameterizedTest
      @ValueSource(strings = {"ID\r\n", "ID invalid_user\r\n", "IDmessi\r\n"})
      @DisplayName("invalid decode")
      void testInvalidDecode(String string){
           assertThrows(ValidationException.class, ()-> Message.decode(
                   new MessageInput(new ByteArrayInputStream(
                           string.getBytes(CHR_ENC)))));
      }
   }


   @Nested
   @DisplayName("Encode for ID")
   class IDEncode{

       @Test
       @DisplayName("Test Null")
       void testNull(){
           assertThrows(NullPointerException.class, ()-> id.encode(new MessageOutput(null)));
       }
       @Test
       @DisplayName("should work")
       void testEncode() throws IOException{
           String string = "ID chigo\r\n";
           ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
           id.encode(new MessageOutput(byteArrayOutputStream));
           assertEquals(string, new String(byteArrayOutputStream.toByteArray()));
       }
   }

    @Test
    @DisplayName("ID getOperation")
    void testGetOperation() {
        String string = "ID";
        assertEquals(string, id.getOperation());
    }

   @Test
   @DisplayName("getID")
    void testGetID(){
       String string = "chigo";
       assertEquals(string, id.getID());
   }

    @Nested
    @DisplayName("setID")
    class SetID{
        @Test
        @DisplayName("test null")
        void testSetIDNull() {
            assertThrows(ValidationException.class,
                    () -> id.setID(null));
        }

        @Test
        @DisplayName("should work")
        void testSetID() throws ValidationException{
            String newID = "user101";
            id.setID(newID);
            assertEquals(newID, id.getID());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "not work", "$1245h"})
        @DisplayName("Invalid ID's")
        void setIDFails(String input) {
            assertThrows(ValidationException.class,
                    () -> id.setID(input));
        }
    }

    @Nested
    @DisplayName("ID Equality")
    class IDEquals {
        @Test
        @DisplayName("equal")
        void testEquals() {
            assertEquals(id, id1);
        }

        @Test
        @DisplayName("!equal")
        void testEqualsDiffer() {
            assertNotEquals(id, id2);
        }
    }

    @Nested
    @DisplayName("Hashcode")
    class IDHashCode {
        @Test
        @DisplayName("equal")
        void testHashCode() {
            assertEquals(id.hashCode(), id1.hashCode());
        }
        @Test
        @DisplayName("!equal")
        void testHashCodeDiffer() {
            assertNotEquals(id.hashCode(), id2.hashCode());
        }
    }

    @Test
    @DisplayName("toString")
    void testToString() {
        String expected = "ID: id=chigo";
        assertEquals(expected, id.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "123!"})
    void testConstructor(String string){
        assertThrows(ValidationException.class, ()->new ID(string));
    }



}
