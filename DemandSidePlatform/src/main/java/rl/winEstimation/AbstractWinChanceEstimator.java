package rl.winEstimation;

import java.util.stream.IntStream;
import com.yahoo.labs.samoa.instances.Instances;

import rl.model.AdvertisementClass;

/**
 * Created by Daniel Tyka on 2018-03-22.
 */
public abstract class AbstractWinChanceEstimator implements WinChanceEstimator {

    @Override
    public void train(AdvertisementClass advertisementClass, Instances instances) {
        IntStream.range(0, instances.numInstances())
                 .mapToObj(instances::get)
                 .forEach(x -> train(advertisementClass, x));
    }
}
