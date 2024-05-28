package roomescape.reservation.service;

import roomescape.reservation.dto.UserReservationCreateRequest;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        long amount
) {
    public static PaymentConfirmRequest from(UserReservationCreateRequest request) {
        return new PaymentConfirmRequest(request.paymentKey(), request.orderId(), request.amount());
    }
}
