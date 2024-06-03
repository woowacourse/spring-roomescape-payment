package roomescape.reservation.encoder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicAuthEncoder {

    public static String encode(String s) {
        byte[] encoded = Base64.getEncoder().encode((s + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encoded);
    }
}
