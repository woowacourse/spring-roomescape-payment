package roomescape.config.payment;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("payment")
public class PaymentProperties {

    private final Map<String, PaymentProperty> properties;

    public PaymentProperties(List<PaymentProperty> properties) {
        this.properties = properties.stream()
                .collect(Collectors.toMap(PaymentProperty::vendor, property -> property));
    }

    public Map<String, PaymentProperty> getProperties() {
        return properties;
    }

    public PaymentProperty get(String name) {
        return properties.get(name);
    }
}
