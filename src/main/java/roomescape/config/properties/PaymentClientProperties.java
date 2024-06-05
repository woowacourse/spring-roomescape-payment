package roomescape.config.properties;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment")
public class PaymentClientProperties {
    private final Map<String, PaymentClientProperty> properties;

    public PaymentClientProperties(List<PaymentClientProperty> providers) {
        this.properties = providers.stream()
                .collect(Collectors.toMap(PaymentClientProperty::name, property -> property));
    }

    public PaymentClientProperty getProperty(String name) {
        return properties.get(name);
    }
}
