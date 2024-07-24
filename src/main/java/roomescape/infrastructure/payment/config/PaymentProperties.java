package roomescape.infrastructure.payment.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment")
public record PaymentProperties(List<PaymentProperty> properties) {
}
