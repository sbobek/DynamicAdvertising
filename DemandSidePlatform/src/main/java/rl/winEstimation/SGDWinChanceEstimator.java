package rl.winEstimation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.yahoo.labs.samoa.instances.Instance;

import moa.classifiers.functions.SGD;
import rl.model.AdvertisementClass;
import rl.model.AuctionResult;
import rl.model.jaxb.JaxbSerializeAdapter;
import staticData.Environment;

/**
 * Created by Daniel Tyka on 2018-03-22.
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class SGDWinChanceEstimator extends AbstractWinChanceEstimator{

    private static final String LAMBDA_REGULARIZATION = "lambdaRegularization";
    private static final String LEARNING_RATE = "learningRate";
    private static final String LOSS_FUNCTION = "lossFunction";
    private static final String LOGISTIC_REGRESSION = "LOGLOSS";

    @XmlJavaTypeAdapter(JaxbSerializeAdapter.class)
    private Map<AdvertisementClass,SGD> estimators;

    @Override
    public void initialize(List<AdvertisementClass> advertisementClasses) {
        estimators = new HashMap<>();
        advertisementClasses.forEach(adClass ->
              estimators.put(adClass, newSGD())
        );
    }

    private SGD newSGD(){
        SGD sgd = new SGD();
        sgd.getOptions().getOption(LOSS_FUNCTION).setValueViaCLIString(LOGISTIC_REGRESSION);
        if(Environment.getWinLearningRate() != null) {
            sgd.getOptions().getOption(LEARNING_RATE).setValueViaCLIString(
                  String.valueOf(Environment.getWinLearningRate()));
        }
        if(Environment.getWinLambdaRegularization() != null){
            sgd.getOptions().getOption(LAMBDA_REGULARIZATION).setValueViaCLIString(
                  String.valueOf(Environment.getWinLambdaRegularization()));
        }
        sgd.resetLearning();
        return sgd;
    }

    @Override
    public double estimateWinChance(AdvertisementClass advertisementClass, Instance instance) {
        return estimators.get(advertisementClass)
                         .getPredictionForInstance(instance)
                         .getVotes()[AuctionResult.WON.getId()];
    }

    @Override
    public void train(AdvertisementClass advertisementClass, Instance instance) {
        estimators.get(advertisementClass).trainOnInstance(instance);
    }
}
