package roomescape.dto.payment;

import java.math.BigDecimal;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;

public record PaymentResponse(
        String paymentKey,
        String orderId,
        BigDecimal totalAmount
) {
    public Payment toEntity(final Reservation reservation) {
        return new Payment(paymentKey, totalAmount, reservation);
    }
}
