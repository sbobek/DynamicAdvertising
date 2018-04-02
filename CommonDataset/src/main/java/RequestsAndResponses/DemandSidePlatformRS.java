package RequestsAndResponses;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Vulpes on 2016-12-03.
 */
@XmlRootElement
public class DemandSidePlatformRS implements Serializable {
    String conversationId;

    String advertisementUrl;
    List<String> advertisementTags;
    Double bidPrice;

    String demandSidePlatformId;

    @XmlElement
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    @XmlElement
    public String getAdvertisementUrl() {
        return advertisementUrl;
    }

    public void setAdvertisementUrl(String advertisementUrl) {
        this.advertisementUrl = advertisementUrl;
    }

    @XmlElement
    public Double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(Double bidPrice) {
        this.bidPrice = bidPrice;
    }

    @XmlElement
    public String getDemandSidePlatformId() {
        return demandSidePlatformId;
    }

    public void setDemandSidePlatformId(String demandSidePlatformId) {
        this.demandSidePlatformId = demandSidePlatformId;
    }

    @XmlElement
    public List<String> getAdvertisementTags() {
        return advertisementTags;
    }

    public void setAdvertisementTags(List<String> advertisementTags) {
        this.advertisementTags = advertisementTags;
    }
}
