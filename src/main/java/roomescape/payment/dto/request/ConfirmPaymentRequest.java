package roomescape.payment.dto.request;

import roomescape.reservation.dto.request.CreateReservationRequest;

public record ConfirmPaymentRequest(String paymentKey,
                                    String orderId,
                                    Long amount) {
    public static ConfirmPaymentRequest from(CreateReservationRequest request) {
        return new ConfirmPaymentRequest(
                request.paymentKey(),
                request.orderId(),
                request.amount());
    }
}
