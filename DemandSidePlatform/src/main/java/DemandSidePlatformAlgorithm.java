import RequestsAndResponses.DataExchangeGetRS;
import RequestsAndResponses.DemandSidePlatformRQ;
import RequestsAndResponses.DemandSidePlatformRS;

import java.util.concurrent.Callable;

/**
 * Created by Vulpes on 2016-12-03.
 */
public class DemandSidePlatformAlgorithm implements Callable<DemandSidePlatformRS>{
    DemandSidePlatformRQ demandSidePlatformRQ;
    String demandSidePlatformId;
    DataExchangeGetRS dataExchangeGetRS;
    RuleServiceInterface ruleServiceInterface = new HeartService();


    public DemandSidePlatformAlgorithm(DemandSidePlatformRQ demandSidePlatformRQ, DataExchangeGetRS dataExchangeGetRS, String platformId){
        this.demandSidePlatformRQ = demandSidePlatformRQ;
        this.dataExchangeGetRS = dataExchangeGetRS;
        this.demandSidePlatformId = platformId;
    }

    /**
     * here be dragons!
     * this function shall decide advertisement shall be sent for what bid value
     * basing data on request sent to DSP and (if available) uder data from Data Exchange
     * @return
     */
    private DemandSidePlatformRS decideBidValue(){
        DemandSidePlatformRS demandSidePlatformRS = new DemandSidePlatformRS();
        demandSidePlatformRS.setConversationId(demandSidePlatformRQ.getConversationId());
        demandSidePlatformRS.setAdvertisementUrl("goodUrl.com/advertisement.gif");
        demandSidePlatformRS.setBidPrice((float) (ruleServiceInterface.getProposedPrice(demandSidePlatformRQ) + Math.random()));
        demandSidePlatformRS.setDemandSidePlatformId(demandSidePlatformId);

        return demandSidePlatformRS;
    }

    public DemandSidePlatformRS call() throws Exception {
        return decideBidValue();
    }
}
