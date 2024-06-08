package roomescape.dto;

import roomescape.entity.Payment;
import roomescape.entity.Reservation;

public record ReservationPaymentResponse(ReservationResponse reservationResponse, PaymentResponse paymentResponse) {

    public static ReservationPaymentResponse of(Reservation reservation, Payment payment) {
        return new ReservationPaymentResponse(ReservationResponse.from(reservation), PaymentResponse.from(payment));
    }
}
