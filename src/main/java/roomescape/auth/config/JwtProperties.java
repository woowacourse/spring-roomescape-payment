package roomescape.auth.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("security.jwt.token")
public class JwtProperties {

    private final String secretKey;
    private final Long expireLength;
}
