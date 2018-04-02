package statistics;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Feedback implements Serializable{

    @XmlElement
    private HitMissRate clickRate = new HitMissRate();

    @XmlElement
    private HitMissRate conversionRate = new HitMissRate();

    public void setClickRate(HitMissRate clickRate) {
        this.clickRate = clickRate;
    }

    public void setConversionRate(HitMissRate conversionRate) {
        this.conversionRate = conversionRate;
    }

    public HitMissRate getClickRate() {
        return clickRate;
    }

    public HitMissRate getConversionRate() {
        return conversionRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Feedback)) return false;

        Feedback feedback = (Feedback) o;

        if (clickRate != null ? !clickRate.equals(feedback.clickRate) : feedback.clickRate != null) return false;
        return conversionRate != null ? conversionRate.equals(feedback.conversionRate) : feedback.conversionRate == null;
    }

    @Override
    public int hashCode() {
        int result = clickRate != null ? clickRate.hashCode() : 0;
        result = 31 * result + (conversionRate != null ? conversionRate.hashCode() : 0);
        return result;
    }
}
