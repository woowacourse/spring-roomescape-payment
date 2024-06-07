package roomescape.service.payment.dto;

import roomescape.service.payment.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentCancelOutput(
        String paymentKey,
        String orderId,
        String orderName,
        PaymentStatus status,
        LocalDateTime requestedAt,
        LocalDateTime approvedAt) {
}
