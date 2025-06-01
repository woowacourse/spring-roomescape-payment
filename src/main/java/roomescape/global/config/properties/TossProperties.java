package roomescape.global.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toss")
public record TossProperties(
        String secretKey,
        String baseUrl,
        @Value("${toss.timeout.connect}") int connectTimeout,
        @Value("${toss.timeout.read}") int readTimeout
) {

}
