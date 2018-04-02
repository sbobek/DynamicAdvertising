import RequestsAndResponses.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SupplySidePlatformServer {
    private static final int MIN_RANDOM_TAGS = 1;

    private static SecureRandom random = new SecureRandom();
    private static List<AdvertisementExchangeRQ> adList = new ArrayList<>();
    private static int port;
    private static Integer parentPort = null;
    private static AdFeedbackSimulator adFeedbackSimulator = new DefaultAdFeedbackSimulator();
    private static List<String> availableTags = Arrays.asList("FISH", "FISHING", "TOOLS", "NETS", "HOOKS", "BOATS");

    static HashMap<String, Double> paid = new HashMap<>();
    static HashMap<String, Integer> sold = new HashMap<>();

    /**
     * przykładowy request
     **/
    private static AdvertisementExchangeRQ advertisementExchangeRQ() {
        AdvertisementExchangeRQ output = new AdvertisementExchangeRQ();
        output.setFloorPrice((Math.random() * 100.0));
        output.setConversationId(new BigInteger(130, random).toString(32));
        output.setCity("KRK");
        output.setDateTime(new Date());
        output.setTags(randomTags());
        output.setSystemdata("WINDOWS7, MOZILLAFIREFOX");
        output.setCountry("PL");
        output.setFormat(new AdFormat(100L, 500L, Visibility.SIDEWAY, Position.FIXED));
        output.setRegion("KRK");
        return output;
    }

    private static void readFromFile(String filepath, List<AdvertisementExchangeRQ> list) {
        File file = new File(filepath);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                AdvertisementExchangeRQ advertisementExchangeRQ = new AdvertisementExchangeRQ();
                if (!Objects.equals(line.trim(), "")) {
                    line = br.readLine();
                    String[] data = line.split(" ");
                    Date date = new Date();
                    date.setYear(Integer.parseInt(data[0]));
                    date.setMonth(Integer.parseInt(data[1]));
                    date.setDate(Integer.parseInt(data[2]));

                    line = br.readLine();
                    data = line.split(" ");
                    date.setHours(Integer.parseInt(data[0]));
                    date.setMinutes(Integer.parseInt(data[0]));
                    advertisementExchangeRQ.setDateTime(date);

                    line = br.readLine();
                    advertisementExchangeRQ.setCity(line);

                    line = br.readLine();
                    advertisementExchangeRQ.setRegion(line);

                    line = br.readLine();
                    advertisementExchangeRQ.setCountry(line);

                    line = br.readLine();
                    advertisementExchangeRQ.setSystemdata(line);

                    line = br.readLine();
                    advertisementExchangeRQ.setTags(Arrays.asList(line.split(" ")));

                    line = br.readLine();
                    data = line.split(" ");
                    advertisementExchangeRQ.setFormat(
                            new AdFormat(Long.parseLong(data[0]),
                                    Long.parseLong(data[1]),
                                    Visibility.valueOf(data[2]),
                                    Position.valueOf(data[3])));

                    line = br.readLine();
                    advertisementExchangeRQ.setFloorPrice(Double.parseDouble(line));

                    advertisementExchangeRQ.setConversationId(new BigInteger(130, random).toString(32));
                    list.add(advertisementExchangeRQ);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateRandomAds(Integer amount, List<AdvertisementExchangeRQ> list) {
        for (int i = 0; i < amount; i++) {
            AdvertisementExchangeRQ output = new AdvertisementExchangeRQ();
            output.setFloorPrice((Math.random() * 1.0));
            output.setConversationId(new BigInteger(130, random).toString(32));
            output.setCountry("PL");
            output.setRegion("KRK");
            output.setCity("KRK");
            output.setDateTime(new Date());
            output.setTags(randomTags());
            output.setSystemdata("WINDOWS7, MOZILLAFIREFOX");
            output.setFormat(new AdFormat((long) (Math.random() * 200.0), (long) (Math.random() * 200.0), Visibility.getRandom(), Position.getRandom()));
            list.add(output);
        }
    }

    private static List<String> randomTags(){
        int numberOfTags = MIN_RANDOM_TAGS + random.nextInt(availableTags.size() - MIN_RANDOM_TAGS + 1);
        for (int i = 0; i < numberOfTags; i++)
        {
            int indexToSwap = i + random.nextInt(availableTags.size() - i);
            String temp = availableTags.get(i);
            availableTags.set(i, availableTags.get(indexToSwap));
            availableTags.set(indexToSwap, temp);
        }
        return availableTags.subList(0, numberOfTags).stream()
                            .sorted()
                            .collect(Collectors.toList());
    }

    static int starterData(String[] args) {
        if (args.length < 1) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Please enter: DATA_EXCHANGE_SERVER_PORT PATH_TO_FILE_WITH_ADS AD_FEEDBACK_SIMULATOR\n" +
                    "you can enter NONE as path to file, it will mean to use single example of request\n" +
                    "you can enter DEFAULT as ad feedback simulator, default implementation DefaultAdFeedbackSimulator will be used");
            String command = scanner.nextLine();
            String[] data = command.split(" ");
            port = Integer.parseInt(data[0]);

            if (!data[1].equals("NONE")) {
                readFromFile(data[1], adList);
            } else adList.add(advertisementExchangeRQ());

            try {
                if("DEFAULT".equals(data[2])){
                    adFeedbackSimulator = new DefaultAdFeedbackSimulator();
                }else {
                    adFeedbackSimulator = (AdFeedbackSimulator) Class.forName(data[2]).newInstance();
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            int i = 0;

            while (i < args.length) {
                switch (args[i]) {
                    case "-port":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        port = Integer.parseInt(args[i]);
                        break;
                    case "-file":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        if (Objects.equals(args[i], "NONE"))
                            adList.add(advertisementExchangeRQ());
                        else
                            readFromFile(args[i], adList);
                        break;
                    case "-random":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        generateRandomAds(Integer.parseInt(args[i]), adList);
                        break;
                    case "-tags":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        availableTags = Arrays.asList(args[i].split(","));
                        break;
                    case "-parent":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        parentPort = Integer.parseInt(args[i]);
                        break;
                    case "-feedbackSimulator":
                        i++;
                        if (!(i < args.length))
                            return -1;
                        try {
                            if("DEFAULT".equals(args[i])){
                                adFeedbackSimulator = new DefaultAdFeedbackSimulator();
                            }else {
                                adFeedbackSimulator = (AdFeedbackSimulator) Class.forName(args[i]).newInstance();
                            }
                        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                i++;
            }
            if (adList.isEmpty())
                adList.add(advertisementExchangeRQ());
        }
        return 0;
    }

    private static void notifyParent(String message){
        if(parentPort != null){
            try {
                Socket notice = new Socket("localhost", parentPort);
                PrintWriter out = new PrintWriter(notice.getOutputStream(), true);
                out.println("[SSP] " + message);
                out.flush();
                notice.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void algorithm(String[] args) {
        if (starterData(args) == -1) {
            System.err.println("Some of arguments were wrong!");
            return;
        }
        notifyParent("up");

        for (AdvertisementExchangeRQ advertisementExchangeRQ : adList) {
            try {
                Socket socket = new Socket("localhost", port);

                System.out.println("Connected!");

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                JAXBContext advertisementExchangeRQContext = JAXBContext.newInstance(AdvertisementExchangeRQ.class);
                JAXBContext adChoosenAdExchangeRSContext = JAXBContext.newInstance(AdChoosenAdExchangeRS.class);
                JAXBContext adFeedbackInfoContext = JAXBContext.newInstance(AdFeedbackInfo.class);

                Marshaller jaxbMarshaller = advertisementExchangeRQContext.createMarshaller();
                jaxbMarshaller.marshal(advertisementExchangeRQ, out);
                out.write('\n');
                out.flush();
                System.out.println("Sent!");

                Unmarshaller jaxbUnmarshaller = adChoosenAdExchangeRSContext.createUnmarshaller();
                String str = in.readLine();
                System.out.println("AdEx RS: " + str);
                AdChoosenAdExchangeRS adChoosenAdExchangeRS = (AdChoosenAdExchangeRS) jaxbUnmarshaller.unmarshal(new StringReader(str));

                System.out.println("Sold for: " + adChoosenAdExchangeRS.getPaidPrice());
                System.out.println("Sold for: " + adChoosenAdExchangeRS.getAdvertisementUrl());
                if (!"NULL".equals(adChoosenAdExchangeRS.getAdvertisementUrl())) {
                    AdFeedbackInfo adFeedbackInfo = adFeedbackSimulator.simulateAdFeedback(advertisementExchangeRQ, adChoosenAdExchangeRS);
                    Marshaller feedbackMarshaller = adFeedbackInfoContext.createMarshaller();
                    feedbackMarshaller.marshal(adFeedbackInfo, out);
                    out.write('\n');
                    out.flush();
                    System.out.println("Sent ad feedback!");
                }
                if (!paid.containsKey(adChoosenAdExchangeRS.getAdvertisementUrl()))
                    paid.put(adChoosenAdExchangeRS.getAdvertisementUrl(), 0.0d);
                if (!sold.containsKey(adChoosenAdExchangeRS.getAdvertisementUrl()))
                    sold.put(adChoosenAdExchangeRS.getAdvertisementUrl(), 0);

                paid.put(adChoosenAdExchangeRS.getAdvertisementUrl(), paid.get(adChoosenAdExchangeRS.getAdvertisementUrl()) + adChoosenAdExchangeRS.getPaidPrice());
                sold.put(adChoosenAdExchangeRS.getAdvertisementUrl(), sold.get(adChoosenAdExchangeRS.getAdvertisementUrl()) + 1);

                socket.close();

            } catch (IOException | JAXBException e) {
                e.printStackTrace();
            }
        }

        notifyParent("down");
    }

    public static void main(String[] args) {
        LocalDateTime start = LocalDateTime.now();
        algorithm(args);
        LocalDateTime stop = LocalDateTime.now();
        String time = DurationFormatUtils.formatDuration(Duration.between(start, stop).toMillis(), "HH:mm:ss.SSS");
        System.out.println("Work took " + time + " !");

        for (String key : sold.keySet()) {
            System.out.println("Sold " + sold.get(key) + " to " + key + " for sum of " + paid.get(key));
        }
    }
}
