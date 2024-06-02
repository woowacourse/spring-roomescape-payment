package roomescape.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("toss")
public record PaymentProperties(String secret) {
}
