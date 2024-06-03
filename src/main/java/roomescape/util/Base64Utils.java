package roomescape.util;

import java.util.Base64;

public class Base64Utils {
    private Base64Utils() {
    }

    public static String encode(String text) {
        return Base64.getEncoder()
                .encodeToString(text.getBytes());
    }
}
