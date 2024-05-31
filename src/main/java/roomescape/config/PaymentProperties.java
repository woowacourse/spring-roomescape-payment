package roomescape.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("payment")
public class PaymentProperties {

    private final String tossSecretKey;

    public PaymentProperties(String tossSecretKey) {
        this.tossSecretKey = tossSecretKey;
    }

    public String getTossSecretKey() {
        return tossSecretKey;
    }
}
