package roomescape.core.dto.payment;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;

/**
 * @see <a href="https://docs.tosspayments.com/reference#response">토스 결제 승인 응답 api docs</a>
 */
public class TossPaymentResponse {
    @NotBlank(message = "paymentKey 는 비어있을 수 없습니다.")
    private String paymentKey;

    @JsonCreator
    public TossPaymentResponse(String paymentKey) {
        this.paymentKey = paymentKey;
    }

    public String getPaymentKey() {
        return paymentKey;
    }
}
