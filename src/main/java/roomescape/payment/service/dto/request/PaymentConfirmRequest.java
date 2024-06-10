package roomescape.payment.service.dto.request;

import roomescape.reservation.service.dto.request.ReservationPaymentSaveRequest;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        long amount
) {

    public static PaymentConfirmRequest from(ReservationPaymentSaveRequest request) {
        return new PaymentConfirmRequest(request.paymentKey(), request.orderId(), request.amount());
    }
}
