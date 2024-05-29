package roomescape.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.toss")
public class TossPaymentProperties {

    private final String secretKey;

    private final String confirmUrl;

    private final String cancelUrl;

    public TossPaymentProperties(String secretKey, String confirmUrl, String cancelUrl) {
        this.secretKey = secretKey;
        this.confirmUrl = confirmUrl;
        this.cancelUrl = cancelUrl;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getConfirmUrl() {
        return confirmUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }
}
