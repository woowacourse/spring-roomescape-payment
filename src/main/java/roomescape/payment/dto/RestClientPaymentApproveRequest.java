package roomescape.payment.dto;

import java.math.BigDecimal;

public record RestClientPaymentApproveRequest(String paymentKey, String orderId, BigDecimal amount) {
}
