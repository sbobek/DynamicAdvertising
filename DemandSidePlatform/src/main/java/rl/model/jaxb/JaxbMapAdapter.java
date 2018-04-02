package rl.model.jaxb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by Daniel Tyka on 2018-03-22.
 */
public abstract class JaxbMapAdapter<K,V, MK, MV> extends XmlAdapter<JaxbMapAdapter.MapEntryList<MK,MV>,Map<K,V>> {

    @Override
    public Map<K, V> unmarshal(MapEntryList<MK, MV> mapEntries) {
        HashMap<K, V> result = new HashMap<>();
        mapEntries.getEntries().forEach(x ->
              result.put(unmarshalKey(x.getKey()), unmarshalValue(x.getValue())));
        return result;
    }

    @Override
    public MapEntryList<MK, MV> marshal(Map<K, V> map) {
        MapEntryList<MK, MV> mapEntries = new MapEntryList<>();
        List<MapEntry<MK, MV>> mapEntryList = map.entrySet().stream()
                                       .map(entry -> MapEntry.of(marshalKey(entry.getKey()), marshalValue(entry.getValue())))
                                       .collect(Collectors.toList());
        mapEntries.setEntries(mapEntryList);
        return mapEntries;
    }

    protected abstract MK marshalKey(K key);
    protected abstract K unmarshalKey(MK marshaledKey);
    protected abstract MV marshalValue(V value);
    protected abstract V unmarshalValue(MV marshaledValue);

    @XmlType
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class MapEntry<MK, MV> {

        @XmlElement
        private MK key;

        @XmlElement
        private MV value;

        public MapEntry() {
        }

        public MapEntry(MK key, MV value) {
            this.key = key;
            this.value = value;
        }

        public MK getKey() {
            return key;
        }

        public void setKey(MK key) {
            this.key = key;
        }

        public MV getValue() {
            return value;
        }

        public void setValue(MV value) {
            this.value = value;
        }

        public static <MK, MV> MapEntry<MK, MV> of(MK key, MV value){
            return new MapEntry<>(key, value);
        }
    }

    @XmlType
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class MapEntryList<MK, MV>{

        @XmlElement(name = "entry")
        private List<MapEntry<MK, MV>> entries;

        public List<MapEntry<MK, MV>> getEntries() {
            return entries;
        }

        public void setEntries(List<MapEntry<MK, MV>> entries) {
            this.entries = entries;
        }
    }
}
