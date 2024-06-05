package roomescape.dto;

import java.math.BigDecimal;

public record PaymentResponse(
        long id,
        String paymentKey,
        String orderId,
        BigDecimal totalAmount
) {
}
