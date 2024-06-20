package roomescape.dto.payment;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        long amount
) {
    public PaymentConfirmRequest(final PaymentResponse response) {
        this(response.paymentKey(), response.orderId(), response.amount());
    }
}
