package roomescape.infrastructure.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toss")
public class TossPaymentProperties {

    private final String paymentUri;
    private final String confirmPath;
    private final String secretKey;

    public TossPaymentProperties(String paymentUri, String confirmPath, String secretKey) {
        this.paymentUri = paymentUri;
        this.confirmPath = confirmPath;
        this.secretKey = secretKey;
    }

    public String getPaymentUri() {
        return paymentUri + confirmPath;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
