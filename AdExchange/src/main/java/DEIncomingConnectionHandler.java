import RequestsAndResponses.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Sender implements Callable<DemandSidePlatformRS> {
        Socket socket;
        DemandSidePlatformRQ demandSidePlatformRQ;
        JAXBContext demandSidePlatformRQContext;
        JAXBContext demandSidePlatformRSContext;

        public Sender(DemandSidePlatformRQ demandSidePlatformRQ, Socket socket) {
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
            System.out.println("DSP RS: " + str);

            return (DemandSidePlatformRS) jaxbUnmarshaller.unmarshal(new StringReader(str));
        }
    }

    public void run() {
//        LocalDateTime start = LocalDateTime.now();
        try {
            System.out.println("New connection established!");
            PrintWriter out = new PrintWriter(sspServer.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(sspServer.getInputStream()));

            JAXBContext advertisementExchangeRQContext = JAXBContext.newInstance(AdvertisementExchangeRQ.class);

            Unmarshaller jaxbUnmarshaller = advertisementExchangeRQContext.createUnmarshaller();
            String str = in.readLine();

            if(DataExchangeServer.SHUTDOWN_CMD.equals(str)){
                DataExchangeServer.shutdown = true;
                sspServer.close();
                return;
            }

            System.out.println("AdExRQ: " + str);
            AdvertisementExchangeRQ advertisementExchangeRQ = (AdvertisementExchangeRQ) jaxbUnmarshaller.unmarshal(new StringReader(str));

            DemandSidePlatformRQ demandSidePlatformRQ = createDSPRequest(advertisementExchangeRQ);

            ExecutorService pool = Executors.newFixedThreadPool(dspServers.size());
            HashMap<Socket, Future<DemandSidePlatformRS>> mapaAukcyjna = new HashMap<Socket, Future<DemandSidePlatformRS>>();
            for (Socket socket : dspServers) {
                mapaAukcyjna.put(socket, pool.submit(new Sender(demandSidePlatformRQ, socket)));
            }

            finalizeAuction(mapaAukcyjna, advertisementExchangeRQ.getFloorPrice());
            pool.shutdown();

        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
//        LocalDateTime stop = LocalDateTime.now();
//        long diffInMilli = java.time.Duration.between(start, stop).toMillis();
//        long diffInSeconds = java.time.Duration.between(start, stop).getSeconds();
//        long diffInMinutes = java.time.Duration.between(start, stop).toMinutes();
//
//        System.out.println("Work took full " + diffInMinutes + " minutes!");
//        System.out.println("Work took full " + diffInSeconds + " seconds!");
//        System.out.println("Work took full " + diffInMilli + " miliseconds!");
    }

    private void finalizeAuction(HashMap<Socket, Future<DemandSidePlatformRS>> mapaAukcyjna, Double floorPrice) {
        try {
            JAXBContext bidVictoryAdExchangeRSContext = JAXBContext.newInstance(BidVictoryAdExchangeRS.class);
            JAXBContext adChoosenAdExchangeRSContext = JAXBContext.newInstance(AdChoosenAdExchangeRS.class);
            BidVictoryAdExchangeRS finalVictor = new BidVictoryAdExchangeRS("", "NULL", Collections.emptyList(),0.0d);

            PrintWriter winner = null;
            if (mapaAukcyjna.size() < 2) {
                for (Socket socket : mapaAukcyjna.keySet()) {
                    DemandSidePlatformRS demandSidePlatformRS = mapaAukcyjna.get(socket).get();
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    Marshaller jaxbMarshaller = bidVictoryAdExchangeRSContext.createMarshaller();
                    if (demandSidePlatformRS.getBidPrice() > floorPrice) {
                        finalVictor = new BidVictoryAdExchangeRS(
                                demandSidePlatformRS.getConversationId(),
                                demandSidePlatformRS.getAdvertisementUrl(),
                                demandSidePlatformRS.getAdvertisementTags(),
                                demandSidePlatformRS.getBidPrice());
                        winner = out;
                    } else {
                        finalVictor = new BidVictoryAdExchangeRS(
                                demandSidePlatformRS.getConversationId(),
                                "NULL",
                                Collections.emptyList(),
                                0.0d);
                        System.out.println("Nobody wins");
                    }
                    jaxbMarshaller.marshal(finalVictor, out);
                    out.write('\n');
                    out.flush();
                    System.out.println("Sent!");
                }
            } else {
                Socket bestSocket = null;
                Double bestPrice = floorPrice;
                Double secondBest = 0.0d;

                for (Socket socket : mapaAukcyjna.keySet()) {
                    DemandSidePlatformRS demandSidePlatformRS = mapaAukcyjna.get(socket).get();
                    Double bid = demandSidePlatformRS.getBidPrice();

                    if (bid > bestPrice) {
                        secondBest = bestPrice;
                        bestPrice = bid;
                        bestSocket = socket;
                    } else if (bid > secondBest) {
                        secondBest = bid;
                    }
                }
                for (Socket socket : mapaAukcyjna.keySet()) {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    BidVictoryAdExchangeRS bidVictoryAdExchangeRS;

                    if (socket == bestSocket) {
                        DemandSidePlatformRS demandSidePlatformRS = mapaAukcyjna.get(socket).get();
                        bidVictoryAdExchangeRS = new BidVictoryAdExchangeRS(
                                demandSidePlatformRS.getConversationId(),
                                demandSidePlatformRS.getAdvertisementUrl(),
                                demandSidePlatformRS.getAdvertisementTags(),
                                secondBest);
                        finalVictor = bidVictoryAdExchangeRS;
                        winner = out;
                    } else {
                        DemandSidePlatformRS demandSidePlatformRS = mapaAukcyjna.get(socket).get();
                        bidVictoryAdExchangeRS = new BidVictoryAdExchangeRS(
                                demandSidePlatformRS.getConversationId(),
                                "NULL",
                                Collections.emptyList(),
                                0.0d);
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
            jaxbMarshaller.marshal(new AdChoosenAdExchangeRS(
                            finalVictor.getConversationId(),
                            finalVictor.getAdvertisementUrl(),
                            finalVictor.getAdvertisementTags(),
                            finalVictor.getPaidPrice()),
                    out);
            out.write('\n');
            out.flush();
            System.out.println("Sent!");

            if(winner != null){
                handleAdFeedbackInfo(winner);
            }

            for (Socket socket : dspServers) {
                socket.close();
            }
            sspServer.close();

        } catch (JAXBException | IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleAdFeedbackInfo(PrintWriter dspWinner) throws IOException {
        BufferedReader ssp = new BufferedReader(new InputStreamReader(sspServer.getInputStream()));
        String adFeedback = ssp.readLine();
        dspWinner.write(adFeedback);
        dspWinner.write("\n");
        dspWinner.flush();
    }

    private DemandSidePlatformRQ createDSPRequest(AdvertisementExchangeRQ input) {
        DemandSidePlatformRQ output = new DemandSidePlatformRQ();
        output.setCity(input.getCity());
        output.setRegion(input.getRegion());
        output.setCountry(input.getCountry());
        output.setConversationId(input.getConversationId());
        output.setDataExchangeId(input.getDataExchangeId());
        output.setDateTime(input.getDateTime());
        output.setFormat(input.getFormat());
        output.setPublisherURL(input.getPublisherURL());
        output.setSystemdata(input.getSystemdata());
        if (input.getTags() == null) {
            output.setTags(null);
        } else {
            output.setTags(new ArrayList<String>(input.getTags()));
        }
        return output;
    }
}
