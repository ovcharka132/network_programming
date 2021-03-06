import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MultiThreadServer {
    private static JList<String> clientList;
    private static JButton enterButton;
    private static JTextField enterText;
    private static JScrollPane pane;

    private static DefaultListModel list;
    private static PrintStream printStream;
    private static Socket s;

    public static void main(String[] args) {
        list = new DefaultListModel();

        GUI();

        ServerSocket servSocket = null;
        ArrayList<Socket> listSocket = new ArrayList<>();
        int port = 8071;
        try {
            servSocket = new ServerSocket(port);
            System.out.println("Waiting for connections");

            while (true){
                s = servSocket.accept();
                System.out.println("[" + s.getInetAddress().getHostName() + "] connected");
                ServerThread srvThread = new ServerThread(s);
                srvThread.start();
                list.addElement(s.getInetAddress().getHostName());
                listSocket.add(s);

                enterButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            for(int i = 0; i < list.size(); i++) {
                                if (clientList.isSelectedIndex(i)) {
                                    printStream = new PrintStream(listSocket.get(i).getOutputStream());
                                    printStream.println(enterText.getText());
                                    printStream.flush();
                                }
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

            }
        } catch (Exception e) {
            System.err.println(e);
        }
        finally{
            try {
                servSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void GUI(){
        JFrame frame = new JFrame("Server");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        frame.add(panel);

        panel.setLayout(null);

        JLabel clientListLabel = new JLabel("Connected Clients");
        clientListLabel.setBounds(170, 10, 130, 25);
        panel.add(clientListLabel);

        clientList = new JList<String>(list);
        pane = new JScrollPane(clientList);
        pane.setBounds(125, 40, 200, 80);
        panel.add(pane);

        enterText = new JTextField(20);
        enterText.setBounds(120, 130, 130, 25);
        panel.add(enterText);

        enterButton = new JButton("Enter");
        enterButton.setBounds(260, 130, 80, 25);
        panel.add(enterButton);

        frame.setVisible(true);
    }
}

class ServerThread extends Thread {
    private PrintStream printStream = null;
    private BufferedReader bufferedReader = null;
    private InetAddress inetAddress = null;

    public ServerThread(Socket s) throws IOException {
        printStream = new PrintStream(s.getOutputStream());
        bufferedReader = new BufferedReader(
                new InputStreamReader(s.getInputStream()));
        inetAddress = s.getInetAddress();
    }

    public void disconnect() {
        try {
            System.out.println(inetAddress.getHostName() + " disconnected");
            printStream.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            this.interrupt();
        }
    }

    public void run (){
        int i = 0;
        String str = null;
        try {
            while ((bufferedReader.readLine())!= null){
                if ("ping".equalsIgnoreCase(str)) {
                    printStream.println("pong " + ++i);
                }
                System.out.println("Ping-pong " + i + " with " + inetAddress.getHostName() + " successful");
            }
        } catch (IOException e) {
            System.out.println("Disconnect");
        }
        finally{
            disconnect();
        }
    }
}