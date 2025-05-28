package roomescape.payment.dto;

public record TossPaymentRequest(
        String orderId,
        String paymentKey,
        long amount
) {
}
