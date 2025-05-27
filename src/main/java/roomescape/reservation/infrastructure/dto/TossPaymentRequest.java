package roomescape.reservation.infrastructure.dto;

public record TossPaymentRequest(
        Long amount,
        String orderId,
        String paymentKey
) {

    public static TossPaymentRequest from(final PaymentRequest request) {
        return new TossPaymentRequest(request.amount(), request.orderId(), request.paymentKey());
    }
}
