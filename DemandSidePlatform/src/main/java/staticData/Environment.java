package staticData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Vulpes on 2017-07-26.
 */
public abstract class Environment {

    private Environment() {
        // Exists only to prevent instantiation.
    }

    private static String model = null;
    private static String dsId = null;
    private static Integer portNo = null;
    private static String biddingAlgorithm = "expertServices.HeartService";
    private static List<String> targetTags = Collections.emptyList();
    private static Integer parentPort = null;
    private static String statisticsFile = null;
    private static String winChanceEstimator = "smacRL.winEstimation.SGDWinChanceEstimator";
    private static Double winLearningRate = null;
    private static Double winLambdaRegularization = null;
    private static String adClassResolver = "smacRL.adClass.AdClassResolverByCommonTags";
    private static Double budget;

    public static String getBiddingAlgorithm() {
        return biddingAlgorithm;
    }

    public static void setBiddingAlgorithm(String biddingAlgorithm) {
        Environment.biddingAlgorithm = biddingAlgorithm;
    }

    public static String getModel() {
        return model;
    }

    public static void setModel(String model) {
        Environment.model = model;
    }

    public static String getDsId() {
        return dsId;
    }

    public static void setDsId(String dsId) {
        Environment.dsId = dsId;
    }

    public static Integer getPortNo() {
        return portNo;
    }

    public static void setPortNo(Integer portNo) {
        Environment.portNo = portNo;
    }

    public static List<String> getTargetTags() {
        return targetTags;
    }

    public static void setTargetTags(List<String> targetTags) {
        Environment.targetTags = targetTags;
    }

    public static void setTargetTags(String targetTags) {
        Environment.targetTags = Arrays.asList(targetTags.split(","));
    }

    public static Integer getParentPort() {
        return parentPort;
    }

    public static void setParentPort(Integer parentPort) {
        Environment.parentPort = parentPort;
    }

    public static String getStatisticsFile() {
        return statisticsFile;
    }

    public static void setStatisticsFile(String statisticsFile) {
        Environment.statisticsFile = statisticsFile;
    }

    public static String getWinChanceEstimator() {
        return winChanceEstimator;
    }

    public static void setWinChanceEstimator(String winChanceEstimator) {
        Environment.winChanceEstimator = winChanceEstimator;
    }

    public static Double getWinLearningRate() {
        return winLearningRate;
    }

    public static void setWinLearningRate(Double winLearningRate) {
        Environment.winLearningRate = winLearningRate;
    }

    public static Double getWinLambdaRegularization() {
        return winLambdaRegularization;
    }

    public static void setWinLambdaRegularization(Double winLambdaRegularization) {
        Environment.winLambdaRegularization = winLambdaRegularization;
    }

    public static String getAdClassResolver() {
        return adClassResolver;
    }

    public static void setAdClassResolver(String adClassResolver) {
        Environment.adClassResolver = adClassResolver;
    }

    public static Double getBudget() {
        return budget;
    }

    public static void setBudget(Double budget) {
        Environment.budget = budget;
    }
}
