package RequestsAndResponses;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by Vulpes on 2016-12-03.
 */
@XmlRootElement
public class AdFormat implements Serializable {
    Long width;
    Long height;
    Visibility visibility;
    Position position;

    public AdFormat(){

    }

    public AdFormat(Long width, Long height, Visibility visibility, Position position) {
        this.width = width;
        this.height = height;
        this.visibility = visibility;
        this.position = position;
    }

    @XmlElement
    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    @XmlElement
    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    @XmlElement
    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @XmlElement
    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
