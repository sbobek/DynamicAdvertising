package rl.model.jaxb;

import java.util.ArrayList;
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
public class JaxbAttribute {

    @XmlElement
    private String name;

    @XmlElement
    private boolean isNominal;

    @XmlElement
    private boolean isNumeric;

    @XmlElement(name = "value")
    @XmlElementWrapper(name = "values")
    private List<String> values = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNominal() {
        return isNominal;
    }

    public void setNominal(boolean nominal) {
        isNominal = nominal;
    }

    public boolean isNumeric() {
        return isNumeric;
    }

    public void setNumeric(boolean numeric) {
        isNumeric = numeric;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
