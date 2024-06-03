package roomescape.payment.dto;

import java.math.BigDecimal;

public record PaymentConfirmResponse(
        BigDecimal totalAmount,
        String orderId,
        String paymentKey) {
}
