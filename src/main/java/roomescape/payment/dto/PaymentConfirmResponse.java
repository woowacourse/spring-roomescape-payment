package roomescape.payment.dto;

import java.math.BigDecimal;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;

public record PaymentConfirmResponse(
        BigDecimal totalAmount,
        String orderId,
        String paymentKey
) {

    public Payment toEntity(Reservation reservation) {
        return new Payment(paymentKey, orderId, totalAmount, reservation);
    }
}
