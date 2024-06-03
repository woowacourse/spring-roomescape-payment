package roomescape.infrastructure.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("payments")
public record PaymentProperties(String secretKey, String password) {
}
