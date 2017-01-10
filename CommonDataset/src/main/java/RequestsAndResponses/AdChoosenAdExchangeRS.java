package RequestsAndResponses;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by Vulpes on 2016-12-03.
 */
@XmlRootElement
public class AdChoosenAdExchangeRS implements Serializable{
    String conversationId;

    String advertisementUrl;
    Float paidPrice;

    public AdChoosenAdExchangeRS() {
    }

    public AdChoosenAdExchangeRS(String conversationId, String advertisementUrl, Float paidPrice) {
        this.conversationId = conversationId;
        this.advertisementUrl = advertisementUrl;
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
    public Float getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(Float paidPrice) {
        this.paidPrice = paidPrice;
    }
}
