package roomescape.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("toss")
public record PaymentProperties(
        String baseUrl,
        String secret,
        Duration connectTimeout,
        Duration readTimeout,
        String endpointConfirm,
        String endpointCancel) {
}
