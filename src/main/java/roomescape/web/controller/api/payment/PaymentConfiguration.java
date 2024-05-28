package roomescape.web.controller.api.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment")
public record PaymentConfiguration(String secretKey) {
}
