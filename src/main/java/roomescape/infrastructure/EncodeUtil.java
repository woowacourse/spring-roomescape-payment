package roomescape.infrastructure;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncodeUtil {

    public static String base64Encode(final String string) {
        var base64Encoder = Base64.getEncoder();
        var encodedBytes = base64Encoder.encode(string.getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes);
    }
}
