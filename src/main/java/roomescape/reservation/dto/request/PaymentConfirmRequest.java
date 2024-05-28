package roomescape.reservation.dto.request;

public record PaymentConfirmRequest(String paymentKey, String orderId, Long amount) {

    public static PaymentConfirmRequest from(ReservationSaveRequest request) {
        return new PaymentConfirmRequest(
                request.paymentKey(),
                request.orderId(),
                request.amount()
        );
    }
}
