package platformAlgorithm;

import RequestsAndResponses.AdFeedbackInfo;
import RequestsAndResponses.BidVictoryAdExchangeRS;
import RequestsAndResponses.DemandSidePlatformRQ;
import RequestsAndResponses.DemandSidePlatformRS;
import expertServices.HeartService;
import rl.model.AuctionLogEntry;
import rl.model.AuctionResult;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.Socket;

/**
 * Created by Vulpes on 2016-12-03.
 */
public class DSPIncomingConnectionHandler implements Runnable {
    private Socket socket;

    public DSPIncomingConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            AuctionLogEntry logEntry = new AuctionLogEntry();

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            JAXBContext demandSidePlatformRQContext = JAXBContext.newInstance(DemandSidePlatformRQ.class);
            JAXBContext demandSidePlatformRSContext = JAXBContext.newInstance(DemandSidePlatformRS.class);
            JAXBContext bidVictoryAdExchangeRSContext = JAXBContext.newInstance(BidVictoryAdExchangeRS.class);
            JAXBContext adFeedbackInfoContext = JAXBContext.newInstance(AdFeedbackInfo.class);

            Unmarshaller jaxbUnmarshaller = demandSidePlatformRQContext.createUnmarshaller();
            String str = in.readLine();

            if(str == null) {
                socket.close();
                return;
            }else if(DemandSidePlatformServer.RESET_CMD.equals(str)){
                DemandSidePlatformServer.reset = true;
                socket.close();
                return;
            }else if(DemandSidePlatformServer.SHUTDOWN_CMD.equals(str)){
                DemandSidePlatformServer.shutdown = true;
                socket.close();
                return;
            }

            DemandSidePlatformRQ demandSidePlatformRQ = (DemandSidePlatformRQ) jaxbUnmarshaller.unmarshal(new StringReader(str));
            logEntry.setDemandSidePlatformRQ(demandSidePlatformRQ);

            DemandSidePlatformRS response = BiddingAlgorithm.getInstance().decideBidValue(demandSidePlatformRQ);
            logEntry.setDemandSidePlatformRS(response);

            Marshaller jaxbMarshaller = demandSidePlatformRSContext.createMarshaller();
            jaxbMarshaller.marshal(response, out);
            out.write('\n');
            out.flush();

            jaxbUnmarshaller = bidVictoryAdExchangeRSContext.createUnmarshaller();
            str = in.readLine();
            BidVictoryAdExchangeRS bidVictoryAdExchangeRS = (BidVictoryAdExchangeRS) jaxbUnmarshaller.unmarshal(new StringReader(str));

            if (bidVictoryAdExchangeRS.getPaidPrice() > 0.0) {
                logEntry.setAuctionResult(AuctionResult.WON);
                HeartService.incrementSoldAds();
                HeartService.addPaidMoney(bidVictoryAdExchangeRS.getPaidPrice());

                jaxbUnmarshaller = adFeedbackInfoContext.createUnmarshaller();
                str = in.readLine();
                AdFeedbackInfo adFeedbackInfo = (AdFeedbackInfo) jaxbUnmarshaller.unmarshal(new StringReader(str));
                logEntry.setAdFeedbackInfo(adFeedbackInfo);

            }else {
                logEntry.setAuctionResult(AuctionResult.LOST);
            }
            socket.close();
            BiddingAlgorithm.getInstance().processAuctionResult(logEntry);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
