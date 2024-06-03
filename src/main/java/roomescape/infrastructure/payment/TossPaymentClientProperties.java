package roomescape.infrastructure.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment")
public record TossPaymentClientProperties(
        String secretKey,
        String baseUrl
) {
}
