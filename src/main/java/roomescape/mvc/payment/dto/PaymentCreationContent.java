package roomescape.mvc.payment.dto;

import roomescape.mvc.reservation.request.ReservationCreationRequest;
import roomescape.mvc.waiting.request.WaitingCreationRequest;

public record PaymentCreationContent(
        String orderId,
        String paymentKey,
        Long amount
) {

    public PaymentCreationContent(ReservationCreationRequest request) {
        this(request.orderId(), request.paymentKey(), request.amount());
    }

    public PaymentCreationContent(WaitingCreationRequest request) {
        this(request.orderId(), request.paymentKey(), request.amount());
    }
}
