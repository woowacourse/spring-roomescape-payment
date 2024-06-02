package roomescape.dto;

public record PaymentRequest(String paymentKey,
                             String orderId,
                             int amount) {
    public static PaymentRequest from(ReservationWithPaymentRequest reservationWithPaymentRequest) {
        return new PaymentRequest(reservationWithPaymentRequest.paymentKey(),
                reservationWithPaymentRequest.orderId(),
                reservationWithPaymentRequest.amount());
    }
}
