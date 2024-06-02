package roomescape.payment.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "payment")
public class PaymentProperties {

    private final String secretKey;
    private final String password;

    public PaymentProperties(final String secretKey, final String password) {
        this.secretKey = secretKey;
        this.password = password;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getPassword() {
        return password;
    }
}
