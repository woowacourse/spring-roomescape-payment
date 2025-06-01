package roomescape.payment.dto;

public record TossPaymentRequest(
        String paymentKey,
        String orderId,
        Long amount
) {
}
