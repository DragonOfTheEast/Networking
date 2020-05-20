package tiktak.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tiktak.serialization.NIODeframer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("NIODeframer Tests")
public class NIODeframerTest {
    NIODeframer nioDeframer = new NIODeframer();
    @Test
    @DisplayName("getMessage")
    void testgetMessage(){
        String string = nioDeframer.getMessage(new byte[]{'A', 'C','K','\r', '\n', 'T','O'});
        assertEquals("ACK\r\n", string);
        String string1 = nioDeframer.getMessage(new byte[]{'S','T','\r','\n'});
        assertEquals("TOST\r\n", string1);
    }
}
