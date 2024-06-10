package roomescape.dto;

import java.math.BigDecimal;
import roomescape.domain.Payment;

public record PaymentResponse(
        long id,
        String paymentKey,
        BigDecimal amount
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getPaymentKey(), payment.getAmount());
    }
}
