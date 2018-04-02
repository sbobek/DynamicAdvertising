import RequestsAndResponses.AdChoosenAdExchangeRS;
import RequestsAndResponses.AdFeedbackInfo;
import RequestsAndResponses.AdvertisementExchangeRQ;
import utils.TagFitnessCalculator;

import java.util.Random;

public class DefaultAdFeedbackSimulator implements AdFeedbackSimulator {

    private Random random = new Random();

    @Override
    public AdFeedbackInfo simulateAdFeedback(AdvertisementExchangeRQ advertisementExchangeRQ, AdChoosenAdExchangeRS adChoosenAdExchangeRS) {
            double tagFitness = TagFitnessCalculator.calculateTagFitness(adChoosenAdExchangeRS.getAdvertisementTags(), advertisementExchangeRQ.getTags());
            double clickChance = 0.05 + tagFitness * 0.15;
            double conversionChance = 0.01 + tagFitness * 0.04;

            boolean click = random.nextDouble() < clickChance;
            boolean conversion = click && random.nextDouble() < conversionChance;
            return new AdFeedbackInfo(click, conversion);
    }
}
