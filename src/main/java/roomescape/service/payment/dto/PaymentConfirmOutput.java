package roomescape.service.payment.dto;

import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.service.payment.PaymentStatus;

import java.time.ZonedDateTime;

public record PaymentConfirmOutput(
        String paymentKey,
        String orderId,
        String orderName,
        int totalAmount,
        ZonedDateTime requestedAt,
        ZonedDateTime approvedAt,
        PaymentStatus status) {

    public Payment toPayment(Reservation reservation) {
        return new Payment(paymentKey, orderId, totalAmount, orderName, requestedAt, approvedAt, status, reservation);
    }
}
