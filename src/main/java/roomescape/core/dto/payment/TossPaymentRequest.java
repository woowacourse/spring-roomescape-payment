package roomescape.core.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @see <a href="https://docs.tosspayments.com/reference#request-body-%ED%8C%8C%EB%9D%BC%EB%AF%B8%ED%84%B0">토스 결제 승인 요청
 * api docs</a>
 */
public class TossPaymentRequest {
    @NotBlank(message = "paymentKey 는 비어있을 수 없습니다.")
    private final String paymentKey;

    @NotBlank(message = "orderId 는 비어있을 수 없습니다.")
    private final String orderId;

    @NotNull(message = "amount 는 null일 수 없습니다.")
    private final Long amount;

    public TossPaymentRequest(String paymentKey, String orderId, Long amount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getAmount() {
        return amount;
    }
}
