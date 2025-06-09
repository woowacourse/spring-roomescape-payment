package roomescape.reservation.external.toss;

import roomescape.reservation.service.dto.PaymentRequest;

public record TossPaymentRequest(
        String orderId,
        Long amount,
        String paymentKey) implements PaymentRequest {
}
