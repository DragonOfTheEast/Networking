package tiktak.app.server;

import tiktak.serialization.NIODeframer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;

public class ReadAndWriteHelper {
    public Timer timer = new Timer();
    public NIODeframer nioDeframer = new NIODeframer();
    Queue<String> queue = new LinkedList<>();
    private static final String ID = "ID";
    private static final String CRED = "CRED";
    private static final String TOST = "TOST";
    public ReadAndWriteHelper(){
        queue.add(ID);
        queue.add(CRED);
        queue.add(TOST);
    }

}
