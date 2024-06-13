package roomescape.service.payment.dto;

import roomescape.domain.payment.PaymentInfo;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.payment.ReservationPayment;
import roomescape.domain.reservation.Reservation;

public record PaymentConfirmOutput(
        String paymentKey,
        String type,
        String orderId,
        String orderName,
        String currency,
        String method,
        Long totalAmount,
        PaymentStatus status) {
    public ReservationPayment toReservationPayment(Reservation reservation) {
        PaymentInfo info = new PaymentInfo(paymentKey, orderId, currency, totalAmount, status);
        return new ReservationPayment(info, reservation);
    }
}
