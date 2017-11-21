package staticData;

/**
 * Created by Vulpes on 2017-07-26.
 */
public class Environment {
    private static Environment instance = null;

    private Environment() {
        // Exists only to defeat instantiation.
    }

    public static Environment getInstance() {
        if (instance == null) {
            instance = new Environment();
        }
        return instance;
    }

    private static String model = null;
    private static String dsId = null;
    private static Integer portNo = null;
    private static String biddingAlgorithm = "expertServices.HeartService";

    public static void setInstance(Environment instance) {
        Environment.instance = instance;
    }

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
}
