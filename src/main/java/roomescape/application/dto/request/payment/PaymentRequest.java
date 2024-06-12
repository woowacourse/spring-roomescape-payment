package roomescape.application.dto.request.payment;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        String orderId,
        String paymentKey
) {
}
