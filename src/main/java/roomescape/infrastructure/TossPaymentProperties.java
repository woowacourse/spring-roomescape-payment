package roomescape.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "atto.ash")
public record TossPaymentProperties(String secretKey) {
}
