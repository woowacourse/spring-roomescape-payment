package roomescape.reservation.external.toss;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public record TossAuthToken(String secretKey) {

    public String secretKey() {
        return Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken() {
        return String.format("basic %s", secretKey());
    }
}
