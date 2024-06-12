package roomescape.dto.service;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Payment;

public record TossPaymentResponse(
        @Schema(description = "결제 키")
        String paymentKey,

        @Schema(description = "주문 번호")
        String orderId,

        @Schema(description = "결제 금액")
        Long totalAmount
) {
    public Payment toPayment() {
        return new Payment(null, paymentKey, orderId, totalAmount);
    }
}
