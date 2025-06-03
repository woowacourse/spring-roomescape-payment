package roomescape.infrastructure;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;

public class EncodeUtil {

    private static final Encoder BASE_64_ENCODER = Base64.getEncoder();

    public static String encodeBase64(final String string) {
        var encodedBytes = BASE_64_ENCODER.encode(string.getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes);
    }
}
