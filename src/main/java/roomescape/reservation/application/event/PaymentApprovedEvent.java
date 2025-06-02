package roomescape.reservation.application.event;

import roomescape.payment.presentation.dto.request.PaymentApproveRequest;

public record PaymentApprovedEvent(String paymentKey, String orderId, Long amount, Long reservationId) {

    public static PaymentApprovedEvent from(PaymentApproveRequest request) {
        return new PaymentApprovedEvent(request.paymentKey(), request.orderId(), request.amount(),
                request.reservationId());
    }
}
