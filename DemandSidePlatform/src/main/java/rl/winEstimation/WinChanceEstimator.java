package rl.winEstimation;

import java.util.List;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import rl.model.AdvertisementClass;

/**
 * Created by Daniel Tyka on 2018-03-22.
 */
public interface WinChanceEstimator {

    void initialize(List<AdvertisementClass> advertisementClasses);

    double estimateWinChance(AdvertisementClass advertisementClass, Instance instance);

    void train(AdvertisementClass advertisementClass, Instance instance);
    void train(AdvertisementClass advertisementClass, Instances instances);

}
