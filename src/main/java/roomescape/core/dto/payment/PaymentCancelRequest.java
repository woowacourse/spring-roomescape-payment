package roomescape.core.dto.payment;

import jakarta.validation.constraints.NotNull;

public record PaymentCancelRequest(@NotNull(message = "결제 취소 사유는 비어있을 수 없습니다.") String cancelReason) {
}
