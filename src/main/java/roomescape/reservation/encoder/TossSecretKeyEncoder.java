package roomescape.reservation.encoder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TossSecretKeyEncoder {
    private static final String AUTH_PREFIX = "Basic ";

    public static String encode(String secretKey) {
        byte[] encoded = Base64.getEncoder().encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return AUTH_PREFIX + new String(encoded);
    }
}
