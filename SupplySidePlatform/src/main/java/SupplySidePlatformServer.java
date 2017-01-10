import RequestsAndResponses.AdChoosenAdExchangeRS;
import RequestsAndResponses.AdvertisementExchangeRQ;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.Socket;

/**
 * Created by Vulpes on 2016-12-04.
 */
public class SupplySidePlatformServer {
    private static AdvertisementExchangeRQ advertisementExchangeRQ() {
        AdvertisementExchangeRQ output = new AdvertisementExchangeRQ();
        output.setFloorPrice((float) (Math.random()*10.0));
        output.setCity("alaska");
        output.setConversationId("aaaaabbbbbbcccc");
        return output;
    }

    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket("localhost", 9000);

            System.out.println("Connected!");

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            JAXBContext advertisementExchangeRQContext = JAXBContext.newInstance(AdvertisementExchangeRQ.class);
            JAXBContext adChoosenAdExchangeRSContext = JAXBContext.newInstance(AdChoosenAdExchangeRS.class);

            Marshaller jaxbMarshaller = advertisementExchangeRQContext.createMarshaller();
            jaxbMarshaller.marshal(advertisementExchangeRQ(), out);
            out.write('\n');
            out.flush();
            System.out.println("Sent!");

            Unmarshaller jaxbUnmarshaller = adChoosenAdExchangeRSContext.createUnmarshaller();
            String str = in.readLine();
            AdChoosenAdExchangeRS adChoosenAdExchangeRS = (AdChoosenAdExchangeRS) jaxbUnmarshaller.unmarshal(new StringReader(str));

            System.out.println("Sold for: " + adChoosenAdExchangeRS.getPaidPrice());

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
