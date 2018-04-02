package expertServices;

import RequestsAndResponses.DemandSidePlatformRQ;
import rl.model.AuctionLogEntry;

/**
 * Created by Vulpes on 2017-02-19.
 */
public interface BiddingExpertService {

    double getProposedPrice(DemandSidePlatformRQ demandSidePlatformRQ);

    void processAuctionResult(AuctionLogEntry auctionLogEntry);

    void initialize() throws Exception;

    void reset();

}
