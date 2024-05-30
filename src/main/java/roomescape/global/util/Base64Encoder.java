package roomescape.global.util;

import java.util.Base64;

public class Base64Encoder implements Encoder {
    @Override
    public String encode(String key) {
        String value = key + ":";
        return Base64
                .getEncoder()
                .encodeToString(value.getBytes());
    }
}
