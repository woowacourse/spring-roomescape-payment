package roomescape.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toss")
public record TossProperties(
        String secretKey,
        int connectTimeout,
        int readTimeout
) {

}
