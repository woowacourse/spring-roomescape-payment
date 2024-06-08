package roomescape.service.payment.dto;

import roomescape.service.payment.PaymentStatus;

import java.time.ZonedDateTime;

public record PaymentCancelOutput(
        String paymentKey,
        String orderId,
        String orderName,
        PaymentStatus status,
        ZonedDateTime requestedAt,
        ZonedDateTime approvedAt) {
}
