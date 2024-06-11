package roomescape.infrastructure.payment;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment")
public record PaymentProperties(List<PaymentProperty> properties) {
}
