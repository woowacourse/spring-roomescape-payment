package roomescape.payment.service.dto;

import java.math.BigDecimal;

public record PaymentResponse(
        String paymentKey,
        String status,
        String orderId,
        BigDecimal totalAmount,
        String method
) {
}
