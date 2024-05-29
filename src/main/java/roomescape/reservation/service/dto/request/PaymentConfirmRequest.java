package roomescape.reservation.service.dto.request;

public record PaymentConfirmRequest(String paymentKey, String orderId, Long amount) {

    public static PaymentConfirmRequest from(ReservationPaymentRequest request) {
        return new PaymentConfirmRequest(request.paymentKey(), request.orderId(), request.amount());
    }
}
