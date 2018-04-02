package statistics;

import rl.model.AdvertisementClass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class FeedbackStatistics implements Serializable{

    @XmlElement
    private Map<AdvertisementClass, Feedback> feedbackMap = new HashMap<>();

    public Feedback getFeedback(AdvertisementClass advertisementClass){
        if(!feedbackMap.containsKey(advertisementClass)){
            feedbackMap.put(advertisementClass, new Feedback());
        }
        return feedbackMap.get(advertisementClass);
    }

    public Map<AdvertisementClass, Feedback> getFeedbackMap() {
        return feedbackMap;
    }

    public void setFeedbackMap(Map<AdvertisementClass, Feedback> feedbackMap) {
        this.feedbackMap = feedbackMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeedbackStatistics)) return false;

        FeedbackStatistics that = (FeedbackStatistics) o;

        return getFeedbackMap() != null ? getFeedbackMap().equals(that.getFeedbackMap()) : that.getFeedbackMap() == null;
    }

    @Override
    public int hashCode() {
        return getFeedbackMap() != null ? getFeedbackMap().hashCode() : 0;
    }
}
