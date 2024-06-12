package roomescape.web.support;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt.token")
public record JwtTokenProperties(String secret) {
}
