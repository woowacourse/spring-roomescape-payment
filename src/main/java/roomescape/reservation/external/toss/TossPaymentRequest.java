package roomescape.reservation.external.toss;

public record TossPaymentRequest(
        String paymentKey,
        Long amount,
        String orderId
) {
    public static TossPaymentRequest from(TossPaymentResponse tossPaymentResponse) {
        return new TossPaymentRequest(
                tossPaymentResponse.paymentKey(),
                tossPaymentResponse.totalAmount(),
                tossPaymentResponse.orderId()
        );
    }
}
