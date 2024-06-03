package roomescape.dto.payment;

import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.reservation.ReservationSaveRequest;

public record PaymentDto(
        String paymentKey,
        String orderId,
        Long amount
) {

    public static PaymentDto of(ReservationSaveRequest request) {
        return new PaymentDto(request.paymentKey(), request.orderId(), request.amount());
    }

    public Payment toPayment(final Reservation reservation) {
        return new Payment(reservation, paymentKey, orderId, amount);
    }
}
