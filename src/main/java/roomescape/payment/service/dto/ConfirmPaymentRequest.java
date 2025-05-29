package roomescape.payment.service.dto;

import roomescape.reservation.service.dto.request.ReservationWithPaymentRequest;

public record ConfirmPaymentRequest(
        String paymentKey,
        String orderId,
        Integer amount
) {
    public static ConfirmPaymentRequest from(ReservationWithPaymentRequest paymentRequest) {
        return new ConfirmPaymentRequest(
                paymentRequest.paymentKey(),
                paymentRequest.orderId(),
                paymentRequest.amount()
        );
    }
}
