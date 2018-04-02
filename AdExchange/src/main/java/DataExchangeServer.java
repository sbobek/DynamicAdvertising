import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Vulpes on 2016-12-03.
 */
public class DataExchangeServer {
    public static final String SHUTDOWN_CMD = "shutdown";

    private static List<Integer> dspPorts = new ArrayList<Integer>();
    private static Integer portNo;
    public static boolean shutdown = false;
    private static Integer parentPort = null;

    static int starterData(String[] args) {
        if (args.length < 1) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome to DE! Type: PORT_NUMBER [DSP_PORT]+");
            String command = scanner.nextLine();
            String[] data = command.split(" ");
            portNo = Integer.parseInt(data[0]);

            for (int i = 1; i < data.length; i++){
                dspPorts.add(Integer.parseInt(data[i]));
            }
        }
        else {
            int i = 0;

            while (i < args.length) {
                switch (args[i]) {
                    case "-port":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        portNo = Integer.parseInt(args[i]);
                        break;
                    case "-dsp":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        for (i = i; i < args.length && !args[i].contains("-"); i++)
                            dspPorts.add(Integer.parseInt(args[i]));
                        break;
                    case "-parent":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        parentPort = Integer.parseInt(args[i]);
                        break;
                }
                i++;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        if (starterData(args) == -1) {
            System.err.println("Some of arguments were wrong!");
            return;
        }

        try {
            ServerSocket serverSocket = new ServerSocket(portNo);
            serverSocket.setSoTimeout(50);
            System.out.println("DE Sever opened");
            notifyParent("up");

            while(!shutdown){
                try {
                    Socket converstion = serverSocket.accept();
                    (new Thread(new DEIncomingConnectionHandler(converstion, dspPorts))).start();
                }catch (SocketTimeoutException e){}

            }
            notifyParent("down");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void notifyParent(String message){
        if(parentPort != null){
            try {
                Socket notice = new Socket("localhost", parentPort);
                PrintWriter out = new PrintWriter(notice.getOutputStream(), true);
                out.println("[ADX] " + message);
                out.flush();
                notice.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
