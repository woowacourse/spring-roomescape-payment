package roomescape.application.dto;

import roomescape.presentation.dto.request.ReservationWithPaymentRequest;

public record PaymentProcessRequest(String paymentKey, String orderId, String amount) {

    public static PaymentProcessRequest of(ReservationWithPaymentRequest request) {
        return new PaymentProcessRequest(request.paymentKey(), request.orderId(), request.amount());
    }
}
