package roomescape.reservationpayment.dto;

import roomescape.booking.reservation.Reservation;

public record ReservationPaymentRequest(
        String orderId,
        Long amount,
        String paymentKey,
        Reservation reservation
) {
}
