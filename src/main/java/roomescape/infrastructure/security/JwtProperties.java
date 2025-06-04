package roomescape.infrastructure.security;

import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import javax.crypto.SecretKey;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("security.jwt.token")
public record JwtProperties(
        String rawSecretKey,
        Duration expireDuration
) {

    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(rawSecretKey.getBytes());
    }
}
