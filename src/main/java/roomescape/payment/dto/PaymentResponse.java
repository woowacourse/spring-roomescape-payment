package roomescape.payment.dto;

import java.math.BigDecimal;

public record PaymentResponse(
        String paymentKey,
        BigDecimal amount
) {
}
