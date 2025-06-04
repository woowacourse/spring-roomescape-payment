package roomescape.infrastructure.security;

import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import javax.crypto.SecretKey;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("security.jwt.token")
@Validated
public record JwtProperties(
        @NotNull(message = "security.jwt.token.raw-secret-key는 필수입니다.")
        String rawSecretKey,
        @NotNull(message = "security.jwt.token.expire-duration는 필수입니다.")
        Duration expireDuration
) {

    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(rawSecretKey.getBytes());
    }
}
