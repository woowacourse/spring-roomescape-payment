package roomescape.payment.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.toss")
public record TossPaymentProperties(
        String baseUrl,
        int connectionTimeout,
        int readTimeout,
        String secretKey
) {

}
