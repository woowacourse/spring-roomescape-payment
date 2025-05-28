package roomescape.global.api;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TossAuthToken extends AuthToken {

    public TossAuthToken(final String token) {
        super(token);
    }

    @Override
    public String getToken() {
        return Base64.getEncoder().encodeToString((super.getToken() + ":").getBytes(StandardCharsets.UTF_8));
    }

    @Override
    String generateToken() {
        return String.format("basic %s", getToken());
    }
}
