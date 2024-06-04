package roomescape.payment.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "payment.toss")
public class PaymentProperties {

    private final String secretKey;
    private final String baseUrl;

    public PaymentProperties(final String secretKey,
                             final String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
