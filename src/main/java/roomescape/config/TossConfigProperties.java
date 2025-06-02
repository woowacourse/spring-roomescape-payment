package roomescape.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.toss")
public record TossConfigProperties(String baseUrl, String secretKey, int connectTimeout, int readTimeout) {
}
