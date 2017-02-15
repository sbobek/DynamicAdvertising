import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Vulpes on 2016-12-03.
 */
public class DemandSidePlatformServer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to DSP! Type: DSP_ID PORT_NUMBER");
        String command = scanner.nextLine();

        String dsId = command.split(" ")[0];
        Integer portNo = Integer.parseInt(command.split(" ")[1]);

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNo);
            System.out.println("DSP Sever opened");
            while(true){
                Socket conversation = serverSocket.accept();
                (new Thread(new DSPIncomingConnectionHandler(conversation, dsId))).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
