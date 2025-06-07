package roomescape.payment.dto.request;

public record TossPaymentRequest(
        String paymentKey,
        String orderId,
        int amount
) {
}
