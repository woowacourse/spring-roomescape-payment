package roomescape.payment.dto.request;

import roomescape.reservation.dto.MemberReservationWithPaymentAddRequest;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        Long amount
) {

    public PaymentConfirmRequest(MemberReservationWithPaymentAddRequest request) {
        this(request.paymentKey(), request.orderId(), request.amount());
    }
}
