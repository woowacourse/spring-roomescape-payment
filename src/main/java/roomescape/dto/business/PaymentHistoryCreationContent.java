package roomescape.dto.business;

import roomescape.dto.request.ReservationCreationRequest;
import roomescape.dto.request.WaitingCreationRequest;

public record PaymentHistoryCreationContent(
        String orderId,
        String paymentKey,
        String paymentType,
        Integer amount
) {

    public PaymentHistoryCreationContent(ReservationCreationRequest request) {
        this(request.orderId(), request.paymentKey(), request.paymentType(), request.amount());
    }

    public PaymentHistoryCreationContent(WaitingCreationRequest request) {
        this(request.orderId(), request.paymentKey(), request.paymentType(), request.amount());
    }
}
