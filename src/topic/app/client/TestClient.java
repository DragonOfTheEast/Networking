package topic.app.client;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TestClient {

    private static final int MSGMAX = 65507;
    private static final int RADIX = 16;

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            throw new IllegalArgumentException("Parameter(s):   ");
        }

        InetAddress server = InetAddress.getByName(args[0]);
        int servPort = Integer.parseInt(args[1]);
        byte[] hex = new BigInteger(args[2], RADIX).toByteArray();

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(server, servPort);
            socket.setSoTimeout(2000);
            socket.send(new DatagramPacket(hex, hex.length));

            DatagramPacket recvPkt = new DatagramPacket(new byte[MSGMAX], MSGMAX);
            socket.receive(recvPkt); // Receive packet from client

            System.out.println("Response in hex:");
            System.out.println(new BigInteger(recvPkt.getData(), 0, recvPkt.getLength()).toString(RADIX).toUpperCase());

            System.out.println("Response in chars:");
            for (int i=0; i < recvPkt.getLength(); i++) {
                char c = (char) (recvPkt.getData()[i]);
                if (c >= 32 && c < 127) {
                    System.out.print(c);
                }
            }
            System.out.println();
        }
    }
}