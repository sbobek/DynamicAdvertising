import RequestsAndResponses.BidVictoryAdExchangeRS;
import RequestsAndResponses.DemandSidePlatformRQ;
import RequestsAndResponses.DemandSidePlatformRS;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Vulpes on 2016-12-03.
 */
public class DSPIncomingConnectionHandler implements Runnable {
    Socket socket;
    String platformID;

    public DSPIncomingConnectionHandler(Socket socket, String platformId) {
        this.socket = socket;
        this.platformID = platformId;
    }

    public void run() {
        try {
            System.out.println("New connection established!");
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            JAXBContext demandSidePlatformRQContext = JAXBContext.newInstance(DemandSidePlatformRQ.class);
            JAXBContext demandSidePlatformRSContext = JAXBContext.newInstance(DemandSidePlatformRS.class);
            JAXBContext bidVictoryAdExchangeRSContext = JAXBContext.newInstance(BidVictoryAdExchangeRS.class);

            Unmarshaller jaxbUnmarshaller = demandSidePlatformRQContext.createUnmarshaller();
            String str = in.readLine();
            DemandSidePlatformRQ demandSidePlatformRQ = (DemandSidePlatformRQ) jaxbUnmarshaller.unmarshal(new StringReader(str));
            System.out.println("Ad for: " + demandSidePlatformRQ.getCity());

            DemandSidePlatformAlgorithm demandSidePlatformAlgorithm = new DemandSidePlatformAlgorithm(demandSidePlatformRQ, null, platformID);
            ExecutorService executor = Executors.newFixedThreadPool(1);
            Future<DemandSidePlatformRS> future = executor.submit(demandSidePlatformAlgorithm);
            Marshaller jaxbMarshaller = demandSidePlatformRSContext.createMarshaller();

            jaxbMarshaller.marshal(future.get(), out);
            out.write('\n');
            out.flush();
            System.out.println("Sent!");

            jaxbUnmarshaller = bidVictoryAdExchangeRSContext.createUnmarshaller();
            str = in.readLine();
            BidVictoryAdExchangeRS bidVictoryAdExchangeRS = (BidVictoryAdExchangeRS) jaxbUnmarshaller.unmarshal(new StringReader(str));

            if (bidVictoryAdExchangeRS.getPaidPrice() != 0.0) {
                System.out.println("I win! and pay: "+ bidVictoryAdExchangeRS.getPaidPrice());
                HeartService.incrementSoldAds();
                HeartService.addPaidMoney(Double.valueOf(bidVictoryAdExchangeRS.getPaidPrice()));
            }
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
