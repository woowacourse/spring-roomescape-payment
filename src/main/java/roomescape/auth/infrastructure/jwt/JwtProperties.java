package roomescape.auth.infrastructure.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("security.jwt.token")
public class JwtProperties {

    private final String secretKey;
    private final Long expireLength;
}
