package roomescape.service.reservation.dto;

public record ReservationConfirmRequest(
        Long reservationId,
        String paymentKey,
        String orderId,
        Long amount
) {
    public ReservationConfirmRequest(PaymentRequest paymentRequest, Long reservationId) {
        this(reservationId, paymentRequest.paymentKey(), paymentRequest.orderId(), paymentRequest.amount());
    }
}
