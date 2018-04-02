package utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public abstract class TagFitnessCalculator {

    public static double calculateTagFitness(List<String> targetTags, List<String> adTags){
        return ((double)countCommonTags(targetTags, adTags)) / ((double)targetTags.size());
    }

    public static int countCommonTags(List<String> targetTags, List<String> adTags){
        return CollectionUtils.intersection(targetTags, adTags).size();
    }
}
