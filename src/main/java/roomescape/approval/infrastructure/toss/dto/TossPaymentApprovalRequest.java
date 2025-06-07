package roomescape.approval.infrastructure.toss.dto;

import java.math.BigDecimal;
import roomescape.approval.application.dto.PaymentApprovalRequest;

public record TossPaymentApprovalRequest(
        String orderId,
        BigDecimal amount,
        String paymentKey
) implements PaymentApprovalRequest {
}
