package roomescape.payment.application.dto;

import java.math.BigDecimal;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount
) {
}
