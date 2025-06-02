package roomescape.reservation.application.event;

import roomescape.payment.presentation.dto.request.PaymentApproveRequest;

public record PaymentFailedEvent(String paymentKey, String orderId, Long amount, Long reservationId) {

    public static PaymentFailedEvent from(PaymentApproveRequest request) {
        return new PaymentFailedEvent(request.paymentKey(), request.orderId(), request.amount(),
                request.reservationId());
    }
}
