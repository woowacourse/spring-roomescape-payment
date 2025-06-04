package roomescape.client.dto;

import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;

public record PaymentConfirmResultDto(
        String paymentKey,
        String orderId,
        Long totalAmount
) {

    public Payment toPaymentEntity(Reservation reservation) {
        return new Payment(paymentKey, orderId, totalAmount, reservation);
    }
}
