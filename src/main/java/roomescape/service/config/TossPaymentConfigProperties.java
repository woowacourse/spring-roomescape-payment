package roomescape.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toss-payment")
public class TossPaymentConfigProperties {

    private final String paymentApprovalUrl;
    private final String testSecretKey;

    public TossPaymentConfigProperties(String paymentApprovalUrl, String testSecretKey) {
        this.paymentApprovalUrl = paymentApprovalUrl;
        this.testSecretKey = testSecretKey;
    }

    public String getPaymentApprovalUrl() {
        return paymentApprovalUrl;
    }

    public String getTestSecretKey() {
        return testSecretKey;
    }
}
