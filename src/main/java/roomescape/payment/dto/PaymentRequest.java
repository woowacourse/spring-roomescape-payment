package roomescape.payment.dto;

public record PaymentRequest(
        String paymentKey,
        String orderId,
        int amount
) {
}
