package roomescape.service.payment.dto;

import roomescape.service.payment.PaymentStatus;

public record PaymentConfirmResponse(
        String paymentKey,
        String orderId,
        String orderName,
        String totalAmount,
        String provider,
        String paymentMethod,
        PaymentStatus status) {
}
