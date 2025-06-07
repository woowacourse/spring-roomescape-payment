package roomescape.payment.infrastructure.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TossPaymentRequest(
        @NotBlank(message = "페이먼트 키는 비어 있을 수 없습니다.") String paymentKey,
        @NotBlank(message = "주문 번호는 비어 있을 수 없습니다.") String orderId,
        @NotNull(message = "결제 금액은 필수입니다.") int amount
) {
}
