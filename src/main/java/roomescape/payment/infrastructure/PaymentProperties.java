package roomescape.payment.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "custom.payment")
public class PaymentProperties {

    private final String baseUrl;
    private final String widgetSecretKey;

    @ConstructorBinding
    public PaymentProperties(String baseUrl, String widgetSecretKey) {
        this.baseUrl = baseUrl;
        this.widgetSecretKey = widgetSecretKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getWidgetSecretKey() {
        return widgetSecretKey;
    }
}
