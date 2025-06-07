package roomescape.payment.infrastructure.dto.response;

import java.time.OffsetDateTime;

public record TossPaymentResponse(
        String paymentKey,
        String orderId,
        int totalAmount,
        OffsetDateTime approvedAt
) {
}
