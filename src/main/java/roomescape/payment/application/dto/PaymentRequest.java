package roomescape.payment.application.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        String orderId,
        String paymentKey
) {
}
