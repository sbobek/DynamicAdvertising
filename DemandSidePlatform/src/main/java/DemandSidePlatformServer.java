import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Created by Vulpes on 2016-12-03.
 */
public class DemandSidePlatformServer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to DSP! Type: DSP_MODEL_PATH DSP_ID PORT_NUMBER\n " +
                "using 'NONE' as DSP_MODEL_PATH defaults to standard example model");
        String command = scanner.nextLine();

        String model = command.split(" ")[0];
        String dsId = command.split(" ")[1];
        Integer portNo = Integer.parseInt(command.split(" ")[2]);

        HeartService.setModelfile(model);
        new HeartService();

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNo);
            System.out.println("DSP Sever opened");
            while (true) {
                Socket conversation = serverSocket.accept();
                (new Thread(new DSPIncomingConnectionHandler(conversation, dsId))).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
