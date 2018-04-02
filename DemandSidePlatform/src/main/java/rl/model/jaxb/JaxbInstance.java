package rl.model.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Daniel Tyka on 2018-03-19.
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbInstance {

    @XmlElement(name = "attValue")
    @XmlElementWrapper(name = "attValues")
    private double[] attValues;

    @XmlElement
    private double weight;

    public double[] getAttValues() {
        return attValues;
    }

    public void setAttValues(double[] attValues) {
        this.attValues = attValues;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
