package roomescape.dto.service;

import java.math.BigDecimal;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;

public record PaymentApprovalResult(
        String paymentKey,
        String orderId,
        BigDecimal totalAmount
) {
    public Payment toPayment(Reservation reservation) {
        return new Payment(null, paymentKey, orderId, totalAmount, reservation);
    }
}
