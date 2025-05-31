package roomescape.reservation.external.toss;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import roomescape.global.api.AuthToken;

public class TossAuthToken extends AuthToken {

    public TossAuthToken(final String token) {
        super(token);
    }

    @Override
    public String generateToken() {
        return String.format("basic %s", encodeToken());
    }

    private String encodeToken() {
        return Base64.getEncoder().encodeToString((getToken() + ":").getBytes(StandardCharsets.UTF_8));
    }
}
