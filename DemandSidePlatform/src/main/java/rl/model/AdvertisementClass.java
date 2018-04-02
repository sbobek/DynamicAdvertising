package rl.model;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class AdvertisementClass implements Serializable{

    @XmlAttribute
    private int id;

    @XmlAttribute
    private String name;

    public AdvertisementClass() {
    }

    public AdvertisementClass(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdvertisementClass)) return false;

        AdvertisementClass that = (AdvertisementClass) o;

        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
