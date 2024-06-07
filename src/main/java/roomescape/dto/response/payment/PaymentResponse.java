package roomescape.dto.response.payment;

import java.math.BigDecimal;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;

public record PaymentResponse(String paymentKey, String orderId, BigDecimal totalAmount) {
    public Payment toEntity(Reservation reservation) {
        return new Payment(paymentKey, orderId, totalAmount, reservation.getId());
    }
}
