package roomescape.payment.service.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        String orderId,
        String paymentKey
) {
}
