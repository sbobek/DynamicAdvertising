package RequestsAndResponses;

import javax.xml.bind.annotation.XmlEnum;
import java.io.Serializable;

/**
 * Created by Vulpes on 2016-12-03.
 */
@XmlEnum(String.class)
public enum Gender implements Serializable {
    MALE,
    FEMALE
}
