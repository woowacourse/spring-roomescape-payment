package roomescape.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "payment")
public class PaymentClientProperties {
    private final String url;

    @ConstructorBinding
    public PaymentClientProperties(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
