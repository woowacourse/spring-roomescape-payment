package roomescape.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.payment.dto.PaymentResponse;

@Schema(description = "예약 결제 응답")
public record ReservationPaymentResponse(
        ReservationResponse reservationResponse,
        PaymentResponse paymentResponse) {
}
