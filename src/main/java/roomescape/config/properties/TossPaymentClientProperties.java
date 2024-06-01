package roomescape.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "payment.toss")
public class TossPaymentClientProperties implements PaymentClientProperties {
    private final String secretKey;
    private final String baseUrl;

    @ConstructorBinding
    public TossPaymentClientProperties(String secretKey, String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
    }

    @Override
    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }
}
