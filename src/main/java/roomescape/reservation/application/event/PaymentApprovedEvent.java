package roomescape.reservation.application.event;

import roomescape.payment.application.dto.PaymentGatewayRequest;
import roomescape.payment.domain.Payment;

public record PaymentApprovedEvent(String paymentKey, String orderId, Long amount, Long reservationId,
                                   Payment payment) {

    public static PaymentApprovedEvent from(PaymentGatewayRequest request, final Payment payment) {
        return new PaymentApprovedEvent(request.paymentKey(), request.orderId(), request.amount(),
                request.reservationId(), payment);
    }
}
