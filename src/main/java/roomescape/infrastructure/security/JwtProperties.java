package roomescape.infrastructure.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;
import java.time.Duration;

@ConfigurationProperties("security.jwt.token")
public class JwtProperties {

    private final SecretKey secretKey;
    private final Duration expireDuration;

    public JwtProperties(final String secretKey, final Duration expireDuration) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expireDuration = expireDuration;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public Duration getExpireDuration() {
        return expireDuration;
    }
}
