package roomescape.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.toss")
public record TossPaymentProperties(
        String secretKey,
        String baseUrl,
        int connectTimeout,
        int readTimeout
) {
}


