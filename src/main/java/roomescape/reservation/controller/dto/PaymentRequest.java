package roomescape.reservation.controller.dto;

public record PaymentRequest(
        String paymentKey,
        String orderId,
        Long amount
) {
    public PaymentRequest(ReservationPaymentRequest reservationPaymentRequest) {
        this(reservationPaymentRequest.paymentKey(), reservationPaymentRequest.orderId(),
                reservationPaymentRequest.amount());
    }
}
