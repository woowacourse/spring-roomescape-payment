package roomescape.config.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.toss")
public record TossPaymentConfigProperties(
        String secret,
        String baseUri,
        String confirmUri,
        int connectTimeout,
        int readTimeout
) {
}
