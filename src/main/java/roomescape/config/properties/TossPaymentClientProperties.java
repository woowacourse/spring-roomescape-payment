package roomescape.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "payment.toss")
public class TossPaymentClientProperties implements PaymentClientProperties {
    private final String secretKey;
    private final String baseUrl;
    private final int connectionTimeoutSeconds;
    private final int readTimeoutSeconds;

    @ConstructorBinding
    public TossPaymentClientProperties(String secretKey, String baseUrl, int connectionTimeoutSeconds, int readTimeoutSeconds) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
        this.connectionTimeoutSeconds = connectionTimeoutSeconds;
        this.readTimeoutSeconds = readTimeoutSeconds;
    }

    @Override
    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    public int getConnectionTimeoutSeconds() {
        return connectionTimeoutSeconds;
    }

    public int getReadTimeoutSeconds() {
        return readTimeoutSeconds;
    }
}
