package roomescape.dto.payment;

import java.math.BigDecimal;

public record PaymentResponse(
        String paymentKey,
        BigDecimal totalAmount) {
}
