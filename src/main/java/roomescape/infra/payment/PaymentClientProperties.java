package roomescape.infra.payment;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment")
public class PaymentClientProperties {

    private final Map<String, PaymentClientProviderProperties> providers;

    public PaymentClientProperties(List<PaymentClientProviderProperties> providers) {
        this.providers = providers.stream()
                .collect(Collectors.toMap(PaymentClientProviderProperties::name, Function.identity()));
    }

    public PaymentClientProviderProperties getProvider(String name) {
        return providers.get(name);
    }
}
