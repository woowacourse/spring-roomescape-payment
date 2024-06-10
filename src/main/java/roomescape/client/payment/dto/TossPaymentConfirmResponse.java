package roomescape.client.payment.dto;

import java.math.BigDecimal;

public record TossPaymentConfirmResponse(
        String paymentKey,
        String type,
        String orderId,
        String orderName,
        String status,
        String requestedAt,
        String approvedAt,
        BigDecimal totalAmount,
        String method,
        String cancels
) {
}
