package roomescape.payment.infrastructure.dto.request;

import roomescape.reservation.service.dto.request.ReservationWithPaymentRequest;

public record ConfirmPaymentRequest(
        String paymentKey,
        String orderId,
        Long amount
) {
    public static ConfirmPaymentRequest from(ReservationWithPaymentRequest paymentRequest) {
        return new ConfirmPaymentRequest(
                paymentRequest.paymentKey(),
                paymentRequest.orderId(),
                paymentRequest.amount()
        );
    }
}
