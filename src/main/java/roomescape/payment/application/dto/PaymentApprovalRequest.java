package roomescape.payment.application.dto;

import java.math.BigDecimal;

public record PaymentApprovalRequest(String orderId, BigDecimal amount, String paymentKey) {
}
