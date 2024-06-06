package roomescape.core.dto.payment;

import jakarta.validation.constraints.NotNull;

public class PaymentCancelRequest {
    @NotNull(message = "결제 취소 사유는 비어있을 수 없습니다.")
    private final String cancelReason;

    public PaymentCancelRequest(final String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getCancelReason() {
        return cancelReason;
    }
}
