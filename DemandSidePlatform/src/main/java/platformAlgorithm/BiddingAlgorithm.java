package platformAlgorithm;

import RequestsAndResponses.DemandSidePlatformRQ;
import RequestsAndResponses.DemandSidePlatformRS;
import expertServices.BiddingExpertService;
import expertServices.HeartService;
import rl.model.AuctionLogEntry;
import rl.model.AuctionResult;
import staticData.Environment;
import statistics.AuctionSessionStatistics;
import statistics.Budget;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by Vulpes on 2016-12-03.
 */
public class BiddingAlgorithm{

    private static class BiddingAlgorithmInstance{
        private static final BiddingAlgorithm INSTANCE = new BiddingAlgorithm();
    }

    private BiddingExpertService biddingExpertService;
    private AuctionSessionStatistics auctionSessionStatistics;
    private Budget budget;
    private PrintWriter statisticsOutput;

    public static BiddingAlgorithm getInstance(){
        return BiddingAlgorithmInstance.INSTANCE;
    }

    public void initialize() throws Exception {
        biddingExpertService = (BiddingExpertService) Class.forName(Environment.getBiddingAlgorithm()).newInstance();
        biddingExpertService.initialize();

        budget = new Budget(Environment.getBudget());
        auctionSessionStatistics = new AuctionSessionStatistics(budget);

        if(Environment.getStatisticsFile() != null) {
            Path path = Paths.get(Environment.getStatisticsFile());
            boolean appendHeader = Files.notExists(path);
            BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            statisticsOutput = new PrintWriter(bw);
            if(appendHeader){
                statisticsOutput.println(AuctionSessionStatistics.STATISTICS_FILE_HEADER);
                statisticsOutput.flush();
            }
        }
    }

    /**
     * here be dragons!
     * this function shall decide advertisement shall be sent for what bid value
     * basing data on request sent to DSP and (if available) uder data from Data Exchange
     * @return
     */
    public DemandSidePlatformRS decideBidValue(DemandSidePlatformRQ demandSidePlatformRQ){
        DemandSidePlatformRS demandSidePlatformRS = new DemandSidePlatformRS();
        demandSidePlatformRS.setConversationId(demandSidePlatformRQ.getConversationId());
        demandSidePlatformRS.setAdvertisementUrl("goodUrl.com/" + Environment.getDsId());
        demandSidePlatformRS.setAdvertisementTags(Environment.getTargetTags());
        demandSidePlatformRS.setBidPrice(budget.reserveMoney(biddingExpertService.getProposedPrice(demandSidePlatformRQ)));
        demandSidePlatformRS.setDemandSidePlatformId(Environment.getDsId());

        return demandSidePlatformRS;
    }

    public void processAuctionResult(AuctionLogEntry auctionLogEntry){
        if(auctionLogEntry.getAuctionResult() == AuctionResult.LOST){
            // Return reserved budget when you lose
            budget.returnMoney(auctionLogEntry.getDemandSidePlatformRS().getBidPrice());
        }
        biddingExpertService.processAuctionResult(auctionLogEntry);
        auctionSessionStatistics.processAuctionResult(auctionLogEntry);
    }

    public void reset(){
        if(statisticsOutput != null){
            printStatistics();
        }
        budget.reset();
        biddingExpertService.reset();
        auctionSessionStatistics.reset();
    }

    private void printStatistics(){
        statisticsOutput.println(auctionSessionStatistics.printStatistics());
        statisticsOutput.flush();
    }
}
