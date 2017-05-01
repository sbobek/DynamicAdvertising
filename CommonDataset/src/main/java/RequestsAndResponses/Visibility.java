package RequestsAndResponses;

import javax.xml.bind.annotation.XmlEnum;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Vulpes on 2016-12-03.
 */
@XmlEnum(String.class)
public enum Visibility implements Serializable {
    ABOVE_FOLD,
    BELOW_FOLD,
    SIDEWAY;

    public static Visibility getRandom(){
        return Arrays.asList(values()).get(new Random().nextInt(values().length));
    }
}
