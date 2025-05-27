package roomescape.reservation.client.dto;

import roomescape.reservation.dto.CreateReservationWithPaymentRequest;

public record PaymentsConfirmRequest(String paymentKey,
                                     String orderId,
                                     long amount) {

    public PaymentsConfirmRequest(CreateReservationWithPaymentRequest request) {
        this(request.paymentKey(), request.orderId(), request.amount());
    }
}
