package roomescape.service.reservation.pay;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "payment")
@Component
public class PaymentProperties{
    private String approveUrl;
    private String cancelUrl;
    private String secretKey;

    public String getApproveUrl() {
        return approveUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setApproveUrl(String approveUrl) {
        this.approveUrl = approveUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
