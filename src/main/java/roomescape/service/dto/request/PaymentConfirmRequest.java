package roomescape.service.dto.request;

import java.math.BigDecimal;

public record PaymentConfirmRequest(String paymentKey, String orderId, BigDecimal amount) {
}
