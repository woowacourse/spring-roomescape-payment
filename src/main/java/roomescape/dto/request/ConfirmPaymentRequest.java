package roomescape.dto.request;

public record ConfirmPaymentRequest(
        String paymentKey,
        String orderId,
        int amount,
        String paymentType
) {
    public static ConfirmPaymentRequest from(CreateReservationRequest createReservationRequest) {
        return new ConfirmPaymentRequest(
                createReservationRequest.paymentKey(),
                createReservationRequest.orderId(),
                createReservationRequest.amount(),
                createReservationRequest.paymentType()
        );
    }
}
