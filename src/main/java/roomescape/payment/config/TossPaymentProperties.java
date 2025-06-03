package roomescape.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toss")
public class TossPaymentProperties {

    private final String baseUrl;
    private final String secretKey;

    public TossPaymentProperties(final String baseUrl, final String secretKey) {
        this.baseUrl = baseUrl;
        this.secretKey = secretKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
