package roomescape.dto;

import java.math.BigDecimal;

public record PaymentRequest(String paymentKey,
                             String orderId,
                             BigDecimal amount) {
    public static PaymentRequest from(ReservationWithPaymentRequest reservationWithPaymentRequest) {
        return new PaymentRequest(reservationWithPaymentRequest.paymentKey(),
                reservationWithPaymentRequest.orderId(),
                reservationWithPaymentRequest.amount());
    }
}
