package roomescape.service.reservation.pay;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "payment")
@Component
@Validated
public class PaymentProperties {
    @NotNull(message = "결제 승인 url이 비어있습니다.")
    private String approveUrl;
    @NotNull(message = "결제 취소 url이 비어있습니다.")
    private String cancelUrl;
    @NotNull(message = "비밀 키가 비어있습니다.")
    private String secretKey;

    public String getApproveUrl() {
        return approveUrl;
    }

    public void setApproveUrl(String approveUrl) {
        this.approveUrl = approveUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
