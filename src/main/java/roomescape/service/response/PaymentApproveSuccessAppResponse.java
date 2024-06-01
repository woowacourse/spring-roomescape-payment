package roomescape.service.response;

import java.math.BigDecimal;

public record PaymentApproveSuccessAppResponse(
        String paymentKey,
        String orderId,
        BigDecimal totalAmount,
        String status,
        String requestedAt,
        String approvedAt
) {
}
