package roomescape.global.util.encoders;

import java.util.Base64;
import org.springframework.stereotype.Component;
import roomescape.global.util.Encoder;

@Component
public class Base64Encoder implements Encoder {

    @Override
    public String encode(String key) {
        return base64Encode(addColon(key));
    }

    private String addColon(String key) {
        return key + ":";
    }

    private String base64Encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes());
    }
}
