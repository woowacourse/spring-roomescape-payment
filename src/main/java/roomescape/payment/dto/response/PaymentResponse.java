package roomescape.payment.dto.response;

import java.time.OffsetDateTime;

public record PaymentResponse(
        String paymentKey,
        String orderId,
        OffsetDateTime approvedAt,
        Long totalAmount
) {
}
