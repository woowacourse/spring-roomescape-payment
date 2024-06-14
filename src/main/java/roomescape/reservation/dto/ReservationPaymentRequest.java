package roomescape.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import roomescape.payment.PaymentType;
import roomescape.payment.dto.PaymentCreateRequest;
import roomescape.reservation.domain.Reservation;

public record ReservationPaymentRequest(
        @Schema(description = "예약 id", example = "1")
        Long reservationId,
        @Schema(description = "결제 키", example = "tgen_20240528211")
        String paymentKey,
        @Schema(description = "주문 번호", example = "MC40MTMwMTk0ODU0ODU4")
        String orderId,
        @Schema(description = "결제 금액", example = "128000")
        BigDecimal amount,
        @Schema(description = "결제 타입", example = "NORMAL")
        PaymentType paymentType
) {

    public PaymentCreateRequest createPaymentRequest(Reservation reservation) {
        return new PaymentCreateRequest(paymentKey, orderId, amount, reservation);
    }
}
