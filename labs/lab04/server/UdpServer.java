import java.io.*;
import java.net.*;
import java.util.*;

public class UdpServer {
    private DatagramSocket socket;
    private Random random;
    private String timeRemaining;
    private int hours = 2;
    private int minutes = 10;

    public UdpServer(int port) throws SocketException {
        socket = new DatagramSocket(port);
        random = new Random();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: UdpServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try {
            UdpServer server = new UdpServer(port);
            server.service();
        } catch (SocketException ex) {
            System.out.println("Socket error: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    private void service() throws IOException {
        while (true) {
            DatagramPacket request = new DatagramPacket(new byte[1], 1);
            socket.receive(request);

            mockGetTimeRemaining();
            byte[] buffer = timeRemaining.getBytes();

            InetAddress clientAddress = request.getAddress();
            int clientPort = request.getPort();

            DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            socket.send(response);
        }
    }

    private void mockGetTimeRemaining() {
        if(minutes == 0){
            --hours;
            minutes = 59;
        } else {
            --minutes;
        }
        timeRemaining = Integer.toString(hours) + " hour(s) " + Integer.toString(minutes) + " minute(s) left";
    }

}