package roomescape.application.payment.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.toss")
public record TossPaymentProperties(
        String baseUrl,
        String authScheme,
        String secretKey,
        String confirmUri
) {
}
