package rl.model.jaxb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class JaxbInstances {

    @XmlElement(name = "instance")
    @XmlElementWrapper(name = "instances")
    private List<JaxbInstance> instances = new ArrayList<>();

    @XmlElement
    private JaxbInstanceInformation instanceInformation;

    @XmlElement
    private HashMap<String, Integer> hsAttributesIndices;

    public List<JaxbInstance> getInstances() {
        return instances;
    }

    public void setInstances(List<JaxbInstance> instances) {
        this.instances = instances;
    }

    public JaxbInstanceInformation getInstanceInformation() {
        return instanceInformation;
    }

    public void setInstanceInformation(JaxbInstanceInformation instanceInformation) {
        this.instanceInformation = instanceInformation;
    }

    public HashMap<String, Integer> getHsAttributesIndices() {
        return hsAttributesIndices;
    }

    public void setHsAttributesIndices(HashMap<String, Integer> hsAttributesIndices) {
        this.hsAttributesIndices = hsAttributesIndices;
    }
}
