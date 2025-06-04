package roomescape.payment.dto;

public record TossPaymentConfirmResponse(
        String paymentKey,
        String orderId,
        Long totalAmount
) {
}
