import java.io.*;
import java.net.*;

public class JavaNetClient {
    public static void main(String[] args) {
        InetAddress inetAddress = null;
        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        PrintStream printStream = null;
        try {
            inetAddress = InetAddress.getLocalHost();
            int port = 8071;
            socket = new Socket(inetAddress, port);
            printStream = new PrintStream(socket.getOutputStream());
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);

            for (int i = 0; i < 15; ++i) {
                printStream.println("ping");
                System.out.println(bufferedReader.readLine());
                Thread.sleep(500);
            }

        } catch (UnknownHostException e) {
            System.out.println("Specified server is unavailable");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Input/output error");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Thread error");
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}