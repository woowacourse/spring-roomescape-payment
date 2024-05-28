package roomescape.dto.payment;

import roomescape.dto.reservation.UserReservationPaymentRequest;

public record PaymentRequest(
        String paymentKey,
        String orderId,
        int amount,
        String paymentType
) {
    public static PaymentRequest from(UserReservationPaymentRequest userReservationPaymentRequest) {
        return new PaymentRequest(
                userReservationPaymentRequest.paymentKey(),
                userReservationPaymentRequest.orderId(),
                userReservationPaymentRequest.amount(),
                userReservationPaymentRequest.paymentType()
        );
    }
}
