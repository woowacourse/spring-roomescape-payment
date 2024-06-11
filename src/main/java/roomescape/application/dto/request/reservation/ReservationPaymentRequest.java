package roomescape.application.dto.request.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.application.dto.request.payment.PaymentRequest;

@Schema(name = "예약 결제 요청 정보")
public record ReservationPaymentRequest(
        @Schema(description = "예약 ID", example = "1")
        Long reservationId,

        @Schema(description = "결제 금액", example = "10000")
        Long amount,

        @Schema(description = "주문 ID", example = "1")
        String orderId,

        @Schema(description = "결제 키", example = "imp_1234567890")
        String paymentKey
) {

    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(amount, orderId, paymentKey);
    }
}
