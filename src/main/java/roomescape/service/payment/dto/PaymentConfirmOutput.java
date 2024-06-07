package roomescape.service.payment.dto;

import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.service.payment.PaymentStatus;

public record PaymentConfirmOutput(
        String paymentKey,
        String orderId,
        String orderName,
        int totalAmount,
        String provider,
        String paymentMethod,
        PaymentStatus status) {

    public Payment toPayment(Reservation reservation) {
        return new Payment(paymentKey, orderId, orderName, totalAmount, status, reservation);
    }
}
