package roomescape.reservation.external.toss;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public record TossAuthToken(String token) {

    @Override
    public String token() {
        return Base64.getEncoder().encodeToString((token + ":").getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken() {
        return String.format("basic %s", token());
    }
}
