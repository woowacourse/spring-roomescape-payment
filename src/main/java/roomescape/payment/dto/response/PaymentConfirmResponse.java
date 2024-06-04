package roomescape.payment.dto.response;

import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;

public record PaymentConfirmResponse(
        String paymentKey,
        String orderId,
        long totalAmount
) {

    public Payment toModel(Reservation reservation) {
        return new Payment(paymentKey, orderId, totalAmount, reservation);
    }
}
