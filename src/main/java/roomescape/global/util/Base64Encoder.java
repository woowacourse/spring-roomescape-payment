package roomescape.global.util;

import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class Base64Encoder implements Encoder {

    private static final String PREFIX = "Basic ";

    @Override
    public String encode(String key) {
        String value = key +":";
        return PREFIX + Base64.getEncoder().encodeToString(value.getBytes());
    }
}
