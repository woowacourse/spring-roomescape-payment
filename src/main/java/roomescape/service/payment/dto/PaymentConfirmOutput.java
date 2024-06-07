package roomescape.service.payment.dto;

import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.payment.PaymentType;

public record PaymentConfirmOutput(
        String paymentKey,
        PaymentType type,
        String orderId,
        String orderName,
        String currency,
        String method,
        Long totalAmount,
        PaymentStatus status) {
}
