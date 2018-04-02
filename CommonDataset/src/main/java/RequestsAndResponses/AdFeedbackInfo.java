package RequestsAndResponses;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class AdFeedbackInfo implements Serializable {

    private Boolean click;
    private Boolean conversion;

    public AdFeedbackInfo() {
    }

    public AdFeedbackInfo(Boolean click, Boolean conversion) {
        this.click = click;
        this.conversion = conversion;
    }

    @XmlElement
    public Boolean isClick() {
        return click;
    }

    public void setClick(Boolean click) {
        this.click = click;
    }

    @XmlElement
    public Boolean isConversion() {
        return conversion;
    }

    public void setConversion(Boolean conversion) {
        this.conversion = conversion;
    }
}
