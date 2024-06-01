package roomescape.dto.payment;

import java.math.BigDecimal;

public record PaymentRequest(String orderId, BigDecimal amount, String paymentKey) {
}
