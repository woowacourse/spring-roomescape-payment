package roomescape.payment.application.dto;

import roomescape.payment.domain.PaymentType;
import roomescape.payment.presentation.dto.request.PaymentRequest;

public record PaymentGatewayRequest(
        String paymentKey,
        String orderId,
        Long amount,
        Long reservationId,
        PaymentType paymentType,
        String idempotencyKey
) {
    public static PaymentGatewayRequest from(final PaymentRequest request, final String idempotencyKey) {
        return new PaymentGatewayRequest(request.paymentKey(), request.orderId(),
                request.amount(), request.reservationId(), PaymentType.NORMAL, idempotencyKey);
    }
}
