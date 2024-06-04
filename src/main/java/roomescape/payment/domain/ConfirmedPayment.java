package roomescape.payment.domain;

import roomescape.reservation.domain.Reservation;

public record ConfirmedPayment(
        String paymentKey,
        String orderId,
        long totalAmount
) {

    public Payment toModel(Reservation reservation) {
        return new Payment(paymentKey, orderId, totalAmount, reservation);
    }
}
