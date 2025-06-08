package roomescape.payment.application.dto;

import roomescape.payment.domain.PaymentType;
import roomescape.payment.presentation.dto.request.TossPaymentApproveRequest;

public record PaymentGatewayRequest(
        String paymentKey,
        String orderId,
        Long amount,
        Long reservationId,
        PaymentType paymentType
) {
    public static PaymentGatewayRequest from(final TossPaymentApproveRequest request) {
        return new PaymentGatewayRequest(request.paymentKey(), request.orderId(),
                request.amount(), request.reservationId(), PaymentType.NORMAL);
    }
}
