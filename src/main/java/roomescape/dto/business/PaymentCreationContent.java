package roomescape.dto.business;

import roomescape.dto.request.ReservationCreationRequest;
import roomescape.dto.request.WaitingCreationRequest;

public record PaymentCreationContent(
        String orderId,
        String paymentKey,
        Integer amount
) {

    public PaymentCreationContent(ReservationCreationRequest request) {
        this(request.orderId(), request.paymentKey(), request.amount());
    }

    public PaymentCreationContent(WaitingCreationRequest request) {
        this(request.orderId(), request.paymentKey(), request.amount());
    }
}
