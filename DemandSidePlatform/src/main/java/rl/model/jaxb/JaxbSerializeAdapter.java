package rl.model.jaxb;

import java.io.Serializable;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.commons.lang3.SerializationUtils;

/**
 * Created by Daniel Tyka on 2018-03-22.
 */
public class JaxbSerializeAdapter<T> extends XmlAdapter<byte[], T>{

    @Override
    @SuppressWarnings("unchecked")
    public T unmarshal(byte[] byteArray) {
        return (T) SerializationUtils.deserialize(byteArray);
    }

    @Override
    public byte[] marshal(T serializable) {
        return SerializationUtils.serialize((Serializable)serializable);
    }
}
