package roomescape.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.toss")
public class TossPaymentProperties {

    private final String secretKey;

    private final String baseUrl;

    private final String confirmPath;

    private final String cancelPath;

    public TossPaymentProperties(String secretKey, String baseUrl, String confirmPath, String cancelPath) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
        this.confirmPath = confirmPath;
        this.cancelPath = cancelPath;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getConfirmUrl() {
        return baseUrl + confirmPath;
    }

    public String getCancelUrl() {
        return baseUrl + cancelPath;
    }
}
