package roomescape.reservation.application.event;

import roomescape.payment.domain.Payment;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;

public record PaymentApprovedEvent(String paymentKey, String orderId, Long amount, Long reservationId,
                                   Payment payment) {

    public static PaymentApprovedEvent from(PaymentApproveRequest request, final Payment payment) {
        return new PaymentApprovedEvent(request.paymentKey(), request.orderId(), request.amount(),
                request.reservationId(), payment);
    }
}
