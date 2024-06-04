package roomescape.payment.model;

import roomescape.reservation.model.Reservation;

public record PaymentInfoFromClient(String paymentKey,
                                    String orderId,
                                    Long amount) {
    public Payment toPayment(final Reservation reservation) {
        return new Payment(paymentKey, orderId, amount, reservation);
    }
}
