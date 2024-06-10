package roomescape.dto.payment;

import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.reservation.ReservationSaveRequest;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        long amount,
        long reservationId
) {
    public PaymentConfirmRequest(ReservationSaveRequest request, long reservationId) {
        this(request.paymentKey(), request.orderId(), request.amount(), reservationId);
    }

    public Payment toPayment(final Reservation reservation) {
        return new Payment(reservation, paymentKey, orderId, amount);
    }
}
