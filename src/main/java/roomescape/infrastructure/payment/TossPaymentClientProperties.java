package roomescape.infrastructure.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "payment")
public record TossPaymentClientProperties(
        String secretKey,
        String baseUrl,
        Duration connectionTimeOut,
        Duration readTimeOut
) {
}
