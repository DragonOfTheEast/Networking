package tiktak.serialization;

import java.util.Arrays;
import java.util.Objects;

public class NIODeframer {

    private byte[] bytes; //input
    private static final int MIN = 5;

    public NIODeframer(){
        bytes = new byte[0];
    }

    public static byte[] addThemUpPlease(byte[]a , byte[] b){
        int len = a.length + b.length;
        byte [] ans = new byte[len];
        System.arraycopy(a, 0,ans,0, a.length);
        System.arraycopy(b,0,ans,a.length, b.length);
        return ans;
    }
    public String getMessage(byte[] buffer) throws NullPointerException, IllegalArgumentException{
        Objects.requireNonNull(buffer, "buffer cannot be null");
        this.bytes = addThemUpPlease(bytes, buffer);

        //if its at least 5 bytes
        if (bytes.length < MIN) return null;
        StringBuilder stringBuilder = new StringBuilder("");
        boolean good = false;
        int curr = 0;
        for(int i = 0 ; i < bytes.length - 1; i++){
            curr++;
            if((char)bytes[i] == '\r' && (char)bytes[i+1] == '\n'){
                stringBuilder.append((char)bytes[i]);
                stringBuilder.append((char)bytes[i+1]);
                good = true;
                break;
            }
            stringBuilder.append((char)bytes[i]);
        }
        if (!good)return null;
        bytes = Arrays.copyOfRange(bytes, curr + 1,bytes.length);
        return stringBuilder.toString();
    }
}
