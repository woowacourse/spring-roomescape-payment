package roomescape.dto.request.payment;

import java.math.BigDecimal;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;

public record PaymentRequest(String orderId, BigDecimal amount, String paymentKey) {
    public Payment toEntity(Reservation reservation) {
        return new Payment(paymentKey, orderId, amount, reservation);
    }
}
