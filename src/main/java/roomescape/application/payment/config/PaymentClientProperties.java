package roomescape.application.payment.config;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment")
public class PaymentClientProperties {
    private final Map<String, PaymentClientProperty> properties;

    public PaymentClientProperties(List<PaymentClientProperty> providers) {
        this.properties = providers.stream()
                .collect(Collectors.toMap(PaymentClientProperty::name, property -> property));
    }

    public Set<String> getNames() {
        return properties.keySet();
    }

    public PaymentClientProperty get(String name) {
        return properties.get(name);
    }
}
