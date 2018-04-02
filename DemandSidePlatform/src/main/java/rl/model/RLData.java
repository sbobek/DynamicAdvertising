package rl.model;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstanceImpl;
import com.yahoo.labs.samoa.instances.Instances;

import RequestsAndResponses.DemandSidePlatformRQ;
import rl.adClass.AdClassResolver;
import rl.adClass.AdClassResolverByCommonTags;
import rl.model.jaxb.JaxbInstances;
import rl.model.jaxb.JaxbInstancesAdapter;
import rl.winEstimation.SGDWinChanceEstimator;
import rl.winEstimation.WinChanceEstimator;
import staticData.Environment;
import statistics.Feedback;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({JaxbInstances.class})
public class RLData implements Serializable{

    private static final int BID_LOG_ARFF_HEADER_CLASS_ATTR = -1;
    private static final String BID_LOG_ARFF_HEADER = "@RELATION bidlog\n" +
                                                      "@ATTRIBUTE bid NUMERIC\n" +
                                                      "@ATTRIBUTE auctionResult {lost,won}";

    @XmlElement
    @XmlJavaTypeAdapter(value = JaxbInstancesAdapter.class)
    private HashMap<AdvertisementClass, Instances> biddingLog;

    @XmlElement
    private Feedback feedback;

    @XmlElements({
          @XmlElement(name = "winChanceEstimator-sgd", type = SGDWinChanceEstimator.class)
    })
    private WinChanceEstimator winChanceEstimator;

    @XmlElements({
          @XmlElement(name = "adClassResolver-tags", type = AdClassResolverByCommonTags.class)
    })
    private AdClassResolver adClassResolver;

    public static void main(String[] args) throws Exception {

        Environment.setTargetTags(Collections.singletonList("1"));

        RLData rlData = new RLData();
        rlData.initialize();

        DemandSidePlatformRQ rq = new DemandSidePlatformRQ();
        rq.setTags(Environment.getTargetTags());

        rlData.logBid(rq, 10, AuctionResult.WON);

        Marshaller marshaller = JAXBContext.newInstance(RLData.class).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter strW = new StringWriter();
        marshaller.marshal(rlData, strW);
        String str = strW.toString();
        System.out.println(str);

        Unmarshaller unmarshaller = JAXBContext.newInstance(RLData.class).createUnmarshaller();
        RLData unsmacdata = (RLData) unmarshaller.unmarshal(new StringReader(str));
        System.out.println();
    }

    public void initialize() throws Exception{
        adClassResolver = (AdClassResolver) Class.forName(Environment.getAdClassResolver()).newInstance();
        adClassResolver.initialize();

        winChanceEstimator = (WinChanceEstimator) Class.forName(Environment.getWinChanceEstimator()).newInstance();
        winChanceEstimator.initialize(adClassResolver.listAll());

        biddingLog = new HashMap<>();
        for (AdvertisementClass advertisementClass : adClassResolver.listAll()) {
            Instances instances = new Instances(new StringReader(BID_LOG_ARFF_HEADER), 0, BID_LOG_ARFF_HEADER_CLASS_ATTR);
            biddingLog.put(advertisementClass, instances);
        }
    }

    public void logBid(DemandSidePlatformRQ demandSidePlatformRQ, double bid, AuctionResult auctionResult) {
        AdvertisementClass advertisementClass = adClassResolver.getAdClass(demandSidePlatformRQ);
        Instances instances = biddingLog.get(advertisementClass);

        Optional<Instance> optInstance = IntStream.range(0, instances.numInstances())
                                                  .mapToObj(instances::get)
                                                  .filter(x -> x.value(0) == bid)
                                                  .findAny();
        if (optInstance.isPresent()) {
            optInstance.get().setWeight(optInstance.get().weight() + 1.0d);
        } else {
            Instance instance = new InstanceImpl(1, new double[]{bid, auctionResult.getId()});
            instance.setDataset(instances);
            instances.add(instance);
        }
    }

    public void logFeedback(boolean click, boolean conversion){
        if(click){
            feedback.getClickRate().hit();
        }else {
            feedback.getClickRate().miss();
        }

        if(conversion){
            feedback.getConversionRate().hit();
        }else {
            feedback.getConversionRate().miss();
        }
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public HashMap<AdvertisementClass, Instances> getBiddingLog() {
        return biddingLog;
    }

    public void setBiddingLog(HashMap<AdvertisementClass, Instances> biddingLog) {
        this.biddingLog = biddingLog;
    }

    public WinChanceEstimator getWinChanceEstimator() {
        return winChanceEstimator;
    }

    public void setWinChanceEstimator(WinChanceEstimator winChanceEstimator) {
        this.winChanceEstimator = winChanceEstimator;
    }

    public AdClassResolver getAdClassResolver() {
        return adClassResolver;
    }

    public void setAdClassResolver(AdClassResolver adClassResolver) {
        this.adClassResolver = adClassResolver;
    }
}
