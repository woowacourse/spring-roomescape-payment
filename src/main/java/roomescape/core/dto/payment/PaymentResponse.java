package roomescape.core.dto.payment;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentResponse {
    @NotBlank(message = "paymentKey 는 비어있을 수 없습니다.")
    private String paymentKey;

    @JsonCreator
    public PaymentResponse(String paymentKey) {
        this.paymentKey = paymentKey;
    }

    public String getPaymentKey() {
        return paymentKey;
    }
}
