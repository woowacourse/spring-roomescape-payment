package roomescape.payment.application.dto;

import java.math.BigDecimal;

public record DefaultPaymentRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount
) implements PaymentRequest {
}
