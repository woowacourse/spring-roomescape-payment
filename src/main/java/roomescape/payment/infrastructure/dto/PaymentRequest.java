package roomescape.payment.infrastructure.dto;

public record PaymentRequest(
        String paymentKey,
        String orderId,
        int amount
) {
}
