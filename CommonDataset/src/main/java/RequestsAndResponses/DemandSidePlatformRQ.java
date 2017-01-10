package RequestsAndResponses;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Vulpes on 2016-12-03.
 */
@XmlRootElement
public class DemandSidePlatformRQ implements Serializable {
    String conversationId;

    Date dateTime;
    String country;
    String region;
    String city;
    String publisherURL;
    List<String> tags;
    String systemdata;
    AdFormat format;
    Float floorPrice;

    String dataExchangeId;

    public DemandSidePlatformRQ() {
        tags = new ArrayList<String>();
    }

    @XmlElement
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    @XmlElement
    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @XmlElement
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @XmlElement
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @XmlElement
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @XmlElement
    public String getPublisherURL() {
        return publisherURL;
    }

    public void setPublisherURL(String publisherURL) {
        this.publisherURL = publisherURL;
    }

    @XmlElement
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @XmlElement
    public String getSystemdata() {
        return systemdata;
    }

    public void setSystemdata(String systemdata) {
        this.systemdata = systemdata;
    }

    @XmlElement
    public AdFormat getFormat() {
        return format;
    }

    public void setFormat(AdFormat format) {
        this.format = format;
    }

    @XmlElement
    public Float getFloorPrice() {
        return floorPrice;
    }

    public void setFloorPrice(Float floorPrice) {
        this.floorPrice = floorPrice;
    }

    @XmlElement
    public String getDataExchangeId() {
        return dataExchangeId;
    }

    public void setDataExchangeId(String dataExchangeId) {
        this.dataExchangeId = dataExchangeId;
    }
}
