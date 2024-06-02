package roomescape.payment.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {
    private final Map<String, PaymentProperty> properties;

    public PaymentProperties(List<PaymentProperty> payments) {
        this.properties = payments.stream()
                .collect(Collectors.toMap(
                                PaymentProperty::getName,
                                Function.identity()
                        )
                );
    }

    public PaymentProperty getProperty(String name) {
        return properties.get(name);
    }

    public Set<String> getNames() {
        return properties.keySet();
    }
}
