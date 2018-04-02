package RequestsAndResponses;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Vulpes on 2016-12-03.
 */
@XmlRootElement
public class AdChoosenAdExchangeRS implements Serializable{
    String conversationId;

    String advertisementUrl;
    List<String> advertisementTags;
    Double paidPrice;

    public AdChoosenAdExchangeRS() {
    }

    public AdChoosenAdExchangeRS(String conversationId, String advertisementUrl, List<String> advertisementTags, Double paidPrice) {
        this.conversationId = conversationId;
        this.advertisementUrl = advertisementUrl;
        this.advertisementTags = advertisementTags;
        this.paidPrice = paidPrice;
    }

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
    public Double getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(Double paidPrice) {
        this.paidPrice = paidPrice;
    }

    @XmlElement
    public List<String> getAdvertisementTags() {
        return advertisementTags;
    }

    public void setAdvertisementTags(List<String> advertisementTags) {
        this.advertisementTags = advertisementTags;
    }
}
