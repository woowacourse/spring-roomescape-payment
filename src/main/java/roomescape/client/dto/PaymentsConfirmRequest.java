package roomescape.client.dto;

import roomescape.reservation.dto.UserReservationCreateRequest;

public record PaymentsConfirmRequest(String paymentKey,
                                     String orderId,
                                     long amount) {

    public PaymentsConfirmRequest(final UserReservationCreateRequest request) {
        this(request.paymentKey(), request.orderId(), request.amount());
    }
}
