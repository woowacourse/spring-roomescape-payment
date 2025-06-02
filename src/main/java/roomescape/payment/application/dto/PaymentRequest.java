package roomescape.payment.application.dto;

import roomescape.reservation.domain.Reservation;

public record PaymentRequest(
    Reservation reservation,
    String paymentKey,
    String orderId,
    Long amount
) {

}
