package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class Tost extends Message {
    @Override
    public void encode(MessageOutput out) throws IOException {
        Objects.requireNonNull(out, "out cannot be null");
        try {
            //writing to output stream
            out.getOutputStream().write(Constants.TOST.getBytes(StandardCharsets.ISO_8859_1));
        }catch (IOException e){
            throw new IOException("error in writing to MessageOutput - out");
        }
    }

    @Override
    public String getOperation() {
        return "TOST";
    }

    @Override
    public String toString(){
        return "Tost";
    }

    public static Version getNewTost(char[] chars) throws ValidationException{
        if (!Arrays.equals(chars, Constants.TOST.toCharArray())){
            throw new ValidationException("invalid message", Arrays.toString(chars));
        }
        else{
            return new Version();
        }
    }

    /**
     *Checks inequality of objects
     * @return the hashcode of an object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getOperation());
    }

    /**
     *Checks the equality of objects
     * @param o object to compare to this
     * @return equality of the two objects
     */
    @Override
    public boolean equals(Object o) {
        Objects.requireNonNull(o,"o cannot be null");
        if (this == o) { return true; }
        if (getClass() != o.getClass()) { return false; }
        Tost a_new = (Tost) o;
        return this.getOperation().equals(a_new.getOperation());
    }

    
}
