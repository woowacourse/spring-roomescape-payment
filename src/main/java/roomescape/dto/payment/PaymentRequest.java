package roomescape.dto.payment;

import java.math.BigDecimal;

public record PaymentRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount,
        String paymentType
) {
}
