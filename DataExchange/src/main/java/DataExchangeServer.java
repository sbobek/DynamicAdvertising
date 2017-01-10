import RequestsAndResponses.BidVictoryAdExchangeRS;
import RequestsAndResponses.DemandSidePlatformRQ;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Vulpes on 2016-12-03.
 */
public class DataExchangeServer {
    public static void main(String[] args) {
        List<Integer> dspPorts = new ArrayList<Integer>();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to DE! Type: DE_ID PORT_NUMBER [DSP_PORT]+");
        String command = scanner.nextLine();

        String[] data = command.split(" ");

        String deId = data[0];
        Integer portNo = Integer.parseInt(data[1]);

        for (int i = 2; i < data.length; i++){
            dspPorts.add(Integer.parseInt(data[i]));
        }

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNo);
            System.out.println("DE Sever opened");
            while(true){
                Socket converstion = serverSocket.accept();
                (new Thread(new DEIncomingConnectionHandler(converstion, dspPorts))).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static DemandSidePlatformRQ demandSidePlatformRQ(){
        DemandSidePlatformRQ demandSidePlatformRQ = new DemandSidePlatformRQ();
        demandSidePlatformRQ.setCity("alaska");
        demandSidePlatformRQ.setFloorPrice(10.0f);
        return  demandSidePlatformRQ;
    }

    private static BidVictoryAdExchangeRS bidVictoryAdExchangeRS(){
        BidVictoryAdExchangeRS bidVictoryAdExchangeRS = new BidVictoryAdExchangeRS();
        bidVictoryAdExchangeRS.setPaidPrice(0.10f);
        return  bidVictoryAdExchangeRS;
    }
}
