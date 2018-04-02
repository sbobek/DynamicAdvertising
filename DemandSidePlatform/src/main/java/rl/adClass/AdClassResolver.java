package rl.adClass;

import java.util.List;

import RequestsAndResponses.DemandSidePlatformRQ;
import rl.model.AdvertisementClass;

/**
 * Created by Daniel Tyka on 2018-03-22.
 */
public interface AdClassResolver {

    AdvertisementClass getAdClass(DemandSidePlatformRQ demandSidePlatformRQ);
    List<AdvertisementClass> listAll();
    void initialize();
}
