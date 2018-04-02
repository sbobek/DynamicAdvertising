package statistics;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class HitMissRate implements Serializable {

    @XmlElement
    private long hitCount = 0;

    @XmlElement
    private long missCount = 0;

    public void hit() {
        hitCount++;
    }

    public void miss() {

        missCount++;
    }

    public long getTotalCount() {
        return hitCount + missCount;
    }

    public void reset() {
        hitCount = 0;
        missCount = 0;
    }

    public double getHitRate() {
        return ((double) hitCount) / ((double) getTotalCount());
    }

    public double getMissRate() {
        return ((double) missCount) / ((double) getTotalCount());
    }

    public long getHitCount() {
        return hitCount;
    }

    public void setHitCount(long hitCount) {
        this.hitCount = hitCount;
    }

    public long getMissCount() {
        return missCount;
    }

    public void setMissCount(long missCount) {
        this.missCount = missCount;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof HitMissRate)) {
            return false;
        }

        HitMissRate that = (HitMissRate) o;

        return getHitCount() == that.getHitCount() && getMissCount() == that.getMissCount();
    }

    @Override
    public int hashCode() {
        int result = (int) (getHitCount() ^ (getHitCount() >>> 32));
        result = 31 * result + (int) (getMissCount() ^ (getMissCount() >>> 32));
        return result;
    }
}
