package roomescape.util;

import java.util.Base64;

public class Base64Encoder {

    private Base64Encoder() {
    }

    public static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes());
    }
}
