package platformAlgorithm;

import staticData.Environment;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Vulpes on 2016-12-03.
 */
public class DemandSidePlatformServer {
    private static int starterData(String[] args) {
        if (args.length < 1) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome to DSP! Type: DSP_MODEL_PATH DSP_ID PORT_NUMBER\n " +
                    "using 'NONE' as DSP_MODEL_PATH defaults to standard example model");
            String command = scanner.nextLine();

            Environment.setModel(command.split(" ")[0]);
            Environment.setDsId(command.split(" ")[1]);
            Environment.setPortNo(Integer.parseInt(command.split(" ")[2]));
        } else {
            int i = 0;

            while (i < args.length) {
                switch (args[i]) {
                    case "-id":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        Environment.setDsId(args[i]);
                        break;
                    case "-port":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        Environment.setPortNo(Integer.parseInt(args[i]));
                        break;
                    case "-model":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        Environment.setModel(args[i]);
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

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(Environment.getPortNo());
            System.out.println("DSP Sever opened");
            while (true) {
                Socket conversation = serverSocket.accept();
                (new Thread(new DSPIncomingConnectionHandler(conversation))).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
