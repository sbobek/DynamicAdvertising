package expertServices;

import RequestsAndResponses.DemandSidePlatformRQ;

/**
 * Created by Vulpes on 2017-02-19.
 */
public interface BiddingExpertService {
    double getProposedPrice(DemandSidePlatformRQ demandSidePlatformRQ);
    void startUpConfiguration(String... arguments);
}