package roomescape.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "payment")
public class PaymentConfig {

    private final String approveUrl;
    private final String cancelUrl;
    private final String secretKey;

    public PaymentConfig(String approveUrl, String cancelUrl, String secretKey) {
        this.approveUrl = approveUrl;
        this.cancelUrl = cancelUrl;
        this.secretKey = secretKey;
    }

    public String getApproveUrl() {
        return approveUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
