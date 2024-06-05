package roomescape.dto.payment;

import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.reservation.ReservationSaveRequest;

public record PaymentDto(
        String paymentKey,
        String orderId,
        Long amount
) {

    public static PaymentDto from(final ReservationSaveRequest request) {
        return new PaymentDto(request.paymentKey(), request.orderId(), request.amount());
    }

    public static PaymentDto from(final PaymentSaveRequest request) {
        return new PaymentDto(request.paymentKey(), request.orderId(), request.amount());
    }

    public Payment toPayment(final Reservation reservation, final PaymentStatus status) {
        return new Payment(reservation, paymentKey, orderId, amount, status);
    }
}
