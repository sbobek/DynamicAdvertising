import RequestsAndResponses.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

/**
 * TODO: add usage of different auction modes... Maybe I should create interface for that
 */
public class DEIncomingConnectionHandler implements Runnable {
    List<Socket> dspServers = new ArrayList<Socket>();
    Socket sspServer;

    public DEIncomingConnectionHandler(Socket converstion, List<Integer> dspPorts) {
        try {
            this.sspServer = converstion;
            for (Integer port : dspPorts) {
                Socket server = new Socket("localhost", port);
                dspServers.add(server);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Sender implements Callable<DemandSidePlatformRS>{
        Socket socket;
        DemandSidePlatformRQ demandSidePlatformRQ;
        JAXBContext demandSidePlatformRQContext;
        JAXBContext demandSidePlatformRSContext;

        public Sender(DemandSidePlatformRQ demandSidePlatformRQ, Socket socket){
            this.demandSidePlatformRQ = demandSidePlatformRQ;
            this.socket = socket;

            try {
                demandSidePlatformRQContext = JAXBContext.newInstance(DemandSidePlatformRQ.class);
                demandSidePlatformRSContext = JAXBContext.newInstance(DemandSidePlatformRS.class);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }

        public DemandSidePlatformRS call() throws Exception {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Marshaller jaxbMarshaller = demandSidePlatformRQContext.createMarshaller();
            jaxbMarshaller.marshal(demandSidePlatformRQ, out);
            out.write('\n');
            out.flush();
            System.out.println("Sent!");

            Unmarshaller jaxbUnmarshaller = demandSidePlatformRSContext.createUnmarshaller();
            String str = in.readLine();

            return (DemandSidePlatformRS) jaxbUnmarshaller.unmarshal(new StringReader(str));
        }
    }

    public void run() {
        try {
            System.out.println("New connection established!");
            PrintWriter out = new PrintWriter(sspServer.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(sspServer.getInputStream()));

            JAXBContext advertisementExchangeRQContext = JAXBContext.newInstance(AdvertisementExchangeRQ.class);

            Unmarshaller jaxbUnmarshaller = advertisementExchangeRQContext.createUnmarshaller();
            String str = in.readLine();
            AdvertisementExchangeRQ advertisementExchangeRQ = (AdvertisementExchangeRQ) jaxbUnmarshaller.unmarshal(new StringReader(str));
            System.out.println("Ad for: " + advertisementExchangeRQ.getFloorPrice());

            DemandSidePlatformRQ demandSidePlatformRQ = createDSPRequest(advertisementExchangeRQ);

            ExecutorService executor = Executors.newFixedThreadPool(dspServers.size());
            HashMap<Socket, Future<DemandSidePlatformRS>> mapaAukcyjna = new HashMap<Socket, Future<DemandSidePlatformRS>>();
            for (Socket socket : dspServers){
                mapaAukcyjna.put(socket, executor.submit(new Sender(demandSidePlatformRQ, socket)));
            }

            finalizeAuction(mapaAukcyjna);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private void finalizeAuction(HashMap<Socket, Future<DemandSidePlatformRS>> mapaAukcyjna) {
        try {
            JAXBContext bidVictoryAdExchangeRSContext = JAXBContext.newInstance(BidVictoryAdExchangeRS.class);
            JAXBContext adChoosenAdExchangeRSContext = JAXBContext.newInstance(AdChoosenAdExchangeRS.class);
            BidVictoryAdExchangeRS finalVictor = new BidVictoryAdExchangeRS("", "", 0.0f);

            if (mapaAukcyjna.size() < 2){
                for (Socket socket : mapaAukcyjna.keySet()){
                    DemandSidePlatformRS demandSidePlatformRS = mapaAukcyjna.get(socket).get();
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    Marshaller jaxbMarshaller = bidVictoryAdExchangeRSContext.createMarshaller();
                    finalVictor = new BidVictoryAdExchangeRS(
                            demandSidePlatformRS.getConversationId(),
                            demandSidePlatformRS.getAdvertisementUrl(),
                            demandSidePlatformRS.getBidPrice());
                    jaxbMarshaller.marshal(finalVictor, out);
                    out.write('\n');
                    out.flush();
                    System.out.println("Sent!");
                }
            }else{
                Socket bestSocket = null;
                Float bestPrice = 0.0f;
                Float secondBest = 0.0f;

                for (Socket socket : mapaAukcyjna.keySet()){
                    DemandSidePlatformRS demandSidePlatformRS = mapaAukcyjna.get(socket).get();
                    Float bid = demandSidePlatformRS.getBidPrice();

                    if (bid > bestPrice) {
                        secondBest = bestPrice;
                        bestPrice = bid;
                        bestSocket = socket;
                    }
                    if (bid < bestPrice && bid > secondBest){
                        secondBest = bid;
                    }
                }
                for (Socket socket : mapaAukcyjna.keySet()){
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    BidVictoryAdExchangeRS bidVictoryAdExchangeRS;

                    if (socket == bestSocket){
                        DemandSidePlatformRS demandSidePlatformRS = mapaAukcyjna.get(socket).get();
                        bidVictoryAdExchangeRS = new BidVictoryAdExchangeRS(
                                demandSidePlatformRS.getConversationId(),
                                demandSidePlatformRS.getAdvertisementUrl(),
                                secondBest);
                        finalVictor = bidVictoryAdExchangeRS;
                    }else{
                        DemandSidePlatformRS demandSidePlatformRS = mapaAukcyjna.get(socket).get();
                        bidVictoryAdExchangeRS = new BidVictoryAdExchangeRS(
                                demandSidePlatformRS.getConversationId(),
                                "",
                                0.0f);
                    }

                    Marshaller jaxbMarshaller = bidVictoryAdExchangeRSContext.createMarshaller();
                    jaxbMarshaller.marshal(bidVictoryAdExchangeRS, out);
                    out.write('\n');
                    out.flush();
                    System.out.println("Sent!");
                }
            }

            PrintWriter out = new PrintWriter(sspServer.getOutputStream(), true);
            Marshaller jaxbMarshaller = adChoosenAdExchangeRSContext.createMarshaller();
            jaxbMarshaller.marshal(new AdChoosenAdExchangeRS(finalVictor.getConversationId(), finalVictor.getAdvertisementUrl(), finalVictor.getPaidPrice()), out);
            out.write('\n');
            out.flush();
            System.out.println("Sent!");

            for (Socket socket : dspServers){
                socket.close();
            }
            sspServer.close();

        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DemandSidePlatformRQ createDSPRequest(AdvertisementExchangeRQ input) {
        DemandSidePlatformRQ output = new DemandSidePlatformRQ();
        output.setCity(input.getCity());
        output.setRegion(input.getRegion());
        output.setCountry(input.getCountry());
        output.setConversationId(input.getConversationId());
        output.setDataExchangeId(input.getDataExchangeId());
        output.setDateTime(input.getDateTime());
        output.setFloorPrice(input.getFloorPrice());
        output.setFormat(input.getFormat());
        output.setPublisherURL(input.getPublisherURL());
        output.setSystemdata(input.getSystemdata());
        if (input.getTags() == null){
            output.setTags(null);
        }else {
            output.setTags(new ArrayList<String>(input.getTags()));
        }
        return output;
    }
}
