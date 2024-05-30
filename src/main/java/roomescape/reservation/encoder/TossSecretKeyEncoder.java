package roomescape.reservation.encoder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TossSecretKeyEncoder {

    public static String encode(String secretKey) {
        byte[] encoded = Base64.getEncoder().encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encoded);
    }
}
