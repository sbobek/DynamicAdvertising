package statistics;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import rl.model.AuctionLogEntry;
import rl.model.AuctionResult;

/**
 * Created by Daniel Tyka on 2018-03-19.
 */
public class AuctionSessionStatistics {
    public static final String STATISTICS_FILE_HEADER = "Auction Count;Won Auctions;Win Rate;Spent Budget;Remaining Budget;Click Rate;Conversion Rate";

    private Budget budget;
    private HitMissRate winRate;
    private HitMissRate clickRate;
    private HitMissRate conversionRate;

    public AuctionSessionStatistics(Budget budget) {
        this.budget = budget;
        winRate = new HitMissRate();
        clickRate = new HitMissRate();
        conversionRate = new HitMissRate();
    }

    public synchronized void processAuctionResult(AuctionLogEntry auctionLogEntry){
        if(auctionLogEntry.getAuctionResult() == AuctionResult.WON){
            winRate.hit();
            if(auctionLogEntry.getAdFeedbackInfo().isClick()){
                clickRate.hit();
            }else {
                clickRate.miss();
            }
            if(auctionLogEntry.getAdFeedbackInfo().isConversion()){
                conversionRate.hit();
            }else {
                conversionRate.miss();
            }
        }else {
            winRate.miss();
        }
    }

    public String printStatistics(){
        return Stream.of(winRate.getTotalCount(), winRate.getHitCount(), winRate.getHitRate(), budget.getSpentBudget(),
              budget.getAvailableBudget(), clickRate.getHitRate(), conversionRate.getHitRate())
              .map(Object::toString)
              .collect(Collectors.joining(";"));
    }

    public void reset(){
        winRate.reset();
        clickRate.reset();
        conversionRate.reset();
    }

    public HitMissRate getWinRate() {
        return winRate;
    }

    public void setWinRate(HitMissRate winRate) {
        this.winRate = winRate;
    }

    public HitMissRate getClickRate() {
        return clickRate;
    }

    public void setClickRate(HitMissRate clickRate) {
        this.clickRate = clickRate;
    }

    public HitMissRate getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(HitMissRate conversionRate) {
        this.conversionRate = conversionRate;
    }


}
