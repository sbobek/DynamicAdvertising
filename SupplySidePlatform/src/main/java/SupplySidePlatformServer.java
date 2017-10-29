import RequestsAndResponses.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

public class SupplySidePlatformServer {
    private static SecureRandom random = new SecureRandom();
    private static List<AdvertisementExchangeRQ> adList = new ArrayList<>();
    private static int port;
    static HashMap<String, Float> paid = new HashMap<>();
    static HashMap<String, Integer> sold = new HashMap<>();

    /**
     * przykładowy request
     **/
    private static AdvertisementExchangeRQ advertisementExchangeRQ() {
        AdvertisementExchangeRQ output = new AdvertisementExchangeRQ();
        output.setFloorPrice((float) (Math.random() * 100.0));
        output.setConversationId(new BigInteger(130, random).toString(32));
        output.setCity("KRK");
        output.setDateTime(new Date());
        output.setTags(Arrays.asList("FISH", "FISHING", "TOOLS"));
        output.setSystemdata("WINDOWS7, MOZILLAFIREFOX");
        output.setCountry("PL");
        output.setFormat(new AdFormat(100l, 500l, Visibility.SIDEWAY, Position.FIXED));
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
                    advertisementExchangeRQ.setFloorPrice(Float.parseFloat(line));

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
            output.setFloorPrice((float) (Math.random() * 1.0));
            output.setConversationId(new BigInteger(130, random).toString(32));
            output.setCountry("PL");
            output.setRegion("KRK");
            output.setCity("KRK");
            output.setDateTime(new Date());
            output.setTags(Arrays.asList("FISH", "FISHING", "TOOLS", "NETS", "HOOKS", "BOATS"));
            output.setSystemdata("WINDOWS7, MOZILLAFIREFOX");
            output.setFormat(new AdFormat((long) (Math.random() * 200.0), (long) (Math.random() * 200.0), Visibility.getRandom(), Position.getRandom()));
            list.add(output);
        }
    }

    static int starterData(String[] args) {
        if (args.length < 1) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Please enter: DATA_EXCHANGE_SERVER_PORT PATH_TO_FILE_WITH_ADS\n" +
                    "you can enter NONE as path to file, it will mean to use single example of request");
            String command = scanner.nextLine();
            String[] data = command.split(" ");
            port = Integer.parseInt(data[0]);

            if (!data[1].equals("NONE")) {
                readFromFile(data[1], adList);
            } else adList.add(advertisementExchangeRQ());
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
                }
                i++;
            }
            if (adList.isEmpty())
                adList.add(advertisementExchangeRQ());
        }
        return 0;
    }

    private static void algorithm(String[] args) {
        if (starterData(args) == -1) {
            System.err.println("Some of arguments were wrong!");
            return;
        }

        for (AdvertisementExchangeRQ advertisementExchangeRQ : adList) {
            Socket socket = null;
            try {
                socket = new Socket("localhost", port);

                System.out.println("Connected!");

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                JAXBContext advertisementExchangeRQContext = JAXBContext.newInstance(AdvertisementExchangeRQ.class);
                JAXBContext adChoosenAdExchangeRSContext = JAXBContext.newInstance(AdChoosenAdExchangeRS.class);

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
                if (!paid.containsKey(adChoosenAdExchangeRS.getAdvertisementUrl()))
                    paid.put(adChoosenAdExchangeRS.getAdvertisementUrl(), 0.00f);
                if (!sold.containsKey(adChoosenAdExchangeRS.getAdvertisementUrl()))
                    sold.put(adChoosenAdExchangeRS.getAdvertisementUrl(), 0);

                paid.put(adChoosenAdExchangeRS.getAdvertisementUrl(), paid.get(adChoosenAdExchangeRS.getAdvertisementUrl()) + adChoosenAdExchangeRS.getPaidPrice());
                sold.put(adChoosenAdExchangeRS.getAdvertisementUrl(), sold.get(adChoosenAdExchangeRS.getAdvertisementUrl()) + 1);


                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        LocalDateTime start = LocalDateTime.now();
        algorithm(args);
        LocalDateTime stop = LocalDateTime.now();
        long diffInMilli = java.time.Duration.between(start, stop).toMillis();
        long diffInSeconds = java.time.Duration.between(start, stop).getSeconds();
        long diffInMinutes = java.time.Duration.between(start, stop).toMinutes();
        System.out.println("Work took full " + diffInMinutes + " minutes!");
        System.out.println("Work took full " + diffInSeconds + " seconds!");
        System.out.println("Work took full " + diffInMilli + " miliseconds!");

        for (String key : sold.keySet()) {
            System.out.println("Sold " + sold.get(key) + " to " + key + " for sum of " + paid.get(key));
        }
    }
}
