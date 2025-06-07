package roomescape.payment.dto.response;

import java.time.OffsetDateTime;

public record PaymentResponse(
        String paymentKey,
        String orderId,
        int totalAmount,
        OffsetDateTime approvedAt
) {
}
