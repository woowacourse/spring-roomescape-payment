package roomescape.payment.service.dto.request;

import roomescape.reservation.service.dto.request.ReservationPaymentRequest;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        long amount
) {

    public static PaymentConfirmRequest from(ReservationPaymentRequest request) {
        return new PaymentConfirmRequest(request.paymentKey(), request.orderId(), request.amount());
    }
}
