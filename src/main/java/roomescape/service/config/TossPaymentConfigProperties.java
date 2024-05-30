package roomescape.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "toss-payment")
public class TossPaymentConfigProperties {

    private String paymentApprovalUrl;
    private String testSecretKey;

    public void setPaymentApprovalUrl(String paymentApprovalUrl) {
        this.paymentApprovalUrl = paymentApprovalUrl;
    }

    public void setTestSecretKey(String testSecretKey) {
        this.testSecretKey = testSecretKey + ":";
    }

    public String getPaymentApprovalUrl() {
        return paymentApprovalUrl;
    }

    public String getTestSecretKey() {
        return testSecretKey;
    }
}
