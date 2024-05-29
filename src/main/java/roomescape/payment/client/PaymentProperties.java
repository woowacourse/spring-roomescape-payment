package roomescape.payment.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "payment")
public class PaymentProperties {

    private final String secretKey;

    public PaymentProperties(final String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
