package rl.model.jaxb;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Daniel Tyka on 2018-03-20.
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbInstanceInformation {

    @XmlElement
    private String relationName;

    @XmlElement(name = "attribute")
    @XmlElementWrapper(name = "attributes")
    private List<JaxbAttribute> attributes;

    @XmlElement
    private int classIndex;

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public List<JaxbAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<JaxbAttribute> attributes) {
        this.attributes = attributes;
    }

    public int getClassIndex() {
        return classIndex;
    }

    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }
}
