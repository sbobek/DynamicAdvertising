package expertServices;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import RequestsAndResponses.DemandSidePlatformRQ;
import rl.model.AuctionLogEntry;
import rl.model.AuctionResult;
import rl.model.RLData;
import staticData.Environment;

public class RLService implements BiddingExpertService {

    private RLData rlData;

    private Path modelFilePath;
    private Marshaller smacDataMarshaller;

    @Override
    public double getProposedPrice(DemandSidePlatformRQ demandSidePlatformRQ) {
        return 0;
    }

    @Override
    public void initialize() throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(RLData.class);
        smacDataMarshaller = jaxbContext.createMarshaller();

        if(Environment.getModel() != null){
            modelFilePath = Paths.get(URI.create(Environment.getStatisticsFile()));
        }

        if(Environment.getModel() == null || Files.notExists(modelFilePath)){
            rlData = new RLData();
            rlData.initialize();
        }else {
            try(BufferedReader br = Files.newBufferedReader(modelFilePath)){
                rlData = (RLData) jaxbContext.createUnmarshaller().unmarshal(br);
            }
        }
    }

    @Override
    public void processAuctionResult(AuctionLogEntry auctionLogEntry) {
        synchronized (this) {
            rlData.logBid(auctionLogEntry.getDemandSidePlatformRQ(), auctionLogEntry.getDemandSidePlatformRS().getBidPrice(), auctionLogEntry.getAuctionResult());
            if (auctionLogEntry.getAuctionResult() == AuctionResult.WON) {
                rlData.logFeedback(auctionLogEntry.getAdFeedbackInfo().isClick(), auctionLogEntry.getAdFeedbackInfo().isConversion());
            }
        }
    }

    @Override
    public void reset() {

        //TODO learning process
        saveModel();
    }

    private void saveModel(){
        if(Environment.getModel() != null){
            try (BufferedWriter bw = Files.newBufferedWriter(modelFilePath, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)){
                smacDataMarshaller.marshal(rlData, bw);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
