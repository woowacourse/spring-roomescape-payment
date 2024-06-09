package roomescape.dto.response.payment;

import java.math.BigDecimal;

public record PaymentResponse(String paymentKey, String orderId, BigDecimal totalAmount) {
}
