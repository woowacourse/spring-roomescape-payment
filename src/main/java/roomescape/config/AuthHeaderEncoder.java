package roomescape.config;

import java.util.Base64;
import java.util.function.Function;

public enum AuthHeaderEncoder {
    BASIC("Basic", (key) -> Base64.getEncoder().encodeToString((key + ":").getBytes())),
    ;

    private final String type;
    private final Function<String, String> encoder;

    AuthHeaderEncoder(String type, Function<String, String> encoder) {
        this.type = type;
        this.encoder = encoder;
    }

    public String getHeaderValue(String rawKey) {
        return type + " " + encoder.apply(rawKey);
    }
}
