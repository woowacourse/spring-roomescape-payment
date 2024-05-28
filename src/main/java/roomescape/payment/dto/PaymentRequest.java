package roomescape.payment.dto;

import java.math.BigDecimal;

public record PaymentRequest(String paymentKey, String orderId, BigDecimal amount) {
}
