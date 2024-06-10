package roomescape.config.payment;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.toss")
public record TossPaymentConfigProperties(
        String secret,
        String baseUri,
        String confirmUri,
        Duration connectTimeout,
        Duration readTimeout
) {
}
