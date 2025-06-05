package roomescape.reservation.application.event;

import roomescape.payment.domain.Payment;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;

public record PaymentFailedEvent(String paymentKey, String orderId, Long amount, Long reservationId, Payment payment) {

    public static PaymentFailedEvent from(PaymentApproveRequest request, final Payment payment) {
        return new PaymentFailedEvent(request.paymentKey(), request.orderId(), request.amount(),
                request.reservationId(), payment);
    }
}
