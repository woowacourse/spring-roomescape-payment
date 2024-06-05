package roomescape.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        long reservationId,
        String paymentKey,
        String orderId,
        BigDecimal amount
) {
}
