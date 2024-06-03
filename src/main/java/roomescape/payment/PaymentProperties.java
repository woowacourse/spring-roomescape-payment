package roomescape.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.widget.confirm")
public class PaymentProperties {

    private final String secretKey;

    public PaymentProperties(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
