package rl.adClass;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import RequestsAndResponses.DemandSidePlatformRQ;
import rl.model.AdvertisementClass;
import staticData.Environment;
import utils.TagFitnessCalculator;

/**
 * Created by Daniel Tyka on 2018-03-22.
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class AdClassResolverByCommonTags implements AdClassResolver{

    @XmlElementWrapper(name = "targetTags")
    @XmlElement(name = "tag")
    private List<String> targetTags;

    @XmlElement(name = "advertisementClass")
    @XmlElementWrapper(name = "advertisementClasses")
    private List<AdvertisementClass> advertisementClasses;

    @Override
    public AdvertisementClass getAdClass(DemandSidePlatformRQ demandSidePlatformRQ) {
        int commonTags = TagFitnessCalculator.countCommonTags(targetTags, demandSidePlatformRQ.getTags());
        return advertisementClasses.stream()
                                   .filter(x -> x.getId() == commonTags)
                                   .findAny()
                                   .orElseThrow(() -> new RuntimeException("Unexpected number of commonTags="+commonTags));
    }

    @Override
    public List<AdvertisementClass> listAll() {
        return getAdvertisementClasses();
    }

    @Override
    public void initialize() {
        targetTags = Environment.getTargetTags();
        generateAdClasses();
    }

    private void generateAdClasses(){
        advertisementClasses = IntStream.rangeClosed(0, targetTags.size())
                 .mapToObj(id -> new AdvertisementClass(id, id+" common tag(s)"))
                 .collect(Collectors.toList());

    }

    public List<AdvertisementClass> getAdvertisementClasses() {
        return advertisementClasses;
    }

    protected void setAdvertisementClasses(List<AdvertisementClass> advertisementClasses) {
        this.advertisementClasses = advertisementClasses;
    }

    protected List<String> getTargetTags() {
        return targetTags;
    }

    protected void setTargetTags(List<String> targetTags) {
        this.targetTags = targetTags;
    }
}
