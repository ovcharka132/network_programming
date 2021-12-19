import java.net.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Scanner {
    private String host = "192.168.1.98";
    private int minPort = 1;
    private int maxPort = 0x10000;
    private int timeout = 100;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getMinPort() {
        return minPort;
    }

    public void setMinPort(int minPort) {
        this.minPort = minPort;
    }

    public int getMaxPort() {
        return maxPort;
    }

    public void setMaxPort(int maxPort) {
        this.maxPort = maxPort;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    private List<Integer> scan() {
        try {
            InetAddress ia = InetAddress.getByName(getHost());
            return scan(ia);
        } catch (IOException ioe) {
            return null;
        }
    }

    private List<Integer> scan(InetAddress inetAddress) {
        List<Integer> openPortsList = new ArrayList<Integer>(0xFF);
        for (int currentPort = minPort; currentPort <= maxPort; ++currentPort) {
            try {
                InetSocketAddress isa = new InetSocketAddress(inetAddress, currentPort);
                Socket socket = new Socket();
                socket.connect(isa, timeout);
                System.out.printf("Port %d is open\n", currentPort);
                openPortsList.add(currentPort);
                socket.close();
            } catch (IOException ioe) {
            }
        }
        return openPortsList;
    }

    public static void main(String[] args) {
        if (args.length <= 2) {
            usagePrint();
            return;
        }

        String host = args[0];

        Scanner scanner = new Scanner();

        String[] ports = { args[1], args[2] };
        try {
            int minPort = Integer.parseInt(ports[0]);
            int maxPort = Integer.parseInt(ports[1]);
            scanner.setMinPort(minPort);
            scanner.setMaxPort(maxPort);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ports format");
            usagePrint();
            return;
        }
        
        System.out.printf("Scanning ports on %s...\n", host);

        scanner.setHost(host);

        List<Integer> openPortsList = scanner.scan();
        if (openPortsList != null) {
            if (openPortsList.size() > 0) {
                System.out.printf("Ports opened on %s:\n", scanner.getHost());
                for (Integer openedPort : openPortsList) {
                    System.out.println(openedPort);
                }
            } else {
                System.out.println("No opened ports found, exit...");
            }
        } else {
            System.out.println("Unknown error");
        }
    }

    private static void usagePrint() {
        System.out.println("Usage:\n\tjava Scanner\n\t<host:IPv4>\n\t<minPort:int>\n\t<maxPort:int>");
    }
}