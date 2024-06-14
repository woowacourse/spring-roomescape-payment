package roomescape.reservation.encoder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicAuthEncoder {

    public static String encode(String id, String password) {
        byte[] encoded = Base64.getEncoder().encode((id + ":" + password).getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encoded);
    }
}
