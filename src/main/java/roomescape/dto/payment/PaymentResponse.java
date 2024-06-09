package roomescape.dto.payment;

import java.math.BigDecimal;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;

public record PaymentResponse(
        String paymentKey,
        String orderId,
        BigDecimal totalAmount
) {
    public static PaymentResponse from(final Payment payment) {
        return new PaymentResponse(payment.getPaymentKey(), payment.getOrderId(), payment.getAmount());
    }

    public static PaymentResponse empty() {
        return null;
    }

    public Payment toEntity(final Reservation reservation) {
        return new Payment(orderId, paymentKey, totalAmount, reservation);
    }
}
