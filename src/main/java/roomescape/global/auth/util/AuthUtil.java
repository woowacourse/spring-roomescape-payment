package roomescape.global.auth.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AuthUtil {

    public static String encodeBasicAuth(String secretKey) {
        String auth = secretKey + ":";
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }
}
