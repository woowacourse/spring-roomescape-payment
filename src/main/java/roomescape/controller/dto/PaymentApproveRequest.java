package roomescape.controller.dto;

public record PaymentApproveRequest(
        String paymentKey,
        String orderId,
        String amount) {

    public static PaymentApproveRequest from(UserReservationSaveRequest request) {
        return new PaymentApproveRequest(
                request.paymentKey(),
                request.orderId(),
                request.amount());
    }
}
