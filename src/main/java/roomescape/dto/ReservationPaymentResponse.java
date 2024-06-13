package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.entity.Payment;
import roomescape.entity.Reservation;

@Schema(description = "예약 결제 응답 DTO 입니다.")
public record ReservationPaymentResponse(
        @Schema(description = "예약 응답 DTO 입니다.")
        ReservationResponse reservationResponse,
        @Schema(description = "결제 응답 DTO 입니다.")
        PaymentResponse paymentResponse) {

    public static ReservationPaymentResponse of(Reservation reservation, Payment payment) {
        return new ReservationPaymentResponse(ReservationResponse.from(reservation), PaymentResponse.from(payment));
    }
}
