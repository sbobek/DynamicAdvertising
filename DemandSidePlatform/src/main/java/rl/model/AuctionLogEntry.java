package rl.model;

import RequestsAndResponses.AdFeedbackInfo;
import RequestsAndResponses.DemandSidePlatformRQ;
import RequestsAndResponses.DemandSidePlatformRS;

public class AuctionLogEntry {
    private DemandSidePlatformRQ demandSidePlatformRQ;
    private DemandSidePlatformRS demandSidePlatformRS;
    private AuctionResult auctionResult;
    private AdFeedbackInfo adFeedbackInfo;

    public DemandSidePlatformRQ getDemandSidePlatformRQ() {
        return demandSidePlatformRQ;
    }

    public void setDemandSidePlatformRQ(DemandSidePlatformRQ demandSidePlatformRQ) {
        this.demandSidePlatformRQ = demandSidePlatformRQ;
    }

    public DemandSidePlatformRS getDemandSidePlatformRS() {
        return demandSidePlatformRS;
    }

    public void setDemandSidePlatformRS(DemandSidePlatformRS demandSidePlatformRS) {
        this.demandSidePlatformRS = demandSidePlatformRS;
    }

    public AuctionResult getAuctionResult() {
        return auctionResult;
    }

    public void setAuctionResult(AuctionResult auctionResult) {
        this.auctionResult = auctionResult;
    }

    public AdFeedbackInfo getAdFeedbackInfo() {
        return adFeedbackInfo;
    }

    public void setAdFeedbackInfo(AdFeedbackInfo adFeedbackInfo) {
        this.adFeedbackInfo = adFeedbackInfo;
    }
}
