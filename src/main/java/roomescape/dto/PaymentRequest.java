package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PaymentRequest(
        @Schema(description = "예약 ID")
        long reservationId,

        @Schema(description = "결제 키")
        String paymentKey,

        @Schema(description = "주문 ID")
        String orderId,

        @Schema(description = "결제 금액")
        long amount
) {
}
