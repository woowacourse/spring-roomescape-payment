package roomescape.controller.dto;

public record PaymentRequest(
        String paymentKey,
        String orderId,
        String amount) {

    public static PaymentRequest from(UserReservationSaveRequest request) {
        return new PaymentRequest(
                request.paymentKey(),
                request.orderId(),
                request.amount());
    }
}
