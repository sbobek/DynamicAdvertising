package rl.model.jaxb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.reflect.FieldUtils;
import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstanceImpl;
import com.yahoo.labs.samoa.instances.InstanceInformation;
import com.yahoo.labs.samoa.instances.Instances;

import rl.model.AdvertisementClass;

/**
 * Created by Daniel Tyka on 2018-03-19.
 */
public class JaxbInstancesAdapter extends JaxbMapAdapter<AdvertisementClass, Instances, AdvertisementClass, JaxbInstances>{

    @Override
    protected AdvertisementClass marshalKey(AdvertisementClass key) {
        return key;
    }

    @Override
    protected AdvertisementClass unmarshalKey(AdvertisementClass marshaledKey) {
        return marshaledKey;
    }

    @Override
    protected JaxbInstances marshalValue(Instances value) {
        return marshalInstances(value);
    }

    @Override
    protected Instances unmarshalValue(JaxbInstances marshaledValue) {
        return unmarshalInstances(marshaledValue);
    }

    private Instances unmarshalInstances(JaxbInstances jaxbInstances) {
        Instances instances = new Instances();
        try {
            FieldUtils.getField(instances.getClass(),"instances",true)
                      .set(instances, new ArrayList<Instance>());
            FieldUtils.getField(instances.getClass(),"instanceInformation",true)
                      .set(instances, unmarshalInstanceInformation(jaxbInstances.getInstanceInformation()));
            FieldUtils.getField(instances.getClass(),"hsAttributesIndices",true)
                      .set(instances, jaxbInstances.getHsAttributesIndices());
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }

        jaxbInstances.getInstances().stream()
                     .map(this::unmarshalInstance)
                     .peek(x -> x.setDataset(instances))
                     .forEach(instances::add);
        return instances;
    }

    @SuppressWarnings("unchecked")
    private JaxbInstances marshalInstances(Instances instances) {
        JaxbInstances jaxbInstances = new JaxbInstances();

        jaxbInstances.setInstances(IntStream.range(0, instances.numInstances())
                                            .mapToObj(instances::get)
                                            .map(this::marshalInstance).collect(Collectors.toList()));
        try {
            InstanceInformation instanceInformation = (InstanceInformation) FieldUtils.getField(instances.getClass(),
                  "instanceInformation",true).get(instances);
            jaxbInstances.setInstanceInformation(marshalInstanceInformation(instanceInformation));

            HashMap<String, Integer> hsAttributesIndices = (HashMap<String, Integer>) FieldUtils.getField(instances.getClass(),
                  "hsAttributesIndices",true).get(instances);
            jaxbInstances.setHsAttributesIndices(hsAttributesIndices);
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }

        return jaxbInstances;
    }

    private JaxbInstance marshalInstance(Instance instance){
        JaxbInstance jaxbInstance = new JaxbInstance();
        jaxbInstance.setAttValues(instance.toDoubleArray());
        jaxbInstance.setWeight(instance.weight());
        return jaxbInstance;
    }

    private Instance unmarshalInstance(JaxbInstance denseInstance){
        return new InstanceImpl(denseInstance.getWeight(), denseInstance.getAttValues());
    }

    private JaxbInstanceInformation marshalInstanceInformation(InstanceInformation instanceInformation){
        JaxbInstanceInformation jaxbInstanceInformation = new JaxbInstanceInformation();
        jaxbInstanceInformation.setAttributes(IntStream.range(0, instanceInformation.numAttributes())
                                                       .mapToObj(instanceInformation::attribute)
                                                       .map(this::marshalAttribute)
                                                       .collect(Collectors.toList()));
        jaxbInstanceInformation.setRelationName(instanceInformation.getRelationName());
        jaxbInstanceInformation.setClassIndex(instanceInformation.classIndex());
        return jaxbInstanceInformation;
    }

    private InstanceInformation unmarshalInstanceInformation(JaxbInstanceInformation jaxbInstanceInfo){
        List<Attribute> attributes = jaxbInstanceInfo.getAttributes().stream()
                                                            .map(this::unmarshalAttribute)
                                                            .collect(Collectors.toList());
        InstanceInformation instanceInfo = new InstanceInformation(jaxbInstanceInfo.getRelationName(), attributes);
        instanceInfo.setClassIndex(jaxbInstanceInfo.getClassIndex());
        return instanceInfo;
    }

    private JaxbAttribute marshalAttribute(Attribute attribute){
        JaxbAttribute jaxbAttribute = new JaxbAttribute();
        jaxbAttribute.setName(attribute.name());
        jaxbAttribute.setNumeric(attribute.isNumeric());
        jaxbAttribute.setNominal(attribute.isNominal());
        if(attribute.isNominal()){
            jaxbAttribute.setValues(attribute.getAttributeValues());
        }
        return jaxbAttribute;
    }

    private Attribute unmarshalAttribute(JaxbAttribute jaxbAttribute){
        if(jaxbAttribute.isNumeric()){
            return new Attribute(jaxbAttribute.getName());
        }else if(jaxbAttribute.isNominal()){
            return new Attribute(jaxbAttribute.getName(), jaxbAttribute.getValues());
        }else {
            throw new UnsupportedOperationException("Unsupported unmarshalling of attribute with unknown type");
        }
    }
}
