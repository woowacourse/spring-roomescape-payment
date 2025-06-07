package roomescape.payment.infrastructure.dto.request;

public record TossPaymentRequest(
        String paymentKey,
        String orderId,
        int amount
) {
}
