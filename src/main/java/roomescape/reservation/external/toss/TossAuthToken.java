package roomescape.reservation.external.toss;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import roomescape.global.api.AuthToken;

public class TossAuthToken extends AuthToken {

    public TossAuthToken(final String token) {
        super(token);
    }

    @Override
    public String getToken() {
        return Base64.getEncoder().encodeToString((super.getToken() + ":").getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken() {
        return String.format("basic %s", getToken());
    }
}
