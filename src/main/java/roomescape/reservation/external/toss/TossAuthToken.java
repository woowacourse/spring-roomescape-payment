package roomescape.reservation.external.toss;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public record TossAuthToken(String secretKey) {

    public String generateToken() {
        return String.format("basic %s", encodeSecretKeyWithColon());
    }

    private String encodeSecretKeyWithColon() {
        return Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}
