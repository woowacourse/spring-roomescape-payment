package roomescape.global.config.restClient.tosspayment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.pg.toss-payment")
public record TossPaymentProperties(
        String baseUrl,
        String secretKey,
        int connectTimeout,
        int readTimeout
) {
}
