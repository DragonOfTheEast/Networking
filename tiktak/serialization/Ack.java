package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class Ack extends Message{


    @Override
    public void encode(MessageOutput out) throws IOException {
        Objects.requireNonNull(out, "out cannot be null");
        try {
            //writing to output stream
            out.getOutputStream().write(Constants.ACK.getBytes(StandardCharsets.ISO_8859_1));
        }catch (IOException e){
            throw new IOException("error in writing to MessageOutput - out");
        }

    }

    @Override
    public String getOperation() {
        return "ACK";
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOperation());
    }

    @Override
    public boolean equals(Object o) {
        Objects.requireNonNull(o,"o cannot be null");
        if (this == o) { return true; }
        if (getClass() != o.getClass()) { return false; }
        Ack a_new = (Ack) o;
        return this.getOperation().equals(a_new.getOperation());
    }

    @Override
    public String toString(){
        return "Ack";
    }

    public static Ack getNewAck(char[] chars)throws ValidationException{
        if(!Arrays.equals(chars, Constants.ACK.toCharArray())){
            throw new ValidationException("invalid message", Arrays.toString(chars));
        }
        else{
            return new Ack();
        }
    }
}
