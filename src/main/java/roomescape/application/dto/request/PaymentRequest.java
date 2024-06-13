package roomescape.application.dto.request;

import java.math.BigDecimal;

public record PaymentRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount
) {
}
