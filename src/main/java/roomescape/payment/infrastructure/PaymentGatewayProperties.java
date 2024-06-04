package roomescape.payment.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ConfigurationProperties("payment")
public class PaymentGatewayProperties {
    private final Map<String, PaymentGatewayProperty> properties;

    @ConstructorProperties({"properties"})
    public PaymentGatewayProperties(List<PaymentGatewayProperty> properties) {
        this.properties = properties.stream()
                .collect(Collectors.toMap(PaymentGatewayProperty::company, Function.identity()));
    }

    public PaymentGatewayProperty get(String company) {
        return properties.get(company);
    }
}
