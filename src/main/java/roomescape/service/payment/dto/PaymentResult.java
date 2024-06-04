package roomescape.service.payment.dto;

import java.math.BigDecimal;

public record PaymentResult(
        BigDecimal totalAmount,
        String orderId,
        String paymentKey
) {
}
