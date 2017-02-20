import RequestsAndResponses.DemandSidePlatformRQ;
import com.sun.deploy.util.StringUtils;
import heart.Configuration;
import heart.HeaRT;
import heart.State;
import heart.StateElement;
import heart.alsvfd.Formulae;
import heart.alsvfd.SimpleNumeric;
import heart.alsvfd.SimpleSymbolic;
import heart.alsvfd.Value;
import heart.alsvfd.expressions.ExpressionInterface;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.BuilderException;
import heart.exceptions.NotInTheDomainException;
import heart.parser.hmr.HMRParser;
import heart.parser.hmr.runtime.SourceFile;
import heart.xtt.*;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: find a way to run HeartDroid without it's LOGS... they pretty much trash my logs...
 */
public class HeartService implements RuleServiceInterface{
    XTTModel model = null;

    private static Integer soldAds = 0;
    private static Double paidMoney = 0.0;
    private static String modelfile = "src/main/resources/XTT2Model.hmr";

    public static String getModelfile() {
        return modelfile;
    }

    public static void setModelfile(String modelfile) {
        if(modelfile.equals("NONE")) return;
        HeartService.modelfile = modelfile;
    }

    public static synchronized Integer getSoldAds() {
        return soldAds;
    }

    public static synchronized void incrementSoldAds() {
        HeartService.soldAds = HeartService.soldAds + 1;
    }

    public static synchronized Double getPaidMoney() {
        return paidMoney;
    }

    public static synchronized void addPaidMoney(Double paidMoney) {
        HeartService.paidMoney += paidMoney;
    }

    HeartService() {
        getModel();
    }

    public boolean getModel() {
        try {
            //Loading a file with a model
            SourceFile hmr_threat_monitor = new SourceFile(modelfile);
            HMRParser parser = new HMRParser();

            //Parsing the file with the model
            parser.parse(hmr_threat_monitor);
            model = parser.getModel();

        } catch (Exception allExceptions) {
            allExceptions.printStackTrace();
            return false;
        }
        return true;
    }

    public void printModelData() {
        LinkedList<Type> types = model.getTypes();
        for (Type t : types) {
            System.out.println("Type id: " + t.getId());
            System.out.println("Type name: " + t.getName());
            System.out.println("Type base: " + t.getBase());
            System.out.println("Type length: " + t.getLength());
            System.out.println("Type scale: " + t.getPrecision());
            System.out.println("desc: " + t.getDescription());

            for (Value v : t.getDomain().getValues()) {
                System.out.println("Value: " + v);
            }
            System.out.println("==========================");
        }

        //Printing all the attributes within the model
        LinkedList<Attribute> atts = model.getAttributes();
        for (Attribute att : atts) {
            System.out.println("Att Id: " + att.getId());
            System.out.println("Att name: " + att.getName());
            System.out.println("Att typeName: " + att.getTypeId());
            System.out.println("Att abbrev: " + att.getAbbreviation());
            System.out.println("Att comm: " + att.getComm());
            System.out.println("Att desc: " + att.getDescription());
            System.out.println("Att class: " + att.getXTTClass());
            System.out.println("==========================");
        }

        //Printing all the tables and rules within the model
        LinkedList<Table> tables = model.getTables();
        for (Table t : tables) {
            System.out.println("Table id:" + t.getId());
            System.out.println("Table name:" + t.getName());
            LinkedList<heart.xtt.Attribute> cond = t.getPrecondition();
            for (heart.xtt.Attribute a : cond) {
                System.out.println("schm Cond: " + a.getName());
            }
            LinkedList<Attribute> concl = t.getConclusion();
            for (heart.xtt.Attribute a : concl) {
                System.out.println("schm Conclusion: " + a.getName());
            }

            System.out.println("RULES FOR TABLE " + t.getName());

            for (Rule r : t.getRules()) {
                System.out.print("Rule id: " + r.getId() + ":\n\tIF ");
                for (Formulae f : r.getConditions()) {
                    System.out.print(f.getAttribute().getName() + " " + f.getOp() + " " + f.getRHS() + ", ");
                }

                System.out.println("THEN ");

                for (Decision d : r.getDecisions()) {
                    System.out.print("\t" + d.getAttribute().getName() + "is set to ");

                    ExpressionInterface e = d.getDecision();
                    System.out.print(e);
                }
                System.out.println();

            }
            System.out.println();
            System.out.println("=============================");


        }
    }

    public void printCurrent() {
        System.out.println("Printing current state");
        State current = HeaRT.getWm().getCurrentState(model);
        for (StateElement se : current) {
            System.out.println("Attribute " + se.getAttributeName() + " = " + se.getValue());
        }
    }

    private String getDayName(int day) {
        switch (day) {
            case 2:
                return "MON";
            case 3:
                return "TUE";
            case 4:
                return "WED";
            case 5:
                return "THU";
            case 6:
                return "FRI";
            case 7:
                return "SAT";
            case 1:
                return "SUN";
        }
        return "";
    }

    private String getMonthName(int month) {
        switch (month) {
            case 0:
                return "JAN";
            case 1:
                return "FEB";
            case 2:
                return "MAR";
            case 3:
                return "APR";
            case 4:
                return "MAY";
            case 5:
                return "JUN";
            case 6:
                return "JUL";
            case 7:
                return "AUG";
            case 8:
                return "SEP";
            case 9:
                return "OCT";
            case 10:
                return "NOV";
            case 11:
                return "DEC";
        }
        return "";
    }

    public State createState(DemandSidePlatformRQ demandSidePlatformRQ) {
        State XTTstate = new State();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(demandSidePlatformRQ.getDateTime());

        // Creating StateElements objects, one for each attribute
        StateElement dayOfWeekE = new StateElement();
        int dowVal = calendar.get(Calendar.DAY_OF_WEEK);
        dayOfWeekE.setAttributeName("day_of_week_attr");
        dayOfWeekE.setValue(new SimpleSymbolic(getDayName(dowVal), dowVal));
        XTTstate.addStateElement(dayOfWeekE);

        StateElement monthE = new StateElement();
        int monthVal = calendar.get(Calendar.MONTH);
        monthE.setAttributeName("month_attr");
        monthE.setValue(new SimpleSymbolic(getMonthName(monthVal), monthVal));
        XTTstate.addStateElement(monthE);

        StateElement countryE = new StateElement();
        String country = demandSidePlatformRQ.getCountry();
        countryE.setAttributeName("country_attr");
        countryE.setValue(new SimpleSymbolic(country));
        XTTstate.addStateElement(countryE);

        StateElement regionE = new StateElement();
        String region = demandSidePlatformRQ.getRegion();
        regionE.setAttributeName("region_attr");
        regionE.setValue(new SimpleSymbolic(region));
        XTTstate.addStateElement(regionE);

        StateElement cityE = new StateElement();
        String city = demandSidePlatformRQ.getCity();
        cityE.setAttributeName("city_attr");
        cityE.setValue(new SimpleSymbolic(city));
        XTTstate.addStateElement(cityE);

        StateElement publisherE = new StateElement();
        String publisher = demandSidePlatformRQ.getPublisherURL();
        publisherE.setAttributeName("publisher_attr");
        publisherE.setValue(new SimpleSymbolic(publisher));
        XTTstate.addStateElement(publisherE);

        StateElement tagsE = new StateElement();
        String tags = StringUtils.join(demandSidePlatformRQ.getTags(), ",");
        tagsE.setAttributeName("tags_attr");
        tagsE.setValue(new SimpleSymbolic(tags));
        XTTstate.addStateElement(tagsE);

        StateElement systemdataE = new StateElement();
        String systemdata = demandSidePlatformRQ.getSystemdata();
        systemdataE.setAttributeName("systemdata_attr");
        systemdataE.setValue(new SimpleSymbolic(systemdata));
        XTTstate.addStateElement(systemdataE);

        StateElement adWidthE = new StateElement();
        Long width = demandSidePlatformRQ.getFormat().getWidth();
        adWidthE.setAttributeName("adwidth_attr");
        adWidthE.setValue(new SimpleNumeric(Double.valueOf(width)));
        XTTstate.addStateElement(adWidthE);

        StateElement adHeightE = new StateElement();
        Long height = demandSidePlatformRQ.getFormat().getHeight();
        adHeightE.setAttributeName("adheight_attr");
        adHeightE.setValue(new SimpleNumeric(Double.valueOf(height)));
        XTTstate.addStateElement(adHeightE);

        StateElement aoldAdsE = new StateElement();
        aoldAdsE.setAttributeName("soldads_attr");
        aoldAdsE.setValue(new SimpleNumeric(Double.valueOf(getSoldAds())));
        XTTstate.addStateElement(aoldAdsE);

        StateElement paidMoneyE = new StateElement();
        paidMoneyE.setAttributeName("paidomoney_attr");
        paidMoneyE.setValue(new SimpleNumeric(getPaidMoney()));
        XTTstate.addStateElement(paidMoneyE);

        return XTTstate;
    }

    public State createDefaultState() {
        Calendar calendar = Calendar.getInstance();
        State XTTstate = new State();

        // Creating StateElements objects, one for each attribute
        StateElement dayOfWeekE = new StateElement();
        int dowVal = calendar.get(Calendar.DAY_OF_WEEK);
        dayOfWeekE.setAttributeName("day_of_week_attr");
        dayOfWeekE.setValue(new SimpleSymbolic(getDayName(dowVal), dowVal));
        XTTstate.addStateElement(dayOfWeekE);

        StateElement monthE = new StateElement();
        int monthVal = calendar.get(Calendar.MONTH);
        monthE.setAttributeName("month_attr");
        monthE.setValue(new SimpleSymbolic(getMonthName(monthVal), monthVal));
        XTTstate.addStateElement(monthE);

        StateElement countryE = new StateElement();
        String country = "PL";
        countryE.setAttributeName("country_attr");
        countryE.setValue(new SimpleSymbolic(country));
        XTTstate.addStateElement(countryE);

        return XTTstate;
    }

    public void runWithStartingState(State state) {
        try {
            // To automate data driven interface we fire every table
            // reason for this - we need information
            // which tables to fire, but we know nothing about table names

            List<String> tableNames = model.getTables().stream()
                    .map(Table::getName)
                    .collect(Collectors.toList());

            HeaRT.dataDrivenInference(model,
                    (String[]) tableNames.toArray(new String[tableNames.size()]),
                    new Configuration.Builder()
                            .setInitialState(state)
                            .build());

        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        } catch (AttributeNotRegisteredException e) {
            e.printStackTrace();
        } catch (NotInTheDomainException e) {
            e.printStackTrace();
        } catch (BuilderException e) {
            e.printStackTrace();
        }
    }

    public double getProposedPrice(DemandSidePlatformRQ demandSidePlatformRQ) {
        runWithStartingState(createState(demandSidePlatformRQ));
        State current = HeaRT.getWm().getCurrentState(model);
        SimpleNumeric maxbid = (SimpleNumeric) current.getValueOfAttribute("maxbid_attr");
        SimpleNumeric bidsize = (SimpleNumeric) current.getValueOfAttribute("bidsize_attr");
        System.out.println("maxbid: " + maxbid.getValue());
        System.out.println("bidsize: " + bidsize.getValue());
        return maxbid.getValue() * bidsize.getValue();
    }

}
