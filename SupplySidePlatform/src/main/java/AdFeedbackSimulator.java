import RequestsAndResponses.AdChoosenAdExchangeRS;
import RequestsAndResponses.AdFeedbackInfo;
import RequestsAndResponses.AdvertisementExchangeRQ;

public interface AdFeedbackSimulator {

    AdFeedbackInfo simulateAdFeedback(AdvertisementExchangeRQ advertisementExchangeRQ, AdChoosenAdExchangeRS adChoosenAdExchangeRS);
}
